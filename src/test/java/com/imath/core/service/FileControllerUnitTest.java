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

import javax.persistence.EntityManager;
import javax.ws.rs.core.SecurityContext;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.junit.Before;
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
    }
    
    @Test
    //Test the normal path of the function when everything is OK
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
    	
    	Set<String> idFiles = new HashSet<String>();
    	idFiles.add(String.valueOf(id_f1));
    	idFiles.add(String.valueOf(id_f2));
    	
    	String trashLocationF1 = new String ("//trash/f1");
    	
    	when(principal.getName()).thenReturn("test_userName");
    	when(fileDB.findById(id_f1)).thenReturn(f1);
    	when(fileDB.findById(id_f2)).thenReturn(f2);
    	when(fileUtils.trashFile(f1)).thenReturn(trashLocationF1);
    	when(fileUtils.trashFile(f2)).thenReturn(null);
    	when(fileUtils.restoreFile(f1, trashLocationF1)).thenReturn(true);
    	
    	try{
    		fileController.eraseListFiles(idFiles, sc);
    	}
    	catch (IMathException e){    		
    		verify(fileDB, times(1)).findById(id_f1);
    		verify(fileDB, times(1)).findById(id_f2);
    		verify(fileUtils, times(1)).trashFile(f2);
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
    		assertEquals(e.getIMATH_ERROR().toString(), "RECOVER_PROBLEM");  		
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
    	
    	//List of childre of the directory to be renamed
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
}
