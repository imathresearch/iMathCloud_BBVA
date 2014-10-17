package com.imath.core.util;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;


public class WebSocketClientConnection {
	public Session session;
    public Session sessionServer;
 
    public void start(String kernelId, String opt){
 
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
}
