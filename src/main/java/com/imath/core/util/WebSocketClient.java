package com.imath.core.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

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
	
	private Logger LOG = Logger.getAnonymousLogger();
	
	private static String LOG_PRE = Constants.LOG_PREFIX_SYSTEM + "[WebsocketServiceClient]";
	
	public WebSocketClient(Session session, String opt){
		LOG.info(LOG_PRE + "[WebSocketClient]" + opt);
		this.sessionServer = session;
		this.option = opt;
	}
	
	@OnOpen
    public void onOpen(Session session) {
		LOG.info(LOG_PRE + "[onOpen]" + this.option);
        session.setMaxTextMessageBufferSize(2000000);      
        
    }
	
	@OnClose
    public void onClose(Session session, CloseReason reason) {
		LOG.info(LOG_PRE + "[onClose]" + this.option + " "+ reason.getCloseCode() + ", " + reason.getReasonPhrase());     
    }
 
	
    @OnMessage
    public  void onMessage(String message) {
    	LOG.info(LOG_PRE + "[onMessage]" + this.option);
        
        try {
			this.sessionServer.getBasicRemote().sendText(message);
        } catch (Exception e) {
        	e.printStackTrace();
        }
                  
    }      
    
    @OnError
    public void onError(Throwable t) {
    	LOG.info(LOG_PRE + "[onError]" + this.option);
        t.printStackTrace();
    }

}



