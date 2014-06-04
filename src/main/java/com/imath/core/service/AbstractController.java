/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.service;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.core.SecurityContext;

import com.imath.core.data.MainServiceDB;
import com.imath.core.security.SecurityOwner;

import com.imath.core.security.SecurityManager;

/**
 * The Abstract Controller class. It abstracts the general functionalities of every controller.
 * All controllers should extend this class. 
 * @author ipinyol
 */
public abstract class AbstractController {
    @Inject protected Logger LOG;
    @Inject protected MainServiceDB db;
    @Inject protected EntityManager em;

    protected boolean accessAllowed(SecurityContext sc, SecurityOwner so) {
    	return SecurityManager.accessGranted(sc, so);
    }
    
    // For testing purposes only, to simulate injection
    public void setMainServiceDB(MainServiceDB db) {
        this.db = db;
    }
    
    // For testing purposes only, to simulate injection    
    public void setLog(Logger LOG) {
        this.LOG = LOG;
    }
}
