package com.imath.core.model;

import java.io.Serializable;
import java.lang.Long;
import java.lang.String;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Entity implementation class for Entity: Role
 *
 */
@Entity
@SequenceGenerator(name="seqJobResult", initialValue=5, allocationSize=1)
public class JobResult implements Serializable {

	   
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seqJobResult")
	private Long id;
	
	@NotNull
	//@Column (columnDefinition="TEXT")
	@Column(length=1024)
	private String json;
	
	private static final long serialVersionUID = 1L;
	
	public JobResult() {
		super();
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getJSON() {
		return this.json;
	}

	public void setJSON(String json) {
		this.json = json;
	}      
}
