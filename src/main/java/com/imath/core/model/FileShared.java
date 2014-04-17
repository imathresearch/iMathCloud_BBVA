package com.imath.core.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Entity implementation class for Entity: FileShared
 * It represents a ManyToMany relation with the entities File and IMR_User with attributes 
 */

@Entity
public class FileShared implements Serializable{
	
	public static enum Permission {READONLY, READWRITE};
	
	@Id
	@GeneratedValue
	private Long id;
	
	// Each entry indicates that the used 'userSharedWith' has access to 'fileShared'
	@ManyToOne(optional=false) 
    @JoinColumn(name="idUser", nullable=false, updatable=false)
	private IMR_User userSharedWith;		
	
	@ManyToOne(optional=false)
    @JoinColumn(name="idFile", nullable=false)
	private File fileShared;
	
	private Permission permission; 
	
    private static final long serialVersionUID = 1L;
    
    public FileShared() {
		super();
	}   
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Permission getPermission() {
		return this.permission;
	}
	
	public void setPermission(Permission permission) {
		this.permission = permission;
	}
	
	public File getFileShared() {
		return this.fileShared;
	}
	
	public void setFileShared(File fileShared) {
		this.fileShared = fileShared;
	}
	
	public IMR_User getUserSharedWith() {
		return this.userSharedWith;
	}
	
	public void setUserSharedWith(IMR_User userSharedWith) {
		this.userSharedWith = userSharedWith; 
	}
}
