package cz.zcu.kiv.crce.mvn.plugin.internal;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.internal.MetadataFactoryImpl;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.service.internal.MetadataServiceImpl;
import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenLocator;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenResolver;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.CentralMavenRestLocator;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.resolver.MavenAetherResolver;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.*;

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
     * Try to load pom from jar without pom, then add pom and try it again.
     */
    @Test
    public void testLoadPom2() throws IOException, XmlPullParserException, URISyntaxException {
        // both files contain same artifact
        String origName = "/hibernate-core-5.2.7.Final-orig.jar";
        String name1="/hibernate-core-5.2.7.Final.jar";
        String pomPath = "/hibernate-core-pom.xml";
        String gid = "org.hibernate";
        String aId = "hibernate-core";
        String v = "5.2.7.Final";
        MavenPlugin mp = new MavenPlugin();

        // copy orig jar to test jar so that test jar won't contain pom.xml
        Path p = Paths.get(getResource(origName).toURI());
        Path p2 = Paths.get(getResource(name1).toURI());
        Files.copy(p, p2, StandardCopyOption.REPLACE_EXISTING);

        // try to load model without pom
        assertFalse("Jar shouldn't contain pom.xml file yet!", FileUtil.isFilePresentInJar(p2.toString(), "pom.xml"));
        Model model = null;
        try {
            model = mp.loadPom(getResource(name1));
        } catch (IllegalArgumentException e) {
            // ok
        } catch (Exception e) {
            fail("Unexpected exception while load pom model: "+e.getMessage());
        }

        // add pom to the jar and load model again
        FileUtil.addPomToJar(p2.toString(), getResource(pomPath).getPath());
        assertTrue("Jar should now contain pom.xml file!", FileUtil.isFilePresentInJar(p2.toString(), "pom.xml"));
        model = mp.loadPom(getResource(name1));
        assertEquals("Wrong groupId!", gid, model.getGroupId());
        assertEquals("Wrong artifactId!", aId, model.getArtifactId());
        assertEquals("Wrong version!", v, model.getVersion());
    }

    /**
     * Test complete functionality.
     */
    @Test
    public void testLocateResolveLoadPom() throws IOException, XmlPullParserException {
        MavenLocator locator = new CentralMavenRestLocator();
        MavenResolver resolver = new MavenAetherResolver();
        MavenPlugin mp = new MavenPlugin();
        String groupId = "org.hibernate";
        String artifactId = "hibernate-core";
        String version = "5.2.7.Final";

        FoundArtifact artifact = locator.locate(groupId, artifactId, version);
        assertNotNull("Null returned by locate() method!", artifact);
        File file = resolver.resolve(artifact);
        assertNotNull("Null returned by resolve() method!", file);
        Model model = mp.loadPom(file.toURI().toURL());
        assertNotNull("Null returned by loadPom() method!", model);

        assertEquals("Wrong groupId!", groupId, model.getGroupId());
        assertEquals("Wrong artifactId!", artifactId, model.getArtifactId());
        assertEquals("Wrong version!", version, model.getVersion());
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
