package com.imath.core.model;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity implementation class for Entity: Role
 *
 */
@Entity
public class Role implements Serializable {

	   
	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	@Size(min = 1, max = 25, message = "1-25 letters and spaces")
	private String shortName;
	
	private String longName;
	
    @ManyToMany
    @JoinTable(name="role_mathgroups")
	private Set<MathGroup> mathGroups;
    
	private static final long serialVersionUID = 1L;

	public Role() {
		super();
	}   
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}   
	public String getShortName() {
		return this.shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}   
	public String getLongName() {
		return this.longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}
	
	public Set<MathGroup> getMathGroups() {
		return this.mathGroups;
	}
}
