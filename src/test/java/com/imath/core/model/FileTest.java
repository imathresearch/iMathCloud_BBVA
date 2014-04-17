/* (C) 2014 iMath Research S.L. - All rights reserved.  */

package com.imath.core.model;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.imath.core.model.File.Sharing;

public class FileTest {
	
	private File file;
	private Long id =  new Long(-200);
	private String name = "testfile";
	private String url = "file://localhost/test/";
	private String imr_type = "ROOT";
	private IMR_User owner = new IMR_User();
	private File dir = new File();
	private Job job1 = new Job();
	private Job job2 = new Job();
	private Set<Job> jobs = new HashSet<Job>();
	private Set<Job> outputJobs = new HashSet<Job>();
	private Set<Job> sourceJobs = new HashSet<Job>();
	
	
	@Before
	public void setUp() throws Exception {
		file = new File();
		file.setId(id);
		file.setName(name);
		file.setUrl(url);
		file.setIMR_Type(imr_type);
		file.setOwner(owner);
		file.setSharingState(Sharing.YES);
		file.setDir(dir);
		
		jobs.add(job1);
		jobs.add(job2);
		file.setJobs(jobs);
		
		outputJobs.add(job1);
		outputJobs.add(job2);
		file.setOutputJobs(outputJobs);
		
		sourceJobs.add(job1);
		sourceJobs.add(job2);
		file.setSourceJobs(sourceJobs);
		
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testGetId() {
		assertTrue(file.getId() == id);
	}
	
	@Test
	public void testGetName() {
		assertTrue(file.getName() == name);
	}
	
	@Test
	public void testGetUrl(){
		assertTrue(file.getUrl() == url);
	}
	
	@Test
	public void testGetIMR_Type(){
		assertTrue(file.getIMR_Type() == imr_type);
	}
	
	@Test
	public void testGetOwner(){
		assertTrue(file.getOwner() == owner);
	}
	
	@Test
	public void testGetSharing(){
		assertTrue(file.getSharingState() == Sharing.YES);
	}
	
	@Test
	public void testGetDir(){
		assertTrue(file.getDir() == dir);
	}
	
	@Test
	public void testGetJobs(){
		assertTrue(file.getJobs()==jobs);
		assertTrue(file.getJobs().size()==2);
		Iterator<Job> it = file.getJobs().iterator();
		Job j1 = it.next();
		Job j2 = it.next();
		assertTrue(j1==job1 || j1 == job2);
		assertTrue(j2==job1 || j2 == job2);
	}
	
	@Test
	public void testGetOutputJobs(){
		assertTrue(file.getOutputJobs()==outputJobs);
		assertTrue(file.getOutputJobs().size()==2);
		Iterator<Job> it = file.getOutputJobs().iterator();
		Job j1 = it.next();
		Job j2 = it.next();
		assertTrue(j1==job1 || j1 == job2);
		assertTrue(j2==job1 || j2 == job2);
	}
	
	@Test
	public void testGetSourceJobs(){
		assertTrue(file.sourceJobs()==sourceJobs);
		assertTrue(file.sourceJobs().size()==2);
		Iterator<Job> it = file.sourceJobs().iterator();
		Job j1 = it.next();
		Job j2 = it.next();
		assertTrue(j1==job1 || j1 == job2);
		assertTrue(j2==job1 || j2 == job2);
	}
}
