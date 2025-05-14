package helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;

public class Properties {

    private static final Logger Log = LogManager.getLogger(Properties.class.getName());

    private final String databaseURL;
    private final String databaseUsername;
    private final String databasePassword;

    private final String siteUrl;

    private final String headless;

    public Properties() {
        java.util.Properties properties = new java.util.Properties();

        try{
            Log.trace("Reading properties file...");
            FileInputStream fileStream = new FileInputStream("config.properties");
            properties.load(fileStream);
            Log.trace("Successfully read properties file...");
        } catch (IOException ex){
            Log.error(ex.getMessage());
            Log.trace("Stack trace: ", ex);
            System.exit(0);
        }

        this.databaseURL = properties.getProperty("database_url");
        this.databaseUsername = properties.getProperty("database_username");
        this.databasePassword = properties.getProperty("database_password");

        this.siteUrl = properties.getProperty("site_url");

        this.headless = properties.getProperty("headless");
    }

    public String getDatabaseURL() {return databaseURL;}
    public String getDatabaseUsername() {return databaseUsername;}
    public String getDatabasePassword() {return databasePassword;}

    public String getSiteUrl() {return siteUrl;}

    public String getHeadless() {return headless;}
}
