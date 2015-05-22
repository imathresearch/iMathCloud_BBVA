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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.imath.core.model.MathLanguage;
import com.imath.core.model.Session;
import com.imath.core.model.Host;
import com.imath.core.data.MainServiceDB;
import com.imath.core.security.SecurityManager;
import com.imath.core.service.SessionController;
import com.imath.core.util.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	private static String LOG_PRE = Constants.LOG_PREFIX_SYSTEM + "[SessionService]";
	
	@GET
    @Path("/newSession/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public HostDTO REST_requestWebSession(@PathParam("id") String userName, @Context SecurityContext sec) {
	    LOG.info(LOG_PRE + "[newSession]" + userName);

		Session session;
		try {
		    SecurityManager.secureBasic(userName, sec);
			session = sc.requestWebSession(userName);
		}
		catch (Exception e) {
			LOG.severe("Error creating a session for " + userName);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		HostDTO hostdto = new HostDTO();
		hostdto.url =session.getHostConsole().getUrl(); 
		hostdto.mathLanguage = session.getUser().getMathLanguage();
		hostdto.port = ""+session.getPortConsole();
		return hostdto;
    }
	
	@GET
    @Path("/closeSession/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void REST_closeSession(@PathParam("id") String userName, @Context SecurityContext sec) {
	    LOG.info(LOG_PRE + "[closeSession]" + userName);
		try {
		    SecurityManager.secureBasic(userName, sec);
			sc.closeSession(userName);
		}
		catch (Exception e) {
			LOG.severe("Error closing a session for " + userName);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
    }
	
	@GET
	@Path("/isConsoleReady/{port}") 
	@Produces(MediaType.APPLICATION_JSON)
	public Response REST_isConsoleReady(@PathParam("port") String port) {
	    LOG.info(LOG_PRE + "[isConsoleReady]" + Constants.LOCALHOST + " " + port);
	    String urlString = "http://" + Constants.LOCALHOST + ":" + port;
	    System.out.println("REST_isConsoleReady url " + urlString);
        try {
            URL url = new URL(urlString);
            URLConnection urlConn = url.openConnection();
            urlConn.setUseCaches(false);
            urlConn.setDoOutput(false);     //Set method to GET
            urlConn.connect();
            return Response.status(Response.Status.OK).build();
        } catch(Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
	}

	
	private class HostDTO {
		public String url;
		public MathLanguage mathLanguage;
		public String port;
		public HostDTO() {}
	}
	
	private class NotebookDTO{
		public String id;
		public String name;
		public String kernelId;
	}
}
