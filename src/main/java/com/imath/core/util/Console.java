package com.imath.core.util;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Console {
    public static void startConsole(String user, String port, boolean newSession, String host) {

        // we close the interactive console if fore whatever reason it was open.
        // This is also a security measure!
        Console.closeConsole(user);
        //String command = "\"/usr/local/bin/ipython notebook --port=" + port + " --ip=* --pylab=inline\"";
        
        // if for whatever reasion the console is alreay up and in the same port, we do not launch it again!
        boolean start=true;
        try {
        	
        	System.out.println("startConsole URL " + Constants.IMATH_HTTP + host+":"+ port);
        	
            URL url = new URL(Constants.IMATH_HTTP + host+":"+ port);
            HttpURLConnection huc =  (HttpURLConnection) url.openConnection();
            //URLConnection urlConn = url.openConnection();
            //urlConn.setUseCaches(false);
            //urlConn.setDoOutput(false);     //Set method to GET
            //urlConn.connect();
            huc.setRequestMethod ("GET");  //OR  huc.setRequestMethod ("HEAD"); 
            huc.connect () ; 
            int code = huc.getResponseCode() ;
            if (code<400) start=false;  //This means that the port is up
        } catch (Exception e) {
            //Do nothing. It means that tornado is not yet started!
        }
        if (start) {
            // We start the console
            ProcessBuilder pb = new ProcessBuilder("./console.sh", user, port);
            pb.redirectInput(Redirect.INHERIT);
            pb.redirectOutput(Redirect.INHERIT);
            pb.redirectError(Redirect.INHERIT);
            try {
                pb.start();
            } catch (IOException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
        }
    
    }
    
    public static void closeConsole(String user) {
        ProcessBuilder pb = new ProcessBuilder("./closeconsole.sh", user);
        pb.redirectInput(Redirect.INHERIT);
        pb.redirectOutput(Redirect.INHERIT);
        pb.redirectError(Redirect.INHERIT);
        try {
            pb.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
