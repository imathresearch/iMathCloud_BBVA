/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.model;

import java.io.Serializable;
import java.lang.String;

import javax.persistence.*;

import com.imath.core.security.SecurityOwner;

import java.util.Set;
/**
 * Entity implementation class for Entity: File
 *
 */
@Entity
public class File implements Serializable, SecurityOwner {
	
	public static enum Sharing {NO, YES};	// Prepared to handle other global states
	
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	
	@Column(unique=true)
	private String url;
	
	@Column(name="imr_type")
	private String imr_Type;
    
	@ManyToOne(optional=false) 
    @JoinColumn(name="idUserOwner", nullable=false, updatable=false)
	private IMR_User owner;
    
	@ManyToOne(optional=true)
    @JoinColumn(name="id_dir", nullable=true)
	private File dir;
	
	// The jobs in which the file has been part as data 
    @ManyToMany(mappedBy="files")
    private Set<Job> jobs;
    
    // The jobs in which the file has been part as output file. In principle it should be only one...left it for future versions
    @ManyToMany(mappedBy="outputFiles")
    //@ManyToMany( fetch = FetchType.LAZY, mappedBy = "outputFiles")//, cascade = CascadeType.ALL) 
    private Set<Job> outputJobs;
    
    // The jobs in which the file has been part as source code
    @ManyToMany(mappedBy="sourceFiles")
    private Set<Job> sourceJobs;
    
    @Column(name="openbyUser")
    private String openbyUser = null;
    
    
    private Sharing sharingState;	// Only for folders. It indicates the sharing state: NO, YES. for the future, it may indicate that the folder is shared by only a group of people, or to everybody in the group etc...
    
    private static final long serialVersionUID = 1L;

	public File() {
		super();
	}   
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}   
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}   
	public String getUrl() {
		return this.url;
	}
	
	public String getOpenByUser(){
		return this.openbyUser;
	}

	public void setUrl(String url) {
		this.url = url;
	}
       
    public String getPath() throws java.net.MalformedURLException {
		return new java.net.URL(this.url).getPath();
	}

	public String getIMR_Type() {
		return this.imr_Type;
	}

	public void setIMR_Type(String imr_Type) {
		this.imr_Type = imr_Type;
	}
   
	public IMR_User getOwner() {
		return this.owner;
	}

	public void setOwner(IMR_User owner) {
		this.owner = owner;
	}
	
	public File getDir() {
		return this.dir;
	}
	
	public void setDir(File dir) {
		this.dir = dir;
	}
	
	public Set<Job> getJobs() {
		return this.jobs;
	}
	
	public Set<Job> getOutputJobs() {
		return this.outputJobs;
	}
	
	public Set<Job> sourceJobs() {
		return this.sourceJobs;
	}
	
	public void setOpenByUser(String user){
		this.openbyUser = user;
	}
	
	//included by amartinez
	
	public void setJobs(Set<Job> jobs){
		this.jobs = jobs;
	}
	
	public void setOutputJobs(Set<Job> OutputJobs){
		this.outputJobs = OutputJobs;
	}
	
	public void setSourceJobs(Set<Job> SourceJobs){
		this.sourceJobs = SourceJobs;
	}
	
	public Sharing getSharingState() {
		return this.sharingState;
	}
	
	public void setSharingState(Sharing sharingState) {
		this.sharingState = sharingState;
	}
	
	// Security purpose
	public IMR_User getOwnerSecurity() {
		return this.getOwner();
	}
}
