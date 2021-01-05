package cz.zcu.kiv.crce.restimpl.indexer.util;

import cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting.RestApiReconstructor;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting.RestApiReconstructorImpl;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;

public class ResourceLoadingTest {

    /**
     * Create parser and make sure resource files are loaded.
     * If something's wrong, NPE should be thrown in the constructor.
     */
    @Test
    public void testWebXmlParser() throws IOException {
        WebXmlParser parser = new WebXmlParser();
        assertNotNull("No parser created!", parser);
    }

    /**
     * Create reconstructor and make sure all configurations from config/def/api are loaded.
     */
    @Test
    public void testRestApiReconstructor() {
        RestApiReconstructor restApiReconstructor = new RestApiReconstructorImpl(Collections.emptyMap(), new WebXmlParser.Result());
        assertNotNull("No reconstructor created!", restApiReconstructor);
    }
}
