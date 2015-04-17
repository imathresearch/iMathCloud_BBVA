/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.service;

import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.codehaus.jackson.type.TypeReference;
import org.codehaus.jackson.map.ObjectMapper;

import com.imath.core.model.File;
import com.imath.core.model.Host;
import com.imath.core.model.IMR_User;
import com.imath.core.model.Job;
import com.imath.core.model.JobResult;
import com.imath.core.model.Job.States;
import com.imath.core.model.Session;
import com.imath.core.service.PluginController.PercDTO;
import com.imath.core.util.Constants;
import com.imath.core.util.FileUtils;
import com.imath.core.config.AppConfig;
import com.imath.core.data.MainServiceDB;
import com.imath.core.exception.IMathException;

import java.util.logging.Logger;
import java.io.*;

/**
 * The Job Controller class. It offers a set of methods to manage and execute jobs from files.
 * @author ipinyol
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class JobController extends AbstractController{
    @Inject private FileController fc;
    @Inject private DeploymentController dc;
    @Inject private FileUtils fileUtils;
    
	@Resource private EJBContext ejb;
    /**
     * Removes all the jobs related to the userName
     * @param userName
     * @throws Exception
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeJobs(String userName) throws Exception {
    	List<Job> jobs = this.getUserJobs(userName);
    	for(Job job:jobs) {
    		db.remove(job);
    	}
    }
    
    /**
     * Executes a Job to the current interactive math console of the user owner.
     * Jobs in console must have one and only one File to execute. 
     * @param Job: The job to be executed 
     */
    public void executeJobConsole(Job job) throws Exception {
    	Session session = job.getSession();
    	if (job.getState()!=States.CREATED) {
    		LOG.warning("Job state different than CREATED!");
    	}
    	if (session==null) {
    		LOG.severe("Session is null for job id " + job.getId());
    		throw new Exception("Null Session");
    	}
    	Set<File> files = job.getFiles();
    	if (files == null) {
    		LOG.severe("File list is null for job id " + job.getId());
    		throw new Exception("Null File List");
    	}
    	if (files.size()!=1) {
    		LOG.severe("File list's size is different than 1!");
    		throw new Exception("Bad number of files!");
    	}
    	try {
    		//List<String> content = fc.getFileContent(job.getOwner().getUserName(), files.iterator().next().getId());
    		
    	}
    	catch (Exception e) {
    		LOG.severe("Error getting the content file");
    		throw e;
    	}
    }

    /**
     * Reports the finalization of a Job by idJob 
     * @param idJob: The job to be finalized
     * @param state: The finalization state  
     * @param json: The json result string 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deprecated_reportJobFinalization(Long idJob, States state, String json) throws Exception {
    	try {
    		Job job= db.getJobDB().findById(idJob);
    		JobResult jobResult = new JobResult();
    		jobResult.setJSON(json);
    		job.setState(state);
    		job.setEndDate(new Date());
    		
    		db.makePersistent(jobResult);
    		job.setJobResult(jobResult);
    		
    		//db.makePersistent(job);
    		// Result processing
    		// We assume result will be a simple list of file names in json format: {file1,file2,..fileN}
    		Set<File> inputFiles = job.getFiles();
    		Iterator<File> it = inputFiles.iterator();
    		if (!it.hasNext()) {
    			LOG.severe("No input files for plugin job id:" + job.getId());
    			throw new Exception("No input files for plugin job id:" + job.getId());
    		}
    		
    		File dir = it.next().getDir(); // For the moment, the output directory is the same as the data
    		LOG.info(json);
    		if(job.getState()==States.FINISHED_OK && !json.matches(".*error.*")) { // We do nothing if an error was retrieved.
    			Set<File> outputFiles = new HashSet<File>();
    			String fl = json.substring(1, json.length()-1);	// We get rid off '{' '}'
    			LOG.info("[reportJobFinalization] " + fl);
    			String []fls = fl.split(",");					// we split to get each filename
    			for (int i=0;i<fls.length;i++) {
    				String filename = fls[i];
    				String []parts = filename.split("\\.");		// We split to get the file type, by 'DOT'. 
    															// Notice that '.' is a metacharacter for regex. So we have to put '\' in front,
    															// But since, '\' is a metacharacter for Java Strings, we have to put '\\.' just to match a single DOT.
    				String type = "";
    				if (parts.length>1) {
    					type = parts[parts.length-1];
    				}
    				File file = fc.createNewFileInDirectory(dir, filename, type);
    				outputFiles.add(file);
    			}
    			job.setOutputFiles(outputFiles);
    		}
    		else {
    			job.setState(States.FINISHED_ERROR);
    		}
    		db.makePersistent(job);
    	}
    	catch (Exception e){
    		LOG.severe("Error finding the Job id:" + idJob);
    		throw e;
    	}
    }
    
    
    
    /**
     * Reports the finalization of a Job by idJob 
     * @param idJob: The job to be finalized
     * @param state: The finalization state  
     * @param json: The json result string 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void reportJobFinalization(Long idJob, States state, String json) throws Exception {
    	
    	//System.out.println("reportJobFinalization JSON: " + json);
    	MyMap <String,List<List<String>>> mm = new MyMap<String, List<List<String>>>();   	
    	mm.jsonToMap(json);
    	
    	List<List<String>> ll_files= mm.getValue("files");
    	List<String> listfiles = new ArrayList<String>();
    	listfiles = processFiles(ll_files);
    	
    	List<List<String>> ll_dirs= mm.getValue("dirs");
    	List<String> listdirs = new ArrayList<String>();
    	listdirs = processFiles(ll_dirs);
    	
    	try {
    		
    		Job job= db.getJobDB().findById(idJob);
    		JobResult jobResult = new JobResult();    		
    		jobResult.setJSON(json);
    		job.setState(state);
    		job.setEndDate(new Date());
    		
    		//db.makePersistent(jobResult);    		
    		job.setJobResult(jobResult);

    		if(state==States.FINISHED_OK && !json.matches(".*error.*")) { // We do nothing if an error was retrieved.
    			  
    			Set<File> outputFiles = new HashSet<File>();
    			//String userName = job.getSession().getUser().getUserName();
    			String userName = job.getOwner().getUserName();
    			
    			for (String dirpath : listdirs){   				
    				String [] parts = dirpath.split("/");
    				File dir = fc.getParentDir(dirpath,userName);
    				File file = fc.createNewFileInDirectory(dir, parts[parts.length-1], "dir");
    				outputFiles.add(file);
    			}
    			
    			for (String filenamepath : listfiles){
    				String [] parts = filenamepath.split("/");
    				String [] file_ext = parts[parts.length-1].split("\\.");
    				
    				String file_type = "";
    				if (parts.length>1) {
    					file_type = file_ext[file_ext.length-1];
    				}   				
    				
    				//System.out.println("fc.getParentDir. Params: " + filenamepath + " " + userName);
    				File dir = fc.getParentDir(filenamepath,userName);  				
    				//System.out.println("fc.createNewFileInDirectory. Params " + parts[parts.length-1] + " " + file_type);
    				File file = fc.createNewFileInDirectory(dir, parts[parts.length-1], file_type);   	 		
    				outputFiles.add(file);
    				
    			}
    			
    			/*for(File f: outputFiles){
    				System.out.println(f.getId());
    			}*/
    			job.setOutputFiles(outputFiles);
    			    			
    		}
    		else {
    			job.setState(States.FINISHED_ERROR);    			
    		}
    		db.makePersistent(job);
    	}
    	catch (Exception e){
    		LOG.severe("Error finding the Job id:" + idJob);
    		throw e;
    	}
    }
    
    
    
    /**
     * Execute an already created job 
     * @param idJob: The job Id to be executed
     * @param sc: The security context specifically generated by the request 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Job executeJob(Long idJob, SecurityContext sc) throws Exception{
    	
    	// We fetch the Job
    	Job job = securedFetchJobById(idJob, sc);
    	
    	// We remove the JobResult
    	JobResult jobResult = job.getJobResult();
    	job.setJobResult(null);
    	if(jobResult!=null) {
    		db.remove(jobResult);
    	}
    	
    	job.setState(States.CREATED);
    	try {
    		Host host = dc.submitJob(job);
    		job.setState(States.RUNNING);
    		job.setHosted(host);
    	} catch (Exception e) {
    		job.setState(States.CANCELLED);
    		db.makePersistent(job);
    		LOG.severe(e.getMessage());
    		throw e;
    	}
    	db.makePersistent(job);
    	return job;
    	
    }
    
    /**
     * Create a job 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Job createJob(IMR_User owner, String description, Set<File> sourceFiles, Date startDate) throws Exception{
    	Job job = new Job();
    	job.setOwner(owner);
    	job.setDescription(description);
    	job.setSourceFiles(sourceFiles);
    	job.setStartDate(startDate);
    	job.setState(States.CREATED);
    	db.makePersistent(job);
    	return job;
    }
    
    /**
     * Modify a job 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Job modifyJob(Job job, SecurityContext sc) throws Exception{
    	Job jobO = securedFetchJobById(job.getId(), sc);
    	jobO.copyValues(job);
    	db.makePersistent(jobO);
    	return jobO;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Job getJobById(Long idJob, SecurityContext sc) throws IMathException{
    	return securedFetchJobById(idJob, sc);
    }
    /**
     * Fetch the object Job by its id. It checks that the job id exists and that the request 
     * has enough privileged to access to the job 
     * 
     * @param idJob The job id to be fetched
     * @param sc the security context of the request
     * @return {@link Job} object
     * @throws Exception
     */
    private Job securedFetchJobById(Long idJob, SecurityContext sc) throws IMathException {
    	Job job;
    	try {
    		job= db.getJobDB().findById(idJob);
    	} catch (Exception e) {
    	    throw new IMathException(IMathException.IMATH_ERROR.JOB_DOES_NOT_EXISTS, "" + idJob);
    	}
    	
    	if (!this.accessAllowed(sc, job)) {
    	    throw new IMathException(IMathException.IMATH_ERROR.NO_AUTHORIZATION);
    	}
    	return job;
    }
    
    /**
     * Returns a {@link Job} structure given its id, if user has enough permissions
     * Returns null if job does not exist or user does not have access to it 
     * @param idFile - The file id
     * @param sc - The security context
     * @throws Exception 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Job getJobStructure(Long idJob, SecurityContext sc) throws Exception {
        Job job = db.getJobDB().findById(idJob);
        if (job!=null) {
            if(!this.accessAllowed(sc, job)) {
                job = null;
            }
        }
        return job;
    }
    
    /**
     * Returns a {@link List} of {@link String} with the URIs of the output files
     * It returns an empty list if no output files are attached 
     * @param idJob - The job id
     * @param sc - The security context
     * @throws IMathException when the Job does not exists, is not in the state OK or when user does not have permissions 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<String> getOutoutFilesURI(Long idJob, SecurityContext sc) throws IMathException {
        // Already throws IMathException when job does not exists or no authorization
        Job job = this.getJobById(idJob, sc);
        
        if (job.getState() != States.FINISHED_OK) {
            throw new IMathException(IMathException.IMATH_ERROR.JOB_NOT_IN_OK_STATE, "" + idJob);
        }
        
        Set<File> files = job.getOutputFiles();
        
        List<String> filesStr = new ArrayList<String>();
        for(File file:files) {
            filesStr.add(file.getUrl());
        }
        return filesStr;
    }  
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) 
    public void makeAJAXCall(Pair pair) throws Exception {
    	LOG.info(pair.finalURL);
		try {
			URL url = new URL(pair.finalURL);
			URLConnection urlConn = url.openConnection();
			//urlConn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			urlConn.setUseCaches(false);
			urlConn.setDoOutput(false); 	//Set method to GET
			//urlConn.setDoInput(true);
			urlConn.connect();    		
			urlConn.getInputStream();
		}
		catch (Exception e) {
			LOG.severe("Error in AJAX call: url:" + pair.finalURL);
			pair.job.setState(Job.States.FINISHED_ERROR);
			try {
				db.beginTransaction();
				Job job = db.getJobDB().findById(pair.job.getId());
				job.setState(Job.States.FINISHED_ERROR);
				db.makePersistent(job);
				pair.job = job;
				db.commitTransaction();
			}
			catch (Exception e2) {
				db.rollBackTransaction();
				LOG.severe("Error updating Job State: " + e2.getMessage());	
				throw e2;
			}
		}
    }
    
    
    /**
     * Stops a job of the given idJob. It updates the state of the job to terminated and calls HPC2 to really stop de job in the server 
     * @param idJob 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Job stopJob(Long idJob) throws Exception{
        try {
            Job job = db.getJobDB().findById(idJob);
            Host host = job.getHosted();
            String finalURL = generateURLForHPC2Generic(host.getUrl(), Constants.HPC2_STOPJOB_SERVICE,idJob, "");
            String json = this.makeAJAXCall_GET_ONE_INPUT(finalURL); 
            job.setState(States.CANCELLED);
            em.persist(job);
            return job;
        } catch(Exception e) {
            return null;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized void removeJob(Long idJob) throws Exception{
    	
    	Job job = db.getJobDB().findById(idJob);
    	if(job != null){
		    List<File> outputFiles = new ArrayList<File>(job.getOutputFiles());		   			    
		    fileUtils.trashListFiles(outputFiles);			    		   
			db.remove(job);
    	}
    	else{
    		throw new IMathException(IMathException.IMATH_ERROR.JOB_NOT_IN_OK_STATE, "" + idJob);
    	}
    }
    
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Job> getUserJobs(String userName){
    	List<Job> userJobs = db.getJobDB().getJobsByUser(userName);
    	return userJobs;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeOutputFileFromJob(Long idJob, Long idFile, String userName) throws IMathException{
    	Job j = db.getJobDB().findByIdSecured(idJob, userName);
    	
    	if(j != null){
	    	Set<File> outputFiles = j.getOutputFiles();
	    	for (File f : outputFiles){
	    		if (f.getId() == idFile){
	    			File file = db.getFileDB().findByIdSecured(idFile, userName);
	    			if(file != null){
	    				outputFiles.remove(file);
	    				j.setOutputFiles(outputFiles);
	    				break;
	    			}
	    			else{
	    				ejb.setRollbackOnly();
	    				throw new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND, "" + idFile);
	    			}
	    		}
	    	}
    	}
    	else{
    		throw new IMathException(IMathException.IMATH_ERROR.JOB_NOT_IN_OK_STATE, "" + idJob);
    	}

    }
    
    // REPLICATED from PluginController:
    // TODO: Refactor as soon as possible
    private String generateURLForHPC2Generic(String hostUrl, String service, Long idJob, String urlParams) throws IOException {
        String finalURL = Constants.HPC2_HTTP + 
                hostUrl +  
                ":" + AppConfig.getProp(AppConfig.HPC2_PORT) + 
                "/" + service + 
                ""  + "?id=" + idJob;
                if (!urlParams.equals("")) {
                    finalURL = finalURL + "&" + urlParams;
                }
                
        return finalURL;
    }
    
    // REPLICATED from PluginController:
    // TODO: Refactor as soon as possible
    private String makeAJAXCall_GET_ONE_INPUT(String finalURL) {
        LOG.info(finalURL);
        String json = "";
        try {
            URL url = new URL(finalURL);
            URLConnection urlConn = url.openConnection();
            //urlConn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlConn.setUseCaches(false);
            urlConn.setDoOutput(false);     //Set method to GET
            //urlConn.setDoInput(true);
            urlConn.connect();          
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            json = in.readLine();
            in.close();
            
        } catch(Exception e) {
            // TODO:
            // do nothing
        }
        return json;
    }
    
    /**
     * Given a list of files, where the absolute path of each file is specified without /, compose the complete path for each file 
     * @param ll_list: It is a list of list of files where each file is structured like this ["", "dir1", "dir2", "file"] 
     * @return  a list of files name [/Path1/file1, /Path2/file2, /Path3/file3] 
     * @param json: The json result string 
     */
    private List<String> processFiles(List<List<String>> ll_list){
	   
    	List<String> listfiles = new ArrayList<String>();
	   
    	for (List<String> l : ll_list) {
    		String p = new String();
			for(int i = 0; i < l.size()-1; i++){
				//System.out.println(l.get(i));
				p = p.concat(l.get(i));
				p = p.concat("/");
			}
			p = p.concat(l.get(l.size()-1));
			listfiles.add(p);
    	}
	   
    	return listfiles;
    }
    
    public class Pair {
    	public String finalURL;
    	public Job job;
    }
    
    public Pair createPair(){
    	Pair p = new Pair();
    	return p;

    }
    
    /**
     * Generic HashMap Class that can be used to manage the params received as a json string
     * @throws IOException
     */
    public class MyMap <T,A> {
    	
		Map <T,A> mymap;
	
		public MyMap(){
			mymap = new HashMap<T,A>();
		}
	
		public Map<T,A> getMap(){
			return this.mymap;
		}
	
		public A getValue(T key){
			return this.mymap.get(key);
		}
	
		public void setValue(T key, A value){
			this.mymap.put(key, value);
		}
	
		public void jsonToMap(String json){
			ObjectMapper mapper = new ObjectMapper();
			try {
				this.mymap = mapper.readValue(json, 
				    new TypeReference<HashMap<T,A>>(){});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
    
    //for testing
    public void setFileController(FileController f){
    	this.fc = f;
    	
    }
    
    public void setFileUtils(FileUtils fileUtils){
    	this.fileUtils = fileUtils;
    }
    
    public void setEJB(EJBContext ejb) {
    	this.ejb = ejb;
    }
    
    
}
