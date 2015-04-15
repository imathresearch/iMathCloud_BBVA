package com.imath.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;



@Entity
public class UserJBoss implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Size(min = 4, max = 25, message = "4 to 25 letters")
    @Pattern(regexp = "[A-Za-z]*", message = "Only letters")
    @Column(name = "username", unique=true)
    private String userName;
	
	@Size(min = 2, max = 200, message = "2-200")
	private String password;
	
	public void setUsername(String userName){
		this.userName = userName;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public String getUsername(){
		return this.userName;
	}
	
	public String getPassword(){
		return this.password;
	}

}