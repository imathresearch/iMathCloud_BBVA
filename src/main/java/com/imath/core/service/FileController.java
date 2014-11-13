/* (C) 2013 iMath Research S.L. - All rights reserved.  */



package com.imath.core.service;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.imath.core.model.File;
import com.imath.core.model.File.Sharing;
import com.imath.core.model.IMR_User;
import com.imath.core.model.Job;
import com.imath.core.util.Constants;
import com.imath.core.util.FileUtils;
import com.imath.core.util.FileUtils.CopyDirVisitor;
import com.imath.core.util.PublicResponse;
import com.imath.core.data.MainServiceDB;
import com.imath.core.exception.IMathException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URI;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.net.URL;
import java.util.logging.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityTransaction;

import org.hibernate.exception.ConstraintViolationException;


/**
 * The File Controller class. It offers a set of methods to manage remote files. 
 * @author iMath
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class FileController extends AbstractController {
	 
    // We do it this for testing purposes, so we can modify the pagination. 
    private static int PAGINATION_REAL = 200;   
    private int PAGINATION = FileController.PAGINATION_REAL;    // See task #47: Used for pagination when requested a file.   
                                                                // Page 1 will download from line 0 to PAGINATION-1
    private String PASTE_ACTION_COPY = "copy";
    private String PASTE_ACTION_MOVE = "move";
    
	@Inject private FileUtils fileUtils;
	@Resource private EJBContext ejb;

	// For testing purposes only, to simulate injection
    public void setFileUtils(FileUtils fu) {
        this.fileUtils = fu;
    }
    
    // For testing purposes only
    public void setPagination(int pagination) {
        this.PAGINATION = pagination;
    }
    
    public int getPagination() {
        return this.PAGINATION;
    }
    
    /**
     * Removes all files of the DB given a userName
     * @param userName
     * @throws Exception
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) 
    public synchronized void removeFiles(String userName) throws Exception {
    	File root = db.getFileDB().findROOTByUserId(userName);
    	List<File> files = db.getFileDB().getFilesByDir(root.getId(), true);
    	for (int i=files.size()-1; i>=0; i--) {
    	    File file = files.get(i);
    		db.remove(file);
    	}
    }
   
    /*@TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String getVideoContent(String userName, File file) throws Exception{
    	//TODO: Test needed!
    	LOG.info("File Video id:" + file.getId() + " requested");
    	try {
    		return(getVideo(userName,file.getUrl()));
    	}
    	catch (Exception e) {
    		LOG.severe("Error opening the file video id: " + file.getId());
    		throw e;
    	}
    }
    
    private String getVideo(String userName, String uri) throws Exception {
    	
    	URI u = URI.create(uri);
    	Path path = Paths.get(u.getPath());
    	String result = new String();
		BufferedReader rd  = null;
	    StringBuilder sb = null;
		//read the result from the server
        rd  = new BufferedReader(new FileReader(path.toString()));
        sb = new StringBuilder();
        String line = new String();
        while ((line = rd.readLine()) != null){
              sb.append(line + '\n');
        }
        rd.close();
        //System.out.println(sb.toString());
        result = sb.toString();
    	// TODO: Test needed!
    	
    	return result;
    }*/
    
    
    /**
     * Retrieve the entire content of a file. It should be only used for source files and small data files.
     * @param String - The authenticated user name of the system. If it is a remote file, might need credentials.
	 * @param File - The {@link File}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<String> getFileContent(String userName, File file) throws Exception{
    	//TODO: Test needed!
    	LOG.info("File id:" + file.getId() + " requested");
    	try {
    		return(getFile(userName,file.getUrl()));
    	}
    	catch (Exception e) {
    		LOG.severe("Error opening the file id: " + file.getId());
    		throw e;
    	}
    }
    
    /**
     * Retrieve the entire content of a file. It should be only used for source files and small data files.
     * @param String - The authenticated user name of the system. If it is a remote file, might need credentials.
	 * @param File - The {@link File}
	 * @param Integer - The page number to be uploaded
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<String> getFileContent(String userName, File file, Integer page) throws Exception{

    	LOG.info("File id:" + file.getId() + " requested");
    	try {
    		return(getFile(userName,file.getUrl(),page));
    	}
    	catch (Exception e) {
    		LOG.severe("Error opening the file id: " + file.getId());
    		throw e;
    	}
    }
    
    /**
     * Save the content of a file. It should be only used for source files and small data files.
     * @param String - The authenticated user name of the system. If it is a remote file, might need credentials.
	 * @param string - The uri of the file
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveFileContent(String userName, File file, List<String> content) throws Exception{
    	//TODO: Test needed!
    	LOG.info("File id:" + file.getId() + " save requested");
    	try {
    		saveFile(userName,file.getUrl(),content);
    		fileUtils.protectFile(file.getUrl(), userName);
    	}
    	catch (Exception e) {
    		LOG.severe("Error opening the file id: " + file.getId());
    		throw e;
    	}
    }
    
    /**
     * Save the content of a file considering pagination. It should be only used for source files and small data files.
     * @param String - The authenticated user name of the system. If it is a remote file, might need credentials.
     * @param String - The uri of the file
     * @param Long - The maximum page loaded 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveFileContent(String userName, File file, List<String> content, Long page) throws Exception{
        //TODO: Test needed!
        if (userName==null || file==null || content ==null || page==null) {
            throw new IMathException(IMathException.IMATH_ERROR.OTHER, "saveFileContent - All parameters must be different than Null");
        }
        LOG.info("File id:" + file.getId() + " save requested with pagination");        
        if (page.longValue()<=0) {
            throw new IMathException(IMathException.IMATH_ERROR.INVALID_PAGINATION);
        }
        try {
            saveFile(userName,file.getUrl(),content, page.longValue());
            fileUtils.protectFile(file.getUrl(), userName);
        }
        catch (Exception e) {
            LOG.severe("Error opening the file id: " + file.getId());
            throw e;
        }
    }
   
   
   /**
     * Check if a file already exist fisically in a specific directory. 
     * @param String filename - the name of the file
	 * @param File dir - the directory to check
	 * @return boolean - true if the file exist, false in other case
	 * We suposse that the file and the dir belong to the same user
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private boolean checkIfFileExistFisically(String filename, File dir) throws Exception {
       	
    	//We have to check if the file already exist fisically inside directory dir
        java.io.File file = new java.io.File(dir.getPath() + "/" + filename);
        return file.exists();
    }
    
    /**
     * Check if a file already exist in a specific directory. 
     * @param String filename - the name of the file
	 * @param File dir - the directory to check
	 * @return boolean - true if the file exist, false in other case
	 * We suposse that the file and the dir belong to the same user
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public synchronized File checkIfFileExistInDirectory(String filename, File dir) throws Exception {  	  	
    	
    	//We have to check if the file already exist in the DB inside directory dir
    	List<File> list_files = db.getFileDB().findAllByName(filename, dir.getOwner().getUserName());
    	    	
    	for (File f : list_files){
    		// The file already exists in the DB    		
    		if (f.getDir().getId() == dir.getId()){
    			return f;
    		}
    	}        	
    	
    	return null;   	
    }
    
    /**
     * Check if a file belongs to an specific user. 
     * @param String filename - the name of the file
	 * @param String userName - the name of the user
	 * @return the file, if it exists, or null in the other case
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public File checkIfFileExistInUser(String filenamePath, String userName) throws Exception {
       	
    	//We obtain the name of the file/directory inside the complete absolute path
    	String [] parts_path = filenamePath.split("/");
    	
    	//SPECIAL CASE: We have to check the special case when filenamePath=/
    	if(parts_path.length == 0){
    		File rootDir = db.getFileDB().findROOTByUserId(userName);
    		return rootDir;
    	}
    	
    	//REGULAR CASE
    	String filename = parts_path[parts_path.length-1];
    	
    	//We have to check if the file already exist in the DB inside directory dir
    	List<File> list_files = db.getFileDB().findAllByName(filename, userName);   	
    	File rootDir = db.getFileDB().findROOTByUserId(userName);
    	
    	if(rootDir != null){
    		String urlRootDir = rootDir.getUrl();  
    		for (File file : list_files){
    			String urlFile = file.getUrl();
    			if (urlFile.indexOf(urlRootDir) != -1){
    				//Getting the absolute path of the file
    				String substring_path = urlFile.substring(urlRootDir.length());
    				if(substring_path.equals(filenamePath)){
    					return file;
    				}
    			}
    		}
    	}
    	return null;
    }
    
    
    /**
     * Returns and persists a new {@link File} contained in a directory passed by parameter. 
     * @param File - The directory
	 * @param String - The name of the file (not the entire path, only the name.
	 * @param String - The IMR_Type of the file.
     * @throws Exception 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public synchronized File createNewFileInDirectory(File dir, String filename, String imrType) throws Exception {
    	    	
    	File file = new File();
    	file.setDir(dir);
    	file.setIMR_Type(imrType);
    	file.setOwner(dir.getOwner());
    	file.setName(filename);
    	file.setUrl(dir.getUrl()+"/"+filename);
    	if (imrType == "dir"){
    		file.setSharingState(File.Sharing.NO);
    	}
    	
    	File f = checkIfFileExistInDirectory(filename, dir);
    	if (f == null){
    		//the file does not exist
    		try{    			
    			db.makePersistent(file);  
    			return file;
    		}
    		catch(Exception e){
    			if(e.getCause() instanceof org.hibernate.exception.ConstraintViolationException){
    			// if the file was created by other transaction, the file exists in DB but it is not seen by the current transaction
    			// Therefore, an exception occurs when trying to insert twice the same file
    			// We return the same file
    				System.out.println("The file exists, and was created by other transaction");
    				return file;
    			}
    			else{
    				throw e;   				
    			}
    		}    		
    	}
    	
    	return f;
    	
    }
    
    /**
     * BUG#20: Creates and persists a file that is uploaded from local workbenches. It requires a new transaction. If either
     * database access of physical writing fail, roll back is produced automatically
     * 
     * @param bytes The content of the file
     * @param fileNamePath The full path name 
     * @param userName the user name of the logged used
     * @param fileName The file name
     * @param imrType The type of the file to be uploaded
     * @throws Exception if an error occurs
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) 
    public File writeFileFromUpload(byte [] bytes, String fileNamePath, String userName, String fileName, String imrType) throws Exception {
        File dir = this.getParentDir(fileNamePath,userName);
        File file = this.createNewFileInDirectory(dir,fileName, imrType); 
        fileUtils.writeFile(bytes,file.getUrl());
        fileUtils.protectFile(file.getUrl(), userName);
        return file;
    }
    
    /**
     * BUG#20: Creates and persists a file that is uploaded from local workbenches to the ROOT in the Cloud. It requires a new transaction. If either
     * database access of physical writing fail, roll back is produced automatically
     * 
     * @param bytes The content of the file
     * @param fileName The file name
     * @param sc The security Context
     * @throws Exception if an error occurs
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) 
    public File writeFileFromUploadInROOT(byte [] bytes, String fileName, SecurityContext sc) throws Exception {
        File file = this.createNewFileInROOTDirectory(fileName, sc);
        fileUtils.writeFile(bytes,file.getUrl());
        if (sc.getUserPrincipal() != null) {
            String userName = sc.getUserPrincipal().getName();
            fileUtils.protectFile(file.getUrl(), userName);
        }
        return file;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public File createNewFile(File dir, String filename, String imrType, IMR_User owner) throws Exception {
    	//TODO: It is necessary to check if the file previously exists in the DB
    	File file = new File();
    	file.setDir(dir);
    	file.setIMR_Type(imrType);
    	file.setOwner(owner);
    	file.setName(filename);
    	file.setUrl("");
    	db.makePersistent(file);
    	return file;
    }
    
    /**
     * Returns and persists a new {@link File} contained in a directory passed by parameter.
     * Here, we assume that all files are in the ROOT directory of the user 
     * @param fileName - the file name 
     * @param sc - The security Context
     * @throws Exception 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public File createNewFileInROOTDirectory(String fileName, SecurityContext sc) throws Exception {
        String userName = sc.getUserPrincipal().getName();
        File file = new File();
        File rootFile = db.getFileDB().findROOTByUserId(userName);
        file.setDir(rootFile);
        String imrType = "";
        // here we obtain the extension of the file
        String parts[] = fileName.split("\\.");
        if (parts.length>1) {
            imrType = parts[parts.length-1];
            LOG.info("TYPE: " + imrType);
        }
   	
        file.setIMR_Type(imrType);
        file.setOwner(rootFile.getOwner());
        file.setName(fileName);
        file.setUrl(rootFile.getUrl()+"/"+fileName);
        
        File f = checkIfFileExistInDirectory(fileName, rootFile);
        
        if (f == null){
    		//the file does not exist
    		db.makePersistent(file);
    		return file;
    	}
    	
    	return f;
              
    }
    
    /**
     * Returns a {@link File} structure given its id
     * @param idFile - The file id
     * @param sc - The security context
     * @throws Exception 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public File getFileStructure(Long idFile, SecurityContext sc) throws Exception {
        File file = db.getFileDB().findById(idFile);
        if (file!=null) {
            if(!this.accessAllowed(sc, file)) {
                file = null;
            }
        }
        return file;
    }
    
    /**
     * Returns a {@link File} structure given its name 
     * @param fileName - The name of the file
     * @param sc - The security context
     * @throws Exception 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<File> getFileStructure(String fileName, SecurityContext sc) throws Exception {
        String userName = sc.getUserPrincipal().getName();
        List<File> files = db.getFileDB().findByName(fileName, userName);
        return files;
    }
    
    /**
     * Receives the Ids of the files in String format, and returns the list of {@link File}s. 
     * It fires an exception when a file is not present or when the file do not belong to the logged user
     * TODO: Accept also files that are shared by other users.
     * @param strFiles - The List of files id in String format
     * @throws IMathException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Set<File> getFilesFromString(List<String> strFiles, SecurityContext sc) throws IMathException {
        Set<File> files = new HashSet<File>();
               
        for(String strFile:strFiles) {
            Long idFile = null;
            try {
                idFile = Long.parseLong(strFile);  // It will raise an exception if cast is not possible
            } catch (Exception e) {
                // Error if strFile cannot be converted to Long, since all id's are long
                throw new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND, "data/" + strFile);
            }
            if (idFile == null) {
                throw new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND, "data/" + strFile);
            }
            File file = db.getFileDB().findById(idFile);
            if(!this.accessAllowed(sc, file)) {
                // Error user is not authorized to access the file. Also return false if file is null
            	throw new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND, "data/" + strFile);
            }
            // At this point the file is found and user has granted access.
            files.add(file);
        }
        return files;
    }
    
    
 
    /**
     * Returns the father {@link File} structure of a file specified in filenamePath
     * @param filenamePath - The name of the file together with the absolute path
     * @param userName - The username of the user to whom belong the file
     * @throws Exception and IMathException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public File getParentDir(String filenamePath, String userName) throws Exception, IMathException{
  	   	
    	//1.Split the path into components
    	String[] pathElements = filenamePath.split("/");
    
  	
    	//2. Obtain the absolute directory path /src/example/file.txt --> /src/example 
    	String[] only_path = Arrays.copyOfRange(pathElements, 0, pathElements.length-1);
    	
    	//3. Join the path again    	
    	if(only_path.length >= 1){
    		String dir_path = "/";
    		//3.1 Check the special case, when de path is /
    		if(only_path.length > 1){
    			for (int i = 1; i < only_path.length -1; i++){
    				dir_path =  dir_path.concat(only_path[i]);
    				dir_path =  dir_path.concat("/");
    			}
    			dir_path = dir_path.concat(only_path[only_path.length-1]);
    		}
    		//System.out.println(dir_path + " " + userName);
    		return getDir(dir_path,userName);
    	}
    	else{
    		//3.2 If the size of only_path is 0, the format of the path is incorrect
    		throw new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND, " empty path");
  		
    	}
   	
    }
    
   
    /**
     * Returns {@link File} structure of a path directory specified in path
     * @param path - The absolute directory path given as a string e.i "/src/example"
     * @param userName - The username of the user to whom belong the file
     * @throws Exception and IMathException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public File getDir(String dirPath, String userName) throws Exception, IMathException{
   		
    	String [] path = dirPath.split("/");
    	//1. Get the root id file of userName 
    	File root = new File();   	
		root = db.getFileDB().findROOTByUserId(userName);
		
    	//2.Check that the path exists
    	Long father_id = root.getId();
    	Boolean find = false;
    	//i starts at 1 because the first element is empty due to the path is absolute
    	for(int i = 1; i < path.length; i++){
    		//System.out.println("Directorio a buscar " + path[i] + " Size " + path.length + "Username " + userName );
    		List<File> file_list = db.getFileDB().findAllByName(path[i], userName);
    		//System.out.println("*****************DENTRO DE getDir despues de findByName ************** ");
    		//System.out.println("List size " + file_list.size());
    		if(file_list.size()>0) {
    			
    			for (File f : file_list) {
    				find = false;
    				//System.out.println("Id candidato " + f.getDir().getId() + " Father id " + father_id);
    				if(father_id == f.getDir().getId()){
    					father_id = f.getId();
    					find = true;
    					break;
    				}
    			}
    			if(find == false){
    				//System.out.println("*****************DENTRO DE getFatherDir antes de ejecutar excepcion por false ************** ");
                    throw new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND, path[i]);
                    
    			}
    		}
    		else{
    			//System.out.println("*****************DENTRO DE getFatherDir antes de ejecutar excepcion por tamano 0 ************** ");
                throw new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND,  path[i]);
            }
    		
    	}
    	
    	if(path.length == 0){
    		return root;
    	}
    	else{
    	
    		if (find == true)
    			return db.getFileDB().findById(father_id);
    		else{
    			//System.out.println("*****************ultima excepcion ************** ");
    			throw new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND, "path does not exist");		
    		}
    	}
    	
  	    	
    }
    
    /**
     * Returns {@link File} structure of specific file for an username
     * @param idFile - the id of the file
     * @param userName - The username of the user to whom belong the file
     * @throws Exception and IMathException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)   
    public File getFile(Long idFile, String userName) throws Exception{
    	return db.getFileDB().findByIdSecured(idFile, userName);
    }
    
    /**
     * Erase a list of files that belongs to an specific user
     * @param all_idFiles - set that contains the idFiles as strings of the files to be erased 
     * @param sc - SecurityContext
     * @throws Exception and IMathException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized void eraseListFiles(Set<String> all_idFiles, SecurityContext sc) throws Exception{
    	 	
    	if(all_idFiles != null && !all_idFiles.isEmpty()){
    		
    		List<String> list_idFiles = new ArrayList<String>(all_idFiles);
    		Set<File> setFiles = this.getFilesFromString(list_idFiles, sc);   	
    		List<File> listFiles = new ArrayList<File>(setFiles);
    	
    		//List<String> list_trashLocation = fileUtils.trashListFiles(listFiles);
    		boolean recover = false;
    		List<String> list_trashLocation = new ArrayList<String>();
    		int i; 
    		for (i = 0; i < listFiles.size(); i++){
    			String trashLocation = this.fileUtils.trashFile(listFiles.get(i));
    			if(trashLocation == null){
    				recover = true;
    				break;
    			}
    			list_trashLocation.add(trashLocation);
    		}
    		
    		if(recover){
    			for(int j = 0; j < list_trashLocation.size(); j++){
    				if(!this.fileUtils.restoreFile(listFiles.get(j), list_trashLocation.get(j))){
    					ejb.setRollbackOnly();
    					throw new IMathException(IMathException.IMATH_ERROR.RECOVER_PROBLEM, "data/" + listFiles.get(j).getId()); 
    				}
    			}
    			ejb.setRollbackOnly();
    			throw new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND, "data/" + listFiles.get(i).getId());
    		}
    		
    		for (File f: listFiles){
    			try{
    				eraseFile(f, sc);
    			}
    			catch(Exception e){
    				for (int j = 0; j < listFiles.size(); j++){
    					if(!this.fileUtils.restoreFile(listFiles.get(j), list_trashLocation.get(j))){
    						ejb.setRollbackOnly();
    						throw new IMathException(IMathException.IMATH_ERROR.RECOVER_PROBLEM, "data/" + listFiles.get(j).getId()); 
    					}
    				}
    				ejb.setRollbackOnly();
    				throw new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND, "data/" + f.getId());
    			}
    		}
    	}
    	else{
    		ejb.setRollbackOnly();
    		throw new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND, "data/empty");
    	}
    }
    
    /**
     * Erase a file that belongs to an specific user
     * @param file - the file object
     * @param sc - Securitycontext
     * @throws Exception and IMathException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)   
    public synchronized void eraseFile(File file, SecurityContext sc) throws Exception{
    	
    	if(file != null){
    		//file is a directory
    		if(file.getIMR_Type().equals("dir")){
    			List<File> subfiles = new ArrayList<File>();
    			subfiles = db.getFileDB().getFilesByDir(file.getId(), true);
  		
    			for(int i = subfiles.size()-1; i >= 0; i--){
    				if(this.accessAllowed(sc, subfiles.get(i))){
    					db.remove(subfiles.get(i));
    				}
    				else{
    					ejb.setRollbackOnly();
    					throw new IMathException(IMathException.IMATH_ERROR.NO_AUTHORIZATION, "No authorised access");
    				}
    			}
    		}
    		//file is a regular file
    		else{
    			if(this.accessAllowed(sc, file)){
    				db.remove(file);
    			}
    			else{
    				ejb.setRollbackOnly();
    				throw new IMathException(IMathException.IMATH_ERROR.NO_AUTHORIZATION, "No authorised access");
    			}
    		}
    	}
    	else{
    		ejb.setRollbackOnly();
    		throw new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND, "data/null");
    	}
    	
    }
    
    /**
     * Rename a file that belongs to an specific user
     * @param idFile - idFile of the file to be renamed
     * @param newName - new name of the file
     * @param sc - Securitycontext
     * @throws Exception and IMathException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized void renameFile(String idFile, String newName, SecurityContext sc) throws Exception{
    	
    	if(idFile == null || newName == null){
     		throw new IMathException(IMathException.IMATH_ERROR.OTHER, "Rename File: file or new name is null");
    	}
  
    	String userName = sc.getUserPrincipal().getName();
    	File file_to_rename = db.getFileDB().findByIdSecured(Long.valueOf(idFile), userName);

    	if(file_to_rename != null){ 		
    		
    		String absolute_pathNameFile = fileUtils.getAbsolutePath(file_to_rename.getUrl(), userName);
    		File parentDir = getParentDir(absolute_pathNameFile, userName);
  
    		//We have to check if a file exists in the parent directory of the file to rename
    		//with the new name
    		
    		if(checkIfFileExistInDirectory(newName, parentDir) == null){
    			//Getting the new url
    			String old_name = file_to_rename.getName();
				String oldUrl = file_to_rename.getUrl();  		
				int indexStart_oldName = oldUrl.length() - old_name.length();
				String newUrl = oldUrl.substring(0, indexStart_oldName).concat(newName);

				//Physically changing the name
				String new_absolute_pathNameFile = fileUtils.getAbsolutePath(newUrl, userName);				
				if(fileUtils.moveFile(oldUrl, newUrl)){					
					//If the file is a regular file then,
					//We have to obtain the extension
					//We supposse the base case, where the extension is the last part after the dot
					if(!file_to_rename.getIMR_Type().equals("dir")){					
						String [] name_extension = newName.split("\\.");
						file_to_rename.setIMR_Type(name_extension[name_extension.length-1]);
					}
					
					file_to_rename.setName(newName);
					file_to_rename.setUrl(newUrl);
					db.makePersistent(file_to_rename);
					
					//If the file is a directory, it is necessary to change the URL of
					//all its children files
	    			if(file_to_rename.getIMR_Type().equals("dir")){
	    				List<File> children_files = new ArrayList<File>();
	        			children_files = db.getFileDB().getFilesByDir(file_to_rename.getId(), false);
	    				for(File file : children_files){
	    					if(file.getUrl().startsWith(oldUrl)){
	    						String file_newUrl = newUrl + file.getUrl().substring(oldUrl.length());
	    						file.setUrl(file_newUrl);
		    					db.makePersistent(file);
	    					}
	    				}
	    			}
				}
				else{
					ejb.setRollbackOnly();
					throw new IMathException(IMathException.IMATH_ERROR.OTHER, "System file error");
				}			
    		}
    		else{
    			ejb.setRollbackOnly();
    			throw new IMathException(IMathException.IMATH_ERROR.OTHER, "A file with the same name already exists in the directory");
    		}   		
    	}
    	else{
    		ejb.setRollbackOnly();
    		throw new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND, "data/" + idFile);
    	}
    	
    }
    
    
    /**
     * Create a directory (physically and in DB)
     * @param idParentDir - id of the parent directory where the new directory must reside
     * @param dirName - name of the directory to be created
     * @param sc - Securitycontext
     * @throws IMathException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createDirectory(String idParentDir, String dirName, SecurityContext sc) throws Exception{
    	
    	if(idParentDir == null || dirName == null){
     		throw new IMathException(IMathException.IMATH_ERROR.OTHER, "Create directory: parent directory or new name is null");
    	}
  
    	String userName = sc.getUserPrincipal().getName();
    	File file_parentDir = db.getFileDB().findByIdSecured(Long.valueOf(idParentDir), userName);

    	if(file_parentDir != null){ 		   	
    		//We have to check if a file/directory exists with the name of the 
    		//directory to be created in the parent directory 
    		if(checkIfFileExistInDirectory(dirName, file_parentDir) == null){			
    			String url_newDirectory = file_parentDir.getUrl() + "/" + dirName;
    			//Physically create the directory
    			if(fileUtils.createDirectory(url_newDirectory)){
    				//Create the directory in DB
    				File newDir = this.createNewFileInDirectory(file_parentDir, dirName, "dir");
    				fileUtils.protectDirectory(newDir.getUrl(), userName);
    			}
    			else{
    				ejb.setRollbackOnly();
    				throw new IMathException(IMathException.IMATH_ERROR.OTHER, "The directory " + dirName + " cannot be created");
    			}						
    		}
    		else{
    			ejb.setRollbackOnly();
    			throw new IMathException(IMathException.IMATH_ERROR.OTHER, "A file with the same name already exists in the parent directory");
    		}
    	}
    	else{
    		ejb.setRollbackOnly();
    		throw new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND, "data/" + idParentDir);    	 		
    	}
    	  	
    }
    
    /**
     * Create a file/directory (physically and in DB)
     * @param idParentDir - id of the parent directory where the new file/directory must reside
     * @param name - name of the file/directory to be created
     * @param type - file type (directory or regular)
     * @param sc - Securitycontext
     * @throws IMathException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createFile(String idParentDir, String name, String type, SecurityContext sc) throws Exception{
    	
    	if(idParentDir == null || name == null || type == null){
     		throw new IMathException(IMathException.IMATH_ERROR.OTHER, "Create directory: parent directory or new name is null");
    	}
  
    	String userName = sc.getUserPrincipal().getName();
    	File file_parentDir = db.getFileDB().findByIdSecured(Long.valueOf(idParentDir), userName);

    	if(file_parentDir != null){ 		   	
    		//We have to check if a file/directory exists with the name of the 
    		//directory to be created in the parent directory 
    		if(checkIfFileExistInDirectory(name, file_parentDir) == null){			
    			//Physically create the file
    			String typeFile = fileUtils.createFile(file_parentDir.getUrl(), name, type);
    			if(typeFile != null){
    				//Create the file in DB
    				File newFile = this.createNewFileInDirectory(file_parentDir, name, typeFile);
    				if (typeFile.equals("dir")) {
    				    fileUtils.protectDirectory(newFile.getUrl(), userName);
    				} else {
    				    fileUtils.protectFile(newFile.getUrl(), userName);
    				}
    			}
    			else{
    				ejb.setRollbackOnly();
    				throw new IMathException(IMathException.IMATH_ERROR.OTHER, "The file " + name + " cannot be created");
    			}						
    		}
    		else{
    			ejb.setRollbackOnly();
    			throw new IMathException(IMathException.IMATH_ERROR.OTHER, "A file with the same name already exists in the parent directory");
    		}
    	}
    	else{
    		ejb.setRollbackOnly();
    		throw new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND, "data/" + idParentDir);    	 		
    	}
    	  	
    }
    

    /**
     * Paste origin Item (file or directory) to destiny location.
     * Once an item is copied or cutted this method is in charge of pasting to de "destiny" location.
     * @param action "copy" or "cut" 
     * @param origin Identification of the item to be pasted
     * @param destiny Identification of the final location
     * @return TODO: id for the new file created
     * @throws IMathException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void pasteItem(String userName, String action, Long origin, Long destiny) 
	    	throws IMathException {
	Long idOnDestiny = null;
	if (PASTE_ACTION_COPY.equals(action) || PASTE_ACTION_MOVE.equals(action)) {
		try {
			File originFile = getFile(origin, userName);
			File destinyDir = getFile(destiny, userName);

   			if (destinyDir != null && originFile != null 
			        && "dir".equals(destinyDir.getIMR_Type())
			) {
                // 1) check if exists the same name on destiny path.
                String fileName = originFile.getName();
				if (checkIfFileExistFisically(fileName, destinyDir)){
                    String parts[] = fileName.split("\\.");
                    if (parts.length>1) {
                        fileName = parts[parts.length-2] + "_new." + parts[parts.length-1];
                    } else {
                        fileName = parts[parts.length-1] + "_new";
                    }
                }
                
                // 2) Select the action
                // TODO: "move" action is not developed yet!
                if (PASTE_ACTION_COPY.equals(action)) {
                    
                    java.nio.file.Path originPath = java.nio.file.Paths.get(originFile.getPath());
                    java.nio.file.Path destinyPath = java.nio.file.Paths.get(destinyDir.getPath() +"/"+ fileName);
                    if ("dir".equals(originFile.getIMR_Type())) {                      
                    	//java.io.File oFile = new java.io.File(originPath.toString());
                        //java.io.File dFile = new java.io.File(destinyPath.toString());
                        //org.apache.commons.io.FileUtils.copyDirectory(oFile, dFile);                        
                        CopyDirVisitor dirVisitor = new CopyDirVisitor(originPath, destinyPath);
                        Files.walkFileTree(originPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
           				     dirVisitor);
                        
                    } else {
                        java.nio.file.Files.copy(originPath, destinyPath, java.nio.file.StandardCopyOption.COPY_ATTRIBUTES);
                    }
                }
                idOnDestiny = 5L;
			} else {
				ejb.setRollbackOnly();
				throw new IMathException(IMathException.IMATH_ERROR.OTHER, 
				"Origin or Destiny locations are wrong.");
			}
		} catch (Exception e) {
			ejb.setRollbackOnly();
			throw new IMathException(IMathException.IMATH_ERROR.OTHER, e.toString());
		}

	} else {
		ejb.setRollbackOnly();
		throw new IMathException(IMathException.IMATH_ERROR.OTHER, "Invalid action " + action);
	}
	//return idOnDestiny;
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void recoverOutputFiles(HashMap<Long, Long>outputFileJob){
    	for(Long key : outputFileJob.keySet()){
    		Long idJob = outputFileJob.get(key);
    		Job j = db.getJobDB().findById(idJob);
    		Set<File> outputFiles = j.getOutputFiles();
    		File f = db.getFileDB().findById(key);
    		outputFiles.add(f);
    		j.setOutputFiles(outputFiles);
    	}
    }
    
    /**
     * Updates the table File with the current physical content of the user's storage
     * @param userName
     * @throws Exception
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateFilesFromStorage(String userName) throws Exception {
        IMR_User user = db.getIMR_UserDB().findById(userName);
        File rootFile = db.getFileDB().findROOTByUserId(userName);
        Map<java.io.File,java.io.File> dirTree = fileUtils.dirFiles(rootFile.getUrl());
        List<File> files = db.getFileDB().findAll(userName);
        Map<String, File> auxMap = new HashMap<String, File>();
        for (File file:files) {
            URI u = URI.create(file.getUrl());
            String path = u.getPath();
            auxMap.put(path, file);
        }
        updateTree(auxMap, dirTree, user);
    }
    
    private void updateTree(Map<String, File> auxMap, Map<java.io.File,java.io.File> dirTree, IMR_User user) throws Exception {
        // First, lets add the new files and directories:
        Set<java.io.File> keySet = dirTree.keySet();
        Iterator<java.io.File> it = keySet.iterator();
        Map<String,java.io.File> auxMapReal = new HashMap<String, java.io.File>();
        while(it.hasNext()) {
            java.io.File realFile = it.next();
            String path = realFile.getPath();
            auxMapReal.put(path, realFile);
            if (!auxMap.containsKey(path)) {
                // It means it is a new file or directory
                addNewFile(auxMap, dirTree, realFile, user);
            }
        }
        // Now we should eliminate the ones that have been removed
        // first, we eliminate all files (not directories)
        eraseFiles(auxMap, auxMapReal, true);
        // Then we eliminate all directories
        eraseFiles(auxMap,auxMapReal, false);
    }
    
    private synchronized void eraseFiles(Map<String, File> auxMap, Map<String,java.io.File> auxMapReal, boolean dir) {
        Set<String> stringSet = auxMap.keySet();
        List<String> toErase = new ArrayList<String>();
        for(String fileInPath:stringSet) {
            File fileIn = auxMap.get(fileInPath);
            boolean b = !dir || (dir && !fileIn.getIMR_Type().equals("dir"));
            if(fileIn.getDir()!=null && b) {
                if(!auxMapReal.containsKey(fileInPath)) {
                    toErase.add(fileInPath);
                    em.remove(fileIn);
                }
            }
        }
        for(String s:toErase) auxMap.remove(s);
    }
    
    private void addNewFile(Map<String, File> auxMap, Map<java.io.File,java.io.File> dirTree, java.io.File realFile, IMR_User user) throws Exception {
        java.io.File parent = dirTree.get(realFile);
        String pathParent = parent.getPath();
        // If parent does not exist, we create first the parent
        if (!auxMap.containsKey(pathParent)) {
            addNewFile(auxMap, dirTree, parent, user);
        }
        
        File parentFile = auxMap.get(pathParent);
        String imrType = "";
        if(realFile.isDirectory()) {
        	imrType = "dir";
        } else {
            String []div = realFile.getName().split("\\.");
            if(div.length>1) {
            	imrType = div[div.length-1];
            }
        }
        
        File newFile = createNewFileInDirectory(parentFile, realFile.getName(), imrType);
        
        URI u = URI.create(newFile.getUrl());
        String newPath = u.getPath();
        auxMap.put(newPath, newFile);
        	        	               
        
        
        /*File newFile= new File();
        File parentFile = auxMap.get(pathParent);
        newFile.setDir(parentFile);
        newFile.setIMR_Type("");
        if(realFile.isDirectory()) {
            newFile.setIMR_Type("dir");
        } else {
            String []div = realFile.getName().split("\\.");
            if(div.length>1) {
                newFile.setIMR_Type(div[div.length-1]);
            }
        }
        newFile.setName(realFile.getName());
        newFile.setOwner(user);
        newFile.setSharingState(Sharing.NO);
        newFile.setUrl(parentFile.getUrl()+"/" + newFile.getName());
        
        System.out.println("addNewFile");
        
        File f = checkIfFileExistInDirectory(realFile.getName(), parentFile);
        if(f == null){
        	em.persist(newFile);
        }
        
        URI u = URI.create(newFile.getUrl());
        String newPath = u.getPath();
        auxMap.put(newPath, newFile);*/
    }
    
    private void saveFile(String serName, String uri, List<String> content) throws Exception {
    	// TODO: Test needed!

    	Charset charset = Charset.forName("US-ASCII");
    	
    	// TODO: We must consider multiple-host support!
    	// TODO: We must handle IO errors... If an error occurs during writing, we should restore the original file
    	
    	URI u = URI.create(uri);
    	Path path = Paths.get(u.getPath());
    	
    	try (BufferedWriter writer = Files.newBufferedWriter(path, charset)) {
    	    boolean first = true;
    		Iterator<String> it = content.iterator();
    	    while (it.hasNext()) {
    	        if(!first) {
    	            writer.write(it.next());
    	        }
    	        first = false;
    	        writer.newLine();
    	    }
    	    writer.flush();
    	    writer.close();
    	} 
    	catch (IOException e) {
    		LOG.severe("File: "+ uri + " cannot be written");
    	    throw e; 
    	}
    }
    
    private void saveFile(String serName, String uri, List<String> content, long page) throws Exception {

        Charset charset = Charset.forName("US-ASCII");
        
        URI u = URI.create(uri);
        Path path = Paths.get(u.getPath());
        Path pathTemp = Paths.get(u.getPath()+"_temp");
        
        // First, we start by writing to a temp file the current content. We do not close it, since we need to append some data.
        try (BufferedWriter writer = Files.newBufferedWriter(pathTemp, charset)) {
            Iterator<String> it = content.iterator();
            boolean first = true;
            boolean something = false;
            while (it.hasNext()) {
                something = true;
                if (!first) {
                    writer.newLine();
                }
                writer.write(it.next());
                first=false;
            }
            // Now, we open the original file, jump the first block and copy to the temp file the rest 
            try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
                int i = 0;
                long lastLineToAvoid = page * this.PAGINATION - 1;      //Lines from 0 to lastLineToAvoid should not be included in the file
                String line = null;
                boolean first2 = true;
                while ((line = reader.readLine()) != null) {
                    if (i>lastLineToAvoid) {
                        if (!first2 || (first2 && something)) {
                            writer.newLine();
                        }
                        writer.write(line);
                        first2 = false;
                    }
                    i++;
                }
                writer.flush();
                writer.close();
                reader.close();
                
            } catch (IOException e) {
                writer.close();
                LOG.severe("File: "+ uri + " cannot be written");
                throw e;
            }
            // Finally, we rename the file properly
            java.io.File tempFile = new java.io.File(pathTemp.toString());
            java.io.File realFile = new java.io.File(path.toString());
            tempFile.renameTo(realFile);
            
        } 
        catch (IOException e) {
            LOG.severe("Temp File for: "+ uri + " cannot be written");
            throw e; 
        }
    }
    
    private  List<String> getFile(String userName, String uri) throws Exception {
    	// TODO: Test needed!
    	
    	// TODO: We must consider multiple-host support!
    	URI u = URI.create(uri);
    	Path path = Paths.get(u.getPath());
    	List<String> output = new ArrayList<String>();
    	try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
    	    String line = null;
    	    while ((line = reader.readLine()) != null) {
    	        output.add(line);
    	    }
        	return output;
    	} 
    	catch (IOException e) {
    		LOG.severe("File: "+ uri + " canno be read");
    	    throw e;
    	}
    }
    
    private  List<String> getFile(String userName, String uri, Integer page) throws Exception {
    	// TODO: Test needed!
    	
        if (page <= 0) {
            throw new IMathException(IMathException.IMATH_ERROR.INVALID_PAGINATION);
        }
        int initLine = this.PAGINATION * (page-1);
        int endLine = this.PAGINATION * page - 1;
        
        URI u = URI.create(uri);
    	Path path = Paths.get(u.getPath());
    	List<String> output = new ArrayList<String>();
    	try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
    	    String line = null;
    	    boolean continueReading = true;
    	    int count = 0;
    	    while ((line = reader.readLine()) != null && continueReading) {
    	        if (count >= initLine) {
    	            output.add(line);    
    	        }
    	        count++;
    	        continueReading = count <=endLine;
    	    }
    	    reader.close();
        	return output;
    	} 
    	catch (IOException e) {
    		LOG.severe("File: "+ uri + " canno be read");
    	    throw e;
    	}
    }
    
    // For testing purposes only
    public void setEJB(EJBContext ejb) {
    	this.ejb = ejb;
    }
    
}
