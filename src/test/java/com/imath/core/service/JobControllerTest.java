package com.imath.core.service;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

//import com.google.common.io.Resources;
import com.imath.core.data.MainServiceDB;
import com.imath.core.exception.IMathException;
import com.imath.core.exception.IMathException.IMATH_ERROR;
import com.imath.core.model.*;
import com.imath.core.model.File.Sharing;
import com.imath.core.model.Job.States;
import com.imath.core.data.*;
import com.imath.core.security.SecurityOwner;
import com.imath.core.security.SecurityManager;

import com.imath.core.util.*;

@RunWith(Arquillian.class)
public class JobControllerTest {
	@Inject HostController hc;
	@Inject JobController jc;
	@Inject FileController fc;
	@Inject MathLanguageController mlc;
	@Inject UserController uc;
	@Inject RoleController rc;
	@Inject MainServiceDB db;
	@Inject Logger log;
    
    
	private Host host;
	private Host host2;
	private IMR_User owner;
	private File file;
	private Job job;
	
	@Deployment
	   public static Archive<?> createTestArchive() {
	      return ShrinkWrap.create(WebArchive.class, "test.war")
	            .addClasses(Job.class, JobController.class, Host.class, HostController.class, File.class, IMR_User.class,
	            		IMathException.class, Role.class, MathLanguage.class, Sharing.class, IMATH_ERROR.class,
	            		AbstractController.class, MainServiceDB.class, SecurityOwner.class, SecurityManager.class, FileController.class,
	            		DeploymentController.class, MathGroup.class, MathFunction.class, Session.class, JobResult.class, FileShared.class,
	            		HostDB.class, FileDB.class, IMR_UserDB.class, JobDB.class, SessionDB.class, MathFunctionDB.class,
	            		FileSharedDB.class, Resources.class, JSONPRequestFilter.class, MathLanguageController.class, RoleController.class,
	            		UserController.class)
	            .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
	            .addAsWebInfResource("arquillian-ds.xml")
	            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	   }
	
	@Before
	public void setUp() throws Exception {
		host = hc.createNewHost("localhost", false, true, "127.0.0.1");
		host2 = hc.createNewHost("localhost2", true, true, "127.0.0.2");
		owner = createUser("test");
		file = createFile();
		job = createJob();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExecuteJob() {
		// We try the normal case. 
		try {
			job = jc.executeJob(job.getId(), null);
		} catch (Exception e) {
			fail("Unexpected Exception");
		}
		assertTrue(job.getHosted().getId() == host2.getId());
		assertTrue(job.getState() == States.RUNNING);
		
		// we empty the source files (assigned to null)
		try {
			emptySourceFiles();
			job = jc.executeJob(job.getId(), null);
			fail("An Exception was expected!");
		} catch (IMathException e) {
			assertTrue(e.getIMATH_ERROR() == IMATH_ERROR.NO_SOURCE_FILES);
			try {
				job = jc.getJobById(job.getId(), null);
			} catch (Exception ee) {
				fail("Unexpected Exception");
			}
			assertTrue(job.getState()==States.CANCELLED);
		}
		catch (Exception e) {
			fail("Unexpected Exception");
		}
		
		// we do the same, but considering the list of files as empty (so, not null)
		try {
			emptySourceFiles2();
			job = jc.executeJob(job.getId(), null);
			fail("An Exception was expected!");
		} catch (IMathException e) {
			assertTrue(e.getIMATH_ERROR() == IMATH_ERROR.NO_SOURCE_FILES);
			try {
				job = jc.getJobById(job.getId(), null);
			} catch (Exception ee) {
				fail("Unexpected Exception");
			}
			assertTrue(job.getState()==States.CANCELLED);
		} catch (Exception e) {
			fail("Unexpected Exception");
		}
		
		// Now we set the host2 to not available and we assign again some source files, so no hosts are available and an exception must be thrown 
		try {
			modifyHost();
			job = jc.executeJob(job.getId(), null);
			fail("An Exception was expected!");
		} catch (IMathException e) {
			System.err.println(e.getMessage());
			assertTrue(e.getIMATH_ERROR() == IMATH_ERROR.NO_AVAILABLE_HOST);
			try {
				job = jc.getJobById(job.getId(), null);
			} catch (Exception ee) {
				fail("Unexpected Exception");
			}
			assertTrue(job.getState()==States.CANCELLED);
		} catch (Exception e) {
			fail("Unexpected Exception");
		}

	}

	private void modifyHost() throws Exception{
		host2.setActive(false);
		host2= hc.modifyHost(host2);
		Set<File> files = new HashSet<File>();
		files.add(file);
		job.setSourceFiles(files);
		job = jc.modifyJob(job, null);
	}
	
	private void emptySourceFiles() throws Exception{
		job.setSourceFiles(null);
		job = jc.modifyJob(job, null);
	}
	
	private void emptySourceFiles2() throws Exception{
		job.setSourceFiles(new HashSet<File>());
		job = jc.modifyJob(job, null);
	}
	
	private Job createJob() throws Exception{
		Set<File> files = new HashSet<File>();
		files.add(file);
		Job job = jc.createJob(owner, "test", files, new Date(1000));
		return job;
	}
	
	private File createFile() throws Exception{
		File root = fc.createNewFile(null, "root", "dir",owner);
		File file = fc.createNewFile(root, "test.py", "py", owner);
		return file;
	}
	
	private IMR_User createUser(String userName) throws Exception{
		Role role = rc.createNewRole("admin", "admin");
		MathLanguage math = mlc.createNewMathLanguage("Python", "code", "1.7.2");
		IMR_User user = uc.createNewUser(userName, "test", "test", role, math, "test@test.com");
		return user;
	}
}
