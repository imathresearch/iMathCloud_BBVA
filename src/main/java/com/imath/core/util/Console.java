package com.imath.core.util;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

public class Console {
    public static void startConsole(String user, String port) {

        String command = "\"/usr/local/bin/ipython notebook --port=" + port + " --ip=* --pylab=inline\"";
        
        ProcessBuilder pb = new ProcessBuilder("su", user,  "-c", command);
        pb.redirectInput(Redirect.INHERIT);
        pb.redirectOutput(Redirect.INHERIT);
        pb.redirectError(Redirect.INHERIT);
        try {
            String fullCommand = "/bin/su " + user + " -c " + command;
            System.out.println(fullCommand);
            //Process p = Runtime.getRuntime().exec(fullCommand);
            pb.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
