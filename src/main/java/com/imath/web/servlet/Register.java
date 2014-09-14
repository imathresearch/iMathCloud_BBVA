package com.imath.web.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import com.imath.core.rest.pub.UserManagement;
import com.imath.core.rest.pub.UserManagement.NewUserDTO;
import com.imath.core.util.Mail;

public class Register extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Inject
    UserManagement userM;
    // imathcloud943793072
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String userName = request.getParameter("usernamesignup");
        String password = request.getParameter("passwordsignup");
        String passwordRep = request.getParameter("passwordsignup_confirm");
        String eMail = request.getParameter("emailsignup");
        
        NewUserDTO newUserDTO = new NewUserDTO();
        
        newUserDTO.password = password;
        newUserDTO.eMail = eMail;
        
        if (!password.equals(passwordRep)) {
            response.sendRedirect("registererrorPasswords.html");
            return;
        }
        
        Response resp = userM.newUser(userName, newUserDTO);
        if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
            try {
            	if (!eMail.trim().equals("")) {
            		Mail mail = new Mail();
            		mail.sendWelcomeMail(eMail, userName);
            	}
            } catch (Exception e) {
                // Nothing happens so far...
            }
            try {
                request.login(userName, password);
                response.sendRedirect("indexNew.jsp");
                return;
            } catch(ServletException e) {
                response.sendRedirect("loginerror.html");
                return;
            }
        } else {
            response.sendRedirect("registererror.html");
            return;
        }
    }
}
