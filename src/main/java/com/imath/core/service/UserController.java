/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

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
	public IMR_User createNewUser(String userName, String password, String firstName, String lastName, Role role, MathLanguage math, String eMail, String organization, String phone1, String phone2, String rootName) throws Exception {
		LOG.info("[IMATH][CLOUD][newUser]:" + userName + ", " + eMail);
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
		if ( rootName == null || rootName.isEmpty()){
			user.setRootName("ROOT");
		}
		else{
			user.setRootName(rootName);
		}
		db.makePersistent(user);
		
		// We create the Linux user
		security.createLinuxUser(userName);
		
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
		
		// And we add the initial files
		
		// we add the user to the jboss system
		//security.createSystemUser(userName, password, Constants.SYSTEM_ROLE);
		security.createSystemUserDB(userName, password, Constants.SYSTEM_ROLE);

		// And we add the initial files
		addInitialFiles(root, userName);
		return user;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
    public long getCurrentStorage(String userName) throws Exception {
	    File rootFile = db.getFileDB().findROOTByUserId(userName);
	    return fileUtils.dirSize(rootFile.getUrl());
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) 
	public void removeUser(String userName) throws Exception {
		IMR_User user = db.getIMR_UserDB().findById(userName);
		db.remove(user);
		security.removeSystemUser(userName);
	}
	
	//TODO: unit tests!
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void addInitialFiles(File root, String userName) {
	    try {
	        InputStream in = this.getClass().getResourceAsStream(Constants.INITIAL_FILE_CONFIGURATION);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
	        String line;
	        Map<String,File> map = new HashMap<String,File>();
	        while((line = br.readLine()) != null) {
	            if (toProcess(line)) {
	                String fields[] = line.split(",");
	                if(fields.length==2) {
	                    if (fields[0].equals("zip")) {
	                        fileUtils.extractInitialFiles(fields[1], root, userName);
	                    }
	                } else if (fields.length==4) {
	                    File file = new File();
	                    file.setIMR_Type(fields[0]);
	                    file.setName(fields[1]);
	                    file.setOwner(root.getOwner());
	                    file.setSharingState(File.Sharing.NO);
	                    if (fields[2].equals("-")) {
	                        file.setDir(root);
	                    } else {
	                        File dir = map.get(fields[2]);
	                        file.setDir(dir);
	                    }
	                    file.setUrl(root.getUrl() + "/" + fields[3]);
	                    if (fields[0].equals("dir")) {
                            map.put(fields[3], file);
                        }
	                    em.persist(file);
	                }
	            }
	        }
	        br.close();
	    } catch (Exception e) {
	    	// We do not do a explicit rollback. 
	        e.printStackTrace();
	    }
	}
	
	private boolean toProcess(String line) {
	    if (line==null) return false;
	    int len = Constants.IGNORE_LINE.length();
	    if (line.length()<len) return false;
	    if (line.length()>=len && line.substring(0,len).equals(Constants.IGNORE_LINE)) return false;
	    return true;
	}
	
	private String createBaseDirectories(String userName) throws Exception {
	    
	    // storage dir directory
	    String urlDirectory = Constants.URI_HEAD + Constants.HOST_STORAGE + Constants.ROOT_FILE_SYSTEM + "/" + userName;
	    fileUtils.createDirectory(urlDirectory);
	    
	    // The exec_dir directory
	    String urlDirectoryExecDir = Constants.URI_HEAD + Constants.HOST_STORAGE + Constants.ROOT_EXEC_DIR + "/" + userName;
	    fileUtils.createDirectory(urlDirectoryExecDir);
	    
	    fileUtils.protectDirectory(urlDirectoryExecDir, userName);
	    fileUtils.protectDirectory(urlDirectory, userName);
	    return urlDirectory;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public IMR_User modifyUser(IMR_User user) throws Exception {
		db.makePersistent(user);
		return user;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String getRootName(String userName){
		IMR_User user = db.getIMR_UserDB().findById(userName);
		return user.getRootName();
	}
	
	
}
