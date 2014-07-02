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
 * @author iMath
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class UserController extends AbstractController {
	
    /**
     * Creates a new user for iMath Cloud, including tables, physical files and jboss update
     * @param userName
     * @param password
     * @param firstName
     * @param lastName
     * @param role
     * @param math
     * @param eMail
     * @param organization
     * @param phone1
     * @param phone2
     * @return
     * @throws Exception
     */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public IMR_User createNewUser(String userName, String password, String firstName, String lastName, Role role, MathLanguage math, String eMail, String organization, String phone1, String phone2) throws Exception {
		IMR_User user = new IMR_User();
		user.setUserName(userName);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setRole(role);
		user.setMathLanguage(math);
		user.setEMail(eMail);
		user.setOrganization(organization);
		user.setPhone1(phone1);
		user.setPhone2(phone2);
		db.makePersistent(user);
		return user;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public IMR_User modifyUser(IMR_User user) throws Exception {
		db.makePersistent(user);
		return user;
	}
	
	
}
