package io.gitbox;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.graphics.ImageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

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

    private static String loadProperty(String propertyName) {
        Properties properties = null;
        try {
            properties = loadProperties();
        } catch (IOException e) {
            LOG.warn("Unable to load the " + propertyName + " property", e);
        }

        if (properties == null) {
            return StringUtils.EMPTY;
        }
        return properties.getProperty(propertyName, StringUtils.EMPTY);
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

    public static String getDirectory() {
        String directory = loadProperty(DIRECTORY_PROPERTY_NAME);
        if (StringUtils.isNotBlank(directory) && !directory.endsWith("/")) {
            directory += "/";
        }
        return directory;
    }

    public static String getGitRepository() {
        return loadProperty(GIT_REPOSITORY_PROPERTY_NAME);
    }

    public static String getNotificationServer() {
        String notifServer = loadProperty(NOTIFICATION_SERVER_PROPERTY_NAME);
        if (StringUtils.isNotBlank(notifServer) && !notifServer.endsWith("/")) {
            notifServer += "/";
        }
        return notifServer;
    }

    public static boolean isValid() {
        return StringUtils.isNotBlank(getNotificationServer())
                && StringUtils.isNotBlank(getGitRepository())
                && StringUtils.isNotBlank(getDirectory())
                && containsDirectory(".git", getDirectory());
    }

    private static boolean containsDirectory(String subDirectory, String directory) {
        return new File(directory + subDirectory).exists();
    }

    public static void setDirectory(String directory) {
        if (StringUtils.isNotBlank(directory) && !directory.endsWith("/")) {
            directory += "/";
        }
        updateProperty(DIRECTORY_PROPERTY_NAME, directory);
    }

    public static void setGitRepository(String gitRepository) {
        updateProperty(GIT_REPOSITORY_PROPERTY_NAME, gitRepository);
    }

    public static void setNotificationServer(String notifServer) {
        if (StringUtils.isNotBlank(notifServer) && !notifServer.endsWith("/")) {
            notifServer += "/";
        }
        updateProperty(NOTIFICATION_SERVER_PROPERTY_NAME, notifServer);
    }

    private static void updateProperty(String propertyName, String propertyValue) {
        URL propertiesURL = Configuration.class.getClassLoader().getResource(PROPERTIES_FILE);
        if (propertiesURL == null) {
            LOG.error("Unable to find the properties file");
            return;
        }
        Properties properties = null;
        try {
            properties = loadProperties();
            properties.setProperty(propertyName, propertyValue);
            File file = new File(propertiesURL.toString().substring(6));
            properties.store(new FileOutputStream(file), "");
        } catch (IOException e) {
            LOG.error("Unable to load the properties file");
        }

    }

}
