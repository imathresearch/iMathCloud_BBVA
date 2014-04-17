/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.security;

import com.imath.core.model.*;

/**
* The Interface SecurityOwner. All entities that must be protected against ownership, must implement
* such interface.
* @author ipinyol
*/
public interface SecurityOwner {
	/**
     * Return the owner of the Entity to be protected, a {@link IMR_User} object.
     */
	IMR_User getOwnerSecurity(); 
}
