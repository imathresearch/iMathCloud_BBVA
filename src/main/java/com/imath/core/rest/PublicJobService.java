/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.rest;

import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.imath.core.data.MainServiceDB;
import com.imath.core.service.JobController;

@Path("/public/job_service")
@RequestScoped
@Stateful
public class PublicJobService {
	@Inject private MainServiceDB db;
	@Inject private Logger LOG;
	@Inject private JobController jc;
	
	@GET
    @Path("/resubmitJob/{idJob}")
    @Produces(MediaType.APPLICATION_JSON)
    public void REST_resubmitJob(@PathParam("idJob") Long idJob, @Context SecurityContext sc) {		
		try {
			jc.executeJob(idJob, sc);
		} catch (Exception e) {
			LOG.severe(e.getMessage());
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
    }
}
