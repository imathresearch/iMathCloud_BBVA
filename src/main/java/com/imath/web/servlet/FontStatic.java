package com.imath.web.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servers static font files. This is a wrapper to allow cross-domain access to font files, to enable firefox usage 
 * @author ipinyol
 *
 */
public class FontStatic extends HttpServlet {
    @Inject 
    private Logger LOG;
    
    private static final long serialVersionUID = 1L;
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = getServletContext().getNamedDispatcher("default");
        response.addHeader("Access-Control-Allow-Origin", "*");
        rd.forward(request, response);    
    }

}
