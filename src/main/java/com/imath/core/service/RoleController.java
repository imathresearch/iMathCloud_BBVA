/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.imath.core.model.Role;

/**
 * The Role Controller class. It offers a set of methods to create/query/modify Roles
 * @author ipinyol
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class RoleController extends AbstractController {
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Role createNewRole(String longName, String shortName) throws Exception {
		Role role = new Role();
		role.setLongName(longName);
		role.setShortName(shortName);
		db.makePersistent(role);
		return role;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Role getRole(Long id) throws Exception {
	    
	}
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Role modifyRole(Role role) throws Exception {
		db.makePersistent(role);
		return role;
	}
}
