package com.imath.core.service;
/*// These class has been commented because it is unused and to avoid test failures
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.imath.core.data.FileDB;
import com.imath.core.data.JobDB;
import com.imath.core.data.MainServiceDB;
import com.imath.core.exception.IMathException;
import com.imath.core.model.File;
import com.imath.core.model.Job;


public class JobPythonControllerUnitTest {
	
	// The class that contains the code we want to unit test 
    // Must not be mocked
    private JobPythonController jobpythonController;        

    // We do not mock the MainServiceDB. We mock the inside elements.
    private MainServiceDB db;
    
    // Inject objects might be mocked. Not all of them, only the ones we need to control 
    // to test what we want to test
    @Mock
    private EntityManager em;
    
    @Mock
    private JobDB jobDB;
    
    @Mock
    private JobController jobController;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        // Create with new the class we want to test
        jobpythonController = new JobPythonController();
        db = new MainServiceDB();
        
        // We simulate the injections
        db.setEntityManager(em);    
        db.setJobDB(jobDB);
        jobpythonController.setMainServiceDB(db);
        
    }
    
    

}*/
