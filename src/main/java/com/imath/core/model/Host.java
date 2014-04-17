package com.imath.core.model;

import java.io.Serializable;
import java.lang.Boolean;
import java.lang.String;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Host
 *
 */
@Entity

public class Host implements Serializable {

	   
	@Id
	@GeneratedValue
	private Long id;
	
	private String url;
	private Boolean active;
	private Boolean console;
	private String alias;
	private static final long serialVersionUID = 1L;

	public Host() {
		super();
	}   
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}   
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}   
	public Boolean getActive() {
		return this.active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}   
	public Boolean getConsole() {
		return this.console;
	}

	public void setIsConsole(Boolean console) {
		this.console = console;
	}   
	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public void copyValues(Host host) {
		this.setActive(host.getActive());
		this.setAlias(host.getAlias());
		this.setIsConsole(host.getConsole());
		this.setUrl(host.getUrl());
	}
   
}
