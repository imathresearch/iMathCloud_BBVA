package com.imath.core.model;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Entity implementation class for Entity: Role
 *
 */
@Entity
public class MathGroup implements Serializable {

	   
	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	private String description;
	
	@NotNull
	private String plugin;
	
    @ManyToMany(mappedBy="mathGroups")
    private Set<Role> roles;
    
	private static final long serialVersionUID = 1L;
	
	public MathGroup() {
		super();
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getPlugin() {
	    return this.plugin;
	}
	
	public void setPlugin(String plugin) {
	    this.plugin = plugin;
	}
	
	public Set<Role> getRoles() {
		return this.roles;
	}
}
