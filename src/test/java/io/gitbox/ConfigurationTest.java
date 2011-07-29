package io.gitbox;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Jean-Baptiste Lemée
 */
public class ConfigurationTest {

    @Test
    public void testUpdateConfiguration() throws IOException {
        assertNotNull(Configuration.getNotificationServer());
        assertNull(Configuration.getDirectory());
        Configuration.setDirectory("/test/toto");
        assertEquals("/test/toto", Configuration.getDirectory());
        assertNotNull(Configuration.getNotificationServer());
    }
}
