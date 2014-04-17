package com.imath.core.service;

import org.junit.matchers.JUnitMatchers;
import org.junit.matchers.JUnitMatchers.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.imath.core.data.JobDB;
import com.imath.core.data.MainServiceDB;
import com.imath.core.model.File;
import com.imath.core.model.IMR_User;
import com.imath.core.model.Job;
import com.imath.core.model.Job.States;
import com.imath.core.model.Session;

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
    private FileController fc;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        // Create with new the class we want to test
        jobController = new JobController();
        db = new MainServiceDB();
        
        // We simulate the injections
        db.setEntityManager(em);    
        db.setJobDB(jobDB);
       
        jobController.setMainServiceDB(db);
        jobController.setFileController(fc);
        
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
    	verify(em, times(2)).persist(jobCaptor.capture());
    	
    	List<Job> capturedJobs = jobCaptor.getAllValues();
    	assertEquals(json, capturedJobs.get(1).getJobResult().getJSON());
    	assertThat(capturedJobs.get(1).getOutputFiles(), JUnitMatchers.hasItem(file_err));
    	assertThat(capturedJobs.get(1).getOutputFiles(), JUnitMatchers.hasItem(file_out));
    	
    	for (File f : capturedJobs.get(1).getOutputFiles()){
    		assertTrue(f.getDir().equals(dir_example));
    	}
    	
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
       
        // When trying to access the job by id, we return the job we just created
        when(db.getJobDB().findById(1L)).thenReturn(job);
  
        //function to be tested
        jobController.reportJobFinalization(idJob, States.FINISHED_OK, json);
        
        //checks
        ArgumentCaptor<Job> jobCaptor = ArgumentCaptor.forClass(Job.class);
    	verify(em, times(2)).persist(jobCaptor.capture());
    	
    	List<Job> capturedJobs = jobCaptor.getAllValues();
    	assertEquals(json, capturedJobs.get(1).getJobResult().getJSON());
    	assertTrue(capturedJobs.get(1).getOutputFiles().isEmpty());
    	
    	
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
    	verify(em, times(2)).persist(jobCaptor.capture());
    	
    	List<Job> capturedJobs = jobCaptor.getAllValues();
    	assertEquals(json, capturedJobs.get(1).getJobResult().getJSON());
    	assertTrue(capturedJobs.get(1).getState().equals(States.FINISHED_ERROR));
    	  	
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
 
    /*
    @Test
    //reportJobPythonFinalization    
    public void testReportJobPythonFinalization() throws Exception {
    	
    	String json = "{\"files\": [[\"\", \"src\", \"examples\", \"5_example1.err\"], [\"\", \"src\", \"examples\", \"5_example1.out\"]], \"dirs\": []}";

    	Long idJob = 1L;
        Job job = new Job();
        Job jobNew = new Job();
        jobNew.setId(idJob);
        
        // When trying to access the job by id, we return the job we just created
        when(db.getJobDB().findById(1L)).thenReturn(job);
        
        jobController.reportJobPythonFinalization(idJob, States.FINISHED_OK, json);
        
    	
    
    }
    */
}
