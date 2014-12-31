package com.imath.core.service;

import static org.junit.Assert.*;

import org.mockito.internal.stubbing.*; 

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJBContext;
import javax.persistence.EntityManager;
import javax.ws.rs.core.SecurityContext;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.imath.core.data.FileDB;
import com.imath.core.data.MainServiceDB;
import com.imath.core.exception.IMathException;
import com.imath.core.model.File;
import com.imath.core.model.IMR_User;
import com.imath.core.model.Job;
import com.imath.core.util.FileUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Exception;
import java.net.URI;
import java.nio.file.Files;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.*;

public class FileControllerUnitTest {

	// The class that contains the code we want to unit test 
    // Must not be mocked
    private FileController fileController;        

    // We do not mock the MainServiceDB. We mock the inside elements.
    private MainServiceDB db;
    
    @Mock
    private Logger LOG;
    
    // Inject objects might be mocked. Not all of them, only the ones we need to control 
    // to test what we want to test
    @Mock
    private EntityManager em;
    
    @Mock
    private FileDB fileDB;
    
    @Mock
    private FileUtils fileUtils;
    
    @Mock private SecurityContext sc;                   // The security context
    @Mock private Principal principal;                  // It contains logged info user
    @Mock EJBContext ejb;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        when(sc.getUserPrincipal()).thenReturn(principal);
        
        // Create with new the class we want to test
        fileController = new FileController();
        db = new MainServiceDB();
        
        // We simulate the injections
        db.setEntityManager(em);    
        db.setFileDB(fileDB);
        fileController.setMainServiceDB(db);
        fileController.setFileUtils(fileUtils);
        fileController.setLog(LOG);
        fileController.setEJB(ejb);
    }
    
    /**
     * Test the normal path of the function when everything is OK
     */
    @Test
    public void test1_getParentDir() throws Exception {
        
    	//The input of the getParentDir function will be
    	String path = "/src/test/test.py";
    	String username = "ammartinez";
        
    	//Define and initialize a file for each directory in the path input parameter
        File file_root = new File();
        File file_src = new File();
        File file_test = new File();
        
        file_root.setId(1L);
        file_src.setId(2L);
        file_test.setId(3L);
        
        file_src.setDir(file_root);
        file_test.setDir(file_src);
        
        List<File> list_src = new ArrayList<File>();
        list_src.add(file_src);
        
        List<File> list_test = new ArrayList<File>();
        list_test.add(file_test);
           
        when(db.getFileDB().findROOTByUserId(username)).thenReturn(file_root);       
        when(db.getFileDB().findAllByName("src", username)).thenReturn(list_src);
        when(db.getFileDB().findAllByName("test", username)).thenReturn(list_test);
        when(db.getFileDB().findById(file_test.getId())).thenReturn(file_test);
        
        // We execute the method
        File result = new File();
        result = fileController.getParentDir(path, username);
        
        // We expect that the returned file is the expected file_test
        assertTrue(result.getDir().getId().equals(file_src.getId()));
        assertTrue(result.getId().equals(file_test.getId()));
        
    }
    
    @Test
    //Test the execution when if(file_list.size() ==0). It should rise an exception 
    public void test2_getParentDir() throws Exception {
        
    	//The input of the getParentDir function will be
    	String path = "/src/test/test.py";
    	String username = "ammartinez";
        
    	//Define and initialize a file for each directory in the path input parameter
        File file_root = new File();
        File file_src = new File();
        File file_test = new File();
        
        file_root.setId(1L);
        file_src.setId(2L);
        file_test.setId(3L);
        
        file_src.setDir(file_root);
        file_test.setDir(file_src);
        
        List<File> list_src = new ArrayList<File>();
        list_src.add(file_src);
        
        List<File> list_empty = new ArrayList<File>();
        
           
        when(db.getFileDB().findROOTByUserId(username)).thenReturn(file_root);       
        when(db.getFileDB().findAllByName("src", username)).thenReturn(list_src);
        when(db.getFileDB().findAllByName("test", username)).thenReturn(list_empty);
        
        
        try{
        // We execute the method
        File result = new File();
        result = fileController.getParentDir(path, username);
        }
        catch (IMathException e){
        	assertTrue(e.getIMATH_ERROR().toString().equals("FILE_NOT_FOUND"));
        	
        }
       
        
    }
    
    
    @Test
    //Test the execution when if(found == false). It should rise an exception 
    //Because the anyone of the found files with an specific name have a the expected parent dir
    public void test3_getParentDir() throws Exception {
        
    	//The input of the getParentDir function will be
    	String path = "/src/test/test.py";
    	String username = "ammartinez";
        
    	//Define and initialize a file for each directory in the path input parameter
        File file_root = new File();
        File file_src = new File();
        File file_test = new File();
        
        //Define a file that will be the parent dir of file_test
        File file_empty = new File();
        
        file_root.setId(1L);
        file_src.setId(2L);
        file_test.setId(3L);
        file_empty.setId(4L);
        
        //Set parent dir of files
        file_src.setDir(file_root);
        file_test.setDir(file_empty);
        
        List<File> list_src = new ArrayList<File>();
        list_src.add(file_src);
        
        List<File> list_test = new ArrayList<File>();
        list_test.add(file_test);
        
           
        when(db.getFileDB().findROOTByUserId(username)).thenReturn(file_root);       
        when(db.getFileDB().findAllByName("src", username)).thenReturn(list_src);
        when(db.getFileDB().findAllByName("test", username)).thenReturn(list_test);
        
        
        try{
        // We execute the method
        File result = new File();
        result = fileController.getParentDir(path, username);
        }
        catch (IMathException e){
        	assertTrue(e.getIMATH_ERROR().toString().equals("FILE_NOT_FOUND"));
        }
       
        
    }
    
    
    @Test
    //Test the execution when the input parameter path is empty 
    //Because the anyone of the found files with an specific name have the expected parent dir
    public void test4_getParentDir() throws Exception {
        
    	//The input of the getParentDir function will be
    	String path = "";
    	String username = "ammartinez";
        
    	//Define and initialize a file for each directory in the path input parameter
        File file_root = new File();
        File file_src = new File();
        File file_test = new File();
        
        //Define a file that will be the parent dir of file_test
        File file_empty = new File();
        
        file_root.setId(1L);
        file_src.setId(2L);
        file_test.setId(3L);
        file_empty.setId(4L);
        
        //Set parent dir of files
        file_src.setDir(file_root);
        file_test.setDir(file_empty);
        
        List<File> list_src = new ArrayList<File>();
        list_src.add(file_src);
        
        List<File> list_test = new ArrayList<File>();
        list_test.add(file_test);
        
           
        when(db.getFileDB().findROOTByUserId(username)).thenReturn(file_root);       
        when(db.getFileDB().findAllByName("src", username)).thenReturn(list_src);
        when(db.getFileDB().findAllByName("test", username)).thenReturn(list_test);
        
        
        try{
        // We execute the method
        File result = new File();
        result = fileController.getParentDir(path, username);
        }
        catch (IMathException e){
        	assertTrue(e.getIMATH_ERROR().toString().equals("FILE_NOT_FOUND"));
        }
       
        
    }
    
    @Test
    public void test_createNewFileInDirectory() throws Exception {
    	
    	File dir = new File();
    	dir.setId(1L);
    	IMR_User user = new IMR_User();
    	user.setUserName("ammartinez");
    	dir.setOwner(user);
    	String filename = new String ("file.txt");
    	String imrType = new String("txt");
    	
    	File f1 = new File();
    	f1.setId(3L);
    	File dir_f1 = new File();
    	dir_f1.setId(2L);
    	f1.setDir(dir_f1);
    	
    	List<File> l_files = new ArrayList<File>();
    	l_files.add(f1);   	
    	
    	when(db.getFileDB().findAllByName(filename, "ammartinez")).thenReturn(l_files);
    	
    	fileController.createNewFileInDirectory(dir, filename, imrType);
    	
    	ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
    	verify(em).persist(fileCaptor.capture());
    	
    	List<File> capturedFiles = fileCaptor.getAllValues();
    	assertEquals(dir, capturedFiles.get(0).getDir());
    	assertEquals(filename, capturedFiles.get(0).getName());
    	assertEquals(imrType, capturedFiles.get(0).getIMR_Type());
   	
    }
    
    @Test
    //Test the case when the filenamePath represents to the root directory
    //HAPPY PATH
    public void test_checkIfFileExistInUser_ROOTpath() throws Exception{
    	
    	String filenamePath = new String("/");
    	String userName = new String("userName");
    	File f1 = new File();
    	
    	when(fileDB.findROOTByUserId(userName)).thenReturn(f1);
    	
    	File f = fileController.checkIfFileExistInUser(filenamePath, userName);
    	
    	assertTrue(f == f1);
    }
 
    @Test
    //Test the case when the rootDir is null
    public void test_checkIfFileExistInUser_rootDirNULL() throws Exception{
    	
    	String filenamePath = new String("/dir/file.txt");
    	String fileName = new String("file.txt");
    	String userName = new String("userName");
    	File f1 = null;
    	
    	List<File> list_files = new ArrayList<File>();
    	
    	when(fileDB.findAllByName(fileName, userName)).thenReturn(list_files);   	
    	when(fileDB.findROOTByUserId(userName)).thenReturn(f1);
    	
    	File f = fileController.checkIfFileExistInUser(filenamePath, userName);
    	
    	verify(fileDB, times(1)).findAllByName(fileName, userName);
    	verify(fileDB, times(1)).findROOTByUserId(userName);
    	
    	assertTrue(f == null);
    }
    
    @Test
    //Test the case when list_files is empty, i.e no files are found with this name
    public void test_checkIfFileExistInUser_listfiles_EMPTY() throws Exception{
    	
    	String filenamePath = new String("/dir/file.txt");
    	String fileName = new String("file.txt");
    	String userName = new String("userName");
    	File f1 = new File();
    	
    	List<File> list_files = new ArrayList<File>();
    	
    	when(fileDB.findAllByName(fileName, userName)).thenReturn(list_files);   	
    	when(fileDB.findROOTByUserId(userName)).thenReturn(f1);
    	
    	File f = fileController.checkIfFileExistInUser(filenamePath, userName);
    	
    	verify(fileDB, times(1)).findAllByName(fileName, userName);
    	verify(fileDB, times(1)).findROOTByUserId(userName);
    	
    	assertTrue(f == null);
    }
    
    @Test
    //Test the case when list_files is empty, i.e no files are found with this name
    public void test_checkIfFileExistInUser_HAPPYPATH() throws Exception{
    	
    	String filenamePath = new String("/dir/file.txt");
    	String fileName = new String("file.txt");
    	String userName = new String("userName");
    	File f_root = new File();
    	f_root.setUrl("file://localhost/user");
    	
    	File f1 = new File();
    	f1.setUrl("file://localhost/user/dir/file.txt");
    	f1.setName(fileName);
    	
    	File f2 = new File();
    	f2.setUrl("file://localhost/user/dir/dir/file.txt");
    	f2.setName(fileName);
    	
    	List<File> list_files = new ArrayList<File>();
    	list_files.add(f2);
    	list_files.add(f1);
    	
    	when(fileDB.findAllByName(fileName, userName)).thenReturn(list_files);   	
    	when(fileDB.findROOTByUserId(userName)).thenReturn(f_root);
    	
    	File f = fileController.checkIfFileExistInUser(filenamePath, userName);
    	
    	assertTrue(f1.getUrl().equals(f.getUrl()));

    }
	
    
    
    @Test
    //Test the case when a file in the set of files does not exist
    public void test_eraseListFiles_FileNotExist() throws Exception {
    	
    	Long id_f1 = new Long(1);
    	Long id_f2 = new Long(2);
    	File f1 = new File();
    	f1.setId(id_f1);
    	
    	IMR_User user = new IMR_User();
    	user.setUserName("test_userName");
    	f1.setOwner(user);
    	
    	Set<String> idFiles = new HashSet<String>();
    	idFiles.add(String.valueOf(id_f1));
    	idFiles.add(String.valueOf(id_f2));
    	
    	when(fileDB.findById(id_f1)).thenReturn(f1);
    	when(fileDB.findById(id_f2)).thenReturn(null);   	
    	when(principal.getName()).thenReturn("test_userName");
    	
    	try{
    		fileController.eraseListFiles(idFiles, sc);
    	}
    	catch (IMathException e){
    		//System.out.println(e.getMessage());
    		verify(fileDB, times(1)).findById(id_f2);
    	}
    }
    
    
    
    @Test
    //Test the case when a file in the set of files cannot be moved to the trash location
    //In this case trashListFiles throws an exception
    public void test_eraseListFiles_FileNotTrashLocation() throws Exception {
    	
    	Long id_f1 = new Long(1);
    	Long id_f2 = new Long(2);
    	File f1 = new File();
    	f1.setId(id_f1);
    	File f2 = new File();
    	f2.setId(id_f2);
    	
    	IMR_User user = new IMR_User();
    	user.setUserName("test_userName");
    	f1.setOwner(user);
    	f2.setOwner(user);
    	
    	List<File> listFiles = new ArrayList<File>();
    	listFiles.add(f1);
    	listFiles.add(f2);
    	
    	Set<String> idFiles = new HashSet<String>();
    	idFiles.add(String.valueOf(id_f1));
    	idFiles.add(String.valueOf(id_f2));
    	
    	String trashLocationF1 = new String ("//trash/f1");
    	
    	when(principal.getName()).thenReturn("test_userName");
    	when(fileDB.findById(id_f1)).thenReturn(f1);
    	when(fileDB.findById(id_f2)).thenReturn(f2);
    	when(fileUtils.trashListFiles(listFiles)).thenThrow(new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND, "data/" + listFiles.get(1).getId()));
    	when(fileUtils.trashFile(f1)).thenReturn(trashLocationF1);
    	when(fileUtils.trashFile(f2)).thenReturn(null);
    	when(fileUtils.restoreFile(f1, trashLocationF1)).thenReturn(true);
    	
    	try{
    		fileController.eraseListFiles(idFiles, sc);
    	}
    	catch (IMathException e){    		
    		verify(fileDB, times(1)).findById(id_f1);
    		verify(fileDB, times(1)).findById(id_f2);    	
    		verify(fileUtils, times(0)).restoreFile(Matchers.eq(f2), (String)Matchers.any());
    		assertEquals(e.getMessage(), "[E0003] - File id: data/2 not found.");
    	}
   	
    }
    
    
    
    @Test
    //Test the case when a recover is required after the first move and the recovering of a file fails
    public void test_eraseListFiles_FileNotRecovered() throws Exception {
    	
    	Long id_f1 = new Long(1);
    	Long id_f2 = new Long(2);
    	Long id_f3 = new Long(3);
    	File f1 = new File();
    	f1.setId(id_f1);
    	File f2 = new File();
    	f2.setId(id_f2);
    	File f3 = new File();
    	f3.setId(id_f3);
    	
    	IMR_User user = new IMR_User();
    	user.setUserName("test_userName");
    	f1.setOwner(user);
    	f2.setOwner(user);
    	f3.setOwner(user);
    	
    	Set<String> idFiles = new HashSet<String>();
    	idFiles.add(String.valueOf(id_f1));
    	idFiles.add(String.valueOf(id_f2));
    	idFiles.add(String.valueOf(id_f3));
    	
    	String trashLocationF1 = new String ("//trash/f1");
    	
    	when(principal.getName()).thenReturn("test_userName");
    	when(fileDB.findById(id_f1)).thenReturn(f1);
    	when(fileDB.findById(id_f2)).thenReturn(f2);
    	when(fileDB.findById(id_f3)).thenReturn(f3);
    	when(fileUtils.trashFile(f1)).thenReturn(trashLocationF1);
    	when(fileUtils.trashFile(f2)).thenReturn(null);
    	when(fileUtils.trashFile(f3)).thenReturn(trashLocationF1);
    	when(fileUtils.restoreFile(f1, trashLocationF1)).thenReturn(false);
    	when(fileUtils.restoreFile(f3, trashLocationF1)).thenReturn(false);
    	
    	try{
    		fileController.eraseListFiles(idFiles, sc);
    	}
    	catch (IMathException e){    		
    		verify(fileDB, times(1)).findById(id_f1);
    		verify(fileDB, times(1)).findById(id_f2);
    		verify(fileDB, times(1)).findById(id_f3);
    		verify(fileUtils, times(1)).trashFile(f2);
    		verify(fileUtils, times(0)).restoreFile(Matchers.eq(f2), (String)Matchers.any());
    		//assertEquals(e.getIMATH_ERROR().toString(), "RECOVER_PROBLEM");  		
    	}
   	
    }  
    
    
    @Test
    //Test the case erase a file fails and throws an exception and also the recovering of a file also fails.
    public void test_eraseListFiles_EraseFileFails_and_FileNotRecovered() throws Exception {
    	
    	Long id_f1 = new Long(1);
    	Long id_f2 = new Long(2);
    	Long id_f3 = new Long(3);
    	File f1 = new File();
    	f1.setId(id_f1);
    	f1.setIMR_Type("dir");
    	File f2 = new File();
    	f2.setId(id_f2);
    	f2.setIMR_Type("dir");
    	File f3 = new File();
    	f3.setId(id_f3);
    	f3.setIMR_Type("dir");
    	
    	IMR_User user = new IMR_User();
    	user.setUserName("test_userName");
    	f1.setOwner(user);
    	f2.setOwner(user);
    	f3.setOwner(user);
    	
    	Set<String> idFiles = new HashSet<String>();
    	idFiles.add(String.valueOf(id_f1));
    	idFiles.add(String.valueOf(id_f2));
    	idFiles.add(String.valueOf(id_f3));
    	
    	String trashLocationF1 = new String ("//trash/f1");
    	
    	List<File> listFiles = new ArrayList<File>();
    	//We use this file to force throwing an exception in eraseFile
    	Long id_f4 = new Long(4);
    	File f4 = new File();
    	f4.setId(id_f4);
    	
    	IMR_User ilegal_user = new IMR_User();
    	ilegal_user.setUserName("ilegal_userName");
    	f4.setOwner(ilegal_user);
    	
    	listFiles.add(f4);
    	
    	when(principal.getName()).thenReturn("test_userName");
    	when(fileDB.findById(id_f1)).thenReturn(f1);
    	when(fileDB.findById(id_f2)).thenReturn(f2);
    	when(fileDB.findById(id_f3)).thenReturn(f3);
    	when(fileUtils.trashFile(f1)).thenReturn(trashLocationF1);
    	when(fileUtils.trashFile(f2)).thenReturn(trashLocationF1);
    	when(fileUtils.trashFile(f3)).thenReturn(trashLocationF1);
    	when(fileUtils.restoreFile(f1, trashLocationF1)).thenReturn(false);
    	when(fileUtils.restoreFile(f3, trashLocationF1)).thenReturn(false);
    	when(fileUtils.restoreFile(f2, trashLocationF1)).thenReturn(false);
    	when(fileDB.getFilesByDir(id_f1, true)).thenReturn(listFiles);
    	when(fileDB.getFilesByDir(id_f2, true)).thenReturn(listFiles);
    	when(fileDB.getFilesByDir(id_f3, true)).thenReturn(listFiles);
    	
    	//Mockito.doThrow(new IMathException(IMathException.IMATH_ERROR.NO_AUTHORIZATION, "No authorised access")).when(fileController).eraseFile(f1, sc);
    	
    	try{
    		fileController.eraseListFiles(idFiles, sc);
    	}
    	catch (IMathException e){    		
    		verify(fileDB, times(1)).findById(id_f1);
    		verify(fileDB, times(1)).findById(id_f2);
    		verify(fileDB, times(1)).findById(id_f3);
    		verify(fileUtils, times(1)).trashFile(f1);
    		verify(fileUtils, times(1)).trashFile(f2);
    		verify(fileUtils, times(1)).trashFile(f3);
    		verify(fileUtils, Mockito.atLeast(1)).restoreFile((File)Matchers.any(), (String)Matchers.any());
    		assertEquals(e.getIMATH_ERROR().toString(), "RECOVER_PROBLEM");
    		
    	}
    	
    	
    	
    }
    
    
    @Test
    //Test the case erase a file fails and throws an exception
    public void test_eraseListFiles_EraseFileFails() throws Exception {
    	
    	Long id_f1 = new Long(1);
    	Long id_f2 = new Long(2);
    	Long id_f3 = new Long(3);
    	File f1 = new File();
    	f1.setId(id_f1);
    	f1.setIMR_Type("dir");
    	File f2 = new File();
    	f2.setId(id_f2);
    	f2.setIMR_Type("dir");
    	File f3 = new File();
    	f3.setId(id_f3);
    	f3.setIMR_Type("dir");
    	
    	IMR_User user = new IMR_User();
    	user.setUserName("test_userName");
    	f1.setOwner(user);
    	f2.setOwner(user);
    	f3.setOwner(user);
    	
    	Set<String> idFiles = new HashSet<String>();
    	idFiles.add(String.valueOf(id_f1));
    	idFiles.add(String.valueOf(id_f2));
    	idFiles.add(String.valueOf(id_f3));
    	
    	String trashLocationF1 = new String ("//trash/f1");
    	
    	List<File> listFiles = new ArrayList<File>();
    	//We use this file to force throwing an exception in eraseFile
    	Long id_f4 = new Long(4);
    	File f4 = new File();
    	f4.setId(id_f4);
    	
    	IMR_User ilegal_user = new IMR_User();
    	ilegal_user.setUserName("ilegal_userName");
    	f4.setOwner(ilegal_user);
    	
    	listFiles.add(f4);
    	
    	when(principal.getName()).thenReturn("test_userName");
    	when(fileDB.findById(id_f1)).thenReturn(f1);
    	when(fileDB.findById(id_f2)).thenReturn(f2);
    	when(fileDB.findById(id_f3)).thenReturn(f3);
    	when(fileUtils.trashFile(f1)).thenReturn(trashLocationF1);
    	when(fileUtils.trashFile(f2)).thenReturn(trashLocationF1);
    	when(fileUtils.trashFile(f3)).thenReturn(trashLocationF1);
    	when(fileUtils.restoreFile(f1, trashLocationF1)).thenReturn(true);
    	when(fileUtils.restoreFile(f3, trashLocationF1)).thenReturn(true);
    	when(fileUtils.restoreFile(f2, trashLocationF1)).thenReturn(true);
    	when(fileDB.getFilesByDir(id_f1, true)).thenReturn(listFiles);
    	when(fileDB.getFilesByDir(id_f2, true)).thenReturn(listFiles);
    	when(fileDB.getFilesByDir(id_f3, true)).thenReturn(listFiles);
    	
    	
    	try{
    		fileController.eraseListFiles(idFiles, sc);
    	}
    	catch (IMathException e){    		
    		verify(fileDB, times(1)).findById(id_f1);
    		verify(fileDB, times(1)).findById(id_f2);
    		verify(fileDB, times(1)).findById(id_f3);
    		verify(fileUtils, times(1)).trashFile(f1);
    		verify(fileUtils, times(1)).trashFile(f2);
    		verify(fileUtils, times(1)).trashFile(f3);
    		verify(fileUtils, times(1)).restoreFile(Matchers.eq(f1), (String)Matchers.any());
    		verify(fileUtils, times(1)).restoreFile(Matchers.eq(f2), (String)Matchers.any());
    		verify(fileUtils, times(1)).restoreFile(Matchers.eq(f3), (String)Matchers.any());
    		assertEquals(e.getIMATH_ERROR().toString(), "FILE_NOT_FOUND");
    		
    	}
   	
    }
    
    @Test
    //Test the case erase a file fails and throws an exception
    public void test_eraseListFiles_HappyPath() throws Exception {
    	
    	Long id_f1 = new Long(1);
    	Long id_f2 = new Long(2);
    	Long id_f3 = new Long(3);
    	File f1 = new File();
    	f1.setId(id_f1);
    	f1.setIMR_Type("dir");
    	File f2 = new File();
    	f2.setId(id_f2);
    	f2.setIMR_Type("dir");
    	File f3 = new File();
    	f3.setId(id_f3);
    	f3.setIMR_Type("dir");
    	
    	IMR_User user = new IMR_User();
    	user.setUserName("test_userName");
    	f1.setOwner(user);
    	f2.setOwner(user);
    	f3.setOwner(user);
    	
    	Set<String> idFiles = new HashSet<String>();
    	idFiles.add(String.valueOf(id_f1));
    	idFiles.add(String.valueOf(id_f2));
    	idFiles.add(String.valueOf(id_f3));
    	
    	String trashLocationF1 = new String ("//trash/f1");
    	
    	List<File> listFiles = new ArrayList<File>();
    	Long id_f4 = new Long(4);
    	File f4 = new File();
    	f4.setId(id_f4);    	
    	f4.setOwner(user);
    	
    	listFiles.add(f4);
    	
    	when(principal.getName()).thenReturn("test_userName");
    	when(fileDB.findById(id_f1)).thenReturn(f1);
    	when(fileDB.findById(id_f2)).thenReturn(f2);
    	when(fileDB.findById(id_f3)).thenReturn(f3);
    	when(fileUtils.trashFile(f1)).thenReturn(trashLocationF1);
    	when(fileUtils.trashFile(f2)).thenReturn(trashLocationF1);
    	when(fileUtils.trashFile(f3)).thenReturn(trashLocationF1);
    	when(fileDB.getFilesByDir(id_f1, true)).thenReturn(listFiles);
    	when(fileDB.getFilesByDir(id_f2, true)).thenReturn(listFiles);
    	when(fileDB.getFilesByDir(id_f3, true)).thenReturn(listFiles);
    	    	
    	fileController.eraseListFiles(idFiles, sc);
    	
    	verify(fileDB, times(1)).findById(id_f1);
    	verify(fileDB, times(1)).findById(id_f2);
    	verify(fileDB, times(1)).findById(id_f3);
    	verify(fileUtils, times(1)).trashFile(f1);
    	verify(fileUtils, times(1)).trashFile(f2);
    	verify(fileUtils, times(1)).trashFile(f3);
    	verify(fileUtils, times(0)).restoreFile(Matchers.eq(f1), (String)Matchers.any());
    	verify(fileUtils, times(0)).restoreFile(Matchers.eq(f2), (String)Matchers.any());
    	verify(fileUtils, times(0)).restoreFile(Matchers.eq(f3), (String)Matchers.any());
    	verify(fileDB, times(1)).getFilesByDir(id_f1, true);
    	verify(fileDB, times(1)).getFilesByDir(id_f2, true);
    	verify(fileDB, times(1)).getFilesByDir(id_f3, true);
    	
    }
    
    
    @Test
    //Test the case when the file is a directory and we dont have authorisation to erase one of its subfiles
    public void test_eraseFile_FileDir_NoAuthorisation() throws Exception {
    	
    	Long id_f1 = new Long(1);
    	File f1 = new File();
    	f1.setId(id_f1);
    	f1.setIMR_Type("dir");
    	
    	IMR_User user = new IMR_User();
    	user.setUserName("test_userName");
    	f1.setOwner(user);
    	
    	List<File> listFiles = new ArrayList<File>();
    	//We use this file to force throwing an exception in eraseFile
    	Long id_f2 = new Long(2);
    	File f2 = new File();
    	f2.setId(id_f2);
    	
    	IMR_User ilegal_user = new IMR_User();
    	ilegal_user.setUserName("ilegal_userName");
    	f2.setOwner(ilegal_user);
    	
    	when(principal.getName()).thenReturn("test_userName");
    	when(fileDB.getFilesByDir(id_f1, true)).thenReturn(listFiles);
    	
    	try{
    		fileController.eraseFile(f1,sc);
    	}
    	catch(IMathException e){
    		
    		verify(fileDB, times(1)).getFilesByDir(id_f1, true);
    		
    		assertEquals(e.getIMATH_ERROR().toString(), "NO_AUTHORIZATION");
    		
    	}
    	
    }
    
    @Test
    //Test the case when the file is a regular file and we dont have authorisation to erase it
    public void test_eraseFile_File_NoAuthorisation() throws Exception {
    	
    	Long id_f1 = new Long(1);
    	File f1 = new File();
    	f1.setId(id_f1);
    	f1.setIMR_Type("txt");
    	
    	IMR_User ilegal_user = new IMR_User();
    	ilegal_user.setUserName("ilegal_userName");
    	f1.setOwner(ilegal_user);
   	
    	when(principal.getName()).thenReturn("test_userName");
    	
    	try{
    		fileController.eraseFile(f1,sc);
    	}
    	catch(IMathException e){
    		assertEquals(e.getIMATH_ERROR().toString(), "NO_AUTHORIZATION");  		
    	}
    	
    }
    
    @Test
    //Test the case when the file is directory, and we erase it, and its subfiles. 
    //HAPPY PATH
    public void test_eraseFile_eraseDir_HappyPath() throws Exception {
    	
    	Long id_f1 = new Long(1);
    	File f1 = new File();
    	f1.setId(id_f1);
    	f1.setIMR_Type("dir");
    	
    	IMR_User user = new IMR_User();
    	user.setUserName("test_userName");
    	f1.setOwner(user);
    	
    	List<File> listFiles = new ArrayList<File>();
    	//We use this file to force throwing an exception in eraseFile
    	Long id_f2 = new Long(2);
    	File f2 = new File();
    	f2.setId(id_f2);
    	f2.setOwner(user);
    	
    	//The order will be important to not violate a restriction of the DB.
    	//We simulate the order of the objects in the same manner that the function getFilesByDir does.
    	listFiles.add(f2);
    	listFiles.add(f1);
    	
    	when(principal.getName()).thenReturn("test_userName");
    	when(fileDB.getFilesByDir(id_f1, true)).thenReturn(listFiles);
    	   	
    	fileController.eraseFile(f1,sc);
    	
    	verify(fileDB, times(1)).getFilesByDir(id_f1, true);
    	verify(em, times(1)).remove(f2);
    	verify(em, times(1)).remove(f1);    	
    }
    
    @Test
    //Test the case when the file is a regular file, and we erase it. 
    //HAPPY PATH
    public void test_eraseFile_eraseFile_HappyPath() throws Exception {
    	
    	Long id_f1 = new Long(1);
    	File f1 = new File();
    	f1.setId(id_f1);
    	f1.setIMR_Type("txt");
    	
    	IMR_User user = new IMR_User();
    	user.setUserName("test_userName");
    	f1.setOwner(user);
    	    	
    	when(principal.getName()).thenReturn("test_userName");
   	
    	fileController.eraseFile(f1,sc);
    	
    	verify(fileDB, times(0)).getFilesByDir(id_f1, true);
    	verify(em, times(1)).remove(f1);
    	
    }
    
    @Test
    //The input parameter that represents the idFile is null
    public void test_renameFile_FileNull()throws Exception{ 
    	
    	String idFile = null;
    	String newName = new String();
    	
    	try{
    		fileController.renameFile(idFile, newName, sc);
    	}
    	catch(IMathException e){
    		assertEquals(e.getIMATH_ERROR().toString(), "OTHER");  		
    	}
    	
    }
    
    @Test
    //The input parameter that represents the new name is null
    public void test_renameFile_NewNameNull()throws Exception{ 
    	
    	String idFile = new String();
    	String newName = null;
    	
    	try{
    		fileController.renameFile(idFile, newName, sc);
    	}
    	catch(IMathException e){
    		assertEquals(e.getIMATH_ERROR().toString(), "OTHER");  		
    	}
    	
    }
    
    @Test
    //The file to be renamed does not exist in the DB o we do not have access to it
    public void test_renameFile_FileNotExist()throws Exception{
    	
    	String str_idFile = new String("1");
    	Long lng_idFile = Long.valueOf(str_idFile);
    	String newName = new String("test_name");
    	String userName = new String("test_userName");
    	
    	when(principal.getName()).thenReturn(userName);
    	when(fileDB.findByIdSecured(lng_idFile, userName)).thenReturn(null);
    	
    	try{
    		fileController.renameFile(str_idFile, newName, sc);
    	}
    	catch (IMathException e) {
    		assertEquals(e.getIMATH_ERROR().toString(), "FILE_NOT_FOUND");  
    	}
    }
    
    @Test
    //The new name matches with the name of other file that already exists below the same directory
    public void test_renameFile_RepetedNewName()throws Exception{
    	
    	String userName = new String("test_userName");
    	    	 	
    	//File to be renamed
    	File f1 = new File();
    	String str_idFile = new String("1");
    	Long lng_idFile = Long.valueOf(str_idFile);
    	f1.setId(lng_idFile);
    	String f1_url = "file://localhost/root/test_dir/test_file.txt";
    	String f1_absolutePath = "/test_dir/test_file.txt";
    	f1.setUrl(f1_url);
        
    	//Definitions required to getParentDir
    	//Define and initialize a file for each directory in the path input parameter
        File dir_root = new File(); // /root
        File dir_test = new File(); // /root/test_dir       
        dir_root.setId(2L);
        dir_test.setId(3L);     
        dir_test.setDir(dir_root);
        IMR_User user = new IMR_User();
        user.setUserName(userName);
        dir_test.setOwner(user);
               
        List<File> list_test = new ArrayList<File>();
        list_test.add(dir_test);
        
        //The file that exists with the same name below the same directory
        String newName = new String("test_name");
    	File file_inDB = new File();
    	file_inDB.setName(newName);
    	file_inDB.setDir(dir_test);
    	List <File> files_name = new ArrayList<File>();
    	files_name.add(file_inDB);
           
        when(principal.getName()).thenReturn(userName);
    	when(fileDB.findByIdSecured(lng_idFile, userName)).thenReturn(f1);
    	when(fileUtils.getAbsolutePath(f1.getUrl(), userName)).thenReturn(f1_absolutePath);
        when(fileDB.findROOTByUserId(userName)).thenReturn(dir_root);            
        when(fileDB.findAllByName("test_dir", userName)).thenReturn(list_test);
        when(fileDB.findById(dir_test.getId())).thenReturn(dir_test);
        when(fileDB.findAllByName(newName, userName)).thenReturn(files_name);
        
        try{
    		fileController.renameFile(str_idFile, newName, sc);
    	}
    	catch (IMathException e) {
    		assertTrue(e.getMessage().contains("A file with the same name already exists in the directory"));
    		assertEquals(e.getIMATH_ERROR().toString(), "OTHER");  
    	}
    	
    }
    
       
    @Test
    //The file cannot be renamed
    public void test_renameFile_CanNOTBERenamed()throws Exception{
    	
    	String userName = new String("test_userName");
    	    	 	
    	//File to be renamed
    	File f1 = new File();
    	String str_idFile = new String("1");
    	Long lng_idFile = Long.valueOf(str_idFile);
    	f1.setId(lng_idFile);
    	String f1_url = "file://localhost/root/test_dir/test_file.txt";
    	String f1_absolutePath = "/test_dir/test_file.txt";
    	f1.setUrl(f1_url);
    	f1.setName("test_file.txt");
        
    	//Definitions required to getParentDir
    	//Define and initialize a file for each directory in the path input parameter
        File dir_root = new File(); // /root
        File dir_test = new File(); // /root/test_dir       
        dir_root.setId(2L);
        dir_test.setId(3L);     
        dir_test.setDir(dir_root);
        IMR_User user = new IMR_User();
        user.setUserName(userName);
        dir_test.setOwner(user);
               
        List<File> list_test = new ArrayList<File>();
        list_test.add(dir_test);
        
        //There are no files with the same name
        String newName = new String("test_name.txt");  	
    	List <File> files_name = new ArrayList<File>();
    	String newUrl = "file://localhost/root/test_dir/test_name.txt";
    	
           
        when(principal.getName()).thenReturn(userName);
    	when(fileDB.findByIdSecured(lng_idFile, userName)).thenReturn(f1);
    	when(fileUtils.getAbsolutePath(f1.getUrl(), userName)).thenReturn(f1_absolutePath);
        when(fileDB.findROOTByUserId(userName)).thenReturn(dir_root);            
        when(fileDB.findAllByName("test_dir", userName)).thenReturn(list_test);
        when(fileDB.findById(dir_test.getId())).thenReturn(dir_test);
        when(fileDB.findAllByName(newName, userName)).thenReturn(files_name);
        when(fileUtils.moveFile(f1_url, newUrl)).thenReturn(false);
        
        try{
    		fileController.renameFile(str_idFile, newName, sc);
    	}
    	catch (IMathException e) {
    		assertTrue(e.getMessage().contains("System file error"));
    		assertEquals(e.getIMATH_ERROR().toString(), "OTHER");  
    	}
  	
    }
    
    @Test
    //The file to be renamed is a regular file
    // HAPPY PATH
    public void test_renameFile_regularFile_HAPPYPATH()throws Exception{
    	
    	String userName = new String("test_userName");
    	    	 	
    	//File to be renamed
    	File f1 = new File();
    	String str_idFile = new String("1");
    	Long lng_idFile = Long.valueOf(str_idFile);
    	f1.setId(lng_idFile);
    	String f1_url = "file://localhost/root/test_dir/test_file.txt";
    	String f1_absolutePath = "/test_dir/test_file.txt";
    	f1.setUrl(f1_url);
    	f1.setName("test_file.txt");
    	f1.setIMR_Type("txt");
        
    	//Definitions required to getParentDir
    	//Define and initialize a file for each directory in the path input parameter
        File dir_root = new File(); // /root
        File dir_test = new File(); // /root/test_dir       
        dir_root.setId(2L);
        dir_test.setId(3L);     
        dir_test.setDir(dir_root);
        IMR_User user = new IMR_User();
        user.setUserName(userName);
        dir_test.setOwner(user);
               
        List<File> list_test = new ArrayList<File>();
        list_test.add(dir_test);
        
        //There are no files with the same name
        String newName = new String("test_name.txt");  	
    	List <File> files_name = new ArrayList<File>();
    	String newUrl = "file://localhost/root/test_dir/test_name.txt";
    	          
        when(principal.getName()).thenReturn(userName);
    	when(fileDB.findByIdSecured(lng_idFile, userName)).thenReturn(f1);
    	when(fileUtils.getAbsolutePath(f1.getUrl(), userName)).thenReturn(f1_absolutePath);
        when(fileDB.findROOTByUserId(userName)).thenReturn(dir_root);            
        when(fileDB.findAllByName("test_dir", userName)).thenReturn(list_test);
        when(fileDB.findById(dir_test.getId())).thenReturn(dir_test);
        when(fileDB.findAllByName(newName, userName)).thenReturn(files_name);
        when(fileUtils.moveFile(f1_url, newUrl)).thenReturn(true);        
       
    	fileController.renameFile(str_idFile, newName, sc);
    	
    	ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
    	verify(em, times(1)).persist(fileCaptor.capture());
    	List<File> captured_files = fileCaptor.getAllValues();
    	assertTrue(captured_files.size() == 1);
    	assertTrue(captured_files.get(0) == f1);
    	assertTrue(captured_files.get(0).getName().equals(newName));
    	assertTrue(captured_files.get(0).getUrl().equals(newUrl));   	
  	
    }
    
    @Test
    //The file to be renamed is a directory
    // HAPPY PATH
    public void test_renameFile_directory_HAPPYPATH()throws Exception{
    	
    	String userName = new String("test_userName");
    	    	 	
    	//File to be renamed
    	File f1 = new File();
    	String str_idFile = new String("1");
    	Long lng_idFile = Long.valueOf(str_idFile);
    	f1.setId(lng_idFile);
    	String f1_url = "file://localhost/root/test_dir/dir_to_rename";
    	String f1_absolutePath = "/test_dir/dir_to_rename";
    	f1.setUrl(f1_url);
    	f1.setName("dir_to_rename");
    	f1.setIMR_Type("dir");
        
    	//Definitions required to getParentDir
    	//Define and initialize a file for each directory in the path input parameter
        File dir_root = new File(); // /root
        File dir_test = new File(); // /root/test_dir       
        dir_root.setId(2L);
        dir_test.setId(3L);     
        dir_test.setDir(dir_root);
        IMR_User user = new IMR_User();
        user.setUserName(userName);
        dir_test.setOwner(user);
               
        List<File> list_test = new ArrayList<File>();
        list_test.add(dir_test);
        
        //There are no files with the same name
        String newName = new String("dir_renamed");  	
    	List <File> files_name = new ArrayList<File>();
    	String newUrl = "file://localhost/root/test_dir/dir_renamed";
    	
    	//List of children of the directory to be renamed
    	File f_children = new File();
    	f_children.setUrl("file://localhost/root/test_dir/dir_to_rename/children.txt");
    	List<File> children_list = new ArrayList<File>();
    	children_list.add(f_children);
    	          
        when(principal.getName()).thenReturn(userName);
    	when(fileDB.findByIdSecured(lng_idFile, userName)).thenReturn(f1);
    	when(fileUtils.getAbsolutePath(f1.getUrl(), userName)).thenReturn(f1_absolutePath);
        when(fileDB.findROOTByUserId(userName)).thenReturn(dir_root);            
        when(fileDB.findAllByName("test_dir", userName)).thenReturn(list_test);
        when(fileDB.findById(dir_test.getId())).thenReturn(dir_test);
        when(fileDB.findAllByName(newName, userName)).thenReturn(files_name);
        when(fileUtils.moveFile(f1_url, newUrl)).thenReturn(true);        
        when(fileDB.getFilesByDir(lng_idFile, false)).thenReturn(children_list);
       
    	fileController.renameFile(str_idFile, newName, sc);
    	
    	ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
    	verify(em, times(2)).persist(fileCaptor.capture());
    	List<File> captured_files = fileCaptor.getAllValues();
    	
    	assertTrue(captured_files.size() == 2);
    	assertTrue(captured_files.get(0) == f1);
    	assertTrue(captured_files.get(0).getName().equals(newName));
    	assertTrue(captured_files.get(0).getUrl().equals(newUrl)); 
    	assertTrue(captured_files.get(1) == f_children);
    	assertTrue(captured_files.get(1).getUrl().equals("file://localhost/root/test_dir/dir_renamed/children.txt"));
  	
    }
    
    @Test
    //The id of the parent dir is null
    public void test_createFile_idDirectoryNULL()throws Exception{
    	
    	String idParentDir = null;
    	String name = new String("name");
    	String type = new String("regular");
 	
    	try{
    		fileController.createFile(idParentDir, name, type, sc);
    	}
    	catch(IMathException e){
    		assertEquals(e.getIMATH_ERROR().toString(), "OTHER");  		
    	}
    	  	
    }
    
    @Test
    //The name of the new file/directory is null
    public void test_createFile_dirfileNameNULL()throws Exception{
    	
    	String idParentDir = new String("1");
    	String name = null;
    	String type = new String("regular");
 	
    	try{
    		fileController.createFile(idParentDir, name, type, sc);
    	}
    	catch(IMathException e){
    		assertEquals(e.getIMATH_ERROR().toString(), "OTHER");  		
    	}
    	  	
    } 
    
    @Test
    //The type of the file is null
    public void test_createFile_typeNULL()throws Exception{
    	
    	String idParentDir = new String("1");
    	String name = new String("name");
    	String type = null;
 	
    	try{
    		fileController.createFile(idParentDir, name, type, sc);
    	}
    	catch(IMathException e){
    		assertEquals(e.getIMATH_ERROR().toString(), "OTHER");  		
    	}
    	  	
    }
    
    @Test
    //The parent directory does not exist
    public void test_createFile_parentDir_NotExist()throws Exception{
    	
    	String userName = new String("test_userName");
    	
    	String idParentDir = new String("1");
    	String name = new String("name");
    	String type = new String("regular");
    	
    	when(principal.getName()).thenReturn(userName);
    	when(fileDB.findByIdSecured(Long.valueOf(idParentDir), userName)).thenReturn(null);
    	
    	try{
    		fileController.createFile(idParentDir, name, type, sc);
    	}
    	catch(IMathException e){
    		assertEquals(e.getIMATH_ERROR().toString(), "FILE_NOT_FOUND");  		
    	}
    }
    
    @Test
    //The name of the file/directory to be created matches with the name of other file that already exists below the same directory
    public void test_createFile_RepetedName()throws Exception{
    	
    	String userName = new String("test_userName");
    	String idParentDir = new String("1");
    	String name = new String("name");
    	String type = new String("regular");
    	
    	//File to be renamed
    	File f_root = new File();
    	String str_idFile = new String("1");
    	Long lng_idFile = Long.valueOf(str_idFile);
    	f_root.setId(lng_idFile);
    	String f_url = "file://localhost/root/test_dir/name";
    	f_root.setUrl(f_url);
    	IMR_User user = new IMR_User();
    	user.setUserName(userName);
    	f_root.setOwner(user);
              
        List<File> list_test = new ArrayList<File>();
        File f_repeated = new File();
        f_repeated.setDir(f_root);
        f_repeated.setName(name);
        list_test.add(f_repeated);
        
        when(principal.getName()).thenReturn(userName);
    	when(fileDB.findByIdSecured(lng_idFile, userName)).thenReturn(f_root);   	      
        when(fileDB.findAllByName(name, userName)).thenReturn(list_test);     
        
        try{
    		fileController.createFile(idParentDir, name, type, sc);
    	}
    	catch (IMathException e) {
    		assertTrue(e.getMessage().contains("A file with the same name already exists in the parent directory"));
    		assertEquals(e.getIMATH_ERROR().toString(), "OTHER");  
    	}
    	
    }
    
    @Test
    //The file/directory cannot be physically created 
    public void test_createFile_FileDirectoryCannotBePhysicallyCreated()throws Exception{
    	
    	String userName = new String("test_userName");
    	String idParentDir = new String("1");
    	String name = new String("name");
    	String type = new String("regular");
    	    	
    	//File to be renamed
    	File f_root = new File();
    	String str_idFile = new String("1");
    	Long lng_idFile = Long.valueOf(str_idFile);
    	f_root.setId(lng_idFile);
    	String f_url = "file://localhost/root/test_dir";
    	f_root.setUrl(f_url);
    	IMR_User user = new IMR_User();
    	user.setUserName(userName);
    	f_root.setOwner(user);
              
        List<File> list_test = new ArrayList<File>();     
             
        when(principal.getName()).thenReturn(userName);
    	when(fileDB.findByIdSecured(lng_idFile, userName)).thenReturn(f_root);   	      
        when(fileDB.findAllByName(name, userName)).thenReturn(list_test);   
        when(fileUtils.createFile(f_root.getUrl(), name, type)).thenReturn(null);
        
        try{
        	fileController.createFile(idParentDir, name, type, sc);
    	}
    	catch (IMathException e) {
    		assertTrue(e.getMessage().contains("The file " + name + " cannot be created"));
    		assertEquals(e.getIMATH_ERROR().toString(), "OTHER");  
    	}
    	
    }
    
    @Test
    //A regular file is created 
    //HAPPY PATH
    public void test_createFile_FileHappyPath()throws Exception{
    	
    	String userName = new String("test_userName");
    	String idParentDir = new String("1");
    	String name = new String("name.txt");
    	String type = new String("regular");
    	
    	//File to be renamed
    	File f_root = new File();
    	String str_idFile = new String("1");
    	Long lng_idFile = Long.valueOf(str_idFile);
    	f_root.setId(lng_idFile);
    	String f_url = "file://localhost/root/test_dir";
    	f_root.setUrl(f_url);
    	IMR_User user = new IMR_User();
    	user.setUserName(userName);
    	f_root.setOwner(user);
              
        List<File> list_test = new ArrayList<File>();     
        
        when(principal.getName()).thenReturn(userName);
    	when(fileDB.findByIdSecured(lng_idFile, userName)).thenReturn(f_root);   	      
        when(fileDB.findAllByName(name, userName)).thenReturn(list_test);   
        when(fileUtils.createFile(f_root.getUrl(), name, type)).thenReturn("txt");
              
        fileController.createFile(idParentDir, name, type, sc);
        
        ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
    	verify(em, times(1)).persist(fileCaptor.capture());
    	List<File> captured_files = fileCaptor.getAllValues();
    	
    	String urlNewFile = f_root.getUrl() + "/" + name;
    	assertTrue(captured_files.size() == 1);
    	assertTrue(captured_files.get(0).getDir() == f_root);
    	assertTrue(captured_files.get(0).getName().equals(name));
    	assertTrue(captured_files.get(0).getIMR_Type().equals("txt"));
    	assertTrue(captured_files.get(0).getUrl().equals(urlNewFile));
        
    }
    
    /**
     * Happy path to check the file is created as img0.png  
     * @throws Exception
     */
    @Ignore("It depends on an accessible direcotry since it creates a file")
    @Test
    public void test_createImage_HappyPath() throws Exception {
    	String userName = new String("test_userName");
    	
    	File f_root = new File();
    	String str_idFile = new String("1");
    	Long lng_idFile = Long.valueOf(str_idFile);
    	f_root.setId(lng_idFile);
    	String f_url = "file://localhost/home/ipinyol";
    	f_root.setUrl(f_url);
    	IMR_User user = new IMR_User();
    	user.setUserName(userName);
    	f_root.setOwner(user);
        
    	when(principal.getName()).thenReturn(userName);
    	when(fileDB.findByIdSecured(lng_idFile, userName)).thenReturn(f_root);   	     
    	when(fileDB.findById(lng_idFile)).thenReturn(f_root);
                
        String content = "/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0aHBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCAFJARQDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD3+iiigAooooAKKKKACiiigAooooAKazYIHc9KdVOCdXE927ARqzKCegVTgn8wfwxQBYZiGCryx59gPWnjpVeyLPbrPICHmG8g/wAIPQfgP1zU0jpFG0jsFRRuJPQAUAJJII8DqzcKvqaEVvvSHLeg6D6VQ0uRr+ManJGyecD5KE8rFng/VuGP4DtmtOgAoopjyohAZgCeg7mgB9FIDkZpaACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigBrFx9wA/U4qB7xYf+PhGiX++3K/mOn44qzTHQOMHI9wcUANYyEZjKMD0zn+dRfbFjkVLhDCzHCsTlWPoD6+xwayr2Sbw8DeIrS6avNxGi5MS93UDsOpA+uM9dlWgvbVWUxzW8yZB4ZXUj9QRQBNRWTDOdLvksLiTNtMCbWV2yQRyY2J6nHIPUgEH7uTpJNG77A3zYzgjBx6/SgB7HapPoM1zTyn/hAbdz1ubeJWPqZSoJ+vzmulYBlIPQ8VxxMknwvi2Izy29qnyKMktEwyMeuUpDR2VYHiy5ePTRbRNtknO0H8lH/jzJW35sY/jX865fxSJJtV0VYkaRDMocoMgATwNz+Cmi6BJnUxxrFGsaKFRQFUDsBT6Kwta1Zl1C20KwkI1O9Rn3qAfs0I4aUg++FUHqxHYGmIum5lvLiSC1JSKM7ZLjGct3VPUjuegPHJzi3FBHAPkXk9WJyT9SeTUdlZxWFrFbQ7vLjUKNxycD196s0AFIWCjLEAeprAm177VdfZrAsV/56Im935xlF6Bf9tsL6bu1yC0kyHe1Qv8A37iXe/8AIgfQHFAGis0TnCyIx9A2afTEDBcMFH+7T6ACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAQkDqcUgdGOFdSfY0Miv95VP1GaglsraVcNbwk+rRg0AWaKxLmz1S0DS6ZIrMOfIlkJR/bByV/Agexo0bxJBqk72VxBLYapEu6WyuMB8f3lI4df9ofjigDaIDKQQCDwQe9ch4fmHh/xJdeE5DttWQ3ul57RE/vIv+AMcgf3WHpXUz3UcA55b0FYt2Y7u5inlhiaWHPlOUBaPPBweoyK5q+Kp0d9zWFKUizrccN9p724lCTqyyQyBd3lyKcqfcZHI7jI71j6jqX9l3lrcRoqWMkgilH/ADxdjhXX0BPysOnIPrm0STVTULOPUNPuLObPlzxshPcZHBHuDzXkVM0m3pojpjh4pGhJfXDHmRvoKzLG2Gn2v2eKR2TzJJBuPI3uWI+mTTrRbhLGBLx0kuVjUSun3WfHJGffmnmvOqYqpJtOR0RhFbIC5pPNYdCaDTa5nVn3NUkSpdyxn5ZGX6Gszwvfecr+IJUV7zURkyNyRCGPloPQBeeOpOauEZGD3qKKGO3hSGGNY4o1CoiDAUDsPat6WPq01ZMiVGEt0dRBq8EvDjYfzFcxrGpX3ijW5vDGjPNa21vtOqagY8YU8iKPPVmHU9APXNO3EVFdavf6ekaadF5l1cOI1LLmOPjl3I7ADp1JwBXrYXNnJqNRfM5qmESV4nQW0Gn+H4orG2E0tw/zCNW3SS9tzHjjtk4A6egrSj+1vzJ5cQ/urlj+fH8qytKlsdPspZpJHacjfcXEg3STEd+P0UcDoBVmG61K+w8NqLKA9GuhmRv+AAjb+Jz6ivahOM480XdHFKLi7M1O1LVeOGdeXu3c/wC4oH8qsdqskKKKKACiiigAooooAKKKKACiiigAooooAKKKKAEzjk1Xa9gXOC8h9I0L/wAhUzoHHIB9M8iqs0UH/L1OSD/CX2r+Qx+uaAGvq0EX+siukHXJtpCPxIHH41NBf2d0jPBdQyKn3trg7fr6fjVL+ytEvBhLa1kK948ZB+o5FZWpeCYLhxcaffXNrdpzG7yGUKfqx3gewYD2NIeh0YvrQuEF1CWPAXzBk1k+IbG11SBI3zHdQt5lvdR8SW7j+JT+hHQjg8VU0t9ajt7i110QSyRuBFKoB8wdc5GP/QQfr1Nljnk1wYzGezXLHc2pUr6szdK1C6vYZor+MR31tJ5U+0EI5wCHTP8ACwIPscjtV40pNNJr56tVc3c7IqwhppNBNMLVztmiQE0w0E0malloQikIpS1ICXOF5qoUp1ZctNXYnJRV5MQimEVKYN33pSoH90gfnQywIOZPzNejHJa7V5NIweMgtlchNNpxEcmfLmGfzFQEzQL+/XeP78a/0/wrOrlOJpq6s/QuGLpydnoWY5GRgQSCO9dDp2oicCKQ/vOx9a5lHV1DIwZT0I6VNFIUYEEgijB4qdCdnt1KrUo1InZ0VUsLsXUGT99eGq3X1UJqcVKOx5MouLswoooqhBRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRUciNJwJCi99vU/j2oAgu7Gyuhm5hjJHR/usv0Ycj8DWVcvqWjRGa3dtUsx1hYjz0H+w3R8ejcn+8elabaTp8hzLZwzN/emUSH82zVF7W0s2eOztYLdWOWEUYQMfU4rCvV9nC5cI8zsVLTUbfVLVbu1mEsT5+boQRwQQeQQeCDyDUp6UBFTdtVV3HccDGSe596YCZU3oQsfdz3+g714PsqledoI6+ZQWojMF5JAqHzlYkDPHtj8qZPd2duCXky3cnrWXLr8aHIjcr0yF4rshlELfvJfcZvEvojUaQ+h/KovNUnG4Z9Kpf29Ft5jf67ad9psNQUpKisPcd6VTJab+CbXrr/kOOMa3Rb3Ubqpzma3BkRTND/s8sP8f50/T5E1CfarZiUbpGHYen17V488BiIVVSa32fQ7FXpuDlfYvwW5ZDPOQsH8AH3nP+FZupazDaqQCq4GcdAo9T/wDXpniHXRbRMEZVVFwOeFFcXaafc+ILgPcBjAWDLE3c+rev06Cvp6NKGGgqdNa/n5s86TlVlzS2JrrxXc3jsmnQTXfbcp8uMf8AAj1/AEe9U3l8TzcmKyjH90tI5/PI/lXo2l+Foo413oPpitkaFbhceWK0997sXuLoeQJqetWOfPtAy92t5cn/AL5b/Guj0bxLFdpsMgbGAykYZfqD0/l711d94bgkU4QVweveGZrOX7VaExzJyGH8vce1NOa63HaD8jqLlTbobq2G5Osievv9fepYLiOeFJY2yjDINc/oOuC+smRows0Z2TReh/wI6f8A1qytWudR0a4S4s5iLUTr50XBG1j9729K48ZgY1/fhpI0pV3S92Wx6Tp139nuFJPyng/SuoByMjpXiQ+Jek2t5Jb3CXaSROyOPKyMg46g13vhjx9oWtRx20V0y3GdqrLGy7vTnp7daWA9pTTp1FYMQ4z96LOxooor0zlCiiigAooooAKKKKACiiigAooooAKKKKACiiigBkjbI2b0FYznLGtS8bFu3vgVkM2MmvMxzbkoo6KK0bDCkFn/ANWOv+0fSua8QeJIbK3lllmWKKNcFscL2AA7n0A5NXNb1P7PH5UR5H6159bQweJ7l7ueYyLDP5drAvQ4+/K34/Ko9MnvXXTpqjDljuZt87uyvHJrviOXfbM+nWhPDkBp3Hrnon0HPvWxafDqGb95crLcyHq9xIXJ/E132h6JHDCpZBmujS1RRjAp8jl8TuPmS2R5Y3gGGAboYTEfWMlT+lUbix1TTDuDtcRjqsn3h9G6/nmvYmt1I6VmX2mRzIwKCl7OK20H7Rvc4LS9eLQknLDO0huCp9CP85rf+2p9hcwgLvwTjufWuV8RaS2lzm9hBAHEij+Jf8R1qVZitqoDEqwyDntW0W7GckrmVqswm1iC1kTzd+W29uOhPqATmvQ/DWkpBbIdozXmkKk+Nl34wLdSPxJ/wr2bSFAt0x6VNtR30Garrml+H4Ua/uBGX+4gG53+gH/6qzrDx7oGoXC24uHgkc4Tz0Khj6Z6V5hcavpeueJNdutf1MWEcLSR27sM42MVVQPwzjua4Cy8QG+ma2nGUdTggYINS5vVrZG6pRuoyerPrJ4wwrE1WySWFsgVmfDLVp9Y8E20lw7SSQSPb726uFPB/LA/Cuju1Bjaq8zGzi2meL3sf9jeKreZPliuT9nlHbn7p/A/zq1q7LJGY5CNkqNA2R6jg/nin+PYtio6/eWVCv13CqfiNGSPcCciUY/OqQpHk3iS5Mev3DIGIfaxJBGTtGSM9RuB5rV8Ja55GqQh96gsBuHbmovFUn/HnKANytNAw6/dfI/RqTQJblLiN/KcLuAzs7ngD86yqWsTC/MfX6nKA5zx1paztDNwdDsjdBxP5S7w4w2feote8R6V4asfteq3awRk4UdWc+igcmtk7q4uprVzPi/xvpXgy0SW/ZpJ5c+Tbx/efHf2HvUXh/4h+HvEd2trZ3TR3L52RTrsL4/u9QT7ZzXgXxZ1WXUPiTqUMzHbassMansoUH+ZJ/GlJ22Glc9Q07422tzdKt1pLw27HG9JdxUfTAzXqVtcw3lrHc28iyQyqGRx0INfH813Hb2KsMDFer+G/ivpvhrwNpttKj3l+5crAjAeWmeCx7ZycDmhMR7fRXn3hf4rabr17HZXVs1jPKdsZZ9yMfTOBg16DVAFFFFABRRRQAUUUUAFFFFAFPUDiED1Nc/fXi2/lhsYcnnPpj/Gsjx947TRM2VjEJrxfvPJwif/ABR/SvP/AA34hvdcbVEvblprhdk6E8AKMqQB2HIrilT566k9kbKVqdupu+MNQNrot5cqfmEZCH/aPA/nSfDuwRbWHjsKzPGEbXPg6+defLRZCPZWBP6V0vw+KmziIxyorqa6kLsemWqBYxXNeNvEdzpMdpp2mDdqV+xWIhdxRRjJA7nJAFdTD92vMfitZX1rqmj+IbKV4hbhoGkX/lkxIKsfbORUybUboqlFSqJM4XUNdu7LWJLbUL69jvY5ArlpWDKevrxXo3w/8ZXOr3lxoupuJZ44/Nt5+8iDAIPqRkHPcV4pf6FrOs61LfXd+J5J33vKx5PHtx0GK9G+F2hyt4vfUlMhtrK2MTM/IMjY+UfgM/lWSmnJKLudU6cvZt1Fa2x6F4isVms5AQOledWLM1oIc8wOyHPoOn6V6lrTAWsmfQ15bb4VLuQjhrhsfhit0cbIr8iDXdOvB911MLH3HI/rXrOh3AktUII6V5RdW/8AaNsYchGHzRyf3WHQ/wCe1dH4R19k/wBFuwY54ztdD2P+FDBK6Mzxr4Cmh1W61C1s3vNOu3MssUS5eJz97gdQTzx0rl9G8ISS3GzSdKn+0Nx5jxlUT6k9BX0Bb3aSICGBqbzRWUqUZbnRDEzilorrZmV4a0OLw54fttNjYO0YJkkx99ycsfzNWbtwsbEkVNLOqgkkVy2va1HDBIxkVUUHLE4AqpSjFXeiMYqUn5nJeIUGo65Z2/GwSiR/ovzf0qpdRf2lqMcSYZN29vTAqS2ucRTahKpEtwm2BG4Kx92Pu3b2+tXtPtWgsXeTKzXHAI6qvf8ASpdTkpupPRDceaShEwdM8OyBJGvEhLvK0gCc4yfU9/pXZeGrKCyv4nVACGHNRpGAuOKtWjeXOCK+Zp4ydSvzzZ6jpqNPkjsd/XzZ8bLq7k+IX2e4LfZ47ePyB22kZJ/76z+VfSET+ZEj/wB4A15j8ZvCH9s6Gut2qZu9PQ+YB1eHqf8Avnr9M19XLVXPGW9jwa9nNpaQ3dtI0VxC6yRupwVYcgj8RWh8V7k3njqK9RUinutOtZ5gP77Rgn9MVzqj7RMYpT8mMZP9Ki1Q6jLefaNQkM8giSNZD3VFCqPyAFCd0J6EN+ZjZJvZcFsYFPsJQ0m7rg4/L/61IYJr6zQYAH3hxzU0VmAFJyki8ZHehjR01vPvnhEWQ+RjHXNfWVl5psLfz/8AXeUu/wD3sDP6189/CXwx/bPiBLqePda2eJHz0Zv4R+fP4V9F00DCiiiqEFFFFABRRRQAUUUUAeC/GWwurXWkkgjaRbpd4IGcY4INcf4AVrTxlafa3KrMHhKg9SynAPtmvcfiNoGo61Y2506Jpnj3AxqQOuOefpXmemfDjV01CG8uZ1tnhkVwm3c2Qc4NcVWrGi7y0RtGm6mx013aKjz2VwMwTIUPupyDWf4CupNLmk0q6OJ7R/LJP8Q/hYexGDXTNaTX1gYLhDHcwsVRz0cdiD9P1rlr2ymmuUuLYiPUrcbQGOPOX+6fcdifpXTzKUeaOqISs7M9mtJ1kiUg9amnhhuoHgnjSWJxtZHGQw9CK858OeLlceRPujmjO1434ZT6YrtINYt5EB8wVKnF7McoNPUx3+G/hszGRYJ40Jz5STsE/wDrV0FvbWel2a2tnCkECdEQYH/1z71Xl1aFRw4P05rntX8RRW8RMkyxKeBk/MfYCplVhDd2KUZz03E8S6oFheNDljXJzQ+RHDZJlpFGGxzljyf1NWDO/mfbbtTGV+a3t3++T/fcdvYfiakhAsLSXVbpcyY/dJ3YnoKcJNpzlov61YSS0jHVlr+y7d4BBDOn2yJQXUNzz6jtWZcW4Zh9pJtbqPCpMBnI9GHcfqK5uCO7W/a/Ezrds5cyrwcnr+Ht6V2VnrVtfwCDVoVDj/lrj5SfX2P6VzQxtCq+SWhvPC1aSUo6i2us6npy4mtpZY1/5a2371fyHI/EVaPj23jGHedWHUGBgajfRFLCSyvCqnkdx+YqVLDVRHj7cD/wKtnQl9mb/Mx9rHrEqS+Kr/Ucpp2mXk5P8ci+Wg+pOKxp4XlmWbVp0vJ1OUs4uYUP+0f4z7dPrXVppM8y/wCl3hYD0qlO+k6WcW5E9x0wh3fmegpexp0v3lR382P2k5+7BfcZ9taEzG91I+6qx6n3rTjd7iQyvwOij0FZX2Q6jdx3E+/5DuWPedufUjua24kCrivDzLHqsvZ09uvmduGwzp+9LcdilRsSA04jio+jV5FN2kmdbWh3Gly+bYRnuOKtSxJNE8Uih43UqynoQeorI8PS7reRPTBrar7fDT56UWeHVjyzaPkXxp4bl8MeJ7zT2B8uN90LH+KM8qfy4+tZrSi50yVHALKvy5r3D48WWnf8I9Z38skcd+k3lRAkbpEOcj6A4Oe2fevA7fzHz5YLD0oScZNdAequaOnJHHAkbAcCrMiJLIscaZdjjis5HdTgoyn3FenfCnwhLq+sx6rcxH7DaMG3MOHcdFHrzyf/AK9aEnrXgPw4vhrwvb2zIBcyjzZz33Ht+AwK6eiirJCiiigAooooAKKKKACiiigArEv4Qty/HXmtus7Uk5V/UYrkxsOakzajK0jElhDexHQ+lY+paVFesGf93N0Dj+Kt9hUEiBhggGvCoYyeFlbePY650lUXmcPqOjPuU39s8hXhLqFisij2I6/Qgiq8MdzGStr4glUD+G5twxH4gjP5V2V2bqJR5EKXAJwyPJsIHrnBz9KheK2/5a2an1IUc16yxWDrpOTXz0ZgoV4aROY23UpCXHiKTB7W9vgn8SamtrFLecSWFnLLP3urkl3GfQ9F/DFa5exhYEW3KngBKJdZnKbLe3VPRn5/QU/b4KjqpL5aj5MRU0s/yKi6Ulpm71CXeRyBnj/69Zt7NLqUwJBEKfcX+tW2gmuZRJO7SN0yeg+g7VbhswO1eRjs0dVclPRfmduHwqpe9LVmXFY/7NXI9Oz1ArWjtgOwqdYgK8eU5M6WzIWwaPmNmQ+qnFMdL1WyLqfj/bNbZQVG0QNOGJrU9Iya+ZLjGW6uc9NDcz8TTzSD0ZiRT7exC4+UCto24pRABTlXnP4nctJLRFeKIIOKsqtOEYFSBazuIZtqKRcc1Z201kyKATNXw5N+/wBv95SK6SSRIomkkZURAWZicAAdzXGaPIYNVjUn5WPFdm6LIjI6hkYYZSMgj0r6/KqvtMOvI8nGR5ah8q/FHxQfFXi+Z49psrXMFuQchlBPzfiefpisHSoQEJAP1FeqeOPhrpl14ilXQ1FiVQGVBlo95yeB/DwRwOPauOl8J6zow2y2pmjH/LSD5h+XUflWscXRdRwclzIiVCooqVtCrZWTanqttYK2DNKqbiM4yQK+ptL0630jTLewtVCwwIEXjGcdz7nrXzj4OtZZPG+lLLHJEDcI3zoRnBB/pX01XZEwYUUUVQgooooAKKKKACiiigAooooAKqX67rfPoat1FcLvgce1RVjzQaKi7STMFhUTCrDjmoiK+TxMbSPSgyuy1E8QNWSKaRXI4mqkZ0lsp7VCbRfStQpmmeWKycDVTM9bYCp0hxVoRil2gUlTBzIgmBS4qTFNNVyk3GEU0qKkNJUNDuR7KTbT6Q4FA7jcUhYKCSQAOSTwBUM10qZCAu/XArxv4jXur6hrenae9y6Ws1wkYt4zhSSwHP8AeP1rvw2XVa65to93+hjUxEYabs9sU5oZaVVxx6U5hxXnK9jZvUrHMciSr1Qhq7aS6ihs2upGxEsfmE+2M1xxGQak1rUf+Ka0+zU/vLp/LPrtQ8/qFH417WVYn2MKl+iv/X4HNiaXtJQXyKVtuuGkupR+8ncyMPTPb+lWVtRcSpFgZdgtJCoWMCtTRofMv956Rrn8eledhqbr1oxfV6/qdFSfJFtdDoljVEVVUAKMD2p9FFfdJWPCCiiigAooooAKKKKACiiigAooooAKQ8jFLRQBhTLtYj0yKgNXb9ds59+aot0r5XHLlqtHo03dXGE03IpHJArmPE/jC18JrazX9tcyW9xIY/NhUMEbqAQSOvPT0rhipTkoxV2zfRK7Oo4oxWTYa/Y6hBHLBK22TlQyEGtIPnsfyq5UasdHF/cLmi9mPxSGmmQAZIIpNxbkKT6cVCpVHtF/cO67immGl2yHpG1V7qd7WIyyKFQfeZmwF9ya2jg8RPaD/L8xOrBbsmNNLKOpFWYtPkkwXY4PPFXYdOiiG4gcdSa66WTVZa1Gl+JlLFwXw6mMC8jbY0JPqeBVlNOZ1zKxPt2rnde+JfhvQNajsi73jBD5zWmHER4wCc8n2HSsDVvjfYQxMumaVcTPjhrhhGv5DJr1KGX4ejqld93/AFYwlVrVNtEdxcC2tLyON2VdyN1/CvM7izTXvihp8Ue149PJupscgbfuj8WIrzjXtZ8WeMNTW9d5281hDDBbEqoyeAAOv1Ne2eAvCCeEtD8qUh9RuMSXUuc5bsoPoM/icmlmWKVGi4r4paL/ADFQpuU9dkdWooalFNavk+h6fUZXNS3U1x4lFs+PJtQRGP8Ae+Zj+ddKelcTot+uq+JNRuosGAT+XEw/iCqAT+efwrampezm1tb9UUmlJJnbL92uh0KHZaPKesjcfQf5Nc6OldhZxeTZxR4wVUZ+telklLmqufZfmceNlaFu5Yooor6g8wKKKKACiiigAooooAKKKKACiiigAooooAzdTGGRvUYrMNcVPq97YfEfUbi7u5JNNuwIIo2clIXQ4GB0XcOeO9dnHIJFDAgg9K+bzNWrtWO/D6000RuM1ieItCtvEOi3Om3QISZeHHVGHIYe4NdAy5qJlrytYtSW6OmL0seNWOvr4ShfSdblW3vbY/ISDtkHZl9Qf/rV2Wj/ABc8KXdsPP1CO3kXhllUj8QccitfxH4U0rxPYm11K2EgHKSLw8Z9Vbt9Oh71wUXwW0eGbE0k80Pqr4f/AA/KvosLmlKorVfdl+BxVMPJO8dUdhffFDwsts5guWunIO1YIycn6nArN0b4pr9jCX+iTGRSQrQSAgr2znHOKksvhb4PhiC7r0N6vMakj8AWsOqQwwXubFgS5flwB2B967+ZyV4NMIKitJ3INQ+MdvaL+50GVm/6azhR+gNeceMPiDrPi+1NlIYbSxJy1vb5+fH95jyfpwK9xj8G+GoQM2NvIf70g3n9abc+GPC8kDRyaXZup4wIgD+lNuy95k81O/uxPFvAlp4/1pHt9C1u9tLCAhWlkmJjQ4+6oOecdhwK7a9+FXiDVLc/2r4z1K5bH3WY7M/7ua7nw9bWHhzRxp2nwskAleQKTk/Mc8k/5xVyW+mlGFUKD3NYTxuHpr3pr5a/kHs6kn7sTxfQPhbM2tXNrqkwS1tsHdFwZQfT0rtovCmj2o8qw02A9jI67vzJroktEWZ5iWZ34Yk8VOFA4GK8yvnCStQj83/kdEcPJ6zZkaR4dsdIJkhiXzW53bcBfZR2FbFLSV4dSpOpJzm7s6oxUVZBTWNKTVS8uVt4WdiBgZrMtIwPGWsfYtKe1ifbPcgoGB5Ve5/p+NY3gKAR25wAAZGOB07dKwPEWom9u/MJyH6A9gOn59a7Lwjb+RZoMY4yfqeTXuYrD/VsBGL+KTu/68jlo1PaV5SWy0OxtI/NuoY+zOAfpXZ1yujJv1KM9lyf0rqq68khajKXd/kYY1++kFFFFe0cQUUUUAFFFFABRRRQAUUUUAFFFRyTRwjMjhfqaTkoq7BK5JTXJCMRjIGaoS6ogGIkLH1PAqjNczXHDthf7o4FefWzOhT+F3fkbRoSe+h5deMJ5JUuV3biS4Pf1/Wtvw7rH2VlsLqUsvAikbv/ALJPrxwe/wBam8R6QI2a/iX5f+WqgdP9r/GucJAyrbcdCOuRXZKnRzXDKS0kvvT/AK+84lUqYKq4vWLPTFYMMigrXIaVrrWwEdyxeDor5yy+x9R79fWuqhuI5kVkYMp5BByDXyeJw1TDz5Kq+fRns0qkKseaDH7aaUB6ipMikrCyNLkJi+lN8od1FT0VLiiuZkHlL6CjYB2FSmmGp5UVdjMUmKcaQ0WGNpDSmkzUtjQUhNIWArJ1XxDp2kQl7q4AOcBV5Yk9vrTjCdR8sFdg2oq70NKWQIpJI4rhPFGuoWMCvwM7seuCQD7f/qrP1XxpPqUgjsyLay6POeXc/wB1e31PIHrmuXuJDcysFzhuAM5PPbP1r6LLMmmqiq11t0/zPPxeOioOFPr1LGl2surarGgRioJYg9gMn+VeqaNbmGDBrB8BaZstJr91Hz/uoz7D7x/Pj8K7COML0rkzvEKrX5I7R0+Ztl9NwpXe7N7w/FmaWT0XH5n/AOtXQVl6HHssS+OXb+Valezl1Pkw0V8zkxMuaqwooortMAooooAKKKKACiiigAooooAz729MbGKLhu7elZbNuYliST3NF07CWQgZO41nT3zQgloZD9Bmvj8Zi5VZvmenY9OjR00NHcKMisW01WS/mkitLWeWSPBZQoBH54qCHxCH8STaAbacajDCs7xfKTtP0PJxzj0rONOpKN4xbXozSUOV2bX3nQOodSCAQeCDXD65oT2l0s0QzaHj3jPofb/Jrs4bhZRxwRwQeCPanyIHUqwBB4IPORWuDxtTC1OeHzRhXw8aseSZ5azDcY37ev8AOrsGo3dnHi1u/KcnIWRN6N9fT6itbWPDhiDz2aM6HJMXUr7j1HtXnfi7UbnStHaa0xmVxGHP8IOefY8V9lGvhsfRu9e6Z4fsa2GqJI7m38f/AGecQ6rZ+TkArKjfI358frXSWfiDT76JXiuFAbkB+K8G0TWNROonSdT2XInTejMARnGeo6jGefUV0GkX8t0bm2WYII5mQJu+YqDxzXnTybD1v4d4v+u52/XalP4tT2gTA85FL5grzMahc6fHElvLIM8ElsgfUVqQeIr1Yd8jo+BySuP8K86rkOKh8DT/AA/4H4nRTzKhLfT+v66Hb7hSFhXFDxjMqFnt04ycbsE49BTn8ZMoUmCNcnADPXJ/ZGM/k/Ff5nR9cw/834M7En3ppYVxT+MJ2SR0ESonRtpOePr61mTeKb+YLmbYDgnaNvHPHatoZFjJ7pL5/wCVyJZhQjs7noM11FAN0sioPVjisS/8VWltG7RZmKjPHA/OuCnv3mlaVs5wACWJJx3/AMms938yPy2ORwTjjOPX1r0qHDkFrVlf8P6/A5amavaCN/UvFt7fQsqM0IcDasZwRnPU/wD6q5q/mSTUdPjvyWhDE7FPLPjqf5U5luJMx2iYbPL9dorKvYI4LtYLY/aL0/M8hO7b+P8AhXtUsJRoR5acbHDLEVKjvJ3MfW57m81V7QSeVFEpIydoA68+3tXQeCbK51iKKPJZmYqGPOFHVj9KoahpNlqDLezzmFnTcdpzuxgEfWvRfhlbxRR3aR7AEVAqjkqOevvXDjK8sNRnUjuv+GOyhTjVnGMjvbO2js7OK3iXEcahVFWAOaQcU9Blq+M1k7s9zZaHV6cmzT4Qf7ufz5q3UNsMWsI/2B/Kpq+1pR5YRXkeHN3k2FFFFaEhRRRQAUUUUAFFFFABRRRQBiX9q8UrSAExsc59DVBkDdQK6rrVKfTYJclcxt/s9PyrxMXlTlJzpdeh10sTbSRwviF7bS9LuNSlJQ2yF1dDtYHsAR054r5q1W9vdQ1iTV5rmY30knm+duO8H+HB7YA/CvZPjRqMtmbTQFkB84faJtv90EhQfxBP5V49tDu57Dp9a3yzCyoU25aNsMRW52ktUj0jwR8Vr+91mDTvEcySSy4iiuwgQuewfHGe2cc55r2qNgy5r44ulaM7lJVlOQR1FfUfgbW/7e8J6ffswMkkQEn++OG/UVwZth1GarR67+v/AATWhPmjyvodGy5rivHfh+0vNGmkMIIdlEihtoPo3sQa7Yms3XY1l0S7V8bdm456cc/0riwVV0q8ZJ9Vf0Kqx5qbTPDtI8M2+l3b6it287W6MQrDBQAdT+HHpTtCih8uE3eY9/zCXH3Seefz61uz2yySSxGJRu+V9jHDe2aeIYo1VUTAxgDtX6BSp8p85Vq82g7ypIlA80SJ1B9vrUZcHjcDnr2qRAVTAA2lfqBVG53oMoPncgZzwvvW5ha4+YgDJIFU5ZGkkDMy7R05pJLCWXIF64boQY8j9KiNhODhrtDjnHl4/rUOaZooWLTShkUFvlBHFRyzKVyePpxVdtOmC/vL0KvX5Yv6k0Lp9opUyGWds9XfA/Slztj5F3K738JkWMOWc9FTk1JBIjNIXUho22uDwR3q1AsNvNiGOOMjuByKqoiLqFwsYLGTEhbHHJNK7vqVyq2hNd3U8sP2azQqG+8wqC2sRp1vvJAkfgsep9zV1yIscjP86iLDYeASe/WhrUEzG+wNM6whzsjLFS3AXPJOa9H+HsdvC99HBKJfljLMOmeeBXCSsGLKEGSMY9q77wHH5L3KDaB5aEKFxjr+deRnMf8AY5W8vzR6GXyvXS/rY7ipE+9+NRCpE618ZT3PfZ2Fqc2kJ/2BU1V7L/jyh/3RVivtabvBHhy+JhRRRVkhRRRQAUUUUAFFFFABRRRQAUUUUAeQfGSx0YGCT7I0uuXoEUcnmkCKNTyxXp3/AM9/Co4JYkkE4AO47QD2r3L4xab5ep2uss7cWxgiUdN2Tn9GrxKfLMTyaQzKvgCeK9c+Buq79Iv9Mdsm3mEiD/ZYf4ivJLpGJPBro/htJqFjqVzd2jsnRCCuVbnkEenuDxXLjMLLEUXCG/Q1pVo0pXlsfTGciq96gksp0IBDRsMevFc5p/i4PH/pdrJFjgunzD8uo/WtiHWdPuxsiu4ix/gLYb8jzXzFTD1qLTnFo74yhPSLueeMsCnMJOO6MeR+dQkDGCSMdARU9zbrJL5sOxkU4yhBx/hUTttj5IHbmv0am7wTPlKitNogCluDj14qrfExWxLk4GCck+v61bUDJyPxI7U26i320zqFztJBY8VTEtyshG8kOR3wwx+lct4o8SSWk5s7F1+0Y/eyYB2+w966lWikAdHDKQBmuK8N2Nne6rd3dzMsl4kzMIWH3eTz7/0rGTdlGPU3go3cpdCbT/DzXFjLPq7yyz3C5QM53Rjru+vtWLdW2q+HpxLDPKYc4EgyVPsQat+KdQvP7fNtJcvbwJt2MuQMY+9x15rT0/z7rSGlm1IG2UMGdrYbmA5z82cj3xWVot8q3RteSXM9mXNH1JdUtRcqoV1O2RQc4Pr9DVtd7aqpx8rRducYxWD4TeB7u+W0iYW4wd8jYLHJxwOBxmt53j/taJVwPkwRnAbgcVad0myGrNpE8kZbnPrmq8uE6knPYVcdfmUYxzUbRFm5Xj1rQixTyyKTCo38Y/8A112ngNn+13hcgsY0JO7Pr+VcohtkYiaYKvQ461u6Fr1hbXc5jZYoVjA3NhQeT/SvLzZc2Fkl/WqO7AaVkel7hTWuo4uWYAD1rhLzx7bKNtrunb1jHH5niuU1PxNqN6WWR2gQ9VjOWx9e1fM4fLq83dqy8z2amJpxWrPdPAni1PFNtqaLCI/7PvHtkYHiRAflb+Y/CuvryD4KysZNQVSqwSRq6Ipz0J5PvzXr9fTxjypI8lu7uFFFFMQUUUUAFFFFABRRRQAUUUUAFFFFAGXr2hWXiPS5NPv0JiYhgynDIw6Ee9cPafBXQobjzLm7u7hAciPhAfqR/TFemUUAcbc/C3wfcwpEdJWML/FHI4J+pzzXC674XsvC2sXEOmwi3tXVXiUMTgHGevuDXtlcR8RLESWMN6oGYwyN7jGQP0P51dOVpEVFzRPPJrzICMMrjH0qndXKR2bA7WQDKh13YHtmqbXsDNxMBjqrcMp9MHp9KggMt9JhQfs6EeZIeVPsK7nyOF2efFTUtC3bWN0I43V0W5AO4RjAYdiVqYagyrsu4QD/AHlH9KWWPGCudyjg85FSpci5j8q5wJDwGPf3qoq0UKT5pMjZomTcjnj0qndiSS2aFSMNwS3NSy2Zjf8AdsMdsHjFQSl87T0HNMRFbMkdv5W5f3WFOev1rjvE2jXFjf8A9r2GSrHe5TrG3r9DXXNEsi7l2rIM4YDnmmebdLEgdEfb8uFO0/Ws5RurGsJWd0Ymk+IbXV4xFexQC7QZ+ZRtfHUj0+lYut6vca1MNPsFZ7eM9I14c+v0HvXS3Njp0k5kk0mEsercdaVESOMw21hDEh6jdgfXioam1ZmicE7obpGlDSNNSNyplb55WB4+n4VNblbm987axSP5VLcD61J5JnwksxwvAUDA/SnFAAEGAvYCrUbEN3LUk0acgoD69az7m7JYiMlvYc1M1vkAkFgcg9qdaWabwWKjnn2ptCTM+10yW5DzSfKigsWbnioyI5Y7aVYVMQcpl+Qx/pxmtHV7xGtFsLUg+YdrMP1qlKQNG+VFCwShhzyccHFZTSa8kawbTC5Ja5EeV8tRu4HH5VFcIJIXwoycbgBTfP8ANx/f/wA81JbYYEBief8AJqWkloaJts9G+A9tP9q1OZgViiXy8epZgf8A2U17fXA/CjT/ALN4aluyuDczcH1VeP5lq76uZ7myVgooopDCiiigAooooAKKKKACiiigAooooAKKKKACsvxDZf2hoN5b7dzGMso9SOcfpitSigDwa6s7eXyzLBDMmMfvF59MVFIwWExrtRQOAgwB7fStnxZpraRrM8AUiGT95F6bT2/DpXLTPIG3K2CfXqfau2nGDVzhquSdi47KxD84PBPWobuEMuFYVU+0tt5XBPpSG+eIc8jPXrW9zHlEJnjBOWBHAyetRu+7AYMfXFOa8jlYZwSeT2oLxL8wYY9KV0OzIVeNGIKsue9ItzCDtLlcdj0p5CyKfLckjoM1G9s+CTGpUEZPBpXHykb+XM2Q3qMHvUS4Cbjjbzz6VYMPkwjYAoJycd/0qpIrSgK3QdBmp5ruxfLZXEN7HGScgnPQCk+2NKSwTYvrSLCoyABk1J9nySW2j0I5oYJE8E5cAHDAZ7VWurl5MojZA9OKfvSKMhAM9yaih2I+XCnPbFJ2KVxkaC3tWuZQTIchAe5qa3hYae0LnKMm0gdTnPf0qO8mM7qCQFToM8CoxdYUouAo4xUSaasXFWdzFDPZXDW8/BHQ9mHrWrpjG6vore3HmTysFVBySTwB+ZptwYriPE6RyKOhYdK9T+EXgJYrn/hJry28pAMWcRXG495Dn64H5+lYSk1ozdRW6PVtD01dH0OzsFwTBEFYju3Vj+ZJrRoorIsKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigDD8T+Ho/EGneVkJcR5MTn19D7GvE9TsbnTp5LW6jaOWM4Kt/nke4r6IrM1fQdO1yDy763VyPuyDh1+h/p0rSFRwInTUj52kYEcdB6GollQhkbB69a9M1j4QyS7m0vVFGeiXCf8Asy/4Vwev/Dbxjo1jdag0FpcW9vGZHME/zbQMk4YDPFbqujB0WjIdAzZU4GOBVY3DwRnLAjPRhXMnxJOhwUkGOx5qOTxI8gwYAfT5cUOqgVJnSpqOX4Qdeoq0upzbdvAz75rhZNduD9yFE/CtDwneDUPF2l2moL5lnPOscqKdhIPHUcis3VNFTOpluyVXODz2Gexqobg7+S34cV1vj/w7o+leErq80e1aG5hZD5nnO2FLAHqSO9eNLql+3H2l/wA6lVCnTR2jXJzg5z65zQl2xOzcT9e1cf8AbdRPPms3vmmNqF4Orn8qr2hPszsZLsnkuMemaja8LZ3MPbmue0eG/wBc1a20+KU75nC5x90dz+A5r2KL4d6BGAHW6lI7tORn8sUnVGqZ50bhccsOO1WbCxv9Uk2WNpJKe7AfKPqTwK9QtfC2hWZBi0yAsOhky5/8ezWsqhFCqAqjoAMAVDm2UoJHKaD4Mis50u9VKXMy8rAP9Wp9/wC9/L61339u3+ABJgDgAcAVm0VBZpf27f8A/PWga7fj/lpWbRQBqDX74fx1IviS9HU/rWPRQBvp4ouB97NWovFLHg/rXLUUAdxD4hV+uKvRarFJXnsQct8ma2LQTcZzQB2qXCSDg1LXO27lR8zgfjWgmoQxL8z5oA0qKy2121U4zRQBqUUUUAFFFFABRRTXUsuAxX3FACk460xpol+9Ig+rVl32kT3IPl3TjPqaxJPCmosxP2mNvqxoA6tr60XrcRj/AIEK5Lxp4+8K6FpNza6pqIZriJo/ItxvkIYEHA6Dr3rm/F8dz4Y0p7iRkMjfKhGTgnvXz7qulz3c8l7PeiSWQ5Jc5NAFC/v7aS7mNrHJ5BY7DJgNj3Azz+NVvtg4Gz9aRtPlU44NV3QxtgkZ9qdxWQ+SbcSNo+tej/CawsLm+urie2jkuYFDwu3JTnGQOg+vWvMq734W6gLDXLjzEYxSQlWfHTkEUrjPaZYYrmF4Jo0likG10cZDA9iK+evFUdrY+Jb2KxgjigSUqqKSVwPTPSvdm1y0RdxLcc9K+d9Zme41e7mkQo7ysxU9iSTQAqaioU5hXI9DUU92spyse38c1UqaOHzD98CncVkdF4H1fTtG8Rw3moySxooIVkQMOQRz7c9s171Z3trqFstxZ3Ec8LdHjbIr5zXRd8PmC5jH+yetbfg/Urrw7q6uLg/ZnOJY8/KR649aQz3iimo4kjV1+6yhh9DTqACiiigAooooAKKmhtLic4igkf6LWlb+G7+bBeJox/tUAY9ORWY8IWrrrbwoiYMzA/rWtBo9rAOEBoA4qCC8b/VwYrQi03U5P9muxWCJPuoo/Cn4x0oA5dNBvnHzT7alXwwzcy3LH8a6SigDAHha2xy5JorfooAKKKKACiiigAooooAQ5xxVa4NxtPlgZq1RQB5f8QtA8T+I9GextIo2BYNgvtzj3rxuT4ReNgSv9lSH3E64/nX1pRQB8nwfBbxtMcNZxxA9TJP/AIZrb0/9njVpWDX+oxRDusSEn8z/AIV9K0UAePad8DbKyjCGaF8dTJHkn61v2/wzjtkCxS24A6AIRXoVFAHBt4AkIx5tufwNZl78Klu/vrZue25Sf6V6fRQB4Nq/wEkvTvglhhcd0PB/DFctc/APxPA2YJLScD1Yqf619RUUAfJ0vwb8Zpx/Zm//AHJx/U0tl8G/GpuUJ09YlVgcyTgj8hmvrCigDzPTfB+u+WoupoVYDnqa2ovBtwR+8vEH0Q12VFAHKr4MX+O9b8I//r1Mng60H37iZvpgV0lFAGLF4W0yM5Mbyf771fi0yyg/1drEP+Ag1booARVVRhQAPaloooAKKKKACiiigAooooAKKKKAP//Z";
        
        // We make sure that the file is not there
        String fullNameOut = "/home/ipinyol/img_0.png";
        java.io.File f = new java.io.File(fullNameOut);
        f.delete();
        
        // we create the file
        fileController.createImage(lng_idFile, "png", "base64", content);
        
        // we check that the file has been created
        f = new java.io.File(fullNameOut);
        assertTrue(f.exists());
    }
    
    /**
     * Happy path to check the file is created with img2.png is img0.png and img1.png 
     * already exist.  
     * @throws Exception
     */
    @Ignore("It depends on an accessible direcotry since it creates a file")
    @Test
    public void test_createImage_HappyPath2() throws Exception {
    	String userName = new String("test_userName");
    	File f_root = new File();
    	String str_idFile = new String("1");
    	Long lng_idFile = Long.valueOf(str_idFile);
    	f_root.setId(lng_idFile);
    	String f_url = "file://localhost/home/ipinyol";
    	f_root.setUrl(f_url);
    	IMR_User user = new IMR_User();
    	user.setUserName(userName);
    	f_root.setOwner(user);
        
    	when(principal.getName()).thenReturn(userName);
    	when(fileDB.findByIdSecured(lng_idFile, userName)).thenReturn(f_root);   	     
    	when(fileDB.findById(lng_idFile)).thenReturn(f_root);
                
        String content = "iVBORw0KGgoAAAANSUhEUgAAAW8AAAEACAYAAAB8nvebAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAALEgAACxIB0t1+/AAAH21JREFUeJzt3X10VPWdx/H3pKBr5BnCBAWbiBsgPGUAwaLoBAhiNRSUYgFJCkFsrVXEFsQtGmSrccEiYrdlESlqxcPZVjawyAroIA9VYEk8sBbZ4xIBmQQhBAghhCR3/7gmQ4QkM2Emd+7M53XOnGYmM3c+PSd8++v3/h4chmEYiIiIrcRYHUBERAKn4i0iYkMq3iIiNqTiLSJiQyreIiI2pOItImJDLfx5U0JCAm3atOF73/seLVu2ZNeuXRQXF/Pggw/y1VdfkZCQwJo1a2jXrl2o84qICH6OvB0OBx6Ph7y8PHbt2gVATk4OaWlpHDx4kBEjRpCTkxPSoCIi4uN32+S7a3lyc3PJzMwEIDMzk7Vr1wY3mYiI1MvvkffIkSMZNGgQy5cvB6CoqAin0wmA0+mkqKgodClFRKQOv3reO3bsoEuXLnzzzTekpaXRs2fPOr93OBw4HI6QBBQRkcv5Vby7dOkCQFxcHOPGjWPXrl04nU4KCwuJj4/H6/XSuXPnyz6ngi4i0jSNbTvVaNukrKyMs2fPAnDu3Dk++OAD+vbty5gxY1i1ahUAq1atYuzYsfUGsOvjueeeszxDNGZXfusfym/twx+NjryLiooYN24cAJWVlUyePJlRo0YxaNAgJkyYwIoVK2qnCoqISPNotHgnJiaSn59/2esdOnRg8+bNIQklIiIN0wrLBrjdbqsjNJmds4PyW035w5/D8LfB0pSLOxx+929ERMTkT+3UyFtExIZUvEVEbEjFW0TEhlS8RURsSMVbRMSGVLxFRGxIxVtExIZUvEVEbEjFW0TEhlS8RURsSMVbRMSGVLxFRGxIxVtExIZUvEVEbEjFW0TEhlS8RURsKOTFW0dbiogEn1/Fu6qqCpfLRXp6OgDZ2dl07doVl8uFy+Vi48aN9X72l7+EY8eCE1ZEREx+Fe8lS5aQnJyMw+EAzCN6Zs2aRV5eHnl5eYwePbrez/785zBtGug0NBGR4Gm0eB89epQNGzYwffr02jPVDMPw+2zKf/onOHkS/vCHqwsqIiI+jRbvJ598koULFxIT43urw+Fg6dKl9O/fn6ysLEpKSur9fMuW8Pbb8OyzcPBgcEKLiES7Bov3+vXr6dy5My6Xq85I++c//zmHDh0iPz+fLl268NRTTzX4JT16QHY2TJkClZVByS0iEtVaNPTLnTt3kpuby4YNGygvL+fMmTNkZGTw5ptv1r5n+vTptTcyryQ7Oxswe95VVW5eeMHNs88GJ7yISCTweDx4PJ6APuMw/Gxeb926lUWLFrFu3Tq8Xi9dunQBYPHixezevZt33nnn8os7HHVG7F9/DQMGwPr1cOutAeUUEYka362dV9LgyPtShmHUzjaZPXs2n332GQ6Hg8TERJYtW+bXNW68EZYsMdsne/dCbKy/3y4iIpfye+TdpIvX878ekyZBp07w6quh+mYREfvyZ+RtSfE+dQr69YM33oC0tFB9u4iIPflTvC3Z26R9e7NwT5tmFnIREQmMJSPvGo8/DidOwBXudYqIRK2wHXnXyMkxb1y++66VKURE7MfSkTfAnj3wwx9CXp45G0VEJNqF/cgbYNAgeOwxmDoVqqutTiMiYg+WF2+AZ56B06fhX//V6iQiIvZgedukxsGDMHQobN8OPXuGKpGISPizRdukRlISPP+8ufry4kWr04iIhLewKd5gHtzQsSP89rdWJxERCW9h0zapcewYuFywbh0MHhyiYCIiYcxWbZMaN9wAr71mtk/KyqxOIyISnsJu5F3joYegXTuzkIuIRJOw3ZjKHyUl5uZVy5fD3XcHOZiISBizZdukRrt2sHIlZGWZBxiLiIhP2I68a8ycCV6vuf/Jt2dBiIhENFuPvGu8+CLs2werV1udREQkfIT9yBvMnQdHj4b//m/o1i0IwUREwlhEjLzBPLT4iSe0eZWISA2/indVVRUul4v09HQAiouLSUtLIykpiVGjRlFSUhLSkABz5sC5c5o6KCICfhbvJUuWkJycXHt6fE5ODmlpaRw8eJARI0aQk5MT0pAALVrAW2+Z+5/8/e8h/zoRkbDWaPE+evQoGzZsYPr06bU9mNzcXDIzMwHIzMxk7dq1oU35rVtuMfc9eeghqKholq8UEQlLjRbvJ598koULFxIT43trUVERTqcTAKfTSVFRUegSfseMGRAfDwsWNNtXioiEnRYN/XL9+vV07twZl8uFx+O54nscDkdtO+VKsrOza392u9243e6m5Lzk++D1183Nq+69F2677aouJyJiOY/HU2+NrU+DUwWfeeYZ3nrrLVq0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xV55BdasgY8/hhaN3l0QETFpebzFHn8crrsO/uVfrE4iIpFGI+8QO3IEBg6E//ovcLmsTiMidqCRdxjo1g0WL4aHHtLmVSISPBp5NwPDgAcfhK5dzVPoRUQaohuWYeTkSejXz1zEM3y41WlEJJypbRJGOnaEFStg6lSI4k0YRSRINPJuZo8+CqWl8OabVicRkXClkXcYWrjQXLjz7/9udRIRsTONvC3w6acwZoy5/0mXLlanEZFwo5F3mBoyBB55BLKyzJkoIiKBUvG2yLx5cPw4LFtmdRIRsSO1TSx04AAMGwY7d8I//qPVaUQkXKhtEuZ69jS3j50yBSorrU4jInai4m2xX/wCWreGK5zfLCJSL7VNwsDRozBgALz/vrmJlYhEN7VNbKJrV1iyxNy86vx5q9OIiB1o5B1GfvIT89DiV16xOomIWEkbU9lMcTH07w8rV8LIkVanERGrqG1iMx06mJtXTZsGp05ZnUZEwlmjxfvIkSOkpqbSu3dv+vTpw6uvvgpAdnY2Xbt2xeVy4XK52LhxY8jDRoNRo8yl8489ZnUSEQlnjbZNCgsLKSwsJCUlhdLSUgYOHMjatWtZs2YNrVu3ZtasWfVfXG2TJikrM2efPP88TJhgdRoRaW7+1M5GzzSPj48nPj4egFatWtGrVy++/vprABXmEImNNQ9tuO8+uP12uPFGqxOJSLgJqOddUFBAXl4et912GwBLly6lf//+ZGVlUaITBoLq1lvNvb+1eZWIXInfs01KS0txu9385je/YezYsRw/fpy4uDgA5s2bh9frZcWKFXUv7nDw3HPP1T53u9243e7gpY9wFy+aI++f/tQs5CISmTweDx6Pp/b5/PnzgzNV8OLFi9x3333cc889zJw587LfFxQUkJ6ezr59++peXD3vq/bFF2YB37EDevSwOo2INIegTBU0DIOsrCySk5PrFG6v11v783vvvUffvn2vIqrUp0cPmD/f3Lzq4kWr04hIuGh05L19+3buvPNO+vXrh8PhAOCFF15g9erV5Ofn43A4SExMZNmyZTidzroX18g7KAwDRo+GoUPhki6UiEQorbCMIF9/bU4fXL/evJkpIpFLKywjyI03wquvmu2TsjKr04iI1TTytpnJk81l9EuXWp1EREJFbZMIdOqUuXnV66+bS+lFJPKobRKB2reHN94wF+8UF1udRkSs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";
        
        // We make sure that the file is not there and that img0.png and img1.png are there
        String fullNameOut = "/home/ipinyol/img_2.png";
        java.io.File f = new java.io.File(fullNameOut);
        f.delete();
        
        f = new java.io.File("/home/ipinyol/img_0.png");
        f.createNewFile();
        
        f = new java.io.File("/home/ipinyol/img_1.png");
        f.createNewFile();
        
        // we call the tested method
        fileController.createImage(lng_idFile, "png", "base64", content);
        
        // we check that the file has been created
        f = new java.io.File(fullNameOut);
        assertTrue(f.exists());
    }
    
    @Test
    //A directory is created 
    //HAPPY PATH
    public void test_createFile_DirectoryHappyPath()throws Exception{
    	
    	String userName = new String("test_userName");
    	String idParentDir = new String("1");
    	String name = new String("name");
    	String type = new String("directory");
    	
    	//File to be renamed
    	File f_root = new File();
    	String str_idFile = new String("1");
    	Long lng_idFile = Long.valueOf(str_idFile);
    	f_root.setId(lng_idFile);
    	String f_url = "file://localhost/root/test_dir";
    	f_root.setUrl(f_url);
    	IMR_User user = new IMR_User();
    	user.setUserName(userName);
    	f_root.setOwner(user);
              
        List<File> list_test = new ArrayList<File>();     
        
             
        when(principal.getName()).thenReturn(userName);
    	when(fileDB.findByIdSecured(lng_idFile, userName)).thenReturn(f_root);   	      
        when(fileDB.findAllByName(name, userName)).thenReturn(list_test);   
        when(fileUtils.createFile(f_root.getUrl(), name, type)).thenReturn("dir");
              
        fileController.createFile(idParentDir, name, type, sc);
        
        ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
    	verify(em, times(1)).persist(fileCaptor.capture());
    	List<File> captured_files = fileCaptor.getAllValues();
    	
    	String urlNewFile = f_root.getUrl() + "/" + name;
    	assertTrue(captured_files.size() == 1);
    	assertTrue(captured_files.get(0).getDir() == f_root);
    	assertTrue(captured_files.get(0).getName().equals(name));
    	assertTrue(captured_files.get(0).getIMR_Type().equals("dir"));
    	assertTrue(captured_files.get(0).getUrl().equals(urlNewFile));
        
    }
    
    @Test
    // Pagination testing 
    public void test_getFileContent_Pagination_Basic() throws Exception {
        int linesPerPage = 3;
        String userName = "user";
        this.fileController.setPagination(linesPerPage);
        String uri = createFile("test.csv", linesPerPage*5+2);
        File file = new File();
        file.setUrl(uri);
        
        // Base case: Pagination is 0 and negative. An exception should be thrown
        try{
            fileController.getFileContent(userName, file, -1);
        }
        catch (IMathException e) {
            assertEquals(e.getIMATH_ERROR(),IMathException.IMATH_ERROR.INVALID_PAGINATION);  
        }

        try{
            fileController.getFileContent(userName, file, 0);
        }
        catch (IMathException e) {
            assertEquals(e.getIMATH_ERROR(),IMathException.IMATH_ERROR.INVALID_PAGINATION);  
        }

        // Standard case: Page = 1
        List<String> lines = fileController.getFileContent(userName, file, 1);
        assertTrue(lines.size()==linesPerPage);
        assertTrue(lines.get(0).equals("0"));
        assertTrue(lines.get(1).equals("1"));
        assertTrue(lines.get(2).equals("2"));
        
        // Standard case: Page = 3
        lines = fileController.getFileContent(userName, file, 3);
        assertTrue(lines.size()==linesPerPage);
        assertTrue(lines.get(0).equals("6"));
        assertTrue(lines.get(1).equals("7"));
        assertTrue(lines.get(2).equals("8"));
        
        // Extreme case: Page = 6 (The last pagination only has 2 lines)
        lines = fileController.getFileContent(userName, file, 6);
        assertTrue(lines.size()==2); // the file only has 17 lines
        assertTrue(lines.get(0).equals("15"));
        assertTrue(lines.get(1).equals("16"));
    }

    @Test
    // Saving content with pagination test - Exceptions
    public void test_saveFileContent_Exceptions() throws Exception {
        String userName = "user";
        File file = new File();
        List<String> content = new ArrayList<String>();
        
        // Base case: Pagination is 0 and negative. An exception should be thrown
        try{
            fileController.saveFileContent(userName, file, content, -1L);
        }
        catch (IMathException e) {
            assertEquals(e.getIMATH_ERROR(),IMathException.IMATH_ERROR.INVALID_PAGINATION);  
        }

        try{
            fileController.saveFileContent(userName, file, content, 0L);
        }
        catch (IMathException e) {
            assertEquals(e.getIMATH_ERROR(),IMathException.IMATH_ERROR.INVALID_PAGINATION);  
        }
        
        // Base case: Username must not be null
        try{
            fileController.saveFileContent(null, file, content, 1L);
        }
        catch (IMathException e) {
            assertEquals(e.getIMATH_ERROR(),IMathException.IMATH_ERROR.OTHER);  
        }
        
        // Base case: file must not be null
        try{
            fileController.saveFileContent(userName, null, content, 1L);
        }
        catch (IMathException e) {
            assertEquals(e.getIMATH_ERROR(),IMathException.IMATH_ERROR.OTHER);  
        }
        
        // Base case: Content must not be null
        try{
            fileController.saveFileContent(userName, file, null, 1L);
        }
        catch (IMathException e) {
            assertEquals(e.getIMATH_ERROR(),IMathException.IMATH_ERROR.OTHER);  
        }
        
        // Base case: Page must not be null
        try{
            fileController.saveFileContent(userName, file, content, null);
        }
        catch (IMathException e) {
            assertEquals(e.getIMATH_ERROR(),IMathException.IMATH_ERROR.OTHER);  
        }
    }
    
    @Test
    // Saving content with pagination test - happy paths
    public void test_saveFileContent() throws Exception {
        int linesPerPage = 3;
        String userName = "user";
        this.fileController.setPagination(linesPerPage);
        String uri = createFile("test.csv", linesPerPage*5+2);
        File file = new File();
        file.setUrl(uri);
        List<String> content = new ArrayList<String>();
        
        // Case 1: We modify the first two lines
        content.add("100");
        content.add("200");
        content.add("2");
        fileController.saveFileContent(userName, file, content, 1L);
        List<String> lines = fileController.getFileContent(userName, file, 1);
        assertTrue(lines.size()==linesPerPage);
        assertTrue(lines.get(0).equals("100"));
        assertTrue(lines.get(1).equals("200"));
        assertTrue(lines.get(2).equals("2"));
        
        // Case 2: We modify the lines 4 and 5
        content = new ArrayList<String>();
        content.add("100");
        content.add("200");
        content.add("3");
        content.add("4");
        content.add("300");
        content.add("400");
        fileController.saveFileContent(userName, file, content, 2L);
        lines = fileController.getFileContent(userName, file, 2);
        assertTrue(lines.size()==linesPerPage);
        assertTrue(lines.get(0).equals("4"));
        assertTrue(lines.get(1).equals("300"));
        assertTrue(lines.get(2).equals("400"));
        
        // Case 3: We erase lines 2 and three after 2 pagnations:
        content = new ArrayList<String>();
        content.add("800");
        content.add("900");
        content.add("800");
        content.add("900");
        fileController.saveFileContent(userName, file, content, 2L);
        lines = fileController.getFileContent(userName, file, 1);
        assertTrue(lines.size()==linesPerPage);
        assertTrue(lines.get(0).equals("800"));
        assertTrue(lines.get(1).equals("900"));
        assertTrue(lines.get(2).equals("800"));
        lines = fileController.getFileContent(userName, file, 2);
        assertTrue(lines.get(0).equals("900"));
        assertTrue(lines.get(1).equals("6"));
        assertTrue(lines.get(2).equals("7"));
        
        // Case 4: We only leave one line 
        content = new ArrayList<String>();
        content.add("9090");
        fileController.saveFileContent(userName, file, content, 20L);
        lines = fileController.getFileContent(userName, file, 1);
        assertTrue(lines.size()==1);
        assertTrue(lines.get(0).equals("9090"));
        
    }
    
    private String createFile(String name, int lines) {
        try {
            java.io.File file = new java.io.File(name);
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i<lines; i++) {
                String line = i + "\n";
                output.write(line);    
            }
            output.close();
            return file.toURI().toString();
          } catch ( IOException e ) {
             e.printStackTrace();
             return "";
          }
    }
    
    /**
     * Test paste file/dir action for non valid values
     */
    @Test
    public void pasteItem_nullValuesTest(){
        String userName = "userName";
        try {
            fileController.pasteItem(userName ,"NonValidAction", null, null);
            fail();
        } catch (IMathException e) {
            assertEquals(e.getIMATH_ERROR(),IMathException.IMATH_ERROR.OTHER);  
        }
        
        try {
            fileController.pasteItem(userName ,"copy", 12L, null);
            fail();
        } catch (IMathException e) {
            assertEquals(e.getIMATH_ERROR(),IMathException.IMATH_ERROR.OTHER);  
        }
        
        try {
            fileController.pasteItem(userName ,"move", null, 12L);
            fail();
        } catch (IMathException e) {
            assertEquals(e.getIMATH_ERROR(),IMathException.IMATH_ERROR.OTHER);  
        }
    }

    /**
     * Test paste file/dir action for un/authorized Items
     */
    @Test
    public void pasteItem_unauthorizedItems() {
        // PREPARE
        String userNotAuth = "userNotAuth";
        String userAuth = "userAuth";

        Long origin = 1L;
        Long destiny = 2L;
        
        File originFile = new File();
        File originDir = new File();
        File destinyFile = new File();
        File destinyDir = new File();

        originFile.setIMR_Type("py");
        originDir.setIMR_Type("dir");
        destinyFile.setIMR_Type("py");
        destinyDir.setIMR_Type("dir");
        
        // ACTION and COMPROVATION: get destiny dir is NOT authorized
        when(db.getFileDB().findByIdSecured(origin, userAuth)).thenReturn(originFile);
        when(db.getFileDB().findByIdSecured(destiny, userNotAuth)).thenReturn(null);

      
        try {
            fileController.pasteItem(userAuth, "copy", origin, destiny);
            fail();
        } catch (IMathException e) {
            assertEquals(e.getIMATH_ERROR(),IMathException.IMATH_ERROR.OTHER);  
        }

        // ACTION and COMPROVATION: Trying to copy into a file. Should fail.
        when(db.getFileDB().findByIdSecured(origin, userAuth)).thenReturn(originFile);
        when(db.getFileDB().findByIdSecured(destiny, userAuth)).thenReturn(destinyFile);

        try {
            fileController.pasteItem(userAuth, "copy", origin, destiny);
            fail();
        } catch (IMathException e) {
        }
    }

    @Test
    public void pasteItem_copyFileToDestiny() {

        // PREPARE
        String userAuth = "userAuth";
        String domain = "file://localhost";
        String rootDir = "/tmp/iMathCloudTests";
        //remove tmp dirs
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(new java.io.File(rootDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Long origDirId = 1L;
        String origDirName = "origPath";
        File origDir = null;

        Long origFileId = 2L;
        String origFileName = "test.py";
        String origFileNewName = "test_new.py";
        File origFile = null;

        Long destDirId = 3L;
        String destDirName = "destPath";
        File destDir = null;

        try {
            origDir = this.createFileToTest(origDirId, origDirName, "dir", domain + rootDir);
            origFile = this.createFileToTest(origFileId, origFileName, "py", origDir.getUrl());
            destDir = this.createFileToTest(destDirId, destDirName, "dir", domain + rootDir);

            // ACTION: Copying a file into a folder
            when(db.getFileDB().findByIdSecured(origFileId, userAuth)).thenReturn(origFile);
            when(db.getFileDB().findByIdSecured(destDirId, userAuth)).thenReturn(destDir);

            fileController.pasteItem(userAuth, "copy", origFileId, destDirId);

            // COMPROVATION:
            // Check if the new file was properly created.
            java.io.File file = new java.io.File(destDir.getPath() + "/" + origFile.getName());
            assertTrue(file.exists());
          
            // ACTION:  Copying a file with existent name into the folder
            fileController.pasteItem(userAuth, "copy", origFileId, destDirId);
           
            java.io.File file_new = new java.io.File(destDir.getPath() + "/" + origFileNewName);
            assertTrue(file_new.exists());

        } catch (IMathException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            //remove tmp dirs
            try {
                org.apache.commons.io.FileUtils.deleteDirectory(new java.io.File(rootDir));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Test
    public void pasteItem_copyDirToDestiny(){


        // PREPARE
        String userAuth = "userAuth";
        String domain = "file://localhost";
        String rootDir = "/tmp/iMathCloudTests";
        //remove tmp dirs
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(new java.io.File(rootDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Long origDirId = 1L;
        String origDirName = "origPath";
        String origDirNewName = "origPath_new";
        File origDir = null;

        Long origFileId = 2L;
        String origFileName = "test.py";
        File origFile = null;
        
        Long origDir2Id = 3L;
        String origDir2Name = "origPath2";
        File origDir2 = null;
        
        Long origFile2Id = 4L;
        String origFile2Name = "test2.py";
        File origFile2 = null;

        Long destDirId = 5L;
        String destDirName = "destPath";
        File destDir = null;

        try {
            origDir = this.createFileToTest(origDirId, origDirName, "dir", domain + rootDir);
            origFile = this.createFileToTest(origFileId, origFileName, "py", origDir.getUrl());
            origDir2 = this.createFileToTest(origDir2Id, origDir2Name, "dir", origDir.getUrl());
            origFile2 = this.createFileToTest(origFile2Id, origFile2Name, "py", origDir2.getUrl());
            destDir = this.createFileToTest(destDirId, destDirName, "dir", domain + rootDir);
                      

            // ACTION: Copying a dir into a folder
            when(db.getFileDB().findByIdSecured(origDirId, userAuth)).thenReturn(origDir);
            when(db.getFileDB().findByIdSecured(destDirId, userAuth)).thenReturn(destDir);

            fileController.pasteItem(userAuth, "copy", origDirId, destDirId);

            // COMPROVATION:
            // Check if the new dir was properly created.
            java.io.File dir = new java.io.File(destDir.getPath() + "/" + origDir.getName());
            assertTrue(dir.exists());
            java.io.File dir2 = new java.io.File(destDir.getPath() + "/" + origDir.getName() + "/" + origDir2.getName() );
            assertTrue(dir2.exists());


            // ACTION: Copying a dir into a folder with an existing dir
            when(db.getFileDB().findByIdSecured(origDirId, userAuth)).thenReturn(origDir);
            when(db.getFileDB().findByIdSecured(destDirId, userAuth)).thenReturn(destDir);

            fileController.pasteItem(userAuth, "copy", origDirId, destDirId);

            // COMPROVATION:
            // Check if the new dir was properly created with a new name.
            java.io.File dirNew = new java.io.File(destDir.getPath() + "/" + origDirNewName);
            assertTrue(dirNew.exists());
            java.io.File dir2New = new java.io.File(destDir.getPath() + "/" + origDirNewName + "/" + origDir2.getName() );
            assertTrue(dir2New.exists());
            
            
            Long destDir2Id = 6L;
            File destDir2 = this.createFileToTest(destDir2Id, origDir2Name, "dir", domain + destDir.getPath() + "/" + origDir.getName() + "/" + origDir2.getName());           
            
            // ACTION: Copying a dir into a folder which is a child of this dir (RECURSIVE COPY, ERROR)
            // orig -> /tmp/iMathCloudTests/destPath
            // dest -> /tmp/iMathCloudTests/destPath/origPath/origPath2
            when(db.getFileDB().findByIdSecured(destDir2Id, userAuth)).thenReturn(destDir2);
            when(db.getFileDB().findByIdSecured(destDirId, userAuth)).thenReturn(destDir);
            
            fileController.pasteItem(userAuth, "copy", destDirId, destDir2Id);
            
            // Check that the dir was not created.
            java.io.File dirError = new java.io.File(destDir.getPath() + "/" + origDir.getName() + "/" + origDir2.getName() + "/" + destDir.getName());
            System.out.println("direrror " + dirError.getPath() + " " + dirError.exists());
            assertTrue(!dirError.exists());
            
            
        } catch (IMathException e) {
            fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            //remove tmp dirs
            try {
                org.apache.commons.io.FileUtils.deleteDirectory(new java.io.File(rootDir));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private File createFileToTest(Long id, String name, String type, String uri) {
    	    	
        File file = new File();
        file.setIMR_Type(type);
        file.setId(id);
        file.setName(name);
        file.setUrl(uri + "/" + name);
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(file.getPath());
            if (type == "dir") {
                java.nio.file.Files.createDirectories(path);
            }
            else {
                java.nio.file.Files.createFile(path);
            }
        } catch (Exception e) {
            fail();
        }

        return file;
    }

}
