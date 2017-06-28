package cz.zcu.kiv.crce.mvn.plugin.internal;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        String jarWithoutFile = "/hibernate-core-5.2.7.Final-orig.jar";
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
        assertFalse("Working copy should not contain pom.xml!", FileUtil.isFilePresentInJar(p2.toString(), filename));

        // add pom file
        InputStream in = new FileInputStream(new File(pathToPom));
        FileUtil.addPomToJar(jarWithoutFileCopy, in);
        in.close();

        // check that the copy now contains pom.xml
        assertTrue("Working copy should now contain pom.xml!", FileUtil.isFilePresentInJar(jarWithoutFileCopy, filename));

        // check that the original pom still exists
        File pomFile = new File(pathToPom);
        assertTrue("Original pom file should still exist!", pomFile.exists());

        // delete working copy
        File f = new File(jarWithoutFileCopy);
        f.delete();
    }

    @Test
    @Ignore
    public void testUnjar() throws FileUtilOperationException, IOException {
        // test jar contains only pom.xml and META-INF/MANIFEST.MF files
        String testJar = "/test-jar.jar";
        String dir = "D:/tmp/bp/test/test-jar-dir";
        String expectedFile = dir+"/pom.xml";
        String expectedFile2 = dir+"/META-INF/MANIFEST.MF";

        // delete the target dir if it exists
        File targetDir = new File(dir);
        if(targetDir.exists()) {
            targetDir.delete();
        }

        // unjar
        FileUtil.unJar(getResource(testJar).getPath(), dir);

        File f = new File(expectedFile);
        File f2 = new File(expectedFile2);
        assertTrue("Target dir should exist!", targetDir.exists());
        assertTrue("Pom.xml should exist in unzipped archive!", f.exists());
        assertTrue("Manifest file should exist in unzipped archive!", f2.exists());
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
