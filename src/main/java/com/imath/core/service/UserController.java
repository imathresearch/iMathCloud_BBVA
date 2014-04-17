/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.imath.core.model.IMR_User;
import com.imath.core.model.MathLanguage;
import com.imath.core.model.Role;

/**
 * The User Controller class. It offers a set of methods to create/query/modify IMR_Users
 * @author ipinyol
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class UserController extends AbstractController {
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public IMR_User createNewUser(String userName, String firstName, String lastName, Role role, MathLanguage math, String eMail) throws Exception {
		IMR_User user = new IMR_User();
		user.setUserName(userName);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setRole(role);
		user.setMathLanguage(math);
		user.setEMail(eMail);
		db.makePersistent(user);
		return user;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public IMR_User modifyUser(IMR_User user) throws Exception {
		db.makePersistent(user);
		return user;
	}
	
	
}
