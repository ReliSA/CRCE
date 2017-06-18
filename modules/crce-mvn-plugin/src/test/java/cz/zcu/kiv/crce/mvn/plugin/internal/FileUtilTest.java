package cz.zcu.kiv.crce.mvn.plugin.internal;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Zdenek Vales on 18.6.2017.
 */
public class FileUtilTest {

    @Test
    public void testIsFilePresentInJar() throws IOException {
        String jarWithFile = "/test-jar.jar";
        String jarWithoutFile = "/hibernate-core-5.2.7.Final.jar";
        String filename = "pom.xml";

        assertTrue("Jar "+jarWithFile+" should contain pom.xml!", FileUtil.isFilePresentInJar(getResource(jarWithFile).getPath(), filename));
        assertFalse("Jar "+jarWithoutFile+" should not contain pom.xml!", FileUtil.isFilePresentInJar(getResource(jarWithoutFile).getPath(), filename));
    }

    /**
     * Take jar without file, check that it doesn't contain pom.xml, add pom.xml to that file
     * and verify it contains the pom.
     */
    @Test
    public void testAddPomToJar() throws IOException, URISyntaxException {
        String jarWithoutFile = "D:/tmp/bp/test/hibernate-core-5.2.7.Final.jar";
        String jarWithoutFileCopy = "D:/tmp/bp/test/jar-without-pom.jar";
        String pathToPom = "D:/tmp/bp/test/pom.xml";
        String filename = "pom.xml";

        // create a working copy of jarWithoutFile
        Path p = Paths.get(jarWithoutFile);
        Path p2 = Paths.get(jarWithoutFileCopy);
        Files.copy(p, p2, StandardCopyOption.REPLACE_EXISTING);

        // check that the copy doesn't contain pom.xml
        assertFalse("Working copy should not contain pom.xml!", FileUtil.isFilePresentInJar(jarWithoutFileCopy, filename));

        // add pom file
        FileUtil.addPomToJar(jarWithoutFileCopy, pathToPom);

        // check that the copy now contains pom.xml
        assertTrue("Working copy should now contain pom.xml!", FileUtil.isFilePresentInJar(jarWithoutFileCopy, filename));

        // delete working copy
        File f = new File(jarWithoutFileCopy);
        f.delete();
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
