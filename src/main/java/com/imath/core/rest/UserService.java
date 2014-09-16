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

import com.imath.core.model.FileShared;
import com.imath.core.model.Session;
import com.imath.core.model.Host;
import com.imath.core.model.IMR_User;
import com.imath.core.data.MainServiceDB;
import com.imath.core.security.SecurityManager;
import com.imath.core.service.SessionController;
import com.imath.core.service.UserController;
import com.imath.core.util.Constants;

import java.text.DecimalFormat;
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
	@Inject private UserController userController;
	@Inject private Logger LOG;
	
	@GET
    @Path("/getUserInfo/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO REST_getUserInfo(@PathParam("id") String userName, @Context SecurityContext sc) {
		try {
		    SecurityManager.secureBasic(userName, sc);
			IMR_User user = db.getIMR_UserDB().findById(userName);
			UserDTO out = new UserDTO();
			out.userName = user.getUserName();
			out.name = user.getFirstName();
			out.email = user.getEMail();
			out.rootName = user.getRootName();
			return out;

		}
		catch (Exception e) {
			LOG.severe("Error creating a session for " + userName);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
    }
	
	@GET
	@Path("/getCurrentStorage/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public UserStorageDTO REST_getCurrentStorage(@PathParam("id") String userName, @Context SecurityContext sc) {
        try {
            SecurityManager.secureBasic(userName, sc);
            DecimalFormat df = new DecimalFormat("0.##");
            UserStorageDTO out = new UserStorageDTO();
            double a = (double)userController.getCurrentStorage(userName);
            double b = (double)Constants.MiB;
            double value = a/b;
            out.currentStorage = df.format(value);
            IMR_User user = db.getIMR_UserDB().findById(userName);
            out.totalStorage = df.format(user.getStorage());
            return out;
        }
        catch (Exception e) {
            LOG.severe("Error creating a session for " + userName);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
	
	private class UserStorageDTO {
	    public String currentStorage;
	    public String totalStorage;
	}
	
	private class UserDTO {
		public String userName;
		public String name;
		public String email;
		public String rootName;
		public UserDTO() {}
				
	}
    
}
