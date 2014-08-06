package com.imath.core.model;

import java.io.Serializable;
import java.lang.String;

import javax.persistence.*;

import com.imath.core.security.SecurityOwner;

import java.util.Date;
import java.util.Set;
/*
 * Entity implementation class for Entity: Job
 *
 */
@Entity
@SequenceGenerator(name="seqJob", initialValue=5, allocationSize=1)
public class Job implements Serializable, SecurityOwner {
	
	/**
	 * The possible states of a {@link Job} 
	 * @author ipinyol
	 */
	public static enum States {CREATED, RUNNING, PAUSED, CANCELLED, FINISHED_OK, FINISHED_ERROR};
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seqJob")	
	private Long id;
	
	@ManyToOne(optional=false) 
    @JoinColumn(name="idUserOwner", nullable=false, updatable=false)
	private IMR_User owner;
    
    @ManyToOne(optional=true) 
    @JoinColumn(name="idHost", nullable=true, updatable=true)
	private Host hosted;
    
    @ManyToOne(optional=true) 
    @JoinColumn(name="idSession", nullable=true, updatable=true)
	private Session session;
    
    @ManyToMany
    @JoinTable(name="file_jobs")
	private Set<File> files;
	
    //@ManyToMany
    @JoinTable(name="output_file_jobs")
    @ManyToMany( fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }  )	
    //@JoinTable(
    //        name = "output_file_jobs",
    //        joinColumns = {@JoinColumn(name = "id_job")},
    //        inverseJoinColumns = {@JoinColumn(name = "id_file")}
    //)
	private Set<File> outputFiles;
    
    @ManyToMany
    @JoinTable(name="source_file_jobs")
	private Set<File> sourceFiles;
    
    @Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	private String description;
	
	@Column(nullable=false)
	private States state;
	
    //@ManyToOne(optional=true) 	 
    //@JoinColumn(name="idJobResult", nullable=true, updatable=true)
	@OneToOne(cascade = CascadeType.ALL)  
	private JobResult jobResult;
    
	private static final long serialVersionUID = 1L;

	public Job() {
		super();
	}   
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}   
	public IMR_User getOwner() {
		return this.owner;
	}

	public void setOwner(IMR_User owner) {
		this.owner = owner;
	}   
	public Host getHosted() {
		return this.hosted;
	}

	public void setHosted(Host hosted) {
		this.hosted = hosted;
	}   
	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}   
	
	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}     
	
	public Session getSession() {
		return this.session;
	}

	public void setSession(Session session) {
		this.session = session;
	}  
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public Set<File> getFiles() {
		return this.files;
	}
	
	public void setFiles(Set<File> files) {
		this.files = files;
	}
	
	public Set<File> getOutputFiles() {
		return this.outputFiles;
	}
	
	public void setOutputFiles(Set<File> outputFiles) {
		this.outputFiles = outputFiles;
	}
	
	public Set<File> getSourceFiles() {
		return this.sourceFiles;
	}
	
	public void setSourceFiles(Set<File> sourceFiles) {
		this.sourceFiles = sourceFiles;
	}
	
	public States getState() {
		return this.state;
	}
	
	public void setState(States state) {
		this.state = state;
	}
	
	public JobResult getJobResult() {
		return this.jobResult;
	}
	
	public void setJobResult(JobResult jobResult) {
		this.jobResult = jobResult;
	}
	
	public void copyValues(Job job) {
		this.setEndDate(job.getEndDate());
		this.setFiles(job.getFiles());
		this.setHosted(job.getHosted());
		this.setJobResult(job.getJobResult());
		this.setOutputFiles(job.getOutputFiles());
		this.setOwner(job.getOwner());
		this.setSession(job.getSession());
		this.setSourceFiles(job.getSourceFiles());
		this.setStartDate(job.getStartDate());
		this.setState(job.getState());
		this.setDescription(job.getDescription());
	}
	// Security purpose
	public IMR_User getOwnerSecurity() {
		return this.getOwner();
	}
}
