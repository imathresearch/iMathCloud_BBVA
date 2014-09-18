package com.imath.core.rest.pub;

import java.util.logging.Logger;

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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.imath.core.data.MainServiceDB;
import com.imath.core.model.IMR_User;
import com.imath.core.model.MathLanguage;
import com.imath.core.model.Role;
import com.imath.core.service.FileController;
import com.imath.core.service.JobController;
import com.imath.core.service.MathLanguageController;
import com.imath.core.service.RoleController;
import com.imath.core.service.UserController;
import com.imath.core.util.Constants;

/**
 * Public REST web services for user management.
 * 
 * @author iMath
 */

@RequestScoped
@Stateful
@Path(Constants.urlUserPath)
public class UserManagement {

    @Inject private Logger LOG;
    @Inject RoleController roleController;
    @Inject MathLanguageController mathLanController;
    @Inject MainServiceDB db;
    @Inject UserController userController;
    @Inject JobController jobController;
    @Inject FileController fileController;
    
    @POST
    @Path("/newUser/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newUser(@PathParam("username")String userName, NewUserDTO newUserDTO) {
        // Check that password is present
        if(newUserDTO.password==null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        // We check that the password is not empty
        if(newUserDTO.password.equals("")) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        Role role = db.getEntityManager().find(Role.class, -1L); // Provisional
        MathLanguage mathLan = db.getEntityManager().find(MathLanguage.class, -1L);  // Python by default
        
        try {
            userController.createNewUser(userName, newUserDTO.password, newUserDTO.firstName, newUserDTO.lastName, role, 
                    mathLan, newUserDTO.eMail, newUserDTO.organization, newUserDTO.phone1, newUserDTO.phone2, newUserDTO.rootName);
        } catch (Exception e) {
        	LOG.severe("Error creating iMth Cloud user");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        return Response.status(Response.Status.OK).build();
    }
    
    @POST
    @Path("/removeUser/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeUser(@PathParam("username")String userName, @Context SecurityContext sc) {
    	try {
    		if (sc!=null) {
    			if (!sc.getUserPrincipal().getName().equals(userName)) {
    				throw new Exception("User: "+ sc.getUserPrincipal().getName() + " tryied to remove user: " + userName);
    			}
    		}
    		this.jobController.removeJobs(userName);
    		this.fileController.removeFiles(userName);
    		this.userController.removeUser(userName);
    		
    		return Response.status(Response.Status.OK).build();
    	} catch (Exception e) {
    	    e.printStackTrace();
    		LOG.severe("Error removing user");
    		return Response.status(Response.Status.BAD_REQUEST).build();
    	}
    }
    
    static public class NewUserDTO {
        public String password;
        public String eMail;
        public String firstName;
        public String lastName;
        public String organization;
        public String phone1;
        public String phone2;
        public String rootName;
    }
}
