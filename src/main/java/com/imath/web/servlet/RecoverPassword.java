package com.imath.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imath.core.data.MainServiceDB;
import com.imath.core.model.IMR_User;
import com.imath.core.service.UserController;
import com.imath.core.util.Mail;
import com.imath.core.util.Security;

import java.util.UUID;


public class RecoverPassword extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Inject UserController userController;
    @Inject MainServiceDB db;
    @Inject Security security;
    
    
    // imathcloud943793072
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	//Encryptor.init();
    	
        String eMail = request.getParameter("emailsignup");
        IMR_User user;
		try {
			user = db.getIMR_UserDB().findByEMail(eMail); 
		} catch (Exception e1) {
			response.sendRedirect("recoverPasswordError.html");
        	return;
		}
        
        if(user == null){
        	response.sendRedirect("recoverPasswordError.html");
        	return;
        }
        
        // Generate an random password
        String randomPassword = UUID.randomUUID().toString();
        
        String userName = user.getUserName();
        try {
            security.updateSystemPassword(userName, randomPassword);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("loginerror.html");
            return;
        }
        
        try {
            Mail mail = new Mail();
            mail.sendRecoverPasswordMail(eMail, userName, randomPassword);
            response.sendRedirect("recoverPasswordInfo.html");
            
        } catch (Exception e) {
            // Nothing happens so far...
        }
        
             
        
        
    }
}