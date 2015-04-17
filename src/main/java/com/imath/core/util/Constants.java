package com.imath.core.util;

public class Constants {
    
    // The current version of the iMathCloud API. 
    static public final String VERSION = "beta";
    
    // The REST url paths for data and exec access
    static public final String urlDataPath = "/" + VERSION + "/api/data";
    static public final String urlExecPath = "/" + VERSION + "/api/exec";
    static public final String urlUserPath = "/" + VERSION + "/api/user";
    
    // The root name used to identify the root directories in iMath Cloud cloud
    static public final String rootNAME = "ROOT";
    
    static public final Long MiB = 1048576L; // bytes: 2^20 
   
    /*
     * HPC2 - iMath Cloud Connection constants
     */ 
    static public final String LOCALHOST = "127.0.0.1";
    
    static public final String LOCALHOST_String = "localhost";
    
    static public final String URI_HEAD = "file://";
    static public final String ROOT_FILE_SYSTEM = "/iMathCloud"; 
    static public final String ROOT_EXEC_DIR = ROOT_FILE_SYSTEM + "/exec_dir"; 
    static public final String iMathTRASH = ROOT_FILE_SYSTEM + "/trash";
    
    static public final String IMATH_HTTP = "http://";
    static public final String HPC2_HTTP = "http://";
    
    static public final String HPC2_PLUGIN_SERVICE = "plugin";
    static public final String HPC2_PCTS_SERVICE = "getpct";
    static public final String HPC2_SUBMITJOB_SERVICE = "core/submit";
    static public final String HPC2_STOPJOB_SERVICE = "stopJob";
    
    static public final String HPC2_REST_PLUGIN_KEY_FILENAME = "fileName";
    static public final String HPC2_REST_PLUGIN_KEY_DIRECTORY = "directory";
    static public final String HPC2_REST_PLUGIN_KEY_PARAMETER = "parameter";
    static public final String HPC2_REST_PLUGIN_KEY_JOBTYPE = "jobType";
    
    
    static public final String HPC2_REST_SUBMITJOB_KEY_FILENAME = "fileName";
    static public final String HPC2_REST_SUBMITJOB_KEY_DIRECTORY = "directory";
    
    static public final String HPC2_SEPARATOR = "|#|";
    
    // System constants
    // IMPORTANT: We assume that JBOSS is launched from JBOSS-HOME/bin
    static public final String ADD_USER_CLI = "./add-user.sh";
    static public final String REMOVE_USER_CLI = "./remove-user.sh";
    static public final String ADD_USER_LINUX = "useradd";
    
    static public final String ROLES_FILE = "../standalone/configuration/application-roles.properties";
    static public final String USERS_FILE = "../standalone/configuration/application-users.properties";
    static public final String ROLES_DOMAIN_FILE = "../domain/configuration/application-roles.properties";
    static public final String USERS_DOMAIN_FILE = "../domain/configuration/application-users.properties";
    static public final String SYSTEM_ROLE = "WebAppUser";
    
    static public final String IMATHSYSTEMGROUP = "imathuser";      // The linux system group. All imath users will belong to this group.
    
    static public final String INITIAL_FILE_CONFIGURATION = "examplesFiles.txt";    // The file name containing the initial files to be included when registered
    static public final String IGNORE_LINE = "--"; // The characters that at the initial of the line in the configuration file, make the line to be ignored
    static public final String WELLCOME_TEMPLATE = "welcomeTemplate.html"; // The html template for wellcome email  
    
    static public final String LOG_PREFIX_SYSTEM = "[IMATH][CLOUD]";    // The prefix of the system
}
