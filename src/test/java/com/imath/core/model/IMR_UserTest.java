package com.imath.core.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IMR_UserTest {
	
	private IMR_User user;
	private String eMail = "test@test.com";
	private String firstName = "testFirstName";
	private String lastName = "testLastName";
	private MathLanguage mathLanguage = new MathLanguage();
	private String organization = "testOrganization";
	private String phone1="938888888";
	private String phone2="939999999";
	private Role role = new Role();
	private String userName = "testUserName";
	
	@Before
	public void setUp() throws Exception {
		user = new IMR_User();
		user.setEMail(eMail);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setMathLanguage(mathLanguage);
		user.setOrganization(organization);
		user.setPhone1(phone1);
		user.setPhone2(phone2);
		user.setRole(role);
		user.setUserName(userName);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetUserName() {
		assertTrue(user.getUserName().equals(userName));
	}

	@Test
	public void testGetLastName() {
		assertTrue(user.getLastName().equals(lastName));
	}

	@Test
	public void testGetFirstName() {
		assertTrue(user.getFirstName().equals(firstName));
	}

	@Test
	public void testGetOrganization() {
		assertTrue(user.getOrganization().equals(organization));
	}

	@Test
	public void testGetEMail() {
		assertTrue(user.getEMail().equals(eMail));
	}

	@Test
	public void testGetPhone1() {
		assertTrue(user.getPhone1().equals(phone1));
	}

	@Test
	public void testGetPhone2() {
		assertTrue(user.getPhone2().equals(phone2));
	}

	@Test
	public void testGetRole() {
		assertTrue(user.getRole()==role);
	}

	@Test
	public void testGetMathLanguage() {
		assertTrue(user.getMathLanguage()==mathLanguage);
	}

}
