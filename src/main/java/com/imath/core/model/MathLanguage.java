package com.imath.core.model;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Entity implementation class for Entity: User
 *
 */
@Entity
public class MathLanguage {

	@Id
	@GeneratedValue
	private Long id;
	
	private String baseName;
	private String version;
	private String consoleCode;	// The code used in the console notebook to treat the language 
	
	private static final long serialVersionUID = 1L;
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getBaseName() {
		return this.baseName;
	}
	
	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getConsoleCode() {
		return this.consoleCode;
	}
	
	public void setConsoleCode(String consoleCode) {
		this.consoleCode = consoleCode;
	}
}
