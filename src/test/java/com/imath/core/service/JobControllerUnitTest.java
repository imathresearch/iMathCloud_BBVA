package com.imath.core.service;

import org.junit.matchers.JUnitMatchers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJBContext;
import javax.persistence.EntityManager;
import javax.ws.rs.core.SecurityContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.imath.core.data.FileDB;
import com.imath.core.data.JobDB;
import com.imath.core.data.MainServiceDB;
import com.imath.core.exception.IMathException;
import com.imath.core.exception.IMathException.IMATH_ERROR;
import com.imath.core.model.File;
import com.imath.core.model.IMR_User;
import com.imath.core.model.Job;
import com.imath.core.model.Job.States;
import com.imath.core.model.Session;
import com.imath.core.util.FileUtils;

public class JobControllerUnitTest {
    
    // The class that contains the code we want to unit test 
    // Must not be mocked
    private JobController jobController;        

    // We do not mock the MainServiceDB. We mock the inside elements.
    private MainServiceDB db;
    
    
    
    // Inject objects might be mocked. Not all of them, only the ones we need to control 
    // to test what we want to test
    @Mock
    private EntityManager em;
    
    @Mock
    private JobDB jobDB;
    
    @Mock
    private FileDB fileDB;
    
    @Mock 
    private FileUtils fileUtils;
    
    @Mock
    private FileController fc;
    
    @Mock 
    private Logger LOG;

    @Mock 
    private SecurityContext sc;                   // The security context
    
    @Mock 
    private Principal principal;                  // It contains logged info user

    @Mock
    private EJBContext ejb;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        // Create with new the class we want to test
        when(sc.getUserPrincipal()).thenReturn(principal);
        
        jobController = new JobController();
        db = new MainServiceDB();
        
        // We simulate the injections
        db.setEntityManager(em);    
        db.setJobDB(jobDB);
        db.setFileDB(fileDB);
       
        jobController.setMainServiceDB(db);
        jobController.setFileController(fc);
        jobController.setLog(LOG);
        jobController.setFileUtils(fileUtils);
        jobController.setEJB(ejb);
        
    }

    
    @Test
    public void testModifyJob() throws Exception {
        String descJob = "A job";
        
        Long idJob = 1L;
        Job job = new Job();
        Job jobNew = new Job();
        jobNew.setId(idJob);
        jobNew.setDescription(descJob);
        
        // When trying to access the job by id, we return the job we just created
        when(db.getJobDB().findById(1L)).thenReturn(job);

        // We execute the method
        jobController.modifyJob(jobNew, null); //If security Context is set to null, the access is granted always
        
        // We expect that job is persisted with the new description
        verify(em).persist(job);
        assertTrue(job.getDescription().equals(descJob));
    }
    
    @Test
    public void test1_reportJobFinalization() throws Exception{
    	
    	String json = "{\"files\": [[\"\", \"src\", \"examples\", \"5_example1.err\"], [\"\", \"src\", \"examples\", \"5_example1.out\"]], \"dirs\": [[\"\", \"src\", \"examples\"]]}";

    	Long idJob = 1L;
        Job job = new Job();
        job.setId(idJob);
        IMR_User user = new IMR_User();
        String userName = new String("test_user");
        user.setUserName(userName);
        Session s = new Session();
        s.setUser(user);
        job.setSession(s);
        job.setOwner(user);
        File dir_example = new File();
        dir_example.setId(1L);
        dir_example.setName("examples");
        
        File file_err = new File();
        file_err.setId(2L);
        file_err.setDir(dir_example);
        
        File file_out = new File();
        file_out.setId(3L);
        file_out.setDir(dir_example);
        
        File dir_src = new File();
        dir_src.setId(4L);
        dir_src.setName("/src");
        
        
        // When trying to access the job by id, we return the job we just created
        when(db.getJobDB().findById(1L)).thenReturn(job);
        
        when(fc.getParentDir("/src/examples", userName)).thenReturn(dir_src);
        when(fc.createNewFileInDirectory(dir_src,"examples", "dir")).thenReturn(dir_example);      
        when(fc.getParentDir("/src/examples/5_example1.err", userName)).thenReturn(dir_example);
        when(fc.getParentDir("/src/examples/5_example1.out", userName)).thenReturn(dir_example);      
        when(fc.createNewFileInDirectory(dir_example,"5_example1.err", "err")).thenReturn(file_err);
        when(fc.createNewFileInDirectory(dir_example,"5_example1.out", "out")).thenReturn(file_out);
        
      
        //function to be tested
        jobController.reportJobFinalization(idJob, States.FINISHED_OK, json);
        
        //checks
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
    	verify(em, times(1)).persist(jobCaptor.capture());
    	
    	List<Job> capturedJobs = jobCaptor.getAllValues();
    	assertEquals(json, capturedJobs.get(0).getJobResult().getJSON());
    	assertThat(capturedJobs.get(0).getOutputFiles(), JUnitMatchers.hasItem(file_err));
    	assertThat(capturedJobs.get(0).getOutputFiles(), JUnitMatchers.hasItem(file_out));
    	
    	assertTrue(capturedJobs.get(0).getOutputFiles().contains(dir_example));
    	assertTrue(capturedJobs.get(0).getOutputFiles().contains(file_err));
    	assertTrue(capturedJobs.get(0).getOutputFiles().contains(file_out));
    	
    	
    	
    }
 
 @Test 
 public void test2_reportJobFinalization() throws Exception{
    	
    	String json = "{\"files\": [], \"dirs\": []}";

    	Long idJob = 1L;
        Job job = new Job();
        job.setId(idJob);
        IMR_User user = new IMR_User();
        String userName = new String("test_user");
        user.setUserName(userName);
        Session s = new Session();
        s.setUser(user);
        job.setSession(s);
        job.setOwner(user);
       
        // When trying to access the job by id, we return the job we just created
        when(db.getJobDB().findById(1L)).thenReturn(job);
  
        //function to be tested
        jobController.reportJobFinalization(idJob, States.FINISHED_OK, json);
        
        //checks
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
    	verify(em, times(1)).persist(jobCaptor.capture());
    	
    	List<Job> capturedJobs = jobCaptor.getAllValues();
    	assertEquals(json, capturedJobs.get(0).getJobResult().getJSON());
    	assertTrue(capturedJobs.get(0).getOutputFiles().isEmpty());
    	
    	
    }
 
 @Test 
 public void test3_reportJobFinalization() throws Exception{
    	
    	String json = "{\"files\": [], \"dirs\": []}";

    	Long idJob = 1L;
        Job job = new Job();
        job.setId(idJob);
        IMR_User user = new IMR_User();
        String userName = new String("test_user");
        user.setUserName(userName);
        Session s = new Session();
        s.setUser(user);
        job.setSession(s);

       
        // When trying to access the job by id, we return the job we just created
        when(db.getJobDB().findById(1L)).thenReturn(job);
  
        //function to be tested
        jobController.reportJobFinalization(idJob, States.CANCELLED, json);
        
        //checks
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
    	verify(em, times(1)).persist(jobCaptor.capture());
    	
    	List<Job> capturedJobs = jobCaptor.getAllValues();
    	assertEquals(json, capturedJobs.get(0).getJobResult().getJSON());
    	assertTrue(capturedJobs.get(0).getState().equals(States.FINISHED_ERROR));
    	  	
    }
 
 @Test 
 public void test4_reportJobFinalization() throws Exception{
    	
    	String json = "{\"files\": [], \"dirs\": []}";

    	Long idJob = 1L;
        Job job = new Job();
        job.setId(idJob);
        IMR_User user = new IMR_User();
        String userName = new String("test_user");
        user.setUserName(userName);
        Session s = new Session();
        s.setUser(user);
        job.setSession(s);
       
        // When trying to access the job by id, we return the job we just created
        when(db.getJobDB().findById(1L)).thenThrow(new RuntimeException());
  
        try{
        	//function to be tested
        	jobController.reportJobFinalization(idJob, States.CANCELLED, json);
        
        }
        catch(Exception e){
        	assertTrue(e instanceof RuntimeException);
        }
     }
 
 	@Test
 	// the job is null, i.e does not exist
 	public void test1_removeOutputFileFromJob(){
 		
 		Long idJob = 1L;
 		String userName = "userTest";
 		Long idFile = 1L;
 		
 		when(db.getJobDB().findByIdSecured(idJob, userName)).thenReturn(null);
 		
 		try{
 			jobController.removeOutputFileFromJob(idJob, idFile, userName);
 		}
 		catch(IMathException e){
 			assertTrue(e.getIMATH_ERROR() == IMATH_ERROR.JOB_NOT_IN_OK_STATE);
 			
 		}
 	}
 	
 	@Test
 	// the file is null, i.e does not exist
 	public void test2_removeOutputFileFromJob(){
 		
 		String userName = "userTest";
 		Long idFile = 1L;
 		
 		Long idJob = 1L;
 		Job j = new Job();
 		j.setId(idJob);
 		
 		File f1 = new File();
 		f1.setId(idFile);
 		
 		Set<File> outputFiles = new HashSet<File>();
 		outputFiles.add(f1);
 		j.setOutputFiles(outputFiles);
 								
 		when(db.getJobDB().findByIdSecured(idJob, userName)).thenReturn(j);
 		when(db.getFileDB().findByIdSecured(idFile, userName)).thenReturn(null);
 		
 		try{
 			jobController.removeOutputFileFromJob(idJob, idFile, userName);
 		}
 		catch(IMathException e){
 			assertTrue(e.getIMATH_ERROR() == IMATH_ERROR.FILE_NOT_FOUND);
 			
 		}
 	}
 	
 	@Test
 	// HAPPY PATH, the file is deleted as output file
 	public void test3_removeOutputFileFromJob(){
 		
 		String userName = "userTest";
 		
 		Long idJob = 1L;
 		Job j = new Job();
 		j.setId(idJob);
 		
 		Long idFile1 = 1L;
 		Long idFile2 = 2L;
 		File f1 = new File();
 		f1.setId(idFile1);
 		File f2 = new File();
 		f2.setId(idFile2);
 		
 		Set<File> outputFiles = new HashSet<File>();
 		outputFiles.add(f1);
 		outputFiles.add(f2);
 		j.setOutputFiles(outputFiles);
 								
 		when(db.getJobDB().findByIdSecured(idJob, userName)).thenReturn(j);
 		when(db.getFileDB().findByIdSecured(idFile1, userName)).thenReturn(f1);
 		when(db.getFileDB().findByIdSecured(idFile1, userName)).thenReturn(f2);
 		
 		try{
 			jobController.removeOutputFileFromJob(idJob, idFile2, userName);
 			
 			assertTrue(j.getOutputFiles().size() == 1);
 			assertTrue(j.getOutputFiles().contains(f1));
 			
 		}
 		catch(IMathException e){ 			 			
 		}
 	}
 	
 	@Test
 	// the job is null, i.e does not exist
 	public void test1_removeJob(){
 		
 		Long idJob = 1L; 		
 		when(db.getJobDB().findById(idJob)).thenReturn(null);
 		 		
		try {
			jobController.removeJob(idJob);
		} catch (Exception e) {
			assertTrue(e instanceof IMathException);
			IMathException im = (IMathException) e;
			assertTrue(im.getIMATH_ERROR() == IMATH_ERROR.JOB_NOT_IN_OK_STATE);
		} 		 		
 	}
 	
 	@Test
 	// the process of physically removing the outputfiles of the job fails
 	public void test2_removeJob() throws Exception{
 		
 		Long idJob = 1L;
 		Job j = new Job();
 		j.setId(idJob);
 		
 		Long idFile1 = 1L;
 		Long idFile2 = 2L;
 		File f1 = new File();
 		f1.setId(idFile1);
 		File f2 = new File();
 		f2.setId(idFile2);
 		
 		Set<File> outputFiles = new HashSet<File>();
 		outputFiles.add(f1);
 		outputFiles.add(f2);
 		j.setOutputFiles(outputFiles);
 		
 		List<File> outputFilesList = new ArrayList<File>(outputFiles);
 		
 		when(db.getJobDB().findById(idJob)).thenReturn(j);
 		when(fileUtils.trashListFiles(outputFilesList)).thenThrow(new IMathException(IMathException.IMATH_ERROR.FILE_NOT_FOUND, "data/" +f1.getId()));
 		 		
		try {
			jobController.removeJob(idJob);
		} catch (Exception e) {
			assertTrue(e instanceof IMathException);
			IMathException im = (IMathException) e;
			assertTrue(im.getIMATH_ERROR() == IMATH_ERROR.FILE_NOT_FOUND);
		} 		 		
 	}
 	
 	@Test
 	// happy path
 	public void test3_removeJob() throws Exception{
 		
 		Long idJob = 1L;
 		Job j = new Job();
 		j.setId(idJob);
 		
 		Long idFile1 = 1L;
 		Long idFile2 = 2L;
 		File f1 = new File();
 		f1.setId(idFile1);
 		File f2 = new File();
 		f2.setId(idFile2);
 		
 		Set<File> outputFiles = new HashSet<File>();
 		outputFiles.add(f1);
 		outputFiles.add(f2);
 		j.setOutputFiles(outputFiles);
 		
 		List<File> outputFilesList = new ArrayList<File>(outputFiles);
 		List<String> trashLocation = new ArrayList<String>();
 		String f1_trashlocation = "/trash/f1";
 		String f2_trashlocation = "/trash/f2";
 		trashLocation.add(f1_trashlocation);
 		trashLocation.add(f2_trashlocation);
 		
 		when(db.getJobDB().findById(idJob)).thenReturn(j);
 		when(fileUtils.trashListFiles(outputFilesList)).thenReturn(trashLocation);
 		 		
		try {
			jobController.removeJob(idJob);
			
			verify(db, times(1)).remove(Matchers.eq(j));
		
		} catch (Exception e) {
			
		} 		 		
 	}
 	
 	
 	
 	
 
    
}
