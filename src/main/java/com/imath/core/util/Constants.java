package com.imath.core.util;

public class Constants {
    
    // The current version of the iMathCloud API. 
    static public final String VERSION = "beta";
    
    // The REST url paths for data and exec access
    static public final String urlDataPath = "/" + VERSION + "/api/data";
    static public final String urlExecPath = "/" + VERSION + "/api/exec";
    
    // The root name used to identify the root directories in iMath Cloud cloud
    static public final String rootNAME = "ROOT";
    static public final String ROOT_FILE_SYSTEM = "/iMathCloud"; //In Production, it must be changed
    
    static public final String iMathTRASH = ROOT_FILE_SYSTEM + "/trash";
    
    /*
     * HPC2 - iMath Cloud Connection constants
     */ 
    
    static public final String LOCALHOST = "127.0.0.1";
    
    static public final String LOCALHOST_String = "localhost";
    
    static public final String URI_HEAD = "file://";
    
    
    
    static public final String HPC2_HTTP = "http://";
    static public final String HPC2_PORT = "8890";
    static public final String IMATH_PORT = "8080";     // In Development 
    //static public final String IMATH_PORT = "80";       // In Production
    
    static public final String IMATH_HOST = LOCALHOST;         // In Development
    //static public final String IMATH_HOST = "158.109.125.112"; // In Production
    static public final String HPC2_PLUGIN_SERVICE = "plugin";
    static public final String HPC2_PCTS_SERVICE = "getpct";
    static public final String HPC2_SUBMITJOB_SERVICE = "core/submit";
    static public final String HPC2_STOPJOB_SERVICE = "stopJob";
    
    static public final String HPC2_REST_PLUGIN_KEY_FILENAME = "fileName";
    static public final String HPC2_REST_PLUGIN_KEY_DIRECTORY = "directory";
    static public final String HPC2_REST_PLUGIN_KEY_PARAMETER = "parameter";
    
    static public final String HPC2_REST_SUBMITJOB_KEY_FILENAME = "fileName";
    static public final String HPC2_REST_SUBMITJOB_KEY_DIRECTORY = "directory";
    
    static public final String HPC2_SEPARATOR = "|#|";
}
