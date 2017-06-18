package cz.zcu.kiv.crce.mvn.plugin.internal;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.internal.MetadataFactoryImpl;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.service.internal.MetadataServiceImpl;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
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

    private static MetadataFactory metadataFactory;
    private static MetadataService metadataService;

    @BeforeClass
    public static void before() {
        metadataFactory = new MetadataFactoryImpl();
        metadataService = new MetadataServiceImpl();
    }

    @Test
    public void testLoadPom() throws /*IOException, XmlPullParserException*/ Exception {
        URL artifactUrl = getResource(TEST_JAR_NAME);
        MavenPlugin mp = new MavenPlugin();

        assertNotNull("Test jar doesn't exists!",artifactUrl);
        Model pomModel = mp.loadPom(artifactUrl);
        assertNotNull("Null returned!", pomModel);
        assertEquals("Wrong artifact id!", TEST_ARTIFACT_ID, pomModel.getArtifactId());
        // version should be taken from <parent> tag
        assertEquals("Wrong version!", TEST_VERSION, pomModel.getVersion());
    }

    /**
     * Verify that the method is not dependent on file name or extension.
     */
    @Test
    // todo: find out what to do when the artifact doesn't contain pom.xml
    public void testLoadPom2() throws IOException, XmlPullParserException {
        // both files contain same artifact
        String name1="/hibernate-core-5.2.7.Final.jar";
        String name2="/test-hibernate-core";
        MavenPlugin mp = new MavenPlugin();

        Model model1 = mp.loadPom(getResource(name1));
        Model model2 = mp.loadPom(getResource(name2));

        assertNotNull("Model for name "+name1+" is null!", model1);
        assertNotNull("Model for name "+name2+" is null!", model2);

        assertEquals("GroupIds are not same!", model1.getGroupId(), model2.getGroupId());
        assertEquals("ArtifactIds are not same!", model1.getArtifactId(), model2.getArtifactId());
        assertEquals("Versions are not same!", model1.getVersion(), model2.getVersion());
    }

    /**
     * Returns URL to files from the resources folder.
     * @param resourceName Name of the resource file.
     * @return URL to the resour ce file.
     */
    private URL getResource(String resourceName) {
        return getClass().getResource(resourceName);
    }
}
