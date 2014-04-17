package com.imath.core.model;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity implementation class for Entity: User
 *
 */
@Entity
public class MathFunction implements Serializable {

	   
	@Id
	@NotNull
	private Long id;
	
	private String description;
	private String shortName;
	private String serviceName;
	private String params;
		
	@ManyToOne(optional=false) 
    @JoinColumn(name="idMathGroup", nullable=false, updatable=false)
	private MathGroup mathGroup;
	
	private static final long serialVersionUID = 1L;

	public MathFunction() {
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
	
	public String getShortName() {
		return this.shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}   
	
	public MathGroup getMathGroup() {
		return this.mathGroup;
	}
	
	public void setMathGroup(MathGroup mathGroup) {
		this.mathGroup=mathGroup;
	}
	
	public String getServiceName() {
		return this.serviceName;
	}
	
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public String getParams() {
		return this.params;
	}
	
	public void setParams(String params) {
		this.params = params;
	}
}
