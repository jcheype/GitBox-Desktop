package io.gitbox;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Jean-Baptiste Lemée
 */
public class ConfigurationTest {

    @Test
    public void testUpdateConfiguration() throws IOException {
        assertFalse(StringUtils.isBlank(Configuration.getNotificationServer()));
        assertTrue(StringUtils.isBlank(Configuration.getDirectory()));
        Configuration.setDirectory("/test/toto");
        assertEquals("/test/toto/", Configuration.getDirectory());
        assertFalse(StringUtils.isBlank(Configuration.getNotificationServer()));
    }
}
