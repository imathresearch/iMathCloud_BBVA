package com.imath.core.util;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

public class Console {
    public static void startConsole(String user, String port) {

        //String command = "\"/usr/local/bin/ipython notebook --port=" + port + " --ip=* --pylab=inline\"";
        
        // We start the console
        ProcessBuilder pb = new ProcessBuilder("./console.sh", user, port);
        pb.redirectInput(Redirect.INHERIT);
        pb.redirectOutput(Redirect.INHERIT);
        pb.redirectError(Redirect.INHERIT);
        try {
            pb.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // We get the pids related to the previous call
        ProcessBuilder pb2 = new ProcessBuilder("./getpids.sh", user);
        pb2.redirectInput(Redirect.INHERIT);
        pb2.redirectOutput(Redirect.INHERIT);
        pb2.redirectError(Redirect.INHERIT);
        try {
            pb2.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
