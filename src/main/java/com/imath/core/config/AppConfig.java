package com.imath.core.config;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
	
	// iMath Port, iMath Host, HPC2 port, console port
    static public String IMATH_PORT = 								"imath.port";
    static public String IMATH_HOST = 								"imath.host";
    static public String HPC2_PORT = 								"hpc2.port";
    static public String CONSOLE_PORT = 							"console.port";
    static public String HOST_STORAGE = 							"host.storage";
    static public String TEST = 									"test.profile";
    
    public static String CONFIG_PROPERTIES_FILE = 					"config.properties";
    
    private static Properties prop = new Properties();
    
    /**
     * Upload the configuration from config.properties files
     */
    private static void uploadConfiguration() throws IOException {
        InputStream input = null;

        try {
            String filename = CONFIG_PROPERTIES_FILE;
            input = AppConfig.class.getClassLoader().getResourceAsStream(filename);
            if (input == null) {
                throw new IOException("No " + filename + " has found!");
            }
            prop.load(input);

        } catch (IOException ex) {
            throw new IOException("Properties file error", ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    throw new IOException("Error closing properties file", e);
                }
            }
        }
    }
    
    public static String getProp(String key) throws IOException {
        if (prop.isEmpty()) {
            uploadConfiguration();
        }
        return prop.getProperty(key);
    }

}
