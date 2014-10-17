package com.imath.core.rest;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.imath.core.util.WebSocketClientConnection;


@ServerEndpoint(value="/simple/{kernel-id}/iopub")
public class NotebookIOPUBService {
	
	private WebSocketClientConnection wcc;
	private static String option = "iopub";
	
	@OnOpen
    public void onOpen(Session session, @PathParam("kernel-id") String kernelId) {
        
        System.out.println("OPEN WEBSOCKET");
        System.out.println("Connected from: " + session.getRequestURI());
        
        // Connection to IPython server
        wcc = new WebSocketClientConnection();
        wcc.sessionServer = session;     
        System.out.println("["+option+"] Server TimeOut " + session.getMaxIdleTimeout());
        wcc.start(kernelId, option);
        System.out.println("Session websocket 1 " + session.getId()); 
        System.out.println("Session websocket 2 " + wcc.session.getId());
        
        session.setMaxTextMessageBufferSize(60000);
        wcc.sessionServer.setMaxTextMessageBufferSize(60000);
    }
    
    @OnMessage
    public void echoText(String name) {
    	System.out.println("["+option+"] Server receiving " + name);
    	wcc.session.getAsyncRemote().sendText(name);
        /*try {
			wcc.session.getBasicRemote().sendText(name);
		} catch (IOException e) {			
			e.printStackTrace();
		}*/       
    }
    /*
    @OnMessage
    public void echoBinary(ByteBuffer data)  {
    	System.out.println("Client receiving binary data ");            
        try {
        	wcc.session.getBasicRemote().sendBinary(data);
		} catch (IOException e) {				
			e.printStackTrace();
		}
    }*/
    
    

	@OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("["+option+"] Closing server");
        System.out.println("CLOSED: " + reason.getCloseCode() + ", " + reason.getReasonPhrase());
        try {
			wcc.session.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
	
	@OnError
    public void onError(Throwable t) {
        System.out.println("["+option+"] ERROR server");    
        System.out.println(t.getMessage());
        
    }
}
