/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import com.imath.core.model.File;
import com.imath.core.model.IMR_User;
import com.imath.core.model.MathLanguage;
import com.imath.core.model.Role;
import com.imath.core.util.Constants;
import com.imath.core.util.FileUtils;
import com.imath.core.util.Security;

/**
 * The User Controller class. It offers a set of methods to create/query/modify IMR_Users
 * @author iMath
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class UserController extends AbstractController {
	
    @Inject
    FileUtils fileUtils;
    
    @Inject
    Security security;
    
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
	    
		//Create the IMR_User Entity
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
		
		// We create the base directories in the system to host the Cloud files
		String url = this.createBaseDirectories(userName);
		
		// We create the ROOT Entity
		File root = new File();
		root.setDir(null);
		root.setIMR_Type("dir");
		root.setName(Constants.rootNAME);
		root.setOwner(user);
		root.setUrl(url);
		root.setSharingState(File.Sharing.NO);
		db.makePersistent(root);
		
		// we add the user to the jboss system
		security.createSystemUser(userName, password, Constants.SYSTEM_ROLE);
		return user;
	}
	
	private String createBaseDirectories(String userName) throws Exception {
	    
	    // storage dir directory
	    String urlDirectory = Constants.URI_HEAD + Constants.HOST_STORAGE + Constants.ROOT_FILE_SYSTEM + "/" + userName;
	    fileUtils.createDirectory(urlDirectory);
	    
	    // The exec_dir directory
	    String urlDirectoryExecDir = Constants.URI_HEAD + Constants.HOST_STORAGE + Constants.ROOT_EXEC_DIR + "/" + userName;
	    fileUtils.createDirectory(urlDirectoryExecDir);
	    
	    return urlDirectory;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public IMR_User modifyUser(IMR_User user) throws Exception {
		db.makePersistent(user);
		return user;
	}
	
	
}
