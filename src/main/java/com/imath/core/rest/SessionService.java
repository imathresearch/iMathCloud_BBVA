/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.rest;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.imath.core.model.MathLanguage;
import com.imath.core.model.Session;
import com.imath.core.model.Host;
import com.imath.core.data.MainServiceDB;
import com.imath.core.service.SessionController;

import java.util.logging.Logger;

/**
 * A REST web service that provides access to session controller
 * 
 * @author ipinyol
 */
@Path("/session_service")
@RequestScoped
@Stateful
public class SessionService {
	@Inject private SessionController sc;
	@Inject private MainServiceDB db;
	@Inject private Logger LOG;
	
	@GET
    @Path("/newSession/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public HostDTO REST_requestWebSession(@PathParam("id") String userName) {
		//TODO: Authenticate the call. Make sure that it is done from index.html
		// and that the user is authenticated
		//System.out.println("Creating new session");
		Session session;
		try {
			session = sc.requestWebSession(userName);
		}
		catch (Exception e) {
			LOG.severe("Error creating a session for " + userName);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		HostDTO hostdto = new HostDTO();
		hostdto.url =session.getHostConsole().getUrl(); 
		hostdto.mathLanguage = session.getUser().getMathLanguage();
		return hostdto;
    }
	
	@GET
    @Path("/closeSession/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void REST_closeSession(@PathParam("id") String userName) {
		//TODO: Authenticate the call. Make sure that it is done from index.html
		// and that the user is authenticated
		try {
			sc.closeSession(userName);
		}
		catch (Exception e) {
			LOG.severe("Error closing a session for " + userName);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
    }
	
	private class HostDTO {
		public String url;
		public MathLanguage mathLanguage;
		public HostDTO() {}
	}
}
