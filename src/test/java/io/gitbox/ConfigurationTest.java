package io.gitbox;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Jean-Baptiste Lemée
 */
public class ConfigurationTest {

    @Test
    public void testUpdateConfiguration() throws IOException {
        assertNull(Configuration.getDirectory());
        Configuration.setDirectory("/test/toto");
        assertEquals("/test/toto", Configuration.getDirectory());
    }
}
