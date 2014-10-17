package com.imath.core.rest;

/* (C) 2014 iMath Research S.L. - All rights reserved.  */


import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Singleton;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.imath.core.util.WebSocketClientConnection;


@ServerEndpoint(value="/simple/{kernel-id}/shell")
public class NotebookShellService {
	
	private WebSocketClientConnection wcc;
	private static String option = "shell";
	
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
        
        System.out.println("["+option+"] Server MaxTextBufferSize " + session.getMaxTextMessageBufferSize());
        System.out.println("["+option+"] Server MaxTimeBeforeClose " + session.getMaxIdleTimeout());
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
		} */         
    }
    
	
    
    

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
	
	
	
    
    /*
    @ClientEndpoint
    public class WebSocketClient{
    	
    	private Session sessionServer;
    	public String option; 
    	
    	public WebSocketClient(Session session, String opt){
    		this.sessionServer = session;
    		this.option = opt;
    	}
    	
    	@OnOpen
        public void onOpen(Session session) {
            System.out.println("Connected to endpoint: " + session.getBasicRemote());
            
        }
    	
    	@OnClose
        public void onClose(Session session) {
            System.out.println("Closing client: " + option);
            
        }
     
    	
        @OnMessage
        public void onMessage(String message) {
        	System.out.println("Client receiving ");
            System.out.println(message);
            try {
				this.sessionServer.getBasicRemote().sendText(message);
			} catch (IOException e) {				
				e.printStackTrace();
			}
        }
        
        @OnMessage
        public void echoBinary(ByteBuffer data)  {
        	System.out.println("Client receiving binary data ");            
            try {
				this.sessionServer.getBasicRemote().sendBinary(data);
			} catch (IOException e) {				
				e.printStackTrace();
			}
        }
     
        @OnError
        public void onError(Throwable t) {
        	System.out.println("Error in client");
            t.printStackTrace();
        }
    	
    }
    
    
    private class WebSocketClientConnection {
   	 
        public Session session;
        public Session sessionServer;
     
        protected void start(String kernelId, String opt){
     
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
 
            String uri = "ws://localhost:8889/kernels/" + kernelId + "/" + opt;
            System.out.println("Connecting to " + uri);
            try {
            	WebSocketClient client = new WebSocketClient(sessionServer, opt);
                session = container.connectToServer(client, URI.create(uri));
            } catch (DeploymentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }             
 
        }        
    }*/
}




