package com.imath.core.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;


@ClientEndpoint
public class WebSocketClient{// implements MessageHandler.Partial<String>{
	
	public Session sessionServer;
	public String option; 
	//public JavaReminder reminderBeep;
	
	public WebSocketClient(Session session, String opt){
		this.sessionServer = session;
		this.option = opt;
	}
	
	@OnOpen
    public void onOpen(Session session) {
        System.out.println("["+option+"] Client Connected to endpoint: " + session.getBasicRemote());               
        session.setMaxTextMessageBufferSize(100000);        
        
    }
	
	@OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("["+option+"] Client Closing");
        System.out.println("CLOSED: " + reason.getCloseCode() + ", " + reason.getReasonPhrase());        
    }
 
	
    @OnMessage
    public  void onMessage(String message) {
    	System.out.println("["+option+"] Client receiving ");
        
        try {
			this.sessionServer.getBasicRemote().sendText(message);
        } catch (Exception e) {
        	e.printStackTrace();
        }
                  
    }      
    
    @OnError
    public void onError(Throwable t) {
    	System.out.println("["+option+"] Error in client");
        t.printStackTrace();
    }
    
    /*public class JavaReminder {
		Timer timer;

	    public JavaReminder(int seconds) {
	        timer = new Timer();  //At this line a new Thread will be created
	        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds*1000);
	    }

	    class RemindTask extends TimerTask {

	        @Override
	        public void run() {
	            System.out.println("ReminderTask is completed by Java timer");
	            try {
	            	ByteBuffer buffer = ByteBuffer.allocate(1);
	                buffer.put((byte)0x9);	
	                //String ping = "Ping";
	                //buffer.put(ping.getBytes());
	                synchronized (sessionServer.getBasicRemote()) {
	                    try {
	                    	sessionServer.getBasicRemote().sendPing(buffer);
	                    } catch (Exception e) {
	                    	e.printStackTrace();
	                    }
	                } 
	                
					//sessionServer.getAsyncRemote().sendPing(buffer);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 	           
	        }
	    }

	}
    
    public class ExecutorPing implements Runnable{
    	@Override
        public void run() {
            System.out.println("ExecutorPing");
            try {
            	ByteBuffer buffer = ByteBuffer.allocate(1);
                buffer.put((byte) 0xFF);              
                synchronized (sessionServer) {
                    try {
                    	sessionServer.getBasicRemote().sendPing(buffer);
                    } catch (Exception e) {
                    	e.printStackTrace();
                    }
                } 
                
				//sessionServer.getAsyncRemote().sendPing(buffer);
			} catch (IllegalArgumentException e) {
				System.out.println("Executor Ping exception");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 	           
        }
    	
    }*/

}



