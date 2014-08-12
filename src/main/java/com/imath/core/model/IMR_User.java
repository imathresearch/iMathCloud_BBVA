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
@Table(name = "IMR_User", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class IMR_User implements Serializable {
	
    public static long DEFAULT_STORAGE = 10;    // 10 MiB = 10 * 2^20 bytes
	@Id
	@NotNull
	@Size(min = 4, max = 25, message = "4 to 25 letters")
	@Pattern(regexp = "[A-Za-z]*", message = "Only letters")
	private String userName;
	
	private String lastName;
	private String firstName;
	private String organization;
	
	@NotNull
	@NotEmpty
	@Email(message = "Invalid format")
	private String eMail;
	
	@Size(min = 9, max = 15, message = "9-15 Numbers")
	@Digits(fraction = 0, integer = 15, message = "Not valid")
	private String phone1;
	
	@Size(min = 9, max = 15, message = "9-15 Numbers")
	@Digits(fraction = 0, integer = 15, message = "Not valid")
	private String phone2;
	
	@ManyToOne(optional=false) 
    @JoinColumn(name="idRole", nullable=false, updatable=false)
	private Role role;
	
	@ManyToOne(optional=false) 
    @JoinColumn(name="idMathLanguage", nullable=false, updatable=true)
	private MathLanguage mathLanguage;
	
	@NotNull
	private long storage = 10;
	
	private static final long serialVersionUID = 1L;

	public IMR_User() {
		super();
	}   
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}   
	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}   
	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}   
	public String getOrganization() {
		return this.organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}   
	public String getEMail() {
		return this.eMail;
	}

	public void setEMail(String eMail) {
		this.eMail = eMail;
	}   
	public String getPhone1() {
		return this.phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}   
	public String getPhone2() {
		return this.phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}
	
	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	public MathLanguage getMathLanguage() {
		return this.mathLanguage;
	}
	
	public void setMathLanguage(MathLanguage mathLanguage) {
		this.mathLanguage=mathLanguage;
	}
	
	public long getStorage() {
	    return this.storage;
	}
   
	public void setStorage(long storage) {
	    this.storage = storage;
	}
}
