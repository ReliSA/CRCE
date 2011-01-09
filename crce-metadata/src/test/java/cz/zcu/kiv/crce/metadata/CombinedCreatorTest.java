package cz.zcu.kiv.crce.metadata;

import cz.zcu.kiv.crce.metadata.internal.CombinedResourceCreatorFactory;
import java.io.File;
import org.junit.*;

/**
 *
 * @author kalwi
 */
public class CombinedCreatorTest {

    private File dir;
    private ResourceCreatorFactory factory;
    private ResourceCreator creator;
    
    @Before
    public void setUp() {
        dir = Util.createTempDir();
        factory = new CombinedResourceCreatorFactory();
        creator = factory.getResourceCreator();
        assert creator != null : "ResourceCreator is null";
    }

    @After
    public void tearDown() {
        Util.deleteDir(dir);
        factory = null;
    }

    @Test
    public void createBundleResource() throws Exception {
        File bundle = Util.prepareFile(dir, "bundle.jar");

        Resource resource = creator.getResource(bundle.toURI());
        assert resource != null : "Resource is null";
        
        String sn = resource.getSymbolicName();
        assert sn != null : "Symbolic name is null";
        assert "eu.kalwi.osgi.OSGi-Bundle1".equals(sn) : "Expected symbolic name: eu.kalwi.osgi.OSGi-Bundle1, found: " + sn;
        
        String version = resource.getVersion().toString();
        assert "1.0.0.SNAPSHOT".equals(version) : "Expected version: 1.0.0.SNAPSHOT, found: " + version;
    }

    @Test
    public void createBundleResourceWithMetafile() throws Exception {
        File bundle = Util.prepareFile(dir, "bundle.jar");
        Util.prepareFile(dir, "bundle.jar.meta");

        Resource resource = creator.getResource(bundle.toURI());
        assert resource != null : "Resource is null";

        String sn = resource.getSymbolicName();
        assert sn != null : "Symbolic name is null";
        assert "eu.kalwi.osgi.OSGi-Bundle1".equals(sn) : "Expected symbolic name: eu.kalwi.osgi.OSGi-Bundle1, found: " + sn;

        String version = resource.getVersion().toString();
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
    public void createOtherResource() throws Exception {
        File bundle = Util.prepareFile(dir, "other.txt");

        System.out.println("bundles URI: " + bundle.toURI() + ", exists: " + bundle.exists());
        
        Resource resource = creator.getResource(bundle.toURI());
        assert resource != null : "Resource is null";

        String sn = resource.getSymbolicName();
        assert sn != null : "Symbolic name is null";
        assert "other.txt".equals(sn) : "Expected symbolic name: other.txt, found: " + sn;

        String version = resource.getVersion().toString();
        assert "0.0.0".equals(version) : "Expected version: 0.0.0, found: " + version;
    }

    @Test
    public void createOtherResourceWithMetafile() throws Exception {
        File bundle = Util.prepareFile(dir, "other.txt");
        Util.prepareFile(dir, "other.txt.meta");

        Resource resource = creator.getResource(bundle.toURI());
        assert resource != null : "Resource is null";

        System.out.println("static sn: " + ((CombinedResource) resource).getStaticResource().getSymbolicName());
        System.out.println("writable sn: " + ((CombinedResource) resource).getWritableResource().getSymbolicName());
        
        String sn = resource.getSymbolicName();
        assert sn != null : "Symbolic name is null";
        assert "other.resource".equals(sn) : "Expected symbolic name: other.resource, found: " + sn;

        String version = resource.getVersion().toString();
        assert "1.0.0".equals(version) : "Expected version: 1.0.0, found: " + version;
    }

//    @Test
//    public void loadBundleCapabilities() throws Exception {
//        fail("test not implemented");
//    }
//    
//    @Test
//    public void loadOtherCapabilities() throws Exception {
//        fail("test not implemented");
//    }
//    
//    @Test
//    public void loadBundleRequirements() throws Exception {
//        fail("test not implemented");
//    }
//    
//    @Test
//    public void loadOtherRequirements() throws Exception {
//        fail("test not implemented");
//    }
    
    // =========================================================================
    
}
