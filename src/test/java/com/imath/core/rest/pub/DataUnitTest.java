package com.imath.core.rest.pub;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.imath.core.data.MainServiceDB;
import com.imath.core.service.FileController;
import com.imath.core.util.FileUtils;
import com.imath.core.util.PublicResponse;

import org.apache.james.mime4j.message.BodyPart;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.ContentBody;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.StringBody;
//import org.hamcrest.Matchers.*;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInputImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;
import org.mockito.Matchers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class DataUnitTest {
	
	// The class that contains the code we want to unit test 
    // Must not be mocked
    private static String destinationDir = "destinationDir";
    private static String uploadedFile = "uploadedFile";
    private static String numberFiles ="numberFiles";
    private static String uploadedFile_1 = "uploadedFile-1";
    private static String uploadedFile_2 = "uploadedFile-2";
    private static String uploadedFile_3 = "uploadedFile-3";
    private static String destinationDir_1 = "destinationDir-1";
    private static String destinationDir_2 = "destinationDir-2";
    private static String destinationDir_3 = "destinationDir-3";
    
    private static String idDeletedFile = "idDeletedFile";
    private static String pathDeletedFile = "pathDeletedFile";
    
    private static String idFile = "idFile";
    private static String pathNameFile = "pathNameFile";
    private static String newName = "newName";
    
    private static String pathParentDir = "pathParentDir";
    private static String idParentDir = "idParentDir";
    private static String dirName = "dirName";
    private static String fileName = "fileName";
    
    
    
    private Data data = new Data();   
    
	@Mock private FileController fileController;        // The file Controller bean
    @Mock private MultipartFormDataInput input;         // The html form map
    @Mock private InputPart inputDir;                   // Input section of the html call regarding directories
    @Mock private InputPart inputDir2;                   // Input section of the html call regarding directories
    @Mock private InputPart inputFile;                  // Input section of the html call regarding files
    @Mock private InputPart inputFile2;                 // Input section of the html call regarding files
    @Mock private InputStream inputStream;              // Input stream of the file that is being uploaded
    @Mock private InputStream inputStream2;              // Input stream of the file that is being uploaded
    @Mock private MultivaluedMap<String, String> header;// The header of the html call
    @Mock private MultivaluedMap<String, String> header2;// The header of the html call
    @Mock private FileUtils fileUtils;                  // Utilities for files
    @Mock private SecurityContext sc;                   // The security context
    @Mock private Principal principal;                  // It contains logged info user
    @Mock private Logger LOG;                           // The Logger
    @Mock private InputPart inputnumberFile;
    
    @Mock private InputPart inputFileId;
    @Mock private InputPart inputFileId1;
    @Mock private InputPart inputFileId2;
    
    @Mock private InputPart inputPath;
    @Mock private InputPart inputPath1;
    @Mock private InputPart inputPath2;
    
    @Mock private InputPart inputNewName1;
    @Mock private InputPart inputNewName2;
    

    
	@Before
    public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    
	    when(sc.getUserPrincipal()).thenReturn(principal);
	    
	    // We simulate the injections
	    data.setFileController(fileController);
	    data.setLOG(LOG);
	    data.setFileUtils(fileUtils);
    }
	
	/*
	 * TEST THE BASIC REST CALL TO UPLOAD FILE
	 * 
	 */
	
	@Test
	// Test the base cases of REST_uploadFile method when inputMap is empty (it does not contain any tag)
	public void test_uploadFilesBase_EmptyInput() throws Exception {
	   Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
	   // The case when no files are uploaded
	   
	   when(input.getFormDataMap()).thenReturn(inputMap);
	   when(principal.getName()).thenReturn("userTest");
	   
	   List<PublicResponse.StateDTO> response = data.REST_uploadData(input, sc);
	   assertTrue(response!=null);
	   assertTrue(response.size()==0);
	}
	
	@Test
    // Test the base cases of REST_uploadFile method when inputMap contains uploadedFile, but the list is empty.
    public void test_uploadFilesBase_EmptyFiles() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       // The case when no files are uploaded
       inputMap.put(uploadedFile, new ArrayList<InputPart>());
       when(input.getFormDataMap()).thenReturn(inputMap);
       when(principal.getName()).thenReturn("userTest");
       
       List<PublicResponse.StateDTO> response = data.REST_uploadData(input, sc);
       assertTrue(response!=null);
       assertTrue(response.size()==0);
    }
	
    @Test
    // Test the base cases of REST_uploadFile method when the inputMap contains more than one directory.
    public void test_uploadFilesBase_ErrorMoreThanOneDirectory() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       List<InputPart> dirs = new ArrayList<InputPart>();
       
       dirs.add(inputDir);
       dirs.add(inputDir);
       // The case when no files are uploaded
       inputMap.put(uploadedFile, new ArrayList<InputPart>());
       inputMap.put(destinationDir, dirs);
       when(input.getFormDataMap()).thenReturn(inputMap);
       when(principal.getName()).thenReturn("userTest");
       
       List<PublicResponse.StateDTO> response = data.REST_uploadData(input, sc);
       assertTrue(response!=null);
       assertTrue(response.size()==1);
       assertTrue(response.get(0).status.code == PublicResponse.Status.FAIL.getValue());
       assertTrue(response.get(0).code == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
    }

    @Test
    // Test REST_uploadFile method when the inputMap contains only one directory and no files.
    public void test_uploadFilesBase_OneDir_NoFiles() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       List<InputPart> dirs = new ArrayList<InputPart>();
       
       String userTest = "userTest";
       String dirTest = "//dir";
       
       dirs.add(inputDir);
       inputMap.put(uploadedFile, new ArrayList<InputPart>());
       inputMap.put(destinationDir, dirs);
       when(input.getFormDataMap()).thenReturn(inputMap);
       when(principal.getName()).thenReturn(userTest);
       when(inputDir.getBodyAsString()).thenReturn(dirTest);
       
       List<PublicResponse.StateDTO> response = data.REST_uploadData(input, sc);
       assertTrue(response!=null);
       assertTrue(response.size()==0);
       verify(fileController).getDir(dirTest,userTest);
    }
    
    @Test
    // Test REST_uploadFile method when the inputMap contains only one directory and one file: happy path
    public void test_uploadFilesBase_OneDir_OneFile() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       
       List<InputPart> dirs = new ArrayList<InputPart>();
       List<InputPart> files = new ArrayList<InputPart>();
       
       String userTest = "userTest";
       String dirTest = "//dir";
       String fileUploads = "filename=file1.csv";
       com.imath.core.model.File fileRoot = new com.imath.core.model.File();
       fileRoot.setUrl("root");
       com.imath.core.model.File fileNoRoot = new com.imath.core.model.File();
       fileNoRoot.setUrl("root/dir");
       
       dirs.add(inputDir);
       files.add(inputFile);
       inputMap.put(uploadedFile, files);
       inputMap.put(destinationDir, dirs);
       when(input.getFormDataMap()).thenReturn(inputMap);
       
       when(principal.getName()).thenReturn(userTest);
       when(inputDir.getBodyAsString()).thenReturn(dirTest);
       when(inputFile.getHeaders()).thenReturn(header);
       when(header.getFirst("Content-Disposition")).thenReturn(fileUploads);
       when(inputFile.getBody(InputStream.class,null)).thenReturn(inputStream);
       when(fileController.createNewFileInROOTDirectory(Matchers.anyString(), (SecurityContext) Matchers.anyObject())).thenReturn(fileRoot);
       when(fileController.createNewFileInDirectory((com.imath.core.model.File)Matchers.anyObject(), Matchers.anyString(),Matchers.anyString())).thenReturn(fileNoRoot);
       
       List<PublicResponse.StateDTO> response = data.REST_uploadData(input, sc);
       
       assertTrue(response!=null);
       assertTrue(response.size()==1);
       assertTrue(response.get(0).status.code == PublicResponse.Status.READY.getValue());
       assertTrue(response.get(0).code == Response.Status.ACCEPTED.getStatusCode());
       
       verify(fileController).getDir(dirTest,userTest);
       verify(fileController).createNewFileInDirectory((com.imath.core.model.File) Matchers.anyObject(),Matchers.eq("file1.csv"), Matchers.eq("csv"));
       verify(fileUtils).writeFile((byte [])Matchers.any(), Matchers.eq(fileNoRoot.getUrl()));
    }
    
    @Test
    // Test REST_uploadFile method when the inputMap contains no directory but one file. 
    // So, file is created under the ROOT : happy path
    public void test_uploadFilesBase_NoDir_OneFile() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       
       List<InputPart> dirs = new ArrayList<InputPart>();
       List<InputPart> files = new ArrayList<InputPart>();
       
       String userTest = "userTest";
       String dirTest = "//dir";
       String fileUploads = "filename=file1.csv";
       com.imath.core.model.File fileRoot = new com.imath.core.model.File();
       fileRoot.setUrl("root");
       com.imath.core.model.File fileNoRoot = new com.imath.core.model.File();
       fileNoRoot.setUrl("root/dir");
       
       dirs.add(inputDir); 
       files.add(inputFile);
       inputMap.put(uploadedFile, files);
       //inputMap.put(destinationDir, dirs);        // We do not put the directory,
       when(input.getFormDataMap()).thenReturn(inputMap);
       
       when(principal.getName()).thenReturn(userTest);
       //when(inputDir.getBodyAsString()).thenReturn(dirTest);
       when(inputFile.getHeaders()).thenReturn(header);
       when(header.getFirst("Content-Disposition")).thenReturn(fileUploads);
       when(inputFile.getBody(InputStream.class,null)).thenReturn(inputStream);
       when(fileController.createNewFileInROOTDirectory(Matchers.anyString(), (SecurityContext) Matchers.anyObject())).thenReturn(fileRoot);
       when(fileController.createNewFileInDirectory((com.imath.core.model.File)Matchers.anyObject(), Matchers.anyString(),Matchers.anyString())).thenReturn(fileNoRoot);
       
       List<PublicResponse.StateDTO> response = data.REST_uploadData(input, sc);
       
       assertTrue(response!=null);
       assertTrue(response.size()==1);
       assertTrue(response.get(0).status.code == PublicResponse.Status.READY.getValue());
       assertTrue(response.get(0).code == Response.Status.ACCEPTED.getStatusCode());
       
       verify(fileController, times(0)).getDir(dirTest,userTest);
       verify(fileController).createNewFileInROOTDirectory(Matchers.eq("file1.csv"), (SecurityContext)Matchers.anyObject());
       verify(fileUtils).writeFile((byte [])Matchers.any(), Matchers.eq(fileRoot.getUrl()));
    }
    
    @Test
    // The same as before but handling more than one file
    public void test_uploadFilesBase_NoDir_MoreThanOneFile() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       
       List<InputPart> dirs = new ArrayList<InputPart>();
       List<InputPart> files = new ArrayList<InputPart>();
       
       String userTest = "userTest";
       String dirTest = "//dir";
       String fileUploads = "filename=file1.csv";
       String fileUploads2 = "filename=file2.xml";
       com.imath.core.model.File fileRoot = new com.imath.core.model.File();
       fileRoot.setUrl("root");
       com.imath.core.model.File fileNoRoot = new com.imath.core.model.File();
       fileNoRoot.setUrl("root/dir");
       
       dirs.add(inputDir); 
       files.add(inputFile);
       files.add(inputFile2);
       inputMap.put(uploadedFile, files);
       //inputMap.put(destinationDir, dirs);        // We do not put the directory,
       when(input.getFormDataMap()).thenReturn(inputMap);
       
       when(principal.getName()).thenReturn(userTest);
       //when(inputDir.getBodyAsString()).thenReturn(dirTest);
       when(inputFile.getHeaders()).thenReturn(header);
       when(inputFile2.getHeaders()).thenReturn(header2);
       when(header.getFirst("Content-Disposition")).thenReturn(fileUploads);
       when(header2.getFirst("Content-Disposition")).thenReturn(fileUploads2);
       when(inputFile.getBody(InputStream.class,null)).thenReturn(inputStream);
       when(inputFile2.getBody(InputStream.class,null)).thenReturn(inputStream2);
       when(fileController.createNewFileInROOTDirectory(Matchers.anyString(), (SecurityContext) Matchers.anyObject())).thenReturn(fileRoot);
       when(fileController.createNewFileInDirectory((com.imath.core.model.File)Matchers.anyObject(), Matchers.anyString(),Matchers.anyString())).thenReturn(fileNoRoot);
       
       List<PublicResponse.StateDTO> response = data.REST_uploadData(input, sc);
       
       assertTrue(response!=null);
       assertTrue(response.size()==2);
       assertTrue(response.get(0).status.code == PublicResponse.Status.READY.getValue());
       assertTrue(response.get(0).code == Response.Status.ACCEPTED.getStatusCode());
       assertTrue(response.get(1).status.code == PublicResponse.Status.READY.getValue());
       assertTrue(response.get(1).code == Response.Status.ACCEPTED.getStatusCode());
       
       verify(fileController, times(0)).getDir(dirTest,userTest);
       verify(fileController).createNewFileInROOTDirectory(Matchers.eq("file1.csv"), (SecurityContext)Matchers.anyObject());
       verify(fileController).createNewFileInROOTDirectory(Matchers.eq("file2.xml"), (SecurityContext)Matchers.anyObject());
       verify(fileUtils, times(2)).writeFile((byte [])Matchers.any(), Matchers.eq(fileRoot.getUrl()));
    }
    
    /*
	 * TEST THE ADVANCED REST CALL TO UPLOAD FILE
	 * 
	 */
    
    @Test
	// Test the base cases of REST_uploadFileAdvanced method when inputMap is empty (it does not contain any tag)
	public void test_uploadFilesAdvanced_EmptyInput() throws Exception {
	   Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
	   // The case when no files are uploaded
	   
	   when(input.getFormDataMap()).thenReturn(inputMap);
	   when(principal.getName()).thenReturn("userTest");
	   	   
	   List<PublicResponse.StateDTO> response = data.REST_uploadDataAdvanced(input, sc);
	   assertTrue(response!=null);
	   assertTrue(response.size()==0);
	}

   @Test
    // Test the base cases of REST_uploadFileAdvanced method when inputMap contains numberFiles, but is emoty.
    public void test_uploadFilesAdvanced_EmptyNumberFiles() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       // The case when no files are uploaded
       inputMap.put(numberFiles, new ArrayList<InputPart>());
       when(input.getFormDataMap()).thenReturn(inputMap);
       when(principal.getName()).thenReturn("userTest");
       
       List<PublicResponse.StateDTO> response = data.REST_uploadDataAdvanced(input, sc);
       assertTrue(response!=null);
       assertTrue(response.size()==0);
    }
    
    @Test
    // Test the base cases of REST_uploadFileAdvanced method when inputMap contains a specific numberFiles X, but uploadedFile-? is not specified.
    public void test_uploadFilesAdvanced_NoSpecifiedUploadedFile() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       
       List<InputPart> nFiles = new ArrayList<InputPart>();
       
       String number_of_files = "1";
       nFiles.add(inputnumberFile);      
           
       // The case when no files are uploaded
       inputMap.put(numberFiles, nFiles);
       when(input.getFormDataMap()).thenReturn(inputMap);
       when(principal.getName()).thenReturn("userTest");
       when(inputnumberFile.getBodyAsString()).thenReturn(number_of_files);
       
       List<PublicResponse.StateDTO> response = data.REST_uploadDataAdvanced(input, sc);
       assertTrue(response!=null);      
       assertTrue(response.size()==1);
       assertTrue(response.get(0).status.code == PublicResponse.Status.FAIL.getValue());
       assertTrue(response.get(0).code == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
      
    }
    
    @Test
    // Test the base cases of REST_uploadFileAdvanced method when inputMap contains a specific numberFiles X, but uploadedFile-? is empty.
    public void test_uploadFilesAdvanced_EmptyUploadedFile() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       
       List<InputPart> nFiles = new ArrayList<InputPart>();
       
       String number_of_files = "1";
       nFiles.add(inputnumberFile);      
           
       // The case when no files are uploaded
       inputMap.put(uploadedFile_1, new ArrayList<InputPart>());
       inputMap.put(numberFiles, nFiles);
       when(input.getFormDataMap()).thenReturn(inputMap);
       when(principal.getName()).thenReturn("userTest");
       when(inputnumberFile.getBodyAsString()).thenReturn(number_of_files);
       
       List<PublicResponse.StateDTO> response = data.REST_uploadDataAdvanced(input, sc);
       assertTrue(response!=null);      
       assertTrue(response.size()==1);
       assertTrue(response.get(0).status.code == PublicResponse.Status.FAIL.getValue());
       assertTrue(response.get(0).code == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            
    }
    
     @Test
    // Test the base cases of REST_uploadFileAdvanced method when inputMap contains a specific numberFiles X, but uploadedFile-id is specified more than one.
    public void test_uploadFilesAdvanced_DuplicateUploadFile_id() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       
       List<InputPart> nFiles = new ArrayList<InputPart>();     
       String number_of_files = "2";
       nFiles.add(inputnumberFile);      
      
       List<InputPart> files = new ArrayList<InputPart>();        
       List<InputPart> dir = new ArrayList<InputPart>();
             
       String userTest = "userTest";
       String dirTest1 = "//dir";
       String fileUploads1 = "filename=file1.csv";
       String fileUploads2 = "filename=file2.xml";
       com.imath.core.model.File fileNoRoot1 = new com.imath.core.model.File();
       fileNoRoot1.setUrl("//dir/file1.csv");
       com.imath.core.model.File fileNoRoot2 = new com.imath.core.model.File();
       fileNoRoot2.setUrl("//dir/file2.xml");
       com.imath.core.model.File dirNoRoot = new com.imath.core.model.File();
       dirNoRoot.setUrl("//dir");
       
       
       nFiles.add(inputnumberFile); 
       dir.add(inputDir);
       files.add(inputFile);
       files.add(inputFile2);
       inputMap.put(numberFiles, nFiles);
       inputMap.put(uploadedFile_1, files);
       inputMap.put(destinationDir_1, dir);
     
      
       when(input.getFormDataMap()).thenReturn(inputMap);
       when(inputnumberFile.getBodyAsString()).thenReturn(number_of_files);  
       when(principal.getName()).thenReturn(userTest);
       when(inputDir.getBodyAsString()).thenReturn(dirTest1);
       when(inputFile.getHeaders()).thenReturn(header);
       when(inputFile2.getHeaders()).thenReturn(header2);
       when(header.getFirst("Content-Disposition")).thenReturn(fileUploads1);
       when(header2.getFirst("Content-Disposition")).thenReturn(fileUploads2);
       when(inputFile.getBody(InputStream.class,null)).thenReturn(inputStream);
       when(inputFile2.getBody(InputStream.class,null)).thenReturn(inputStream2);
       
       when(fileController.writeFileFromUpload((byte[]) Matchers.any(), Matchers.eq("//dir/file1.csv"), Matchers.eq(userTest), Matchers.eq("file1.csv"), Matchers.eq("csv"))).thenReturn(fileNoRoot1);
       when(fileController.writeFileFromUpload((byte[]) Matchers.any(), Matchers.eq("//dir/file2.xml"), Matchers.eq(userTest), Matchers.eq("file2.xml"), Matchers.eq("xml"))).thenReturn(fileNoRoot2);
       //when(fileController.getParentDir("//dir/file1.csv", userTest)).thenReturn(dirNoRoot);
       //when(fileController.getParentDir("//dir/file2.xml", userTest)).thenReturn(dirNoRoot);
       //when(fileController.createNewFileInDirectory(dirNoRoot, "file1.csv","csv")).thenReturn(fileNoRoot1);
       //when(fileController.createNewFileInDirectory(dirNoRoot, "file2.xml","xml")).thenReturn(fileNoRoot2);
          
       List<PublicResponse.StateDTO> response = data.REST_uploadDataAdvanced(input, sc);
       
       assertTrue(response!=null);       
       assertTrue(response.size()==2);
       assertTrue(response.get(0).status.code == PublicResponse.Status.READY.getValue());
       assertTrue(response.get(0).code == Response.Status.ACCEPTED.getStatusCode());
       assertTrue(response.get(1).status.code == PublicResponse.Status.READY.getValue());
       assertTrue(response.get(1).code == Response.Status.ACCEPTED.getStatusCode());
            
    }
    
    @Test
    // Test REST_uploadFileAdvanced method when the inputMap contains no directory but one file. 
    // So, file is created under the ROOT : happy path
    public void test_uploadFilesAdvanced_NoDir_OneFile() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       
       List<InputPart> nFiles = new ArrayList<InputPart>();     
       String number_of_files = "1";
       nFiles.add(inputnumberFile); 
       
          
       List<InputPart> files = new ArrayList<InputPart>();
       files.add(inputFile);
       
       String userTest = "userTest";
       String dirTest = "//dir";
       String fileUploads = "filename=file1.csv";
       com.imath.core.model.File fileRoot = new com.imath.core.model.File();
       fileRoot.setUrl("root");
     
       inputMap.put(uploadedFile_1, files);
       inputMap.put(numberFiles, nFiles);
       
       when(input.getFormDataMap()).thenReturn(inputMap);     
       when(principal.getName()).thenReturn(userTest);
       when(inputnumberFile.getBodyAsString()).thenReturn(number_of_files);     
       when(inputFile.getHeaders()).thenReturn(header);
       when(header.getFirst("Content-Disposition")).thenReturn(fileUploads);
       when(inputFile.getBody(InputStream.class,null)).thenReturn(inputStream);
       
       when(fileController.writeFileFromUploadInROOT((byte[]) Matchers.any(),Matchers.eq("file1.csv"), (SecurityContext) Matchers.anyObject())).thenReturn(fileRoot);
       //when(fileController.createNewFileInROOTDirectory(Matchers.anyString(), (SecurityContext) Matchers.anyObject())).thenReturn(fileRoot);
       
       List<PublicResponse.StateDTO> response = data.REST_uploadDataAdvanced(input, sc);
       
       assertTrue(response!=null);
       assertTrue(response.size()==1);
       assertTrue(response.get(0).status.code == PublicResponse.Status.READY.getValue());
       assertTrue(response.get(0).code == Response.Status.ACCEPTED.getStatusCode());
       
       verify(fileController, times(0)).getDir(dirTest,userTest);
       //verify(fileController).createNewFileInROOTDirectory(Matchers.eq("file1.csv"), (SecurityContext)Matchers.anyObject());
       //verify(fileUtils).writeFile((byte [])Matchers.any(), Matchers.eq(fileRoot.getUrl()));
    }
   
    @Test
    // Test REST_uploadFileAdvanced method when the inputMap contains two directories and two file: happy path
    public void test_uploadFilesAdvanced_TwoDirs_TwoFiles() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       
       List<InputPart> nFiles = new ArrayList<InputPart>();     
       String number_of_files = "2";
       nFiles.add(inputnumberFile); 
       
       List<InputPart> dirs1 = new ArrayList<InputPart>();
       List<InputPart> files1 = new ArrayList<InputPart>();
       
       List<InputPart> dirs2 = new ArrayList<InputPart>();
       List<InputPart> files2 = new ArrayList<InputPart>();
       
       String userTest = "userTest";
       String dirTest1 = "//dir";
       String fileUploads1 = "filename=file1.csv";
       String dirTest2 = "//dir/dir2";
       String fileUploads2 = "filename=file2.xml";
       com.imath.core.model.File fileNoRoot1 = new com.imath.core.model.File();
       fileNoRoot1.setUrl("//dir/file1.csv");
       com.imath.core.model.File fileNoRoot2 = new com.imath.core.model.File();
       fileNoRoot2.setUrl("//dir/dir2/file2.xml");
       com.imath.core.model.File dirNoRoot1 = new com.imath.core.model.File();
       dirNoRoot1.setUrl("//dir");
       com.imath.core.model.File dirNoRoot2 = new com.imath.core.model.File();
       dirNoRoot2.setUrl("//dir/dir2");
       
       nFiles.add(inputnumberFile); 
       dirs1.add(inputDir);
       dirs2.add(inputDir2);
       files1.add(inputFile);
       files2.add(inputFile2);
       inputMap.put(numberFiles, nFiles);
       inputMap.put(uploadedFile_1, files1);
       inputMap.put(destinationDir_1, dirs1);
       inputMap.put(uploadedFile_2, files2);
       inputMap.put(destinationDir_2, dirs2);
       when(input.getFormDataMap()).thenReturn(inputMap);
       when(inputnumberFile.getBodyAsString()).thenReturn(number_of_files);  
       when(principal.getName()).thenReturn(userTest);
       when(inputDir.getBodyAsString()).thenReturn(dirTest1);
       when(inputDir2.getBodyAsString()).thenReturn(dirTest2);
       when(inputFile.getHeaders()).thenReturn(header);
       when(inputFile2.getHeaders()).thenReturn(header2);
       when(header.getFirst("Content-Disposition")).thenReturn(fileUploads1);
       when(header2.getFirst("Content-Disposition")).thenReturn(fileUploads2);
       when(inputFile.getBody(InputStream.class,null)).thenReturn(inputStream);
       when(inputFile2.getBody(InputStream.class,null)).thenReturn(inputStream2);
       
       when(fileController.writeFileFromUpload((byte[]) Matchers.any(), Matchers.eq("//dir/file1.csv"), Matchers.eq(userTest), Matchers.eq("file1.csv"), Matchers.eq("csv"))).thenReturn(fileNoRoot1);
       when(fileController.writeFileFromUpload((byte[]) Matchers.any(), Matchers.eq("//dir/dir2/file2.xml"), Matchers.eq(userTest), Matchers.eq("file2.xml"), Matchers.eq("xml"))).thenReturn(fileNoRoot2);

       //when(fileController.getParentDir("//dir/file1.csv", userTest)).thenReturn(dirNoRoot1);
       //when(fileController.getParentDir("//dir/dir2/file2.xml", userTest)).thenReturn(dirNoRoot2);
       //when(fileController.createNewFileInDirectory(dirNoRoot1, "file1.csv","csv")).thenReturn(fileNoRoot1);
       //when(fileController.createNewFileInDirectory(dirNoRoot2, "file2.xml", "xml")).thenReturn(fileNoRoot2);
       
       List<PublicResponse.StateDTO> response = data.REST_uploadDataAdvanced(input, sc);
       
       assertTrue(response!=null);
       assertTrue(response.size()==2);
       assertTrue(response.get(0).status.code == PublicResponse.Status.READY.getValue());
       assertTrue(response.get(0).code == Response.Status.ACCEPTED.getStatusCode());
       assertTrue(response.get(1).status.code == PublicResponse.Status.READY.getValue());
       assertTrue(response.get(1).code == Response.Status.ACCEPTED.getStatusCode());
       
       
       /* Not necessary. For sure it is executed, if not, status.code would not be READY
       verify(fileController, times(1)).getParentDir("//dir/file1.csv",userTest);
       verify(fileController, times(1)).getParentDir("//dir/dir2/file2.xml",userTest);
       verify(fileController, times(1)).createNewFileInDirectory((com.imath.core.model.File) Matchers.anyObject(),Matchers.eq("file1.csv"), Matchers.eq("csv"));
       verify(fileController, times(1)).createNewFileInDirectory((com.imath.core.model.File) Matchers.anyObject(),Matchers.eq("file2.xml"), Matchers.eq("xml"));
       verify(fileUtils).writeFile((byte [])Matchers.any(), Matchers.eq(fileNoRoot1.getUrl()));
       verify(fileUtils).writeFile((byte [])Matchers.any(), Matchers.eq(fileNoRoot2.getUrl()));
       */
    }
    
    @Test
    // BUG#22: Test REST_uploadFileAdvanced method when the inputMap contains two directories and two file: happy path with exception: One file produces exception
    public void test_uploadFilesAdvanced_TwoDirs_TwoFiles_Exception() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       
       List<InputPart> nFiles = new ArrayList<InputPart>();     
       String number_of_files = "2";
       nFiles.add(inputnumberFile); 
       
       List<InputPart> dirs1 = new ArrayList<InputPart>();
       List<InputPart> files1 = new ArrayList<InputPart>();
       
       List<InputPart> dirs2 = new ArrayList<InputPart>();
       List<InputPart> files2 = new ArrayList<InputPart>();
       
       String userTest = "userTest";
       String dirTest1 = "//dir";
       String fileUploads1 = "filename=file1.csv";
       String dirTest2 = "//dir/dir2";
       String fileUploads2 = "filename=file2.xml";
       com.imath.core.model.File fileNoRoot1 = new com.imath.core.model.File();
       fileNoRoot1.setUrl("//dir/file1.csv");
       com.imath.core.model.File fileNoRoot2 = new com.imath.core.model.File();
       fileNoRoot2.setUrl("//dir/dir2/file2.xml");
       com.imath.core.model.File dirNoRoot1 = new com.imath.core.model.File();
       dirNoRoot1.setUrl("//dir");
       com.imath.core.model.File dirNoRoot2 = new com.imath.core.model.File();
       dirNoRoot2.setUrl("//dir/dir2");
       
       nFiles.add(inputnumberFile); 
       dirs1.add(inputDir);
       dirs2.add(inputDir2);
       files1.add(inputFile);
       files2.add(inputFile2);
       inputMap.put(numberFiles, nFiles);
       inputMap.put(uploadedFile_1, files1);
       inputMap.put(destinationDir_1, dirs1);
       inputMap.put(uploadedFile_2, files2);
       inputMap.put(destinationDir_2, dirs2);
       when(input.getFormDataMap()).thenReturn(inputMap);
       when(inputnumberFile.getBodyAsString()).thenReturn(number_of_files);  
       when(principal.getName()).thenReturn(userTest);
       when(inputDir.getBodyAsString()).thenReturn(dirTest1);
       when(inputDir2.getBodyAsString()).thenReturn(dirTest2);
       when(inputFile.getHeaders()).thenReturn(header);
       when(inputFile2.getHeaders()).thenReturn(header2);
       when(header.getFirst("Content-Disposition")).thenReturn(fileUploads1);
       when(header2.getFirst("Content-Disposition")).thenReturn(fileUploads2);
       when(inputFile.getBody(InputStream.class,null)).thenReturn(inputStream);
       when(inputFile2.getBody(InputStream.class,null)).thenReturn(inputStream2);
       
       when(fileController.writeFileFromUpload((byte[]) Matchers.any(), Matchers.eq("//dir/file1.csv"), Matchers.eq(userTest), Matchers.eq("file1.csv"), Matchers.eq("csv"))).thenThrow(new Exception());
       when(fileController.writeFileFromUpload((byte[]) Matchers.any(), Matchers.eq("//dir/dir2/file2.xml"), Matchers.eq(userTest), Matchers.eq("file2.xml"), Matchers.eq("xml"))).thenReturn(fileNoRoot2);

       //when(fileController.getParentDir("//dir/file1.csv", userTest)).thenReturn(dirNoRoot1);
       //when(fileController.getParentDir("//dir/dir2/file2.xml", userTest)).thenReturn(dirNoRoot2);
       //when(fileController.createNewFileInDirectory(dirNoRoot1, "file1.csv","csv")).thenReturn(fileNoRoot1);
       //when(fileController.createNewFileInDirectory(dirNoRoot2, "file2.xml", "xml")).thenReturn(fileNoRoot2);
       
       List<PublicResponse.StateDTO> response = data.REST_uploadDataAdvanced(input, sc);
       
       assertTrue(response!=null);
       assertTrue(response.size()==2);
       assertTrue(response.get(0).status.code == PublicResponse.Status.FAIL.getValue());
       assertTrue(response.get(0).code == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
       assertTrue(response.get(1).status.code == PublicResponse.Status.READY.getValue());
       assertTrue(response.get(1).code == Response.Status.ACCEPTED.getStatusCode());
       
       
       /* Not necessary. For sure it is executed, if not, status.code would not be READY
       verify(fileController, times(1)).getParentDir("//dir/file1.csv",userTest);
       verify(fileController, times(1)).getParentDir("//dir/dir2/file2.xml",userTest);
       verify(fileController, times(1)).createNewFileInDirectory((com.imath.core.model.File) Matchers.anyObject(),Matchers.eq("file1.csv"), Matchers.eq("csv"));
       verify(fileController, times(1)).createNewFileInDirectory((com.imath.core.model.File) Matchers.anyObject(),Matchers.eq("file2.xml"), Matchers.eq("xml"));
       verify(fileUtils).writeFile((byte [])Matchers.any(), Matchers.eq(fileNoRoot1.getUrl()));
       verify(fileUtils).writeFile((byte [])Matchers.any(), Matchers.eq(fileNoRoot2.getUrl()));
       */
    }
    
    @Test
    // Test REST_uploadFileAdvanced method when the inputMap contains only one directories and two files: happy path
    // So, one file will be stored below root
    public void test_uploadFilesAdvanced_OneDirs_TwoFiles() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       
       List<InputPart> nFiles = new ArrayList<InputPart>();     
       String number_of_files = "2";
       nFiles.add(inputnumberFile); 
       
       List<InputPart> dirs1 = new ArrayList<InputPart>();
       List<InputPart> files1 = new ArrayList<InputPart>();
       
       List<InputPart> files2 = new ArrayList<InputPart>();
       
       String userTest = "userTest";
       String dirTest1 = "//dir";
       String fileUploads1 = "filename=file1.csv";
       String fileUploads2 = "filename=file2.xml";
       com.imath.core.model.File fileNoRoot = new com.imath.core.model.File();
       fileNoRoot.setUrl("//dir/file1.csv");
       com.imath.core.model.File fileRoot = new com.imath.core.model.File();
       fileRoot.setUrl("//file2.xml");
       com.imath.core.model.File dirNoRoot = new com.imath.core.model.File();
       dirNoRoot.setUrl("//dir");
       
       
       nFiles.add(inputnumberFile); 
       dirs1.add(inputDir);
       files1.add(inputFile);
       files2.add(inputFile2);
       inputMap.put(numberFiles, nFiles);
       inputMap.put(uploadedFile_1, files1);
       inputMap.put(destinationDir_1, dirs1);
       inputMap.put(uploadedFile_2, files2);
      
       when(input.getFormDataMap()).thenReturn(inputMap);
       when(inputnumberFile.getBodyAsString()).thenReturn(number_of_files);  
       when(principal.getName()).thenReturn(userTest);
       when(inputDir.getBodyAsString()).thenReturn(dirTest1);
       when(inputFile.getHeaders()).thenReturn(header);
       when(inputFile2.getHeaders()).thenReturn(header2);
       when(header.getFirst("Content-Disposition")).thenReturn(fileUploads1);
       when(header2.getFirst("Content-Disposition")).thenReturn(fileUploads2);
       when(inputFile.getBody(InputStream.class,null)).thenReturn(inputStream);
       when(inputFile2.getBody(InputStream.class,null)).thenReturn(inputStream2);

       when(fileController.writeFileFromUpload((byte[]) Matchers.any(), Matchers.eq("//dir/file1.csv"), Matchers.eq(userTest), Matchers.eq("file1.csv"), Matchers.eq("csv"))).thenReturn(fileNoRoot);
       
       //when(fileController.getParentDir("//dir/file1.csv", userTest)).thenReturn(dirNoRoot);
       //when(fileController.createNewFileInDirectory(dirNoRoot, "file1.csv","csv")).thenReturn(fileNoRoot);
       when(fileController.writeFileFromUploadInROOT((byte[]) Matchers.any(),Matchers.eq("file2.xml"), (SecurityContext) Matchers.anyObject())).thenReturn(fileRoot);
       
       //when(fileController.createNewFileInROOTDirectory(Matchers.anyString(), (SecurityContext)Matchers.anyObject())).thenReturn(fileRoot);
       
       List<PublicResponse.StateDTO> response = data.REST_uploadDataAdvanced(input, sc);
       
       assertTrue(response!=null);
       assertTrue(response.size()==2);
       assertTrue(response.get(0).status.code == PublicResponse.Status.READY.getValue());
       assertTrue(response.get(0).code == Response.Status.ACCEPTED.getStatusCode());
       assertTrue(response.get(1).status.code == PublicResponse.Status.READY.getValue());
       assertTrue(response.get(1).code == Response.Status.ACCEPTED.getStatusCode());
       
       //verify(fileController, times(1)).getParentDir("//dir/file1.csv",userTest);       
       //verify(fileController, times(1)).createNewFileInDirectory((com.imath.core.model.File) Matchers.anyObject(),Matchers.eq("file1.csv"), Matchers.eq("csv"));
       //verify(fileController).createNewFileInROOTDirectory(Matchers.eq("file2.xml"), (SecurityContext)Matchers.anyObject());
       //verify(fileUtils).writeFile((byte [])Matchers.any(), Matchers.eq(fileNoRoot.getUrl()));
       //verify(fileUtils).writeFile((byte [])Matchers.any(), Matchers.eq(fileRoot.getUrl()));
    }
    

    @Test
    // BUG#20: Test REST_uploadFileAdvanced method when the inputMap contains only one directories and two files: happy path with exception
    // the file to be stored in ROOT throws an exeption
    public void test_uploadFilesAdvanced_OneDirs_TwoFiles_Exception() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       
       List<InputPart> nFiles = new ArrayList<InputPart>();     
       String number_of_files = "2";
       nFiles.add(inputnumberFile); 
       
       List<InputPart> dirs1 = new ArrayList<InputPart>();
       List<InputPart> files1 = new ArrayList<InputPart>();
       
       List<InputPart> files2 = new ArrayList<InputPart>();
       
       String userTest = "userTest";
       String dirTest1 = "//dir";
       String fileUploads1 = "filename=file1.csv";
       String fileUploads2 = "filename=file2.xml";
       com.imath.core.model.File fileNoRoot = new com.imath.core.model.File();
       fileNoRoot.setUrl("//dir/file1.csv");
       com.imath.core.model.File fileRoot = new com.imath.core.model.File();
       fileRoot.setUrl("//file2.xml");
       com.imath.core.model.File dirNoRoot = new com.imath.core.model.File();
       dirNoRoot.setUrl("//dir");
       
       
       nFiles.add(inputnumberFile); 
       dirs1.add(inputDir);
       files1.add(inputFile);
       files2.add(inputFile2);
       inputMap.put(numberFiles, nFiles);
       inputMap.put(uploadedFile_1, files1);
       inputMap.put(destinationDir_1, dirs1);
       inputMap.put(uploadedFile_2, files2);
      
       when(input.getFormDataMap()).thenReturn(inputMap);
       when(inputnumberFile.getBodyAsString()).thenReturn(number_of_files);  
       when(principal.getName()).thenReturn(userTest);
       when(inputDir.getBodyAsString()).thenReturn(dirTest1);
       when(inputFile.getHeaders()).thenReturn(header);
       when(inputFile2.getHeaders()).thenReturn(header2);
       when(header.getFirst("Content-Disposition")).thenReturn(fileUploads1);
       when(header2.getFirst("Content-Disposition")).thenReturn(fileUploads2);
       when(inputFile.getBody(InputStream.class,null)).thenReturn(inputStream);
       when(inputFile2.getBody(InputStream.class,null)).thenReturn(inputStream2);

       when(fileController.writeFileFromUpload((byte[]) Matchers.any(), Matchers.eq("//dir/file1.csv"), Matchers.eq(userTest), Matchers.eq("file1.csv"), Matchers.eq("csv"))).thenReturn(fileNoRoot);
       
       //when(fileController.getParentDir("//dir/file1.csv", userTest)).thenReturn(dirNoRoot);
       //when(fileController.createNewFileInDirectory(dirNoRoot, "file1.csv","csv")).thenReturn(fileNoRoot);
       when(fileController.writeFileFromUploadInROOT((byte[]) Matchers.any(),Matchers.eq("file2.xml"), (SecurityContext) Matchers.anyObject())).thenThrow(new Exception());
       
       //when(fileController.createNewFileInROOTDirectory(Matchers.anyString(), (SecurityContext)Matchers.anyObject())).thenReturn(fileRoot);
       
       List<PublicResponse.StateDTO> response = data.REST_uploadDataAdvanced(input, sc);
       
       assertTrue(response!=null);
       assertTrue(response.size()==2);
       assertTrue(response.get(0).status.code == PublicResponse.Status.READY.getValue());
       assertTrue(response.get(0).code == Response.Status.ACCEPTED.getStatusCode());
       assertTrue(response.get(1).status.code == PublicResponse.Status.FAIL.getValue());
       assertTrue(response.get(1).code == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
       
       //verify(fileController, times(1)).getParentDir("//dir/file1.csv",userTest);       
       //verify(fileController, times(1)).createNewFileInDirectory((com.imath.core.model.File) Matchers.anyObject(),Matchers.eq("file1.csv"), Matchers.eq("csv"));
       //verify(fileController).createNewFileInROOTDirectory(Matchers.eq("file2.xml"), (SecurityContext)Matchers.anyObject());
       //verify(fileUtils).writeFile((byte [])Matchers.any(), Matchers.eq(fileNoRoot.getUrl()));
       //verify(fileUtils).writeFile((byte [])Matchers.any(), Matchers.eq(fileRoot.getUrl()));
    }

    
    @Test
    // Test REST_uploadFileAdvanced method when the specified number of files is less than the real files to be uploaded 
    public void test_uploadFilesAdvanced_OneDirs_TwoFiles_LessNumberFiles() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       
       List<InputPart> nFiles = new ArrayList<InputPart>();     
       String number_of_files = "1";
       nFiles.add(inputnumberFile); 
       
       List<InputPart> dirs1 = new ArrayList<InputPart>();
       List<InputPart> files1 = new ArrayList<InputPart>();
       
       List<InputPart> files2 = new ArrayList<InputPart>();
       
       String userTest = "userTest";
       String dirTest1 = "//dir";
       String fileUploads1 = "filename=file1.csv";
       String fileUploads2 = "filename=file2.xml";
       com.imath.core.model.File fileNoRoot = new com.imath.core.model.File();
       fileNoRoot.setUrl("//dir/file1.csv");
       com.imath.core.model.File fileRoot = new com.imath.core.model.File();
       fileRoot.setUrl("//file2.xml");
       com.imath.core.model.File dirNoRoot = new com.imath.core.model.File();
       dirNoRoot.setUrl("//dir");
       
       
       nFiles.add(inputnumberFile); 
       dirs1.add(inputDir);
       files1.add(inputFile);
       files2.add(inputFile2);
       inputMap.put(numberFiles, nFiles);
       inputMap.put(uploadedFile_1, files1);
       inputMap.put(destinationDir_1, dirs1);
       inputMap.put(uploadedFile_2, files2);
      
       when(input.getFormDataMap()).thenReturn(inputMap);
       when(inputnumberFile.getBodyAsString()).thenReturn(number_of_files);  
       when(principal.getName()).thenReturn(userTest);
       when(inputDir.getBodyAsString()).thenReturn(dirTest1);
       when(inputFile.getHeaders()).thenReturn(header);
       when(inputFile2.getHeaders()).thenReturn(header2);
       when(header.getFirst("Content-Disposition")).thenReturn(fileUploads1);
       when(header2.getFirst("Content-Disposition")).thenReturn(fileUploads2);
       when(inputFile.getBody(InputStream.class,null)).thenReturn(inputStream);
       when(inputFile2.getBody(InputStream.class,null)).thenReturn(inputStream2);

       when(fileController.writeFileFromUpload((byte[]) Matchers.any(), Matchers.eq("//dir/file1.csv"), Matchers.eq(userTest), Matchers.eq("file1.csv"), Matchers.eq("csv"))).thenReturn(fileNoRoot);
       //when(fileController.getParentDir("//dir/file1.csv", userTest)).thenReturn(dirNoRoot);
       //when(fileController.createNewFileInDirectory(dirNoRoot, "file1.csv","csv")).thenReturn(fileNoRoot);
       when(fileController.createNewFileInROOTDirectory(Matchers.anyString(), (SecurityContext)Matchers.anyObject())).thenReturn(fileRoot);
       
       List<PublicResponse.StateDTO> response = data.REST_uploadDataAdvanced(input, sc);
       
       assertTrue(response!=null);
       assertTrue(response.size()==1);
       assertTrue(response.get(0).status.code == PublicResponse.Status.READY.getValue());
       assertTrue(response.get(0).code == Response.Status.ACCEPTED.getStatusCode());
       
       //verify(fileController, times(1)).getParentDir("//dir/file1.csv",userTest);     
       //verify(fileController, times(1)).createNewFileInDirectory((com.imath.core.model.File) Matchers.anyObject(),Matchers.eq("file1.csv"), Matchers.eq("csv"));
       verify(fileController, times(0)).createNewFileInROOTDirectory(Matchers.eq("file2.xml"), (SecurityContext)Matchers.anyObject());
       //verify(fileUtils).writeFile((byte [])Matchers.any(), Matchers.eq(fileNoRoot.getUrl()));
       verify(fileUtils, times(0)).writeFile((byte [])Matchers.any(), Matchers.eq(fileRoot.getUrl()));
    }
    
    
   @Test
    // Test REST_uploadFileAdvanced method when the specified number of files is more than the real files to be uploaded 
    public void test_uploadFilesAdvanced_OneDirs_TwoFiles_MoreNumberFiles() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       
       List<InputPart> nFiles = new ArrayList<InputPart>();     
       String number_of_files = "3";
       nFiles.add(inputnumberFile); 
       
       List<InputPart> dirs1 = new ArrayList<InputPart>();
       List<InputPart> files1 = new ArrayList<InputPart>();
       
       List<InputPart> files2 = new ArrayList<InputPart>();
       
       String userTest = "userTest";
       String dirTest1 = "//dir";
       String fileUploads1 = "filename=file1.csv";
       String fileUploads2 = "filename=file2.xml";
       com.imath.core.model.File fileNoRoot = new com.imath.core.model.File();
       fileNoRoot.setUrl("//dir/file1.csv");
       com.imath.core.model.File fileRoot = new com.imath.core.model.File();
       fileRoot.setUrl("//file2.xml");
       com.imath.core.model.File dirNoRoot = new com.imath.core.model.File();
       dirNoRoot.setUrl("//dir");
       
       
       nFiles.add(inputnumberFile); 
       dirs1.add(inputDir);
       files1.add(inputFile);
       files2.add(inputFile2);
       inputMap.put(numberFiles, nFiles);
       inputMap.put(uploadedFile_1, files1);
       inputMap.put(destinationDir_1, dirs1);
       inputMap.put(uploadedFile_2, files2);
      
       when(input.getFormDataMap()).thenReturn(inputMap);
       when(inputnumberFile.getBodyAsString()).thenReturn(number_of_files);  
       when(principal.getName()).thenReturn(userTest);
       when(inputDir.getBodyAsString()).thenReturn(dirTest1);
       when(inputFile.getHeaders()).thenReturn(header);
       when(inputFile2.getHeaders()).thenReturn(header2);
       when(header.getFirst("Content-Disposition")).thenReturn(fileUploads1);
       when(header2.getFirst("Content-Disposition")).thenReturn(fileUploads2);
       when(inputFile.getBody(InputStream.class,null)).thenReturn(inputStream);
       when(inputFile2.getBody(InputStream.class,null)).thenReturn(inputStream2);
       
       when(fileController.writeFileFromUploadInROOT((byte[]) Matchers.any(),Matchers.eq("file2.xml"), (SecurityContext) Matchers.anyObject())).thenReturn(fileRoot);
       when(fileController.writeFileFromUpload((byte[]) Matchers.any(), Matchers.eq("//dir/file1.csv"), Matchers.eq(userTest), Matchers.eq("file1.csv"), Matchers.eq("csv"))).thenReturn(fileNoRoot);
       //when(fileController.getParentDir("//dir/file1.csv", userTest)).thenReturn(dirNoRoot);
       //when(fileController.createNewFileInDirectory(dirNoRoot, "file1.csv","csv")).thenReturn(fileNoRoot);
       //when(fileController.createNewFileInROOTDirectory(Matchers.anyString(), (SecurityContext)Matchers.anyObject())).thenReturn(fileRoot);
       
       List<PublicResponse.StateDTO> response = data.REST_uploadDataAdvanced(input, sc);
       
       assertTrue(response!=null);       
       assertTrue(response.size()==3);      
       assertTrue(response.get(0).status.code == PublicResponse.Status.READY.getValue());
       assertTrue(response.get(0).code == Response.Status.ACCEPTED.getStatusCode());
       assertTrue(response.get(1).status.code == PublicResponse.Status.READY.getValue());
       assertTrue(response.get(1).code == Response.Status.ACCEPTED.getStatusCode());
       assertTrue(response.get(2).status.code == PublicResponse.Status.FAIL.getValue());
       assertTrue(response.get(2).code == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
       
       //verify(fileController, times(1)).getParentDir("//dir/file1.csv",userTest);     
       //verify(fileController, times(1)).createNewFileInDirectory((com.imath.core.model.File) Matchers.anyObject(),Matchers.eq("file1.csv"), Matchers.eq("csv"));
       //verify(fileController, times(1)).createNewFileInROOTDirectory(Matchers.eq("file2.xml"), (SecurityContext)Matchers.anyObject());
       //verify(fileUtils).writeFile((byte [])Matchers.any(), Matchers.eq(fileNoRoot.getUrl()));
       //verify(fileUtils).writeFile((byte [])Matchers.any(), Matchers.eq(fileRoot.getUrl()));
    }
    
    
    @Test
    // Test the base cases of REST_uploadFileAdvanced method when inputMap contains a specific numberFiles X, but destinationDir-id is specified more than one.
    public void test_uploadFilesAdvanced_DuplicateDestinationDir_id() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       
       List<InputPart> nFiles = new ArrayList<InputPart>();     
       String number_of_files = "1";
       nFiles.add(inputnumberFile);      
      
       List<InputPart> files = new ArrayList<InputPart>();  
       String fileUploads = "filename=file1.csv";
       files.add(inputFile);
       
       List<InputPart> dir = new ArrayList<InputPart>();  
       dir.add(inputDir);
       dir.add(inputDir);
       
           
       // The case when no files are uploaded
       inputMap.put(destinationDir_1, dir);
       inputMap.put(uploadedFile_1, files);
       inputMap.put(numberFiles, nFiles);
       when(input.getFormDataMap()).thenReturn(inputMap);
       when(principal.getName()).thenReturn("userTest");
       when(inputnumberFile.getBodyAsString()).thenReturn(number_of_files);       
       when(inputFile.getHeaders()).thenReturn(header);     
       when(header.getFirst("Content-Disposition")).thenReturn(fileUploads); 
       when(inputFile.getBody(InputStream.class,null)).thenReturn(inputStream);
       
       
       List<PublicResponse.StateDTO> response = data.REST_uploadDataAdvanced(input, sc);
       assertTrue(response!=null);
       
       assertTrue(response.size()==1);
       assertTrue(response.get(0).status.code == PublicResponse.Status.FAIL.getValue());
       assertTrue(response.get(0).code == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            
    }
    
    
    @Test
    // Test REST_uploadFileAdvanced method when the inputMap contains only one directory and one file: happy path
    public void test_uploadFilesAdvanced_OneDir_OneFile() throws Exception {
       Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
       
       List<InputPart> nFiles = new ArrayList<InputPart>();     
       String number_of_files = "1";
       nFiles.add(inputnumberFile); 
       
       List<InputPart> dirs = new ArrayList<InputPart>();
       List<InputPart> files = new ArrayList<InputPart>();
       
       String userTest = "userTest";
       String dirTest = "//dir";
       String fileUploads = "filename=file1.csv";
       com.imath.core.model.File fileNoRoot = new com.imath.core.model.File();
       fileNoRoot.setUrl("//dir/file1.csv");
       com.imath.core.model.File dirNoRoot = new com.imath.core.model.File();
       dirNoRoot.setUrl(dirTest);
       
       nFiles.add(inputnumberFile); 
       dirs.add(inputDir);
       files.add(inputFile);
       inputMap.put(numberFiles, nFiles);
       inputMap.put(uploadedFile_1, files);
       inputMap.put(destinationDir_1, dirs);
       when(input.getFormDataMap()).thenReturn(inputMap);
       when(inputnumberFile.getBodyAsString()).thenReturn(number_of_files);  
       when(principal.getName()).thenReturn(userTest);
       when(inputDir.getBodyAsString()).thenReturn(dirTest);
       when(inputFile.getHeaders()).thenReturn(header);
       when(header.getFirst("Content-Disposition")).thenReturn(fileUploads);
       when(inputFile.getBody(InputStream.class,null)).thenReturn(inputStream);
       
       when(fileController.writeFileFromUpload((byte[]) Matchers.any(), Matchers.eq("//dir/file1.csv"), Matchers.eq(userTest), Matchers.eq("file1.csv"), Matchers.eq("csv"))).thenReturn(fileNoRoot);
       //when(fileController.getParentDir("//dir/file1.csv", userTest)).thenReturn(dirNoRoot);
       //when(fileController.createNewFileInDirectory((com.imath.core.model.File)Matchers.anyObject(), Matchers.anyString(),Matchers.anyString())).thenReturn(fileNoRoot);
       
       List<PublicResponse.StateDTO> response = data.REST_uploadDataAdvanced(input, sc);
       
       assertTrue(response!=null);
       assertTrue(response.size()==1);
       assertTrue(response.get(0).status.code == PublicResponse.Status.READY.getValue());
       assertTrue(response.get(0).code == Response.Status.ACCEPTED.getStatusCode());
       
       //verify(fileController, times(1)).getParentDir("//dir/file1.csv",userTest);
       //verify(fileController).createNewFileInDirectory((com.imath.core.model.File) Matchers.anyObject(),Matchers.eq("file1.csv"), Matchers.eq("csv"));
       //verify(fileUtils).writeFile((byte [])Matchers.any(), Matchers.eq(fileNoRoot.getUrl()));
    }
   
    
    /*
   	 * TEST REST CALL TO DOWNLOAD A FILE/DIRECTORY INTO A ZIP FILE
   	 * 
   	 */
    
    @Test
    //The id/path of the file or directory to be uploaded are not specified
    public void test_download_filedirectoryNULL()throws Exception{   	
    	String path_file_directory = null;
    	String id_file_directory = null;
    	String zipFile = null;
    	
    	Response response = data.REST_download(path_file_directory, id_file_directory, zipFile, sc);
    	
    	assertTrue(response!=null);
        assertTrue(response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode());
    }
    
    @Test
    //The id/path of the file or directory to be uploaded are both specified specified
    public void test_download_filedirectoryNOTNULL()throws Exception{   	
    	String path_file_directory = "/path/file.txt";
    	String id_file_directory = "202";
    	String zipFile = null;
    	
    	Response response = data.REST_download(path_file_directory, id_file_directory, zipFile, sc);
    	
    	assertTrue(response!=null);
        assertTrue(response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode());
    }
    
    @Test
    //The id of the file or directory to be uploaded does not exist
    public void test_download_ID_filedirectoryNOTEXIST()throws Exception{   	
    	
    	String userTest = "userTest";
    	String path_file_directory = null;
    	String id_file_directory = "1";
    	Long idFile = Long.parseLong(id_file_directory);
    	String zipFile = null;
    	com.imath.core.model.File f_d_NULL = null;
    	
    	when(principal.getName()).thenReturn(userTest);
    	when(fileController.getFile(idFile, userTest)).thenReturn(f_d_NULL);    	
    	
    	Response response = data.REST_download(path_file_directory, id_file_directory, zipFile, sc);
    	
    	assertTrue(response!=null);
        assertTrue(response.getStatus() == Response.Status.NOT_FOUND.getStatusCode());
        
        verify(fileController, times(1)).getFile(idFile,userTest);
    }
    
    @Test
    //The path of the file or directory to be uploaded does not exist
    public void test_download_PATH_filedirectoryNOTEXIST()throws Exception{   	
    	
    	String userTest = "userTest";
    	String path_file_directory = "/path/file.txt";
    	String id_file_directory = null;
    	String zipFile = null;
    	com.imath.core.model.File f_d_NULL = null;
    	
    	when(principal.getName()).thenReturn(userTest);
    	when(fileController.checkIfFileExistInUser(path_file_directory, userTest)).thenReturn(f_d_NULL);
    	
    	Response response = data.REST_download(path_file_directory, id_file_directory, zipFile, sc);
    	
    	assertTrue(response!=null);
        assertTrue(response.getStatus() == Response.Status.NOT_FOUND.getStatusCode());
        
        verify(fileController, times(1)).checkIfFileExistInUser(path_file_directory,userTest);
    }
    
    @Test
    //An exception occurss in getFile
    public void test_download_filedirectory_GETEXCEPTION()throws Exception{   	
    	
    	String userTest = "userTest";
    	String id_file_directory = "1";
    	String path_file_directory = null;
    	Long idFile = Long.parseLong(id_file_directory);
    	String zipFile = null;
    	Exception e = new Exception ();
    	
    	when(principal.getName()).thenReturn(userTest);
    	when(fileController.getFile(idFile, userTest)).thenThrow(e);
    
    	try{
    		Response response = data.REST_download(path_file_directory, id_file_directory, zipFile, sc);
    	}
    	catch(WebApplicationException webexcep){
    		assertTrue(webexcep.getResponse().getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());    
    	}
    	       
        verify(fileController, times(1)).getFile(idFile, userTest);
    }
    
    @Test
    //An exception occurss in checkIfFileExistInUser
    public void test_download_filedirectory_CHECKEXCEPTION()throws Exception{   	
    	
    	String userTest = "userTest";
    	String path_file_directory = "//dir/file.txt";
    	String id_file_directory = null;
    	String zipFile = null;
    	Exception e = new Exception ();
    	
    	when(principal.getName()).thenReturn(userTest);
    	when(fileController.checkIfFileExistInUser(path_file_directory, userTest)).thenThrow(e);
    
    	try{
    		Response response = data.REST_download(path_file_directory,id_file_directory, zipFile, sc);
    	}
    	catch(WebApplicationException webexcep){
    		assertTrue(webexcep.getResponse().getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());    
    	}
    	       
        verify(fileController, times(1)).checkIfFileExistInUser(path_file_directory,userTest);
    }
    
    @Test
    //Given the id of the file, the zip is created using the name of the file, because zipFile is null
    //HAPPY PATH
    public void test_download_ID_filedirectory_GENERATEZIP_nullname()throws Exception{   	
    	
    	String userTest = "userTest";
    	String id_file_directory = "1";
    	Long idFile = Long.parseLong(id_file_directory);
    	String path_file_directory = null;
    	String zipFile = null;
    	com.imath.core.model.File f_d = new com.imath.core.model.File();
    	f_d.setName("file.txt");
    	f_d.setUrl("file://localhost/dir/file.txt");
    	
    	when(principal.getName()).thenReturn(userTest);
    	when(fileController.getFile(idFile, userTest)).thenReturn(f_d);
    	
    	Response response = data.REST_download(path_file_directory, id_file_directory, zipFile, sc);  
    	
    	assertTrue(response.getMetadata().get("Content-Disposition").get(0).equals("attachment; filename=\"file.txt.zip\"") );
	       
        verify(fileController, times(1)).getFile(idFile, userTest);
        
    }
    
    @Test
    //Given the namepath of the file, the zip is created using the name of the file, because zipFile is null
    //HAPPY PATH
    public void test_download_PATH_filedirectory_GENERATEZIP_nullname()throws Exception{   	
    	
    	String userTest = "userTest";
    	String path_file_directory = "/dir/file.txt";
    	String id_file_directory = null;
    	String zipFile = null;
    	com.imath.core.model.File f_d = new com.imath.core.model.File();
    	f_d.setName("file.txt");
    	f_d.setUrl("file://localhost/dir/file.txt");
    	
    	when(principal.getName()).thenReturn(userTest);
    	when(fileController.checkIfFileExistInUser(path_file_directory, userTest)).thenReturn(f_d);
    	
    	Response response = data.REST_download(path_file_directory, id_file_directory, zipFile, sc);  
    	
    	assertTrue(response.getMetadata().get("Content-Disposition").get(0).equals("attachment; filename=\"file.txt.zip\"") );
	       
        verify(fileController, times(1)).checkIfFileExistInUser(path_file_directory,userTest);
        
    }
    
    
    @Test
    //Given the id of the file, the zip is created using the name specified as parameter
    //HAPPY PATH
    public void test_download_ID_filedirectory_GENERATEZIP_specifiedname()throws Exception{   	
    	
    	String userTest = "userTest";
    	String id_file_directory = "1";
    	Long idFile = Long.parseLong(id_file_directory);
    	String path_file_directory = null;
    	String zipFile = "zip_test";
    	com.imath.core.model.File f_d = new com.imath.core.model.File();
    	f_d.setName("file.txt");
    	f_d.setUrl("file://localhost/dir/file.txt");
    	
    	when(principal.getName()).thenReturn(userTest);
    	when(fileController.getFile(idFile, userTest)).thenReturn(f_d);
    	
    	Response response = data.REST_download(path_file_directory, id_file_directory, zipFile, sc);  
    	
    	assertTrue(response.getMetadata().get("Content-Disposition").get(0).equals("attachment; filename=\"zip_test.zip\"") );
	       
        verify(fileController, times(1)).getFile(idFile, userTest);
        
    }
    
    @Test
    //Given the namepath of the file, the zip is created using the name specified as parameter
    //HAPPY PATH
    public void test_download_PATH_filedirectory_GENERATEZIP_specifiednamee()throws Exception{   	
    	
    	String userTest = "userTest";
    	String path_file_directory = "/dir/file.txt";
    	String id_file_directory = null;
    	String zipFile = "zip_test";
    	com.imath.core.model.File f_d = new com.imath.core.model.File();
    	f_d.setName("file.txt");
    	f_d.setUrl("file://localhost/dir/file.txt");
    	
    	when(principal.getName()).thenReturn(userTest);
    	when(fileController.checkIfFileExistInUser(path_file_directory, userTest)).thenReturn(f_d);
    	
    	Response response = data.REST_download(path_file_directory, id_file_directory, zipFile, sc);  

    	assertTrue(response.getMetadata().get("Content-Disposition").get(0).equals("attachment; filename=\"zip_test.zip\"") );
	       
        verify(fileController, times(1)).checkIfFileExistInUser(path_file_directory,userTest);
        
    }
    
    /*
	 * TEST THE REST CALL TO ERASE FILES/DIRECTORIES
	 * 
	 */

    
    @Test
    //Not specified input
    public void test_erase_emptyInput()throws Exception{ 
    	 Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
  	   // The case when no files are uploaded
  	   
    	 when(input.getFormDataMap()).thenReturn(inputMap);
    	 when(principal.getName()).thenReturn("userTest");
  	   
    	 PublicResponse.StateDTO response = data.REST_eraseFiles(input, sc);
    	 assertTrue(response!=null);
    	 assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());

    }
    
    @Test
    //InputMap is specified but the list of each input is empty
    public void test_erase_emptyInputList()throws Exception{ 
    	 Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
  	   // The case when no files are uploaded
    	 inputMap.put(idDeletedFile, new ArrayList<InputPart>());
    	 inputMap.put(pathDeletedFile, new ArrayList<InputPart>());
  	   
    	 when(principal.getName()).thenReturn("userTest");
    	 when(input.getFormDataMap()).thenReturn(inputMap);
    
    	 PublicResponse.StateDTO response = data.REST_eraseFiles(input, sc);
    	 assertTrue(response!=null);
    	 assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
    	 
    }
    
    @Test
    //File IDs are specified in 'idDeletedFile' as part of the inputMap and a exception arises from one of these in getBodyAsString
    public void test_erase_InputList_idFiles()throws Exception{ 
    	 Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
    	 List<InputPart> idFiles = new ArrayList<InputPart>();
         
    	 String id_f1 = new String ("1");
    	 
    	 idFiles.add(inputFileId1);
    	 idFiles.add(inputFileId2);
         // The case when no files are uploaded
        
    	 IOException e = new IOException();
         inputMap.put(idDeletedFile, idFiles);
         
         when(principal.getName()).thenReturn("userTest");
         when(input.getFormDataMap()).thenReturn(inputMap);
         when(inputFileId1.getBodyAsString()).thenReturn(id_f1);
         when(inputFileId2.getBodyAsString()).thenThrow(e);
         
         PublicResponse.StateDTO response = data.REST_eraseFiles(input, sc);
         verify(inputFileId1, times(1)).getBodyAsString();
         verify(inputFileId2, times(1)).getBodyAsString();
         assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
    }
    
    @Test
    //File paths are specified in 'pathDeletedFile' as part of the inputMap
    //File IDs are specified in 'idDeletedFile' as part of the inputMap and one of the files represents the root
    public void test_erase_InputList_idFiles_ONEROOT()throws Exception{ 
    	 Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
    	 
    	 List<InputPart> idFiles = new ArrayList<InputPart>();
    	 String id_f1 = new String ("1");
    	 String id_f2 = new String ("2");
    	 
    	 idFiles.add(inputFileId1);
    	 idFiles.add(inputFileId2);    	
         inputMap.put(idDeletedFile, idFiles);
         
         //The files associated with inputFileId1 and inputFileId2
         com.imath.core.model.File f1 = new com.imath.core.model.File();
         com.imath.core.model.File f2 = new com.imath.core.model.File();
         f1.setName("f1_test.txt");
         f2.setName("ROOT");
    	 
    	 List<InputPart> path_Files = new ArrayList<InputPart>();
         
    	 String path_f3 = new String ("//dir/f3");
    	 String path_f4 = new String ("//dir/f4");
    	 
    	 com.imath.core.model.File f3 = new com.imath.core.model.File();
    	 Long id_f3 = new Long (3);
    	 f3.setId(id_f3);
    	 
    	 com.imath.core.model.File f4 = new com.imath.core.model.File();
    	 Long id_f4 = new Long (4);
    	 f4.setId(id_f4);
    	 
    	 path_Files.add(inputPath1);
    	 path_Files.add(inputPath2);
         inputMap.put(pathDeletedFile, path_Files);
         
         when(principal.getName()).thenReturn("userTest");
         when(input.getFormDataMap()).thenReturn(inputMap);
         when(inputFileId1.getBodyAsString()).thenReturn(id_f1);
         when(inputFileId2.getBodyAsString()).thenReturn(id_f2);
         when(fileController.getFile(Long.valueOf(id_f1), "userTest")).thenReturn(f1);
         when(fileController.getFile(Long.valueOf(id_f2), "userTest")).thenReturn(f2);
                  
         PublicResponse.StateDTO response = data.REST_eraseFiles(input, sc);
         
         verify(inputFileId1, times(1)).getBodyAsString();
         verify(inputFileId2, times(1)).getBodyAsString();
         verify(inputPath1, times(0)).getBodyAsString();
         verify(inputPath2, times(0)).getBodyAsString();
         verify(fileController, times(2)).getFile(Matchers.anyLong(), Matchers.anyString());
         verify(fileController, times(0)).checkIfFileExistInUser(path_f3, "userTest");
         verify(fileController, times(0)).checkIfFileExistInUser(path_f4, "userTest");
                  
         assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
    }
    
    @Test
    //File paths are specified in 'pathDeletedFile' as part of the inputMap and one of them represents the root =/
    //File IDs are specified in 'idDeletedFile' as part of the inputMap
    public void test_erase_InputList_pathFiles_ONEROOT()throws Exception{ 
    	 Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
    	 
    	 List<InputPart> idFiles = new ArrayList<InputPart>();
    	 String id_f1 = new String ("1");
    	 String id_f2 = new String ("2");
    	 
    	 idFiles.add(inputFileId1);
    	 idFiles.add(inputFileId2);    	
         inputMap.put(idDeletedFile, idFiles);
         
         //The files associated with inputFileId1 and inputFileId2
         com.imath.core.model.File f1 = new com.imath.core.model.File();
         com.imath.core.model.File f2 = new com.imath.core.model.File();
         f1.setName("f1_test.txt");
         f2.setName("f2_test.txt");
    	 
    	 List<InputPart> path_Files = new ArrayList<InputPart>();
         
    	 String path_f3 = new String ("//dir/f3");
    	 String path_f4 = new String ("/");
    	 
    	 com.imath.core.model.File f3 = new com.imath.core.model.File();
    	 Long id_f3 = new Long (3);
    	 f3.setId(id_f3);
    	 
    	 com.imath.core.model.File f4 = new com.imath.core.model.File();
    	 Long id_f4 = new Long (4);
    	 f4.setId(id_f4);
    	 
    	 path_Files.add(inputPath1);
    	 path_Files.add(inputPath2);
         inputMap.put(pathDeletedFile, path_Files);
         
         when(principal.getName()).thenReturn("userTest");
         when(input.getFormDataMap()).thenReturn(inputMap);
         when(inputPath1.getBodyAsString()).thenReturn(path_f3);
         when(inputPath2.getBodyAsString()).thenReturn(path_f4);
         when(inputFileId1.getBodyAsString()).thenReturn(id_f1);
         when(inputFileId2.getBodyAsString()).thenReturn(id_f2);
         when(fileController.getFile(Long.valueOf(id_f1), "userTest")).thenReturn(f1);
         when(fileController.getFile(Long.valueOf(id_f2), "userTest")).thenReturn(f2);
         when(fileController.checkIfFileExistInUser(path_f3, "userTest")).thenReturn(f3);
         when(fileController.checkIfFileExistInUser(path_f4, "userTest")).thenReturn(f4);
         
         PublicResponse.StateDTO response = data.REST_eraseFiles(input, sc);
         
         verify(inputFileId1, times(1)).getBodyAsString();
         verify(inputFileId2, times(1)).getBodyAsString();
         verify(inputPath1, times(1)).getBodyAsString();
         verify(inputPath2, times(1)).getBodyAsString();
         verify(fileController, times(2)).getFile(Matchers.anyLong(), Matchers.anyString());
         verify(fileController, times(1)).checkIfFileExistInUser(path_f3, "userTest");
         verify(fileController, times(0)).checkIfFileExistInUser(path_f4, "userTest");
                  
         assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
    }
    
    @Test
    //File IDs are specified in 'idDeletedFile' as part of the inputMap and the files are erased
    //HAPPY PATH
    public void test_erase_InputList_idFiles_erased()throws Exception{ 
    	 Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
    	 List<InputPart> idFiles = new ArrayList<InputPart>();
         
    	 String id_f1 = new String ("1");
    	 String id_f2 = new String ("2");
    	 
    	 idFiles.add(inputFileId1);
    	 idFiles.add(inputFileId2);    	
         inputMap.put(idDeletedFile, idFiles);

         //The files associated with inputFileId1 and inputFileId2
         com.imath.core.model.File f1 = new com.imath.core.model.File();
         com.imath.core.model.File f2 = new com.imath.core.model.File();
         f1.setName("f1_test.txt");
         f2.setName("f2_test.txt");
         
         when(principal.getName()).thenReturn("userTest");
         when(input.getFormDataMap()).thenReturn(inputMap);
         when(inputFileId1.getBodyAsString()).thenReturn(id_f1);
         when(inputFileId2.getBodyAsString()).thenReturn(id_f2);
         when(fileController.getFile(Long.valueOf(id_f1), "userTest")).thenReturn(f1);
         when(fileController.getFile(Long.valueOf(id_f2), "userTest")).thenReturn(f2);
         
         PublicResponse.StateDTO response = data.REST_eraseFiles(input, sc);
         
         verify(inputFileId1, times(1)).getBodyAsString();
         verify(inputFileId2, times(1)).getBodyAsString();
         verify(fileController, times(2)).getFile((Long) Matchers.any(), (String)Matchers.any());
                
         ArgumentCaptor<Set> fileCaptor = ArgumentCaptor.forClass(Set.class);
         verify(fileController, times(1)).eraseListFiles(fileCaptor.capture(), Matchers.eq(sc));
         List<Set> captured_idFiles = fileCaptor.getAllValues();
         Set<String> set_idFiles = (Set<String>)captured_idFiles.get(0);
         assertTrue(set_idFiles.contains(id_f1));
         assertTrue(set_idFiles.contains(id_f2));
         
         assertTrue(response.code == Response.Status.ACCEPTED.getStatusCode());     	
    }
    
    
    @Test
    //File paths are specified in 'pathDeletedFile' as part of the inputMap and a exception arises from one of these in getBodyAsString
    public void test_erase_InputList_pathFilesException1()throws Exception{ 
    	 Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
    	 List<InputPart> path_Files = new ArrayList<InputPart>();
         
    	 String path_f1 = new String ("//dir/f1");
    	 String path_f2 = new String ("//dir/f2");
    	 
    	 com.imath.core.model.File f1 = new com.imath.core.model.File();
    	 Long id_f1 = new Long (1);
    	 f1.setId(id_f1);
    	 
    	 path_Files.add(inputPath1);
    	 path_Files.add(inputPath2);
         // The case when no files are uploaded
        
    	 IOException e = new IOException();
         inputMap.put(pathDeletedFile, path_Files);
         
         when(principal.getName()).thenReturn("userTest");
         when(input.getFormDataMap()).thenReturn(inputMap);
         when(inputPath1.getBodyAsString()).thenReturn(path_f1);
         when(inputPath2.getBodyAsString()).thenThrow(e);
         when(fileController.checkIfFileExistInUser(path_f1, "userTest")).thenReturn(f1);
         
         PublicResponse.StateDTO response = data.REST_eraseFiles(input, sc);
         
         verify(inputPath1, times(1)).getBodyAsString();
         verify(inputPath2, times(1)).getBodyAsString();
         assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
    }
    
    @Test
    //File paths are specified in 'pathDeletedFile' as part of the inputMap and the file returned by checkIfFileExistInUser is null 
    public void test_erase_InputList_pathFilesException2()throws Exception{ 
    	 Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
    	 List<InputPart> path_Files = new ArrayList<InputPart>();
         
    	 String path_f1 = new String ("//dir/f1");
    	 String path_f2 = new String ("//dir/f2");
    	 
    	 com.imath.core.model.File f1 = new com.imath.core.model.File();
    	 Long id_f1 = new Long (1);
    	 f1.setId(id_f1);
    	 
    	 com.imath.core.model.File f2 = null;
    	 
    	 path_Files.add(inputPath1);
    	 path_Files.add(inputPath2);
         // The case when no files are uploaded
        
    	 IOException e = new IOException();
         inputMap.put(pathDeletedFile, path_Files);
         
         when(principal.getName()).thenReturn("userTest");
         when(input.getFormDataMap()).thenReturn(inputMap);
         when(inputPath1.getBodyAsString()).thenReturn(path_f1);
         when(inputPath2.getBodyAsString()).thenReturn(path_f2);
         when(fileController.checkIfFileExistInUser(path_f1, "userTest")).thenReturn(f1);
         when(fileController.checkIfFileExistInUser(path_f2, "userTest")).thenReturn(f2);
         
         PublicResponse.StateDTO response = data.REST_eraseFiles(input, sc);
         
         verify(inputPath1, times(1)).getBodyAsString();
         verify(inputPath2, times(1)).getBodyAsString();
         assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
    }
    
    @Test
    //File paths are specified in 'pathDeletedFile' as part of the inputMap and they are erased
    //HAPPY PATH
    public void test_erase_InputList_pathFiles_erased()throws Exception{ 
    	 Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
    	 List<InputPart> path_Files = new ArrayList<InputPart>();
         
    	 String path_f1 = new String ("//dir/f1");
    	 String path_f2 = new String ("//dir/f2");
    	 
    	 com.imath.core.model.File f1 = new com.imath.core.model.File();
    	 Long id_f1 = new Long (1);
    	 f1.setId(id_f1);
    	 
    	 com.imath.core.model.File f2 = new com.imath.core.model.File();
    	 Long id_f2 = new Long (2);
    	 f2.setId(id_f2);
    	 
    	 path_Files.add(inputPath1);
    	 path_Files.add(inputPath2);
         // The case when no files are uploaded
        
    	 IOException e = new IOException();
         inputMap.put(pathDeletedFile, path_Files);
         
         when(principal.getName()).thenReturn("userTest");
         when(input.getFormDataMap()).thenReturn(inputMap);
         when(inputPath1.getBodyAsString()).thenReturn(path_f1);
         when(inputPath2.getBodyAsString()).thenReturn(path_f2);
        
         when(fileController.checkIfFileExistInUser(path_f1, "userTest")).thenReturn(f1);
         when(fileController.checkIfFileExistInUser(path_f2, "userTest")).thenReturn(f2);
         
         PublicResponse.StateDTO response = data.REST_eraseFiles(input, sc);
         
         verify(inputPath1, times(1)).getBodyAsString();
         verify(inputPath2, times(1)).getBodyAsString();
         verify(fileController, times(1)).checkIfFileExistInUser(path_f1, "userTest");
         verify(fileController, times(1)).checkIfFileExistInUser(path_f2, "userTest");
         
         ArgumentCaptor<Set> fileCaptor = ArgumentCaptor.forClass(Set.class);
         verify(fileController, times(1)).eraseListFiles(fileCaptor.capture(), Matchers.eq(sc));
         List<Set> captured_idFiles = fileCaptor.getAllValues();
         Set<String> set_idFiles = (Set<String>)captured_idFiles.get(0);
         assertTrue(set_idFiles.contains(String.valueOf(id_f1)));
         assertTrue(set_idFiles.contains(String.valueOf(id_f2)));
         
         assertTrue(response.code == Response.Status.ACCEPTED.getStatusCode());
    }
    
    @Test
    //File paths are specified in 'pathDeletedFile' as part of the inputMap and they are erased
    //File IDs are specified in 'idDeletedFile' as part of the inputMap and the files are erased
    //HAPPY PATH
    public void test_erase_InputList_idFiles_pathFiles_erased()throws Exception{ 
    	 Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
    	 
    	 List<InputPart> idFiles = new ArrayList<InputPart>();
    	 String id_f1 = new String ("1");
    	 String id_f2 = new String ("2");
    	 
    	 idFiles.add(inputFileId1);
    	 idFiles.add(inputFileId2);    	
         inputMap.put(idDeletedFile, idFiles);
         
         //The files associated with inputFileId1 and inputFileId2
         com.imath.core.model.File f1 = new com.imath.core.model.File();
         com.imath.core.model.File f2 = new com.imath.core.model.File();
         f1.setName("f1_test.txt");
         f2.setName("f2_test.txt");
    	 
    	 List<InputPart> path_Files = new ArrayList<InputPart>();
         
    	 String path_f3 = new String ("//dir/f3");
    	 String path_f4 = new String ("//dir/f4");
    	 
    	 com.imath.core.model.File f3 = new com.imath.core.model.File();
    	 Long id_f3 = new Long (3);
    	 f3.setId(id_f3);
    	 
    	 com.imath.core.model.File f4 = new com.imath.core.model.File();
    	 Long id_f4 = new Long (4);
    	 f4.setId(id_f4);
    	 
    	 path_Files.add(inputPath1);
    	 path_Files.add(inputPath2);
         inputMap.put(pathDeletedFile, path_Files);
         
         when(principal.getName()).thenReturn("userTest");
         when(input.getFormDataMap()).thenReturn(inputMap);
         when(inputPath1.getBodyAsString()).thenReturn(path_f3);
         when(inputPath2.getBodyAsString()).thenReturn(path_f4);
         when(inputFileId1.getBodyAsString()).thenReturn(id_f1);
         when(inputFileId2.getBodyAsString()).thenReturn(id_f2);
         when(fileController.getFile(Long.valueOf(id_f1), "userTest")).thenReturn(f1);
         when(fileController.getFile(Long.valueOf(id_f2), "userTest")).thenReturn(f2);
         when(fileController.checkIfFileExistInUser(path_f3, "userTest")).thenReturn(f3);
         when(fileController.checkIfFileExistInUser(path_f4, "userTest")).thenReturn(f4);
         
         PublicResponse.StateDTO response = data.REST_eraseFiles(input, sc);
         
         verify(inputFileId1, times(1)).getBodyAsString();
         verify(inputFileId2, times(1)).getBodyAsString();
         verify(inputPath1, times(1)).getBodyAsString();
         verify(inputPath2, times(1)).getBodyAsString();
         verify(fileController, times(2)).getFile(Matchers.anyLong(), Matchers.anyString());
         verify(fileController, times(1)).checkIfFileExistInUser(path_f3, "userTest");
         verify(fileController, times(1)).checkIfFileExistInUser(path_f4, "userTest");
         
         ArgumentCaptor<Set> fileCaptor = ArgumentCaptor.forClass(Set.class);
         verify(fileController, times(1)).eraseListFiles(fileCaptor.capture(), Matchers.eq(sc));
         List<Set> captured_idFiles = fileCaptor.getAllValues();
         Set<String> set_idFiles = (Set<String>)captured_idFiles.get(0);
         assertTrue(set_idFiles.contains(id_f1));
         assertTrue(set_idFiles.contains(id_f2));
         assertTrue(set_idFiles.contains(String.valueOf(id_f3)));
         assertTrue(set_idFiles.contains(String.valueOf(id_f4)));
                  
         assertTrue(response.code == Response.Status.ACCEPTED.getStatusCode());
    }
    
    @Test
    //File paths are specified in 'pathDeletedFile' as part of the inputMap and they are erased
    //File IDs are specified in 'idDeletedFile' as part of the inputMap and the files are erased
    //A file id corresponds with a specified path
    //In this test we check not to have duplicated files to erased
    //HAPPY PATH
    public void test_erase_InputList_idFiles_pathFiles_erased2()throws Exception{ 
    	 Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
    	 
    	 List<InputPart> idFiles = new ArrayList<InputPart>();
    	 String id_f1 = new String ("1");
    	 String id_f2 = new String ("2");
    	 
    	 idFiles.add(inputFileId1);
    	 idFiles.add(inputFileId2);    	
         inputMap.put(idDeletedFile, idFiles);
         
         //The files associated with inputFileId1 and inputFileId2
         com.imath.core.model.File f1 = new com.imath.core.model.File();
         com.imath.core.model.File f2 = new com.imath.core.model.File();
         f1.setName("f1_test.txt");
         f2.setName("f2_test.txt");
    	 
    	 List<InputPart> path_Files = new ArrayList<InputPart>();
         
    	 String path_f3 = new String ("//dir/f3");
    	 String path_f4 = new String ("//dir/f4");
    	 
    	 com.imath.core.model.File f3 = new com.imath.core.model.File();
    	 Long id_f3 = new Long (2);
    	 f3.setId(id_f3);
    	 
    	 com.imath.core.model.File f4 = new com.imath.core.model.File();
    	 Long id_f4 = new Long (4);
    	 f4.setId(id_f4);
    	 
    	 path_Files.add(inputPath1);
    	 path_Files.add(inputPath2);
         inputMap.put(pathDeletedFile, path_Files);
         
         when(principal.getName()).thenReturn("userTest");
         when(input.getFormDataMap()).thenReturn(inputMap);
         when(inputPath1.getBodyAsString()).thenReturn(path_f3);
         when(inputPath2.getBodyAsString()).thenReturn(path_f4);
         when(inputFileId1.getBodyAsString()).thenReturn(id_f1);
         when(inputFileId2.getBodyAsString()).thenReturn(id_f2);
         when(fileController.getFile(Long.valueOf(id_f1), "userTest")).thenReturn(f1);
         when(fileController.getFile(Long.valueOf(id_f2), "userTest")).thenReturn(f2);
         when(fileController.checkIfFileExistInUser(path_f3, "userTest")).thenReturn(f3);
         when(fileController.checkIfFileExistInUser(path_f4, "userTest")).thenReturn(f4);
         
         PublicResponse.StateDTO response = data.REST_eraseFiles(input, sc);
         
         verify(inputFileId1, times(1)).getBodyAsString();
         verify(inputFileId2, times(1)).getBodyAsString();
         verify(inputPath1, times(1)).getBodyAsString();
         verify(inputPath2, times(1)).getBodyAsString();
         verify(fileController, times(2)).getFile(Matchers.anyLong(), Matchers.anyString());
         verify(fileController, times(1)).checkIfFileExistInUser(path_f3, "userTest");
         verify(fileController, times(1)).checkIfFileExistInUser(path_f4, "userTest");
         
         ArgumentCaptor<Set> fileCaptor = ArgumentCaptor.forClass(Set.class);
         verify(fileController, times(1)).eraseListFiles(fileCaptor.capture(), Matchers.eq(sc));
         List<Set> captured_idFiles = fileCaptor.getAllValues();
         Set<String> set_idFiles = (Set<String>)captured_idFiles.get(0);
         assertTrue(set_idFiles.contains(id_f1));
         assertTrue(set_idFiles.contains(id_f2));
         assertTrue(!set_idFiles.contains("3"));
         assertTrue(set_idFiles.contains(String.valueOf(id_f4)));
         assertTrue(set_idFiles.size() == 3);
                  
         assertTrue(response.code == Response.Status.ACCEPTED.getStatusCode());
    }
    
    /*
   	 * TEST THE REST CALL TO RENAME A FILE/DIRECTORY
   	 * 
   	 */
    
    @Test
    //The input newName does not exist
    public void test_rename_newName_NotExist()throws Exception{ 
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
    	 
    	List<InputPart> idFiles = new ArrayList<InputPart>();   	 
    	idFiles.add(inputFileId1);    	    	
        inputMap.put(idFile, idFiles);
        
        List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        inputMap.put(pathNameFile, pathFiles);
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //The input newName is null
    public void test_rename_newName_null()throws Exception{ 
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
    	 
    	List<InputPart> idFiles = new ArrayList<InputPart>();   	 
    	idFiles.add(inputFileId1);    	    	
        inputMap.put(idFile, idFiles);
        
        List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        inputMap.put(pathNameFile, pathFiles);
        
        List<InputPart> newNames = null;
        inputMap.put(newName, newNames);
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //The input newName is empty
    public void test_rename_newName_empty()throws Exception{ 
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
    	 
    	List<InputPart> idFiles = new ArrayList<InputPart>();   	 
    	idFiles.add(inputFileId1);    	    	
        inputMap.put(idFile, idFiles);
        
        List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        inputMap.put(pathNameFile, pathFiles);
        
        List<InputPart> newNames = new ArrayList<InputPart>(); ;
        inputMap.put(newName, newNames);
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    
    @Test
    //The input newName is specified more than once
    public void test_rename_newName_morethanOne()throws Exception{ 
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
    	 
    	List<InputPart> idFiles = new ArrayList<InputPart>();   	 
    	idFiles.add(inputFileId1);    	    	
        inputMap.put(idFile, idFiles);
        
        List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        inputMap.put(pathNameFile, pathFiles);
        
        List<InputPart> newNames =  new ArrayList<InputPart>(); 
        newNames.add(inputNewName1);
        newNames.add(inputNewName2);
        inputMap.put(newName, newNames);
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //The inputs idFile and pathNameFile are not null in the same call
    public void test_rename_idFilepathNameFile_NOTnull()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> idFiles = new ArrayList<InputPart>();   	 
    	idFiles.add(inputFileId1);    	    	
        inputMap.put(idFile, idFiles);
        
        List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        inputMap.put(pathNameFile, pathFiles);
        
        List<InputPart> newNames =  new ArrayList<InputPart>(); 
        newNames.add(inputNewName1);
        inputMap.put(newName, newNames);
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
   
    
    @Test
    //The inputs idFile and pathNameFile are null in the same call
    public void test_rename_idFilepathNameFile_null()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> idFiles = null;   	    	    	
        inputMap.put(idFile, idFiles);
        
        List<InputPart> pathFiles = null;
        inputMap.put(pathNameFile, pathFiles);
        
        List<InputPart> newNames =  new ArrayList<InputPart>(); 
        newNames.add(inputNewName1);
        inputMap.put(newName, newNames);
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //When idFile is not null, cannot be empty (we force to be empty) 
    public void test_rename_idFile_NOTnull_empty()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> idFiles = new ArrayList<InputPart>();   	 
        inputMap.put(idFile, idFiles);
        
        List<InputPart> pathFiles = null; 
        inputMap.put(pathNameFile, pathFiles);
        
        List<InputPart> newNames =  new ArrayList<InputPart>(); 
        newNames.add(inputNewName1);
        inputMap.put(newName, newNames);
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //When idFile is not null, cannot have a size greater than 1
    public void test_rename_idFile_NOTnull_morethan1()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> idFiles = new ArrayList<InputPart>();
    	idFiles.add(inputFileId1);
    	idFiles.add(inputFileId2);
        inputMap.put(idFile, idFiles);
        
        List<InputPart> pathFiles = null; 
        inputMap.put(pathNameFile, pathFiles);
        
        List<InputPart> newNames =  new ArrayList<InputPart>(); 
        newNames.add(inputNewName1);
        inputMap.put(newName, newNames);
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);
        
        assertTrue(response!=null);
  	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //When pathNameFile is not null, cannot be empty (we force to be empty) 
    public void test_rename_pathNameFile_NOTnull_empty()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> idFiles = null;   	 
        inputMap.put(idFile, idFiles);
        
        List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        inputMap.put(pathNameFile, pathFiles);
        
        List<InputPart> newNames =  new ArrayList<InputPart>(); 
        newNames.add(inputNewName1);
        inputMap.put(newName, newNames);
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //When pathNameFile is not null, cannot have a size greater than 1
    public void test_rename_pathNameFile_NOTnull_morethan1()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> idFiles = null;
        inputMap.put(idFile, idFiles);
        
        List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        pathFiles.add(inputPath2);
        inputMap.put(pathNameFile, pathFiles);
        
        List<InputPart> newNames =  new ArrayList<InputPart>(); 
        newNames.add(inputNewName1);
        inputMap.put(newName, newNames);
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
   
    @Test
    //Arises exception in getBodyAsString when list_idFiles is managed
    public void test_rename_idFile_getBodyAsStringException()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> idFiles = new ArrayList<InputPart>();
    	idFiles.add(inputFileId1);
        inputMap.put(idFile, idFiles);
       
        List<InputPart> newNames =  new ArrayList<InputPart>(); 
        newNames.add(inputNewName1);
        inputMap.put(newName, newNames);
        
        IOException e = new IOException();
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        when(inputFileId1.getBodyAsString()).thenThrow(e);
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);
        
        verify(inputFileId1, times(1)).getBodyAsString();
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //Arises exception in getBodyAsString when list_pathNameFile is managed
    public void test_rename_pathNameFile_getBodyAsStringException()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        inputMap.put(pathNameFile, pathFiles);
         
        List<InputPart> newNames =  new ArrayList<InputPart>(); 
        newNames.add(inputNewName1);
        inputMap.put(newName, newNames);
        
        IOException e = new IOException();
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        when(inputPath1.getBodyAsString()).thenThrow(e);
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);
        
        verify(inputPath1, times(1)).getBodyAsString();
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //The file specified in pathNameFile does not exist
    public void test_rename_pathNameFile_fileNotExist()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        inputMap.put(pathNameFile, pathFiles);
       
        List<InputPart> newNames =  new ArrayList<InputPart>(); 
        newNames.add(inputNewName1);
        inputMap.put(newName, newNames);
        
        IOException e = new IOException();
        String path_file = "/test_dir/test_file.txt";
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        when(inputPath1.getBodyAsString()).thenReturn(path_file);
        when(fileController.checkIfFileExistInUser(path_file, "userTest")).thenReturn(null);
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);
        
        verify(inputPath1, times(1)).getBodyAsString();
        verify(fileController, times(1)).checkIfFileExistInUser(path_file, "userTest");
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //The file specified in pathNameFile corresponds to the root, i.e pathNameFile=/
    public void test_rename_pathNameFile_ROOT()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        inputMap.put(pathNameFile, pathFiles);
       
        List<InputPart> newNames =  new ArrayList<InputPart>(); 
        newNames.add(inputNewName1);
        inputMap.put(newName, newNames);
        
        IOException e = new IOException();
        String path_file = "/"; //ROOT
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        when(inputPath1.getBodyAsString()).thenReturn(path_file);
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);
        
        verify(inputPath1, times(1)).getBodyAsString();
        verify(fileController, times(0)).checkIfFileExistInUser(Matchers.anyString(), Matchers.anyString());
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //Test the case when the idFile corresponds with the ROOT
    public void test_rename_idFile_ROOT()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
      	 
    	List<InputPart> idFiles = new ArrayList<InputPart>();
    	idFiles.add(inputFileId1);
        inputMap.put(idFile, idFiles);
       
        List<InputPart> newNames =  new ArrayList<InputPart>(); 
        newNames.add(inputNewName1);
        inputMap.put(newName, newNames);
        
        //The file represent the root
        com.imath.core.model.File f1 = new com.imath.core.model.File();
        f1.setName("ROOT");
        
        String id_file = new String("1");
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        when(inputFileId1.getBodyAsString()).thenReturn(id_file);
        when(fileController.getFile(Long.valueOf(id_file), "userTest")).thenReturn(f1);
        
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);
        
        verify(inputFileId1, times(1)).getBodyAsString();
        verify(fileController, times(1)).getFile(Matchers.anyLong(), Matchers.anyString());
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
    	       
    }
    
    @Test
    //Arises exception in getBodyAsString when list_newName is managed
    public void test_rename_newName_getBodyAsStringException()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
      	 
    	List<InputPart> idFiles = new ArrayList<InputPart>();
    	idFiles.add(inputFileId1);
        inputMap.put(idFile, idFiles);
       
        List<InputPart> newNames =  new ArrayList<InputPart>(); 
        newNames.add(inputNewName1);
        inputMap.put(newName, newNames);
        
        com.imath.core.model.File f1 = new com.imath.core.model.File();
        f1.setName("test_file.txt");
        
        IOException e = new IOException();
        String id_file = new String("1");
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        when(inputFileId1.getBodyAsString()).thenReturn(id_file);
        when(fileController.getFile(Long.valueOf(id_file), "userTest")).thenReturn(f1);
        when(inputNewName1.getBodyAsString()).thenThrow(e);
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);
        
        verify(inputFileId1, times(1)).getBodyAsString();
        verify(inputNewName1, times(1)).getBodyAsString();
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
    	       
    }
    
    @Test
    //A new name and a idFile is specified.
    //HAPPY PATH
    public void test_rename_idFiles_HAPPYPATH()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
      	 
    	List<InputPart> idFiles = new ArrayList<InputPart>();
    	idFiles.add(inputFileId1);
        inputMap.put(idFile, idFiles);
       
        List<InputPart> newNames =  new ArrayList<InputPart>(); 
        newNames.add(inputNewName1);
        inputMap.put(newName, newNames);
        
        com.imath.core.model.File f1 = new com.imath.core.model.File();
        f1.setName("test_file.txt");
        
        IOException e = new IOException();
        String id_file = new String("1");
        String new_name = new String("newFileName.txt");
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        when(inputFileId1.getBodyAsString()).thenReturn(id_file);
        when(fileController.getFile(Long.valueOf(id_file), "userTest")).thenReturn(f1);
        when(inputNewName1.getBodyAsString()).thenReturn(new_name);
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);        
        
        verify(inputFileId1, times(1)).getBodyAsString();
        verify(inputNewName1, times(1)).getBodyAsString();
        verify(fileController, times(1)).getFile(Long.valueOf(id_file), "userTest");
        verify(fileController, times(1)).renameFile(Matchers.eq(id_file), Matchers.eq(new_name), Matchers.eq(sc));
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.ACCEPTED.getStatusCode());
    	       
    }
    
    @Test
    //A new name and a pathNameFile is specified.
    //HAPPY PATH
    public void test_rename_pathNameFile_HAPPYPATH()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
      	 
    	List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        inputMap.put(pathNameFile, pathFiles);
       
        List<InputPart> newNames =  new ArrayList<InputPart>(); 
        newNames.add(inputNewName1);
        inputMap.put(newName, newNames);
        
        IOException e = new IOException();
        String new_name = new String("newFileName.txt");
        String path_file = "/test_dir/test_file.txt";
        String id_file = new String("1");
        com.imath.core.model.File file = new com.imath.core.model.File();
        file.setId(Long.valueOf(id_file));
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        when(inputPath1.getBodyAsString()).thenReturn(path_file);
        when(fileController.checkIfFileExistInUser(path_file, "userTest")).thenReturn(file);   
        when(inputNewName1.getBodyAsString()).thenReturn(new_name);
        
        PublicResponse.StateDTO response = data.REST_renameFiles(input, sc);        
        
        verify(inputPath1, times(1)).getBodyAsString();
        verify(inputNewName1, times(1)).getBodyAsString();
        verify(fileController, times(1)).checkIfFileExistInUser(path_file, "userTest");
        verify(fileController, times(1)).renameFile(Matchers.eq(id_file), Matchers.eq(new_name), Matchers.eq(sc));
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.ACCEPTED.getStatusCode());
    	       
    }
    
    /*
   	 * TEST THE REST CALL TO CREATE AN EMPTY FILE/DIRECTORY
   	 * 
   	 */
    
    @Test
    //The inputs dirName and fileName are not null in the same call
    public void test_createFile_dirNamefileName_NOTNULL()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> list_dirName= new ArrayList<InputPart>();   	 
    	list_dirName.add(inputNewName1);    	    	
        inputMap.put(dirName, list_dirName);
        
        List<InputPart> list_fileName = new ArrayList<InputPart>(); 
        list_fileName.add(inputNewName2);
        inputMap.put(fileName, list_fileName);
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //The inputs dirName and fileName are null in the same call
    public void test_createFile_dirNamefileName_NULL()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> list_dirName= null;   	 
        inputMap.put(dirName, list_dirName);
        
        List<InputPart> list_fileName = null; 
        inputMap.put(fileName, list_fileName);
             
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //When dirName is not null, cannot be empty (we force to be empty) 
    public void test_createFile_dirName_NOTnull_empty()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> list_dirName= new ArrayList<InputPart>();   	 
        inputMap.put(dirName, list_dirName);
        
        List<InputPart> list_fileName = null; 
        inputMap.put(fileName, list_fileName);
             
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //When dirName is not null, cannot have a size greater than 1
    public void test_createFile_dirName_NOTnull_morethan1()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> list_dirName = new ArrayList<InputPart>();
    	list_dirName.add(inputNewName1);
    	list_dirName.add(inputNewName2);
        inputMap.put(dirName, list_dirName);
        
        List<InputPart> list_fileName = null; 
        inputMap.put(fileName, list_fileName);
               
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);
        
        assertTrue(response!=null);
  	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //When fileName is not null, cannot be empty (we force to be empty) 
    public void test_createFile_fileName_NOTnull_empty()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> list_dirName= null;	 
        inputMap.put(dirName, list_dirName);
        
        List<InputPart> list_fileName = new ArrayList<InputPart>();    
        inputMap.put(fileName, list_fileName);
             
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //When fileName is not null, cannot have a size greater than 1
    public void test_createFile_fileName_NOTnull_morethan1()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> list_dirName = null;
        inputMap.put(dirName, list_dirName);
        
        List<InputPart> list_fileName = new ArrayList<InputPart>();
        list_fileName.add(inputNewName1);
        list_fileName.add(inputNewName2);
        inputMap.put(fileName, list_fileName);
               
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);
        
        assertTrue(response!=null);
  	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //Arises exception in getBodyAsString when list_dirName is managed
    public void test_createFile_dirName_getBodyAsStringException()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> list_dirName = new ArrayList<InputPart>();
    	list_dirName.add(inputNewName1);
        inputMap.put(dirName, list_dirName);
        
        //At least, the pathParentDir or the idParentDir must be specified in this path
        List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        inputMap.put(pathParentDir, pathFiles);
      
        IOException e = new IOException();
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        when(inputNewName1.getBodyAsString()).thenThrow(e);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);
        
        verify(inputNewName1, times(1)).getBodyAsString();
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //Arises exception in getBodyAsString when list_fileName is managed
    public void test_createFile_fileName_getBodyAsStringException()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> list_fileName = new ArrayList<InputPart>();
    	list_fileName.add(inputNewName1);
        inputMap.put(fileName, list_fileName);
        
      //At least, the pathParentDir or the idParentDir must be specified in this path
        List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        inputMap.put(pathParentDir, pathFiles);
      
        IOException e = new IOException();
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        when(inputNewName1.getBodyAsString()).thenThrow(e);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);
        
        verify(inputNewName1, times(1)).getBodyAsString();
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //The inputs idParentDir and pathParentDir are not null in the same call
    public void test_createFile_name_idParentDirpathParentDir_NOTNULL()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> idFiles = new ArrayList<InputPart>();   	 
    	idFiles.add(inputFileId1);    	    	
        inputMap.put(idParentDir, idFiles);
        
        List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        inputMap.put(pathParentDir, pathFiles);
        
        //At least, the list_dirName or the list_fileName must be specified in this path
        List<InputPart> list_dirName =  new ArrayList<InputPart>(); 
        list_dirName.add(inputNewName1);
        inputMap.put(dirName, list_dirName);
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
   
    
    @Test
    //The inputs idParentDir and pathParentDir are null in the same call
    public void test_createFile_name_idParentDirpathParentDir_NULL()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> idFiles = null;   	    	    	
        inputMap.put(idParentDir, idFiles);
        
        List<InputPart> pathFiles = null;
        inputMap.put(pathParentDir, pathFiles);
        
        //At least, the list_dirName or the list_fileName must be specified in this path
        List<InputPart> list_dirName =  new ArrayList<InputPart>(); 
        list_dirName.add(inputNewName1);
        inputMap.put(dirName, list_dirName);
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //When idParentDir is not null, cannot be empty (we force to be empty) 
    public void test_creatFile_idParentDir_NOTnull_empty()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> idFiles = new ArrayList<InputPart>();   	 
        inputMap.put(idParentDir, idFiles);
        
        List<InputPart> pathFiles = null; 
        inputMap.put(pathParentDir, pathFiles);
        
        //At least, the list_dirName or the list_fileName must be specified in this path
        List<InputPart> list_dirName =  new ArrayList<InputPart>(); 
        list_dirName.add(inputNewName1);
        inputMap.put(dirName, list_dirName);
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //When idParentDir is not null, cannot have a size greater than 1
    public void test_createFile_idParentDir_NOTnull_morethan1()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> idFiles = new ArrayList<InputPart>();
    	idFiles.add(inputFileId1);
    	idFiles.add(inputFileId2);
        inputMap.put(idParentDir, idFiles);
        
        List<InputPart> pathFiles = null; 
        inputMap.put(pathParentDir, pathFiles);
        
        //At least, the list_dirName or the list_fileName must be specified in this path
        List<InputPart> list_dirName =  new ArrayList<InputPart>(); 
        list_dirName.add(inputNewName1);
        inputMap.put(dirName, list_dirName);
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);
        
        assertTrue(response!=null);
  	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //When pathParentDir is not null, cannot be empty (we force to be empty) 
    public void test_createFile_pathParentDir_NOTnull_empty()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> idFiles = null;   	 
        inputMap.put(idParentDir, idFiles);
        
        List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        inputMap.put(pathParentDir, pathFiles);
        
        //At least, the list_dirName or the list_fileName must be specified in this path
        List<InputPart> list_dirName =  new ArrayList<InputPart>(); 
        list_dirName.add(inputNewName1);
        inputMap.put(dirName, list_dirName);
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //When pathParentDir is not null, cannot have a size greater than 1
    public void test_createFile_pathParentDir_NOTnull_morethan1()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> idFiles = null;
        inputMap.put(idParentDir, idFiles);
        
        List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        pathFiles.add(inputPath2);
        inputMap.put(pathParentDir, pathFiles);
        
        //At least, the list_dirName or the list_fileName must be specified in this path
        List<InputPart> list_dirName =  new ArrayList<InputPart>(); 
        list_dirName.add(inputNewName1);
        inputMap.put(dirName, list_dirName);
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
   
    @Test
    //Arises exception in getBodyAsString when list_idParentDir is managed
    public void test_createFile_idParentDir_getBodyAsStringException()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> idFiles = new ArrayList<InputPart>();
    	idFiles.add(inputFileId1);
        inputMap.put(idParentDir, idFiles);
       
        //At least, the list_dirName or the list_fileName must be specified in this path
        List<InputPart> list_dirName =  new ArrayList<InputPart>(); 
        list_dirName.add(inputNewName1);
        inputMap.put(dirName, list_dirName);
        
        IOException e = new IOException();
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        when(inputFileId1.getBodyAsString()).thenThrow(e);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);
        
        verify(inputFileId1, times(1)).getBodyAsString();
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //Arises exception in getBodyAsString when list_pathParentDir is managed
    public void test_createFile_pathParentDir_getBodyAsStringException()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        inputMap.put(pathParentDir, pathFiles);
         
        List<InputPart> list_dirName =  new ArrayList<InputPart>(); 
        list_dirName.add(inputNewName1);
        inputMap.put(dirName, list_dirName);
        
        IOException e = new IOException();
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        when(inputPath1.getBodyAsString()).thenThrow(e);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);
        
        verify(inputPath1, times(1)).getBodyAsString();
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
    
    @Test
    //The file specified in pathParentDir does not exist
    public void test_createFile_pathParentDir_fileNotExist()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
   	 
    	List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        inputMap.put(pathParentDir, pathFiles);
       
        List<InputPart> list_dirName =  new ArrayList<InputPart>(); 
        list_dirName.add(inputNewName1);
        inputMap.put(dirName, list_dirName);
        
        IOException e = new IOException();
        String path_file = "/test_dir/test_file.txt";
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        when(inputPath1.getBodyAsString()).thenReturn(path_file);
        when(fileController.checkIfFileExistInUser(path_file, "userTest")).thenReturn(null);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);
        
        verify(inputPath1, times(1)).getBodyAsString();
        verify(fileController, times(1)).checkIfFileExistInUser(path_file, "userTest");
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.NOT_FOUND.getStatusCode());
        
    }
     
    @Test
    //A directory name and a idParentDir is specified.
    //HAPPY PATH
    public void test_createFile_dirName_idParentDir_HAPPYPATH()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
      	 
    	List<InputPart> idFiles = new ArrayList<InputPart>();
    	idFiles.add(inputFileId1);
        inputMap.put(idParentDir, idFiles);
       
        List<InputPart> list_dirName =  new ArrayList<InputPart>(); 
        list_dirName.add(inputNewName1);
        inputMap.put(dirName, list_dirName);       
             
        String id_file = new String("1");
        String name = new String("newDirName");
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        when(inputFileId1.getBodyAsString()).thenReturn(id_file);
        when(inputNewName1.getBodyAsString()).thenReturn(name);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);        
        
        verify(inputFileId1, times(1)).getBodyAsString();
        verify(inputNewName1, times(1)).getBodyAsString();
        verify(fileController, times(1)).createFile(id_file, name, "directory", sc); 
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.ACCEPTED.getStatusCode());
    	       
    }
    
    @Test
    //A directory name and a pathParentDir is specified.
    //HAPPY PATH
    public void test_createFile_dirNamepathParentDir_HAPPYPATH()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
      	 
    	List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        inputMap.put(pathParentDir, pathFiles);
       
        List<InputPart> list_dirName =  new ArrayList<InputPart>(); 
        list_dirName.add(inputNewName1);
        inputMap.put(dirName, list_dirName);
        
        String name = new String("newDirName");
        String path_file = "/test_dir";
        String id_file = new String("1");
        com.imath.core.model.File file = new com.imath.core.model.File();
        file.setId(Long.valueOf(id_file));
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        when(inputPath1.getBodyAsString()).thenReturn(path_file);
        when(fileController.checkIfFileExistInUser(path_file, "userTest")).thenReturn(file);   
        when(inputNewName1.getBodyAsString()).thenReturn(name);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);        
        
        verify(inputPath1, times(1)).getBodyAsString();
        verify(inputNewName1, times(1)).getBodyAsString();
        verify(fileController, times(1)).checkIfFileExistInUser(path_file, "userTest");
        verify(fileController, times(1)).createFile(id_file, name, "directory", sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.ACCEPTED.getStatusCode());	       
    }
    
    @Test
    //A file name and a idParentDir is specified.
    //HAPPY PATH
    public void test_createFile_fileName_idParentDir_HAPPYPATH()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
      	 
    	List<InputPart> idFiles = new ArrayList<InputPart>();
    	idFiles.add(inputFileId1);
        inputMap.put(idParentDir, idFiles);
       
        List<InputPart> list_fileName =  new ArrayList<InputPart>(); 
        list_fileName.add(inputNewName1);
        inputMap.put(fileName, list_fileName);
       
        String id_file = new String("1");
        String name = new String("file_test.txt");		     
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        when(inputFileId1.getBodyAsString()).thenReturn(id_file);
        when(inputNewName1.getBodyAsString()).thenReturn(name);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);        
        
        verify(inputFileId1, times(1)).getBodyAsString();
        verify(inputNewName1, times(1)).getBodyAsString();
        verify(fileController, times(1)).createFile(id_file, name, "regular", sc); 
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.ACCEPTED.getStatusCode());
    	       
    }
    
    @Test
    //A file name and a pathParentDir is specified.
    //HAPPY PATH
    public void test_createFile_fileNamepathParentDir_HAPPYPATH()throws Exception{ 
    	
    	Map<String, List<InputPart>> inputMap = new HashMap<String, List<InputPart>>();
      	 
    	List<InputPart> pathFiles = new ArrayList<InputPart>(); 
        pathFiles.add(inputPath1);
        inputMap.put(pathParentDir, pathFiles);
       
        List<InputPart> list_fileName =  new ArrayList<InputPart>(); 
        list_fileName.add(inputNewName1);
        inputMap.put(fileName, list_fileName);
        
        String id_file = new String("1");
        String name = new String("file_test.txt");	
        
        String path_file = "/test_dir";
        com.imath.core.model.File file = new com.imath.core.model.File();
        file.setId(Long.valueOf(id_file));
        
        when(principal.getName()).thenReturn("userTest");
        when(input.getFormDataMap()).thenReturn(inputMap);
        when(inputPath1.getBodyAsString()).thenReturn(path_file);
        when(fileController.checkIfFileExistInUser(path_file, "userTest")).thenReturn(file);   
        when(inputNewName1.getBodyAsString()).thenReturn(name);
        
        PublicResponse.StateDTO response = data.REST_createFile(input, sc);        
        
        verify(inputPath1, times(1)).getBodyAsString();
        verify(inputNewName1, times(1)).getBodyAsString();
        verify(fileController, times(1)).checkIfFileExistInUser(path_file, "userTest");
        verify(fileController, times(1)).createFile(id_file, name, "regular", sc);
        
        assertTrue(response!=null);
   	 	assertTrue(response.code == Response.Status.ACCEPTED.getStatusCode());	       
    }
      
    
}
