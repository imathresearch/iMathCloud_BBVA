package com.imath.core.rest;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.imath.core.util.WebSocketClientConnection;

@ServerEndpoint(value="/websocket/{kernel-id}/{opt}/{ipython-port}")
public class WebsocketService {
	
	private WebSocketClientConnection wcc;
	private String option;
	private JavaReminder reminderBeep;
	private boolean ping;
			
	@OnOpen
    public void onOpen(Session session, @PathParam("kernel-id") String kernelId, @PathParam("opt") String opt, @PathParam("ipython-port") String portIpython) {
        
        System.out.println("OPEN WEBSOCKET");
        System.out.println("Connected from: " + session.getRequestURI());
        
        // Connection to IPython server
        this.wcc = new WebSocketClientConnection();
        this.wcc.sessionServer = session;  
        this.option = opt;
        this.wcc.start(kernelId, portIpython, this.option);
        
        session.setMaxTextMessageBufferSize(100000);
        this.wcc.sessionServer.setMaxTextMessageBufferSize(100000);
        
        this.ping = false;
    }
    
    @OnMessage
    public void echoText(String name) {
    	System.out.println("["+this.option+"] Server receiving " + name);
    	
    	//It is a ping message
    	if(name.equals("[IMATH]Ping")){
    		System.out.println("["+this.option+"] Server receiving PING" );
    		return;
    	}    
        
    	//It is a normal message to transfer
    	try {
        	wcc.session.getBasicRemote().sendText(name);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
    	//The ping thread is only started once
    	if(!ping){
    		reminderBeep = new JavaReminder(30);
    		ping = true;
    	}
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
        
        reminderBeep.timer.cancel();
        
    }
		
	@OnError
    public void onError(Throwable t) {
        System.out.println("["+option+"] ERROR server");    
        System.out.println(t.getMessage());
        reminderBeep.timer.cancel();
    }
	
	private class JavaReminder {
		Timer timer;

	    public JavaReminder(int seconds) {
	        timer = new Timer();  //At this line a new Thread will be created
	        timer.scheduleAtFixedRate(new RemindTask(), 30, seconds*1000);
	    }

	    class RemindTask extends TimerTask {

	        @Override
	        public void run() {
	            System.out.println("ReminderTask is completed by Java timer");
	            try {	            		                                    	
	               wcc.sessionServer.getBasicRemote().sendText("{\"ping\":\"[IMATH]Ping\"}");	                				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}            
	        }
	    }

	}
}
