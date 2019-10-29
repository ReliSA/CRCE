package cz.zcu.kiv.crce.restimpl.indexer.util;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class WebXmlParserTest {

    /**
     * Create parser and make sure resource files are loaded.
     * If something's wrong, NPE should be thrown in the constructor.
     */
    @Test
    public void testCreateParser() throws IOException {
        WebXmlParser parser = new WebXmlParser();
        assertNotNull("No parser created!", parser);
    }
}
