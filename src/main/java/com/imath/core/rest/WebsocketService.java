package com.imath.core.rest;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.imath.core.util.Constants;
import com.imath.core.util.WebSocketClientConnection;

@ServerEndpoint(value="/websocket/{kernel-id}/{opt}/{ipython-port}")
public class WebsocketService {
	
	private WebSocketClientConnection wcc;
	private String option;
	private JavaReminder reminderBeep;
	private boolean ping;
		
	private Logger LOG = Logger.getAnonymousLogger();
	
	private static String LOG_PRE = Constants.LOG_PREFIX_SYSTEM + "[WebsocketService]";
			
	@OnOpen
    public void onOpen(Session session, @PathParam("kernel-id") String kernelId, @PathParam("opt") String opt, @PathParam("ipython-port") String portIpython) {
		LOG.info(LOG_PRE + "[onOpen]" + opt);
        //System.out.println("OPEN WEBSOCKET");
        //System.out.println("Connected from: " + session.getRequestURI());
        
        // Connection to IPython server
        this.wcc = new WebSocketClientConnection();
        this.wcc.sessionServer = session;  
        this.option = opt;
        this.wcc.start(kernelId, portIpython, this.option);
        
        session.setMaxTextMessageBufferSize(2000000);
        this.wcc.sessionServer.setMaxTextMessageBufferSize(2000000);
        
        this.ping = false;
    }
    
    @OnMessage
    public void OnMessage(String name) {    	    	
    	
    	//It is a ping message
    	if(name.equals("[IMATH]Ping")){    	
    		return;
    	}    
        
    	LOG.info(LOG_PRE + "[OnMessage]" + this.option);
    	
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
		LOG.info(LOG_PRE + "[onClose]" + this.option);
        
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
		LOG.info(LOG_PRE + "[onError]" + this.option);       
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
	            try {	            		                                    	
	               wcc.sessionServer.getBasicRemote().sendText("{\"ping\":\"[IMATH]Ping\"}");	                				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					this.cancel();
				}            
	        }
	    }

	}
}
