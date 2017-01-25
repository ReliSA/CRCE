package cz.zcu.kiv.crce.internal;

import cz.zcu.kiv.crce.ExamplePlugin;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Zdenek Vales
 */
public class PluginTest {

    public static final String TEST_JAR_NAME = "test-jar.jar";
    public static final String TEST_ARTIFACT_ID = "crce-modules-parent";

    @Test
    public void testLoadPom() throws IOException, XmlPullParserException {
        //TODO
        URL artifactUrl = getClass().getResource(TEST_JAR_NAME);
        ExamplePlugin ep = new ExamplePlugin();

        Model pomModel = ep.loadPom(artifactUrl);
        assertNotNull("Null returned!", pomModel);
        assertEquals("Wrong value of artifact id!", TEST_ARTIFACT_ID, pomModel.getArtifactId());
    }
}
