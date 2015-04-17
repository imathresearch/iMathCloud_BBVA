package com.imath.core.util;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.imath.core.config.AppConfig;


public class WebSocketClientConnection {
	public Session session;
    public Session sessionServer;
    
    private Logger LOG = Logger.getAnonymousLogger();
	
	private static String LOG_PRE = Constants.LOG_PREFIX_SYSTEM + "[WebSocketClientConnection]";
 
    public void start(String kernelId, String port, String opt) throws IOException{
    	LOG.info(LOG_PRE + "[start]" + opt);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        String uri = "ws://" + AppConfig.getProp(AppConfig.IMATH_HOST) + ":" + port + "/kernels/" + kernelId + "/" + opt;
        LOG.info(LOG_PRE + "Connecting to " + uri);
        try {
        	WebSocketClient client = new WebSocketClient(sessionServer, opt);
            session = container.connectToServer(client, URI.create(uri));
        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }             

    }     
}
