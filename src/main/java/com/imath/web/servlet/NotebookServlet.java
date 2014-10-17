package com.imath.web.servlet;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/*
import org.jboss.as.websockets.WebSocket;
import org.jboss.as.websockets.servlet.WebSocketServlet;
import org.jboss.websockets.Frame;
import org.jboss.websockets.frame.TextFrame;
 */
@WebServlet("/notebookservlet")
public class NotebookServlet{// extends WebSocketServlet {
 
/*	

	  @Override
	  protected void onSocketOpened(WebSocket socket) throws IOException {
	    System.out.println("Websocket opened :)");
	  }

	  @Override
	  protected void onSocketClosed(WebSocket socket) throws IOException {
	    System.out.println("Websocket closed :(");
	  }

	  @Override
	  protected void onReceivedFrame(WebSocket socket) throws IOException {
	    final Frame frame = socket.readFrame();
	    if (frame instanceof TextFrame) {
	      final String text = ((TextFrame) frame).getText();
	      if ("Hello".equals(text)) {
	        socket.writeFrame(TextFrame.from("Hey, there!"));
	      }
	    }
	  }
	  */
	
}