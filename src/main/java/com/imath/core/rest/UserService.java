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

import com.imath.core.model.Session;
import com.imath.core.model.Host;
import com.imath.core.model.IMR_User;
import com.imath.core.data.MainServiceDB;
import com.imath.core.service.SessionController;

import java.util.logging.Logger;

/**
 * A REST web service that provides access to session controller
 * 
 * @author ipinyol
 */
@Path("/user_service")
@RequestScoped
@Stateful
public class UserService {
	@Inject private MainServiceDB db;
	@Inject private Logger LOG;
	
	@GET
    @Path("/getUserInfo/{id}")
    @Produces(MediaType.APPLICATION_JSON)		//TODO: Authenticate the call. Make sure that it is done from index.html
    		// and that the user is authenticated

    public IMR_User REST_getUserInfo(@PathParam("id") String userName) {
		try {
			IMR_User user = db.getIMR_UserDB().findById(userName);
			return user;
		}
		catch (Exception e) {
			LOG.severe("Error creating a session for " + userName);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
    }
}
