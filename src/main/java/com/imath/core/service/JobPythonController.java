/* (C) 2014 iMath Research S.L. - All rights reserved.  */


package com.imath.core.service;

import com.imath.core.config.AppConfig;
import com.imath.core.util.Constants;

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





//import com.imath.core.service.PluginController.Pair;
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
import java.io.IOException;
import java.io.InputStream;

//import com.imath.core.util.Constants;

//import javax.transaction.UserTransaction;


import java.io.InputStreamReader;

import com.imath.core.data.MainServiceDB;
import com.imath.core.exception.IMathException;

import java.util.logging.Logger;

/**
 * The JobPython Controller class. It offers a set of methods to manage the execution of python job
 * @author ammartinez
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class JobPythonController extends AbstractController{
	
	@Inject private JobController jc;
	
	/**
     * execute the concrete plugin to the best host. Call from iMathCloud_vi
     * @param List<String> - The list of parameters
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Pair callPythonExec(Session session, String params, Set<File> dataFiles) throws Exception{
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
                return callPythonExecToHost(session, params, host, dataFiles); //session, params, host, dataFiles
            }
        }
        catch (Exception e) {
            LOG.severe("Error getting the hosts");
            throw e;
        }
    }
	
	
	
	
	/**
     * execute the concrete plugin to the best host 
     * @param List<String> - The list of parameters
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Pair callPythonExecPublic(String params, Set<File> dataFiles, SecurityContext sc) throws Exception{
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
                return callPythonExecToHost(params, host, dataFiles, sc);
            }
        }
        catch (Exception e) {
            LOG.severe("Error getting the hosts");
            throw e;
        }
    }
    
    /**
     * Execute a python program. Call from iMathCloud_vi
     * @param Host - The {@link Host} to use 
     * @param String - String of parameters
     * @param Session - The open session 
     */
    private Pair callPythonExecToHost(Session session, String params, Host host, Set<File> execFiles) throws Exception {
    		
    		Iterator<File> it = execFiles.iterator();
    		//Now we only have one exec files
    		File f = it.next();
            //LOG.info("Executing python job: " + f.getUrl() + " name " + f.getName() + " user " + session.getUser().getUserName());
            
            try {
                // We create the Job with the proper parameters
                Job job = new Job();
                String description = new String ();
                description = f.getUrl();
          
                
                //IMPORTANT !!! URI PATTER --> URI_HEAD + LOCALHOST_STRING + ROOT_SYSTEM_FILE + USERNAME
                description = description.replaceAll(Constants.URI_HEAD+AppConfig.getProp(AppConfig.HOST_STORAGE)+AppConfig.getProp(AppConfig.IMATH_ROOT)+"/"+session.getUser().getUserName(), "");
               
                job.setDescription(description);
                job.setStartDate(new Date());
                job.setHosted(host);
           
                
                job.setSession(session);
        		job.setOwner(session.getUser());
        		
                
               
                job.setState(Job.States.RUNNING);
                job.setFiles(execFiles);          
                db.makePersistent(job);
                //job = (Job) db.save(job);
                                          
                Long idJob = job.getId();
               
                
                
                // We generate the AJAX call string
                String urlParams = params;
                String finalURL =  this.generateURLForHPC2PythonExec(host.getUrl(), idJob, urlParams);
                /*
                String finalURL = Constants.HPC2_HTTP + 
                        host.getUrl() + 
                        ":" + AppConfig.getProp(AppConfig.HPC2_PORT) + 
                        "/" + Constants.HPC2_PLUGIN_SERVICE + 
                        ""  + "?host=" + AppConfig.getProp(AppConfig.IMATH_HOST) + 
                        "&" + "port=" + AppConfig.getProp(AppConfig.IMATH_PORT) + 
                        "&" + "plugin=" + namePlugin + 
                        "&" + "function=" + nameModule +
                        "&" + "id=" + idJob + 
                        "&" + urlParams; */
                LOG.info(finalURL);
                Pair pair  = jc.createPair();
                pair.finalURL=finalURL;
                pair.job = job;
                return pair;
            }
            catch (Exception e) {
                LOG.severe("Executing python job: " + f.getUrl() + ", " + e.getMessage());
                throw e;
            }
    }
    
    
    /**
     * Execute a python program. Call from REST console
     * @param Host - The {@link Host} to use 
     * @param String - String of parameters
     * @param Session - The open session 
     */
    private Pair callPythonExecToHost(String params, Host host, Set<File> execFiles, SecurityContext sc) throws Exception {
    		
    		Iterator<File> it = execFiles.iterator();
    		//Now we only have one exec files
    		File f = it.next();
            //LOG.info("Executing python job: " + f.getUrl());
            
            try {
                // We create the Job with the proper parameters
                Job job = new Job();               
                
                String description = f.getUrl();                       
                //IMPORTANT !!! URI PATTER --> URI_HEAD + LOCALHOST_STRING + ROOT_SYSTEM_FILE + USERNAME
                description = description.replaceAll(Constants.URI_HEAD+Constants.LOCALHOST_String+AppConfig.getProp(AppConfig.IMATH_ROOT)+"/"+sc.getUserPrincipal().getName(), "");
                
                job.setDescription(description);
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
                /*
                String finalURL = Constants.HPC2_HTTP + 
                        host.getUrl() + 
                        ":" + AppConfig.getProp(AppConfig.HPC2_PORT) + 
                        "/" + Constants.HPC2_PLUGIN_SERVICE + 
                        ""  + "?host=" + AppConfig.getProp(AppConfig.IMATH_HOST) + 
                        "&" + "port=" + AppConfig.getProp(AppConfig.IMATH_PORT) + 
                        "&" + "plugin=" + namePlugin + 
                        "&" + "function=" + nameModule +
                        "&" + "id=" + idJob + 
                        "&" + urlParams; */
                //LOG.info(finalURL);
                Pair pair  = jc.createPair();
                pair.finalURL=finalURL;
                pair.job = job;
                return pair;
            }
            catch (Exception e) {
                LOG.severe("Executing python job: " + f.getUrl() + ", " + e.getMessage());
                throw e;
            }
    }
    
    private String generateURLForHPC2PythonExec(String hostUrl, Long idJob, String urlParams) throws IOException{
        String finalURL = Constants.HPC2_HTTP + 
                hostUrl +  
                ":" + AppConfig.getProp(AppConfig.HPC2_PORT) + 
                "/" + Constants.HPC2_SUBMITJOB_SERVICE + 
                ""  + "?host=" + AppConfig.getProp(AppConfig.IMATH_HOST) + 
                "&" + "port=" + AppConfig.getProp(AppConfig.IMATH_PORT) + 
                "&" + "id=" + idJob + 
                "&" + urlParams;
        return finalURL;
    }

    /*
    public class Pair {
    	public String finalURL;
    	public Job job;
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
    */

}
