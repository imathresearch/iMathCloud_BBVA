package com.imath.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ProcessBuilder.Redirect;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;



/**
 * Implements a set of utilities to create users in the jboss 
 * @author iMath
 *
 */

public class Security {
    
    /**
     * Change or set parameters user=password in JBOSS authentication files.
     * The generic format for these key - values is:
     * <userName>=hex(md5(<password>))
     * Which value password contains the string -> userName:ApplicationRealm:password
     * The role is static: WebAppUser
     * @param userName
     * @param password
     * @throws Exception
     */
    public void updateSystemPassword(String userName, String password) throws Exception {
        
        String hexPass = generateHexMd5Password(userName, password);
        
        // Here the pass in hex(md5) is set as property
        updateProperty(userName, hexPass.toString(), Constants.USERS_FILE);
        updateProperty(userName, hexPass.toString(), Constants.USERS_DOMAIN_FILE);
        
        // Here the role "WebAppUser" is set as property
        updateProperty(userName, "WebAppUser", Constants.ROLES_FILE);
        updateProperty(userName, "WebAppUser", Constants.ROLES_DOMAIN_FILE);

    }
    
    /**
     * Generate the properties that jboss uses to store passwords in its properties files.
     * The generic format for these key - values is:
     * <userName>=hex(md5(<password>))
     * Which value password contains the string -> userName:ApplicationRealm:password
     * @param userName
     * @param password
     * @return the hex(md5) generated from the password
     * @throws Exception
     */
    public static String generateHexMd5Password(String userName, String password) throws Exception {
       
        // To generate md5 of password stored in JBOSS' files
        MessageDigest md = MessageDigest.getInstance("MD5");
        String concatPass = userName + ":ApplicationRealm:" + password;
        byte[] md5Pass = md.digest(concatPass.getBytes());
        
        //convert the md5 byte to hex format method 1
        StringBuffer hexPass = new StringBuffer();
        for (int i=0;i<md5Pass.length;i++) {
            String hex=Integer.toHexString(0xff & md5Pass[i]);
            if(hex.length()==1) hexPass.append('0');
            hexPass.append(hex);
        }
        
        return hexPass.toString();
    }
    
    private void updateProperty(String key, String value, String propertiesPath) {
        try {
            // Get property of userName in JBOSS' file
            FileReader reader = new FileReader(propertiesPath);
            Properties props = new Properties();
            props.load(reader);
            props.setProperty(key, value);
            
            // We store the updated value.
            File f = new File(propertiesPath);
            OutputStream out = new FileOutputStream( f );
            props.store(out, "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void createLinuxUser(String userName) throws Exception {
    	// Remove the user from jboss AS
        ProcessBuilder pb = new ProcessBuilder(Constants.ADD_USER_LINUX, "-d",  "/home/" + userName, "-m", userName, "-g", Constants.IMATHSYSTEMGROUP);
        pb.redirectInput(Redirect.INHERIT);
        pb.redirectOutput(Redirect.INHERIT);
        pb.redirectError(Redirect.INHERIT);
        Process p = pb.start();
        p.waitFor();
        
        // Remove the user from the linux system
        
    }
    
    public void createSystemUser(String userName, String password, String role) throws Exception {
        // We add the system user
        //Process p = Runtime.getRuntime().exec(Constants.ADD_USER_CLI + " -a " + userName + " " + password + " > /dev/tty");
        ProcessBuilder pb = new ProcessBuilder(Constants.ADD_USER_CLI, "-a",  userName, password);
        
        /*
        File commands = new File("/home/andrea/workspace/AddUserJboss/src/Commands.txt");
		File output = new File("/home/andrea/workspace/AddUserJboss/src/ProcessLog.txt");
		File errors = new File("/home/andrea/workspace/AddUserJboss/src/ErrorLog.txt");
		 
		pb.redirectInput(commands);
		pb.redirectError(errors);
		pb.redirectOutput(output);
        */
        
        
        pb.redirectInput(Redirect.INHERIT);
        pb.redirectOutput(Redirect.INHERIT);
        pb.redirectError(Redirect.INHERIT);
        
        
        
        //String error  = loadStream(p.getErrorStream());
        //String output = loadStream(p.getInputStream());
        
        //System.out.println("STDERROR: add-uset.sh--------------:" + error);
        //System.out.println("STDOUTPUT: add-user.sh-------------:" + output);
        Process p = pb.start();
        int signal = p.waitFor();
        
        System.out.println("SIGNAL " + signal);
        
        // We add the role of the user if role is not null
        if (role != null) {
            String line = userName + "=" + role;
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Constants.ROLES_FILE, true), "UTF-8"));
            writer.append(line + "\n");
            writer.close();
        }
    }
    
	public synchronized void removeSystemUser(String userName) throws Exception {
	  	ProcessBuilder pb = new ProcessBuilder(Constants.REMOVE_USER_CLI, userName);
	    pb.redirectInput(Redirect.INHERIT);
	    pb.redirectOutput(Redirect.INHERIT);
	    pb.redirectError(Redirect.INHERIT);
	    
	    Process p = pb.start();
        p.waitFor();
        
	}
}
    
