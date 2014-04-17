/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.security;

import javax.ws.rs.core.SecurityContext;

/**
 * The Security Manager class. It offers a set of methods to grant access to individual entities,
 * depending on the user's credentials. 
 * @author ipinyol
 */
public class SecurityManager {
	public static boolean accessGranted(SecurityContext sc, SecurityOwner so) {
		String userCredentialsName;
		if(sc!=null) {
		    if (so!=null) {
		        userCredentialsName = sc.getUserPrincipal().getName();
		        String userOwnerName = so.getOwnerSecurity().getUserName();
		        return userOwnerName.equals(userCredentialsName);
		    } else {
		        return false;
		    }
		} else {
			return true;
		}
		
	}
}
