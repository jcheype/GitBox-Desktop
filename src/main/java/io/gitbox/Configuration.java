package io.gitbox;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.graphics.ImageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * @author Jean-Baptiste Lemée
 */
public class Configuration {

    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

    private static final String PROPERTIES_FILE = "gitbox.properties";
    private static final String DIRECTORY_PROPERTY_NAME = "gitbox.directory";
    private static final String GIT_REPOSITORY_PROPERTY_NAME = "gitbox.repository";
    private static final String NOTIFICATION_SERVER_PROPERTY_NAME = "gitbox.notification.server";

    public static ImageData getIcon() {
        return new ImageData(Configuration.class.getClassLoader().getResourceAsStream("051.gif"));
    }

    private static String loadProperty(String propertyName) throws IOException {
        Properties properties = loadProperties();
        if (properties == null) {
            return null;
        }
        return properties.getProperty(propertyName);
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        InputStream propertiesInput = Configuration.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
        if (propertiesInput == null) {
            return null;
        }
        properties.load(propertiesInput);
        return properties;
    }

    public static String getDirectory() throws IOException {
        return loadProperty(DIRECTORY_PROPERTY_NAME);
    }

    public static String getGitRepository() throws IOException {
        return loadProperty(GIT_REPOSITORY_PROPERTY_NAME);
    }

    public static String getNotificationServer() throws IOException {
        String notifServer = loadProperty(NOTIFICATION_SERVER_PROPERTY_NAME);
        if (notifServer != null && !notifServer.endsWith("/")) {
            notifServer += "/";
        }
        return notifServer;
    }

    public static boolean isValid() {
        try {
            return StringUtils.isNotBlank(getNotificationServer())
                    && StringUtils.isNotBlank(getGitRepository())
                    && StringUtils.isNotBlank(getDirectory());
        } catch (IOException e) {
            return false;
        }
    }

    public static void setDirectory(String directory) throws IOException {
        updateProperty(DIRECTORY_PROPERTY_NAME, directory);
    }

    public static void setGitRepository(String gitRepository) throws IOException {
        updateProperty(GIT_REPOSITORY_PROPERTY_NAME, gitRepository);
    }

    public static void setNotificationServer(String notifServer) throws IOException {
        if (notifServer != null && !notifServer.endsWith("/")) {
            notifServer += "/";
        }
        updateProperty(NOTIFICATION_SERVER_PROPERTY_NAME, notifServer);
    }

    private static void updateProperty(String propertyName, String propertyValue) throws IOException {
        URL propertiesURL = Configuration.class.getClassLoader().getResource(PROPERTIES_FILE);
        if (propertiesURL == null) {
            LOG.error("Unable to find the properties file");
            return;
        }
        Properties properties = loadProperties();
        properties.setProperty(propertyName,propertyValue);
        File file = new File(propertiesURL.toString().substring(6));
        properties.store(new FileOutputStream(file), "" );
    }

}
