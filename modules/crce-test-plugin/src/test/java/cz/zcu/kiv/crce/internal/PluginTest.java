package cz.zcu.kiv.crce.internal;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.internal.MetadataFactoryImpl;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.service.internal.MetadataServiceImpl;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Zdenek Vales
 */
public class PluginTest {

    public static final String TEST_JAR_NAME = "/test-jar.jar";
    public static final String TEST_ARTIFACT_ID = "crce-target";
    public static final String TEST_VERSION = "2.1.1-SNAPSHOT";

//    private static MetadataFactory metadataFactory;
//    private static MetadataService metadataService;
//
//    @BeforeClass
//    public static void before() {
//        metadataFactory = new MetadataFactoryImpl();
//        metadataService = new MetadataServiceImpl();
//    }

    @Test
    public void testLoadPom() throws IOException, XmlPullParserException {
        //TODO
        URL artifactUrl = getClass().getResource(TEST_JAR_NAME);
        ExamplePlugin ep = new ExamplePlugin();

        assertNotNull("Test jar doesn't exists!",artifactUrl);
        Model pomModel = ep.loadPom(artifactUrl);
        assertNotNull("Null returned!", pomModel);
        assertEquals("Wrong artifact id!", TEST_ARTIFACT_ID, pomModel.getArtifactId());
        // version should be taken from <parent> tag
        assertEquals("Wrong version!", TEST_VERSION, pomModel.getVersion());
    }
}
