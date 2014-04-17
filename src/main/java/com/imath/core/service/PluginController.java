/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Date;
import java.util.Set;
import java.net.URLEncoder;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.core.SecurityContext;







import org.codehaus.jackson.map.ObjectMapper;



//import org.apache.xalan.xsltc.compiler.Constants;
import com.imath.core.util.Constants;
import com.imath.core.model.File;
import com.imath.core.model.IMR_User;
import com.imath.core.model.MathFunction;
import com.imath.core.model.Host;
import com.imath.core.model.Job;
import com.imath.core.model.Session;
import com.imath.core.service.JobController.Pair;

import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStream;

//import com.imath.core.util.Constants;

//import javax.transaction.UserTransaction;

import java.io.InputStreamReader;

import com.imath.core.data.MainServiceDB;
import com.imath.core.exception.IMathException;

import java.util.logging.Logger;

/**
 * The Plugin Controller class. It offers a set of methods to manage the math functionalities 
 * @author ipinyol
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class PluginController extends AbstractController{
	
	@Inject private JobController jc;
    
	/**
     * Retrieve the list of math functions for a user.
     * @param String - The authenticated user name of the system. If it is a remote file, might need credentials.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<MathFunction> getMathFunctions(String userName) throws Exception{
    	//TODO: Complete method! Now, it returns all math functionalities from MathGroup 1 
    	LOG.info("Get math functions of userId: "+ userName + " requested");
   		try {
   			return db.getMathFunctionDB().getMathFunctionsByGroup(new Long(1));
   		}
   		catch (Exception e) {
   			LOG.severe("Error retriving math functions for userName: " + userName);
   			throw e;
   		}
    }
    
    /**
     * execute the concrete plugin to the best host 
     * @param Long - The Id of the plugin
     * @param Long - The Id of the {@link Session}
     * @param List<String> - The list of parameters
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Pair callPlugin(Long id, Session session, List<String> params, Set<File> dataFiles) throws Exception{
    	// TODO: Search for the best host to execute the job
    	try {
    		// We get the hosts that are active and have the console 
    		// TODO: Do it better!!!!! - PROVISIONAL
    		List<Host> hosts = db.getHostDB().getHostByConsole_and_State(true, true);
    		if (hosts.size()==0) {
    			LOG.severe("No hosts available");
    			throw new Exception("No Hosts available");
    		}
    		else {
    			Host host = hosts.get(0);
    			return callPluginToHost(id,session, params, host, dataFiles);
    		}
    	}
    	catch (Exception e) {
    		LOG.severe("Error getting the hosts");
    		throw e;
    	}
    }
    
    
    
    /**
     * execute the concrete plugin to the best host 
     * @param Long - The Id of the plugin
     * @param Long - The Id of the {@link Session}
     * @param List<String> - The list of parameters
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Pair callPluginPublic(String namePlugin, String nameModule, String params, Set<File> dataFiles, SecurityContext sc) throws Exception{
        // TODO: Search for the best host to execute the job
        try {
            // We get the hosts that are active and have the console 
            // TODO: Do it better!!!!! - PROVISIONAL
            List<Host> hosts = db.getHostDB().getHostByConsole_and_State(true, true);
            if (hosts.size()==0) {
                LOG.severe("No hosts available");
                throw new IMathException(IMathException.IMATH_ERROR.ANY_AVAILABLE_HOST);
            }
            else {
                Host host = hosts.get(0);
                return callPluginToHost(namePlugin, nameModule, params, host, dataFiles, sc);
            }
        }
        catch (Exception e) {
            LOG.severe("Error getting the hosts");
            throw e;
        }
    }
    
    
    /**
     * Execute the concrete plugin. Call from iMathCloud_vi
     * @param Long - The Id of the plugin
     * @param Host - The {@link Host} to use 
     * @param List<String> - The list of parameters
     * @param Session - The open session 
     */
    //@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private Pair callPluginToHost(Long id, Session session, List<String> params, Host host, Set<File> dataFiles) throws Exception {
    	LOG.info("Execute plugin id: " + id);
    	try {
    		// We get the concrete function
    		MathFunction mfunction = db.getMathFunctionDB().findById(id);

    		// We create the Job with the proper parameters
    		Job job = new Job();
    		job.setDescription(mfunction.getDescription());
    		job.setStartDate(new Date());
    		job.setHosted(host);
    		job.setSession(session);
    		job.setOwner(session.getUser());
    		job.setState(Job.States.RUNNING);
    		job.setFiles(dataFiles);
    		db.makePersistent(job);
    		Long idJob = job.getId();
    		
    		// We generate the AJAX call string
    		String urlParams = mfunction.getParams();
    		for(int i=0; i<params.size();i++) {
    			String p = params.get(i);
    			String tag = "#" + i + "#";
    			urlParams = urlParams.replaceAll(tag, URLEncoder.encode(p, "UTF-8"));
    		}
    		LOG.info("CallPlugintToHost. URL Params: " + urlParams);
    		String finalURL =  this.generateURLForHPC2(host.getUrl(), mfunction.getMathGroup().getPlugin(), mfunction.getServiceName(), idJob, urlParams);
    		/*
    		String finalURL = Constants.HPC2_HTTP + 
    		        host.getUrl() + 
    		        ":" + Constants.HPC2_PORT + 
    		        "/" + Constants.HPC2_PLUGIN_SERVICE + 
    		        ""  + "?host=" + Constants.IMATH_HOST + 
    		        "&" + "port=" + Constants.IMATH_PORT + 
    		        "&" + "plugin=" + mfunction.getMathGroup().getPlugin() + 
    		        "&" + "function=" + mfunction.getServiceName() +
    		        "&" + "id=" + idJob + 
    		        "&" + urlParams;*/
    		LOG.info(finalURL);
    		//Pair pair = new Pair();
    		Pair pair  = jc.createPair();
    		pair.finalURL=finalURL;
    		pair.job = job;
    		return pair;
    	}
    	catch (Exception e) {
    		LOG.severe("Error retriving math function id: " + id + " or host id:" + host.getId() + ": " + e.getMessage());
   			throw e;
    	}
    }
    
    
    /**
     * Execute a python program. Call from REST console
     * @param Host - The {@link Host} to use 
     * @param String - String of parameters
     * @param Session - The open session 
     */
    /*private Pair callPythonExecToHost(String params, Host host, Set<File> execFiles, SecurityContext sc) throws Exception {
    		
    		Iterator<File> it = execFiles.iterator();
    		File f = it.next();
            LOG.info("Executing python job: " + f.getUrl());
            
            try {
                // We create the Job with the proper parameters
                Job job = new Job();
                job.setDescription(f.getUrl());
                job.setStartDate(new Date());
                job.setHosted(host);
                job.setSession(null);
                
                String userName = sc.getUserPrincipal().getName();
                IMR_User owner = db.getIMR_UserDB().findById(userName);
                if (owner == null) {
                    throw new IMathException(IMathException.IMATH_ERROR.NOT_USER_IN_DB, userName);
                }
                job.setOwner(owner);
                job.setState(Job.States.RUNNING);
                job.setFiles(execFiles);
                db.makePersistent(job);
                Long idJob = job.getId();
                
                // We generate the AJAX call string
                String urlParams = params;
                String finalURL =  this.generateURLForHPC2PythonExec(host.getUrl(), idJob, urlParams);
                
                LOG.info(finalURL);
                Pair pair = new Pair();
                pair.finalURL=finalURL;
                pair.job = job;
                return pair;
            }
            catch (Exception e) {
                LOG.severe("Executing python job: " + f.getUrl() + ", " + e.getMessage());
                throw e;
            }
    }*/
    
    
    /**
     * Execute the concrete plugin. Call from REST console
     * @param Long - The Id of the plugin
     * @param Host - The {@link Host} to use 
     * @param List<String> - The list of parameters
     * @param Session - The open session 
     */
    //@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private Pair callPluginToHost(String namePlugin, String nameModule, String params, Host host, Set<File> dataFiles, SecurityContext sc) throws Exception {
        LOG.info("Executing plugin: " + namePlugin + ", module: " + nameModule);
        try {
            // We create the Job with the proper parameters
            Job job = new Job();
            job.setDescription(namePlugin + " - " + nameModule);
            job.setStartDate(new Date());
            job.setHosted(host);
            //job.setSession(null);
            
            String userName = sc.getUserPrincipal().getName();
            IMR_User owner = db.getIMR_UserDB().findById(userName);
            if (owner == null) {
                throw new IMathException(IMathException.IMATH_ERROR.NOT_USER_IN_DB, userName);
            }
            
            Session s = db.getSessionDB().findByUser_and_OpenSession(userName);
            
            if (s == null) {
                throw new IMathException(IMathException.IMATH_ERROR.NOT_USER_IN_DB, userName);
            }
            
            job.setSession(s);        
            job.setOwner(owner);
            job.setState(Job.States.RUNNING);
            job.setFiles(dataFiles);
            db.makePersistent(job);
            Long idJob = job.getId();
            
            // We generate the AJAX call string
            String urlParams = params;
            String finalURL =  this.generateURLForHPC2(host.getUrl(), namePlugin, nameModule, idJob, urlParams);
            /*
            String finalURL = Constants.HPC2_HTTP + 
                    host.getUrl() + 
                    ":" + Constants.HPC2_PORT + 
                    "/" + Constants.HPC2_PLUGIN_SERVICE + 
                    ""  + "?host=" + Constants.IMATH_HOST + 
                    "&" + "port=" + Constants.IMATH_PORT + 
                    "&" + "plugin=" + namePlugin + 
                    "&" + "function=" + nameModule +
                    "&" + "id=" + idJob + 
                    "&" + urlParams; */
            LOG.info(finalURL);
            Pair pair = jc.createPair();
            pair.finalURL=finalURL;
            pair.job = job;
            return pair;
        }
        catch (Exception e) {
            LOG.severe("Executing plugin: " + namePlugin + ", module: " + nameModule + ", " + e.getMessage());
            throw e;
        }
    }

    private String generateURLForHPC2(String hostUrl, String namePlugin, String nameModule, Long idJob, String urlParams) {
        String finalURL = Constants.HPC2_HTTP + 
                hostUrl +  
                ":" + Constants.HPC2_PORT + 
                "/" + Constants.HPC2_PLUGIN_SERVICE + 
                ""  + "?host=" + Constants.IMATH_HOST + 
                "&" + "port=" + Constants.IMATH_PORT + 
                "&" + "plugin=" + namePlugin + 
                "&" + "function=" + nameModule +
                "&" + "id=" + idJob + 
                "&" + urlParams;
        return finalURL;
    }
    
    /*
    private String generateURLForHPC2PythonExec(String hostUrl, Long idJob, String urlParams) {
        String finalURL = Constants.HPC2_HTTP + 
                hostUrl +  
                ":" + Constants.HPC2_PORT + 
                "/" + Constants.HPC2_SUBMITJOB_SERVICE + 
                ""  + "?host=" + Constants.IMATH_HOST + 
                "&" + "port=" + Constants.IMATH_PORT + 
                "&" + "id=" + idJob + 
                "&" + urlParams;
        return finalURL;
    }*/
    
    private String generateURLForHPC2Generic(String hostUrl, String service, Long idJob, String urlParams) {
        String finalURL = Constants.HPC2_HTTP + 
                hostUrl +  
                ":" + Constants.HPC2_PORT + 
                "/" + service + 
                ""  + "?id=" + idJob;
                if (!urlParams.equals("")) {
                    finalURL = finalURL + "&" + urlParams;
                }
                
        return finalURL;
    }
    
    /**
     * Returns the completion percentage of the idJob. It generates a sync rest full call to HPC2 to obtain the values 
     * @param idJob
     * @return PercDTO object, which includes a list of percentages. 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PercDTO getCompletionPercentages(Long idJob) throws Exception{
        PercDTO out = null;
        try {
            // We get the hosts that are active and have the console 
            // TODO: Do it better!!!!! - PROVISIONAL
            List<Host> hosts = db.getHostDB().getHostByConsole_and_State(true, true);
            if (hosts.size()==0) {
                LOG.severe("No hosts available");
                throw new IMathException(IMathException.IMATH_ERROR.ANY_AVAILABLE_HOST);
            }
            else {
                Host host = hosts.get(0);
                String finalURL = generateURLForHPC2Generic(host.getUrl(), Constants.HPC2_PCTS_SERVICE,idJob, "");
                String json = this.makeAJAXCall_GET_ONE_INPUT(finalURL); 
                //LOG.info("JSON: " + json);
                // TODO. Refactor this!
                if (json.equals("")) {
                    json = "{\"perc\":[]}";
                }
                ObjectMapper objectMapper = new ObjectMapper();
                out = objectMapper.readValue(json, PercDTO.class);
            }
        } catch(Exception e) {
            out = new PercDTO();
            List<String> x = new ArrayList<String>();
            x.add("NO INFO");
            out.setPerc(x);
        }
        return out;
    }
    
    
    // TODO: re-factor as soon as possible. Put it in another place
    // Rest calls should be place in its class module
    
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
    
    /*
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
    
    public class Pair {
    	public String finalURL;
    	public Job job;
    }
    */
    
    public static class PercDTO {
        private List<String> perc;
        
        public PercDTO(){}
                
        public void setPerc(List<String> perc) {
            this.perc = perc;
        }
        
        public List<String> getPerc() {
            return this.perc;
        }
    }
}
