/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.service;

import java.util.List;

import com.imath.core.exception.IMathException;
import com.imath.core.exception.IMathException.IMATH_ERROR;
import com.imath.core.model.File;
import com.imath.core.model.Host;
import com.imath.core.model.Job;

/**
 * The Deployment Controller class. It offers the functionalities to deploy a Job in a Host 
 * @author iMath
 */
public class DeploymentController extends AbstractController {
	
    /**
     * Submit a Job. The method decides the best {@link Host} to submit the job, and accesses the low level 
     * API methods to start the submission request in the chosen {@link Host}
     * @param job The {@link Job} to be submitted
     * @return The chosen {@link Host} 
     * @throws Exception If no {@link Host} is available 
     */
	public Host submitJob(Job job) throws IMathException, Exception {
		if (job.getSourceFiles() == null) {
			throw new IMathException(IMATH_ERROR.NO_SOURCE_FILES, job.getId().toString());
		}
		if (job.getSourceFiles().size()==0) {
			throw new IMathException(IMATH_ERROR.NO_SOURCE_FILES, job.getId().toString());
		}
		
		// Currently, we only support jobs with one single source file
		if (job.getSourceFiles().size()>1) {
			throw new Exception ("The system only supports jobs with 1 source file. Found:" + job.getSourceFiles().size());
		}
		Host host = chooseBestHost(job);
		startDeploymentSpace(job,host);
		return host;
	}
	
	// TODO: Must be finished
	private Host chooseBestHost(Job job) throws IMathException, Exception {
		// We assume that only one source file is assigned.
		
		File sourceFile = job.getSourceFiles().iterator().next();
		List<Host> hosts = db.getHostDB().getHostByType_and_State(sourceFile.getIMR_Type(),true);
		if (hosts == null) {
			throw new IMathException(IMATH_ERROR.NO_AVAILABLE_HOST, job.getId().toString());
		} 
		if(hosts.size()==0) {
			throw new IMathException(IMATH_ERROR.NO_AVAILABLE_HOST, job.getId().toString());
		}
		return hosts.get(0);
	}
	
	// Creates the needed space in the host to execute the job
	private void startDeploymentSpace(Job job, Host host) throws Exception {
		//throw new Exception("Error starting the Job id:" + job.getId());
		// Makes the call...
	}
}
