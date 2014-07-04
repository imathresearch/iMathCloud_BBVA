package com.imath.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ProcessBuilder.Redirect;



/**
 * Implements a set of utilities to create users in the jboss 
 * @author iMath
 *
 */

public class Security {
    public void createSystemUser(String userName, String password, String role) throws Exception {
        // We add the system user
        //Process p = Runtime.getRuntime().exec(Constants.ADD_USER_CLI + " -a " + userName + " " + password + " > /dev/tty");
        ProcessBuilder pb = new ProcessBuilder(Constants.ADD_USER_CLI, "-a",  userName, password);
        pb.redirectInput(Redirect.INHERIT);
        pb.redirectOutput(Redirect.INHERIT);
        pb.redirectError(Redirect.INHERIT);
        //String error  = loadStream(p.getErrorStream());
        //String output = loadStream(p.getInputStream());
        
        //System.out.println("STDERROR: add-uset.sh--------------:" + error);
        //System.out.println("STDOUTPUT: add-user.sh-------------:" + output);
        Process p = pb.start();
        p.waitFor();
        
        // We add the role of the user
        String line = userName + "=" + role;
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Constants.ROLES_FILE, true), "UTF-8"));
        writer.append(line + "\n");
        writer.close();
    }
    
    private static String loadStream(InputStream s) throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(s));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line=br.readLine()) != null)
            sb.append(line).append("\n");
        return sb.toString();
    }
}
