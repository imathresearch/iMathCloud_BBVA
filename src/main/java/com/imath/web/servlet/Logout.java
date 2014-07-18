package com.imath.web.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.imath.core.data.MainServiceDB;
import com.imath.core.model.Session;
import com.imath.core.service.SessionController;
import com.imath.core.util.Console;

public class Logout extends HttpServlet{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    @Inject
    private MainServiceDB db;
    
    @Inject
    private SessionController sc;
    
    @Inject 
    private Logger LOG;
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getUserPrincipal().getName();
        
        // We close the imath cloud web session
        Session session = db.getSessionDB().findByUser_and_OpenSession(userName);
        if (session == null) {
            LOG.severe("No open session to close");
            //throw new ServletException("No open session!");
        } else {
            try {
                //sc.closeSession(userName);    //Provisional, we do not close the session
            } catch (Exception e) {
                LOG.severe("Error closing session");
                //throw new ServletException(e.getMessage());
            }
        }
        
        // we close the interactive console
        Console.closeConsole(userName);
        
        // we logout from the system
        request.logout();
        response.sendRedirect(".");
    }
}
