/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.imath.core.model.Job.States;

public class JobTest {

	private Job job;
	private String desc = "DESCRTIPTION";
	private Date endDate = new Date(1000);
	private Date startDate = new Date(1001);
	private File file1 = new File();
	private File file2 = new File();
	private Set<File> files = new HashSet<File>();
	private Set<File> filesSource = new HashSet<File>();
	private Set<File> filesOutput = new HashSet<File>();
	private Session session = new Session();
	private IMR_User owner = new IMR_User();
	private Host hosted = new Host();
	private JobResult jobResult = new JobResult();
	
	@Before
	public void setUp() throws Exception {
		job = new Job();
		job.setDescription(desc);
		job.setEndDate(endDate);
		job.setStartDate(startDate);
		files.add(file1);
		files.add(file2);
		job.setFiles(files);
		filesSource.add(file1);
		filesSource.add(file2);
		job.setSourceFiles(filesSource);
		filesOutput.add(file1);
		filesOutput.add(file2);
		job.setOutputFiles(filesOutput);
		job.setSession(session);
		job.setOwner(owner);
		job.setHosted(hosted);
		job.setJobResult(jobResult);
		job.setState(States.CREATED);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetOwner() {
		assertTrue(job.getOwner() == owner);
	}

	@Test
	public void testGetHosted() {
		assertTrue(job.getHosted()== hosted);
	}

	@Test
	public void testGetStartDate() {
		assertTrue(job.getStartDate()== startDate);
		assertTrue(job.getStartDate().getTime()== startDate.getTime());
	}

	@Test
	public void testGetEndDate() {
		assertTrue(job.getEndDate()== endDate);
		assertTrue(job.getEndDate().getTime()== endDate.getTime());
	}

	@Test
	public void testGetSession() {
		assertTrue(job.getSession()==session);
	}

	@Test
	public void testGetDescription() {
		assertTrue(job.getDescription()==desc);
		assertTrue(job.getDescription().equals(desc));
	}

	@Test
	public void testGetFiles() {
		assertTrue(job.getFiles()==files);
		assertTrue(job.getFiles().size()==2);
		Iterator<File> it = job.getFiles().iterator();
		File a1 = it.next();
		File a2 = it.next();
		assertTrue(a1==file1 || a1 == file2);
		assertTrue(a2==file1 || a2 == file2);
	}

	@Test
	public void testGetOutputFiles() {
		assertTrue(job.getOutputFiles()==filesOutput);
		assertTrue(job.getOutputFiles().size()==2);
		Iterator<File> it = job.getOutputFiles().iterator();
		File a1 = it.next();
		File a2 = it.next();
		assertTrue(a1==file1 || a1 == file2);
		assertTrue(a2==file1 || a2 == file2);
	}

	@Test
	public void testGetSourceFiles() {
		assertTrue(job.getSourceFiles()==filesSource);
		assertTrue(job.getSourceFiles().size()==2);
		Iterator<File> it = job.getSourceFiles().iterator();
		File a1 = it.next();
		File a2 = it.next();
		assertTrue(a1==file1 || a1 == file2);
		assertTrue(a2==file1 || a2 == file2);
	}

	@Test
	public void testGetState() {
		assertTrue(job.getState()==States.CREATED);
	}

	@Test
	public void testGetJobResult() {
		assertTrue(job.getJobResult()==jobResult);
	}

}
