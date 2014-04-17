package com.imath.core.rest.pub;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.imath.core.exception.IMathException;
import com.imath.core.model.File;
import com.imath.core.model.Job;
import com.imath.core.model.Job.States;
import com.imath.core.model.Session;
import com.imath.core.service.FileController;
import com.imath.core.service.JobController;
import com.imath.core.service.JobController.Pair;
import com.imath.core.service.JobPythonController;
import com.imath.core.service.PluginController;
import com.imath.core.util.Constants;
import com.imath.core.util.FileUtils;
import com.imath.core.util.PublicResponse;


/**
 * Public REST web services for Exec Access
 * 
 * @author iMath
 */

@RequestScoped
@Stateful
@Path(Constants.urlExecPath)
public class Exec {
    @Inject private Logger LOG;
    @Inject private JobController jc; 
    @Inject private FileController fc;
    @Inject private PluginController pc;
    @Inject private JobPythonController jpc;
    
    @GET
    @Path("/{resourceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public PublicResponse.StateDTO REST_getExecStatus(@PathParam("resourceId") String resourceId, @Context SecurityContext sc){
        //TODO: Test
        try { 
            Long idJob = Long.parseLong(resourceId);
            Job job = jc.getJobStructure(idJob, sc);
            PublicResponse.StateDTO out = null;
            if (job == null) {
                out = PublicResponse.generateStatus(Response.Status.ACCEPTED.getStatusCode(), "exec/"+resourceId, "", PublicResponse.Status.NOTFOUND);
            } else {
                PublicResponse.Status aux = null;
                Job.States state = job.getState();
                switch(state) {
                case CANCELLED:
                case FINISHED_OK:
                    aux = PublicResponse.Status.READY;
                    break;
                case FINISHED_ERROR:
                    aux = PublicResponse.Status.FAIL;
                    break;
                case RUNNING:
                    aux = PublicResponse.Status.INPROGRESS;
                    break;
                case CREATED:
                    aux = PublicResponse.Status.QUEUED;
                    break;
                case PAUSED:
                    aux = PublicResponse.Status.WAITING;
                    break;
                }
                out = PublicResponse.generateStatus(Response.Status.ACCEPTED.getStatusCode(), "exec/"+resourceId, job.getDescription(), aux);
                PluginController.PercDTO percDTO = pc.getCompletionPercentages(job.getId());
                if (percDTO != null) {
                    out.setPcts(percDTO.getPerc());
                }
            }
            return out;
        }
        catch (Exception e) {
            LOG.severe("Error accessing resource exec/" + resourceId);
            PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "exec/"+resourceId, "", PublicResponse.Status.NOTFOUND); 
            return out;
        }
    }
    
    /**
     * REST call that returns the output files given a jobId
     */

    @GET
    @Path("/result/{resourceId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getOutputFiles(@PathParam("resourceId") String resourceId, @Context SecurityContext sc) {
        Long idJob = null;
        try {
            idJob = Long.parseLong(resourceId);
        } catch (Exception e) {
            throw new WebApplicationException(e.getCause(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        List<String> outputURIs = null;
        try {
            outputURIs = jc.getOutoutFilesURI(idJob, sc);
        } catch (IMathException e) {
            LOG.severe(e.getMessage());
            throw new WebApplicationException(e.getCause(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        
        if (outputURIs.size()==0) {
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }
        
        // We get the path of the directory
        URI aux = URI.create(outputURIs.get(0));
        java.nio.file.Path auxPath = Paths.get(aux.getPath());
        java.nio.file.Path rootDir = auxPath.getParent();
        
        // The output zip will be <resourceId>.zip 
        java.nio.file.Path zipFile = Paths.get(rootDir.toString(), resourceId + ".zip");

        URI u = zipFile.toUri();
        java.nio.file.Path pathZipFile = Paths.get(u.getPath());
        
        FileUtils fileUtils = new FileUtils();
        try {
            fileUtils.generateZip(zipFile.toUri().toString(), outputURIs);
        } catch (IOException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        java.io.File fileStream = new java.io.File(pathZipFile.toString());
        Response.ResponseBuilder response = Response.ok((Object) fileStream);
        response.header("Content-Disposition", "attachment; filename=" + resourceId + ".zip");
        return response.build();

    }
    
    /**
     * REST call that submits a simple math function that requires a single/multiple data file.
     */
    
    @POST
    @Path("/plugin/{namePlugin}/{nameModule}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PublicResponse.StateDTO REST_submitPlugin(@PathParam("namePlugin") String namePlugin, @PathParam("nameModule") String nameModule , InfoDTO infoDTO, @Context SecurityContext sc) {        
        Set<File> files = null; 
        try {
            files =  fc.getFilesFromString(infoDTO.getDataFiles(), sc);
        } catch (Exception e) {
            LOG.severe("Error gathering files ");
            PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", e.getMessage() , PublicResponse.Status.NOTFOUND);
            return out;
        }
        List<Param> params = infoDTO.getParams();
        if (files.size()>0) {
            // In the case the file list is not empty, we add two parameters to put:
            Iterator<File> it = files.iterator();
            File file = it.next();       // For sure we have more than one element because of the IF statement
            Param p1 = new Param();
            p1.setKey(Constants.HPC2_REST_PLUGIN_KEY_FILENAME);
            p1.setValue(file.getUrl());
            Param p2 = new Param();
            p2.setKey(Constants.HPC2_REST_PLUGIN_KEY_DIRECTORY);
            p2.setValue(file.getDir().getUrl());
            
            // File names will be listed in the parameter fileName separated by Constants.HPC2_SEPARATOR
            while (it.hasNext()) {
                file = it.next();
                String value = p1.getValue();
                value = value + Constants.HPC2_SEPARATOR + file.getUrl();
                
                //System.out.println("VALUE " + value);
                p1.setValue(value);
            }
            params.add(p1);
            params.add(p2);
        }
        
        try {
            String paramsString = generateExtraParams(params);
            //System.out.println("Extra params " + paramsString);
            Pair pair = pc.callPluginPublic(namePlugin, nameModule, paramsString, files, sc);
            jc.makeAJAXCall(pair);
            PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.ACCEPTED.getStatusCode(), "exec/" + pair.job.getId(), pair.job.getDescription(), PublicResponse.Status.INPROGRESS); 
            return out;
        }  catch (Exception e) {
            LOG.severe("Error submitting plugin: " + namePlugin + ", module: " + nameModule);
            PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "", "", PublicResponse.Status.FAIL); 
            return out;
        }
    }
    
    private String generateExtraParams(List<Param> params) throws Exception {
        String out = "";
        for(Param param:params) {
            String assign = param.getKey() + "=" + URLEncoder.encode(param.getValue(), "UTF-8");
            if (!out.equals("")) {
                // We are not the first in the list
                out = out + "&";
            }
            out = out + assign;
        }
        return out;
    }
    
    /**
     * REST call that submits a simple python job.
     * @author andrea  
     */
    
    @POST
    @Path("/jobpython/exec/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PublicResponse.StateDTO REST_submitPythonJob(InfoDTO infoDTO, @Context SecurityContext sc) {        
        
    	Set<File> files = null; 
        try {
            files =  fc.getFilesFromString(infoDTO.getExecFiles(), sc);
        } catch (Exception e) {
            LOG.severe("Error gathering files ");
            PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.NOT_FOUND.getStatusCode(), "", e.getMessage() , PublicResponse.Status.NOTFOUND);
            return out;
        }
        List<Param> params = infoDTO.getParams();
        if (files.size()>0) {
            // In the case the file list is not empty, we add two parameters to put:
            Iterator<File> it = files.iterator();
            File file = it.next();       // For sure we have more than one element because of the IF statement
            Param p1 = new Param();
            p1.setKey(Constants.HPC2_REST_SUBMITJOB_KEY_FILENAME);
            p1.setValue(file.getUrl());
            Param p2 = new Param();
            p2.setKey(Constants.HPC2_REST_SUBMITJOB_KEY_DIRECTORY);
            p2.setValue(file.getDir().getUrl());
            
            // File names will be listed in the parameter fileName separated by Constants.HPC2_SEPARATOR
            while (it.hasNext()) {
                file = it.next();
                String value = p1.getValue();
                value = value + Constants.HPC2_SEPARATOR + file.getUrl();
                
                //System.out.println("VALUE " + value);
                p1.setValue(value);
            }
            params.add(p1);
            params.add(p2);
        }
        
        try {
            String paramsString = generateExtraParams(params);
            //System.out.println("Extra params " + paramsString);
            //PluginController.Pair pair = pc.callPythonExecPublic(paramsString, files, sc);
            Pair pair = jpc.callPythonExecPublic(paramsString, files, sc);
            //pc.makeAJAXCall(pair);
            jc.makeAJAXCall(pair);
            PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.ACCEPTED.getStatusCode(), "exec/" + pair.job.getId(), pair.job.getDescription(), PublicResponse.Status.INPROGRESS); 
            return out;
        }  catch (Exception e) {
            LOG.severe("Error submitting python job");
            PublicResponse.StateDTO out = PublicResponse.generateStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "", "", PublicResponse.Status.FAIL); 
            return out;
        }
    }
    
    /*
     * Class InfoDTO and Param are privte classes prepared to des-serialize JSON structure comming from REST calls 
     */
    private static class InfoDTO {
        private List<String> dataFiles;
        private List<String> execFiles;
        private List<Param> params;
        
        public InfoDTO() {}
        public InfoDTO(List<String> dataFiles, List<String> execFiles, List<Param> params) {
            this.dataFiles = dataFiles;
            this.execFiles = execFiles;
            this.params = params;
        }
        
        public List<String> getDataFiles() {
            return this.dataFiles;
        }
        
        public void setDataFiles(List<String> dataFiles) {
            this.dataFiles = dataFiles;
        }
        
        public List<String> getExecFiles() {
            return this.execFiles;
        }
        
        public void setExecFiles(List<String> execFiles) {
            this.execFiles = execFiles;
        }
        
        public List<Param> getParams() {
            return this.params;
        }
        
        public void setParams(List<Param> params) {
            this.params = params;
        }
     }
    
    private static class Param {
        private String key;
        private String value;
        
        public Param() {}
        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }
        
        public void setKey(String key) {
            this.key = key;
        }
        
        public String getKey() {
            return this.key;
        }
        
        public void setValue(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
}
