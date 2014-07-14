package com.imath.core.model;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;
/**
 * Entity implementation class for Entity: Session
 *
 */
@Entity
@SequenceGenerator(name="seqSession", initialValue=5, allocationSize=1)
public class Session implements Serializable {

	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seqSession")
	private Long id;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=true)
	private Date endDate;
	
    @ManyToOne(optional=false) 
    @JoinColumn(name="idUser", nullable=false, updatable=false)
	private IMR_User user;
	
    @ManyToOne(optional=true) 
    @JoinColumn(name="idHostConsole", nullable=false, updatable=false)
	private Host hostConsole;
    
    @Column(nullable=true)
    private int portConsole;
    
	private static final long serialVersionUID = 1L;

	public Session() {
		super();
	}  
	
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Date getStartDate() {
		return this.startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate=startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate=endDate;
	}
	
	public IMR_User getUser() {
		return this.user;
	}
	public void setUser(IMR_User user) {
		this.user=user;
	}
	
	public Host getHostConsole() {
		return this.hostConsole;
	}
	public void setHostConsole(Host hostConsole) {
		this.hostConsole=hostConsole;
	}
	
	public int getPortConsole() {
	    return this.portConsole;
	}
	
	public void setPortConsole(int portConsole) {
	    this.portConsole = portConsole;
	}
}
