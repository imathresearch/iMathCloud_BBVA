package com.imath.core.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;


@ClientEndpoint
public class WebSocketClient{// implements MessageHandler.Partial<String>{
	
	public Session sessionServer;
	private Session ownSession;
	public String option; 
	
	public WebSocketClient(Session session, String opt){
		this.sessionServer = session;
		this.option = opt;
	}
	
	@OnOpen
    public void onOpen(Session session) {
        System.out.println("["+option+"] Client Connected to endpoint: " + session.getBasicRemote());
        this.ownSession = session;  
        session.setMaxTextMessageBufferSize(60000);
    }
	
	@OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("["+option+"] Client Closing");
        System.out.println("CLOSED: " + reason.getCloseCode() + ", " + reason.getReasonPhrase());
        
    }
 
	
    @OnMessage
    public void onMessage(String message) {
    	System.out.println("["+option+"] Client receiving ");
        System.out.println(message);
        JavaReminder reminderBeep = new JavaReminder(5);       
        this.sessionServer.getAsyncRemote().sendText(message);	        	        
    }
    
    /*
    @Override
    public void onMessage(String msgPart, boolean last) {
        if( messageBuffer.length() + msgPart.length() > maxMessageSize) {
            try {
				this.ownSession.close(new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, "Message is too long"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        else {
            messageBuffer.append(msgPart);
            if (last) {
                String message = messageBuffer.toString();
                this.sessionServer.getAsyncRemote().sendText(message);
                JavaReminder reminderBeep = new JavaReminder(5);       
                messageBuffer.setLength(0);
            }
        }
    }*/
    
   
 
    @OnError
    public void onError(Throwable t) {
    	System.out.println("["+option+"] Error in client");
        t.printStackTrace();
    }
    
    public class JavaReminder {
		Timer timer;

	    public JavaReminder(int seconds) {
	        timer = new Timer();  //At this line a new Thread will be created
	        //timer.schedule(new RemindTask(), seconds*1000); //delay in milliseconds
	        timer.scheduleAtFixedRate(new RemindTask(), 0, 60*1000);
	    }

	    class RemindTask extends TimerTask {

	        @Override
	        public void run() {
	            System.out.println("ReminderTask is completed by Java timer");
	            try {
	            	byte[] bytes = new byte[10];
	                ByteBuffer buffer = ByteBuffer.wrap(bytes);
					sessionServer.getBasicRemote().sendPing(buffer);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            //timer.cancel(); //Not necessary because we call System.exit
	            //System.exit(0); //Stops the AWT thread (and everything else)
	        }
	    }

	}

}


/*
public class FermiMessageHandler implements MessageHandler.Partial<String> {

    private Session session;
    private int maxMessageSize = 20000;
    private StringBuilder messageBuffer = new StringBuilder(maxMessageSize);

    FermiMessageHandler(Session session) {
        this.session = session;
    }

    @Override
    public void onMessage(String msgPart, boolean last) {
        if( messageBuffer.length() + msgPart.length() > maxMessageSize) {
            session.close(new
CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, "Message is too
long");
        }
        else {
            messageBuffer.append(msgPart);
            if (last) {
                String message = messageBuffer.toString();
                // We have a complete message.  Do something with it.
                messageBuffer.setLength(0);
            }
        }
    }
}*/
