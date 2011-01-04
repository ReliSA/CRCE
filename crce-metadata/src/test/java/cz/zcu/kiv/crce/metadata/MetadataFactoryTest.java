package cz.zcu.kiv.crce.metadata;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import cz.zcu.kiv.crce.metadata.internal.MetadataFactoryImpl;
import java.io.File;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author kalwi
 */
public class MetadataFactoryTest {

    private File dir;
    private MetadataFactory factory;
    
    @Before
    public void setUp() {
        dir = createTempDir();
        factory = new MetadataFactoryImpl();
    }

    @After
    public void tearDown() {
        deleteDir(dir);
        dir = null;
        factory = null;
    }

    @Test
    public void createForBundle() throws Exception {
        File testBundle = createResource("bundle.jar");

        Metadata meta = factory.createMetadataFor(testBundle);

        assert meta != null : "Metadata is null";
        assert meta.getResource() != null : "Resource is null";

        String sn = meta.getResource().getSymbolicName();

        assert sn != null : "Symbolic name is null";
        assert "eu.kalwi.osgi.OSGi-Bundle1".equals(sn) : "Expected symbolic name: eu.kalwi.osgi.OSGi-Bundle1, found: " + sn;
        
        String version = meta.getResource().getVersion().toString();
        assert "1.0.0.SNAPSHOT".equals(version) : "Expected version: 1.0.0.SNAPSHOT, found: " + version;
    }

    @Test
    public void createForBundleWithMetafile() throws Exception {
        File testBundle = createResource("bundle.jar");
        createResource("bundle.jar.meta");

        Metadata meta = factory.createMetadataFor(testBundle);

        assert meta != null : "Metadata is null";
        assert meta.getResource() != null : "Resource is null";

        String sn = meta.getResource().getSymbolicName();

        assert sn != null : "Symbolic name is null";
        assert "eu.kalwi.osgi.OSGi-Bundle1".equals(sn) : "Expected symbolic name: eu.kalwi.osgi.OSGi-Bundle1, found: " + sn;

        String version = meta.getResource().getVersion().toString();
        assert "1.0.0.SNAPSHOT".equals(version) : "Expected version: 1.0.0.SNAPSHOT, found: " + version;
        
//        for (Capability c : meta.getResource().getCapabilities()) {
//            if ("feature".equals(c.getName())) {
//                String value = (String) c.getProperties().get("some.name");
//                assert "some.value".equals(value) : "Expected value: some.value, found:" + value;
//            }
//        }
        
        // TODO test for requirements
    }

    @Test
    public void createForOther() throws Exception {
        File testBundle = createResource("other.txt");

        Metadata meta = factory.createMetadataFor(testBundle);

        assert meta != null : "Metadata is null";
        assert meta.getResource() != null : "Resource is null";

        String sn = meta.getResource().getSymbolicName();

        assert sn != null : "Symbolic name is null";
        assert "other.txt".equals(sn) : "Expected symbolic name: other.txt, found: " + sn;

        String version = meta.getResource().getVersion().toString();
        assert "0.0.0".equals(version) : "Expected version: 0.0.0, found: " + version;
    }

    @Test
    public void createForOtherWithMetafile() throws Exception {
        File testBundle = createResource("other.txt");
        createResource("other.txt.meta");

        Metadata meta = factory.createMetadataFor(testBundle);

        assert meta != null : "Metadata is null";
        assert meta.getResource() != null : "Resource is null";

        String sn = meta.getResource().getSymbolicName();

        assert sn != null : "Symbolic name is null";
        assert "other.resource".equals(sn) : "Expected symbolic name: other.resource, found: " + sn;

        String version = meta.getResource().getVersion().toString();
        assert "1.0.0".equals(version) : "Expected version: 1.0.0, found: " + version;
    }

    @Test
    public void loadBundleCapabilities() throws Exception {
        fail("test not implemented");
    }
    
    @Test
    public void loadOtherCapabilities() throws Exception {
        fail("test not implemented");
    }
    
    @Test
    public void loadBundleRequirements() throws Exception {
        fail("test not implemented");
    }
    
    @Test
    public void loadOtherRequirements() throws Exception {
        fail("test not implemented");
    }
    
    // =========================================================================
    
    private File createResource(String file) {
        File resource = new File("src/test/resources/" + file);
        assert resource.exists() : "Resource file not exists : " + resource.getAbsolutePath();

        File temp = new File(dir, file);

        copyfile(resource, temp);
        
        return temp;
    }

    private static File createTempDir() {
        final String baseTempPath = System.getProperty("java.io.tmpdir");

        File tempDir;

        do {
            tempDir = new File(baseTempPath, "crcetest" + System.nanoTime());
        } while (tempDir.exists());

        tempDir.mkdir();
        tempDir.deleteOnExit();

        return tempDir;
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    private static void copyfile(File f1, File f2) {
        try {
            InputStream in = new FileInputStream(f1);
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException ex) {
            fail("File not found");
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
