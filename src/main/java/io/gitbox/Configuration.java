package io.gitbox;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.graphics.ImageData;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Jean-Baptiste Lemée
 */
public class Configuration {

    private static final String PROPERTIES_FILE = "gitbox.properties";
    private static final String DIRECTORY_PROPERTY_NAME = "gitbox.directory";
    private static final String GIT_REPOSITORY_PROPERTY_NAME = "gitbox.repository";
    private static final String NOTIFICATION_SERVER_PROPERTY_NAME = "gitbox.notification.server";

    public static ImageData getIcon() {
        return new ImageData(Configuration.class.getClassLoader().getResourceAsStream("051.gif"));
    }

    private static String loadProperty(String propertyName) throws IOException {
        Properties properties = new Properties();
        InputStream propertiesInput = Configuration.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
        if (propertiesInput == null) {
            return null;
        }
        properties.load(propertiesInput);
        return properties.getProperty(propertyName);
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

}
