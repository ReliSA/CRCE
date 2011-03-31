package cz.zcu.kiv.crce.metadata.combined;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAO;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAOFactory;
import java.io.File;
import org.junit.*;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import static org.ops4j.pax.exam.CoreOptions.*;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import static org.junit.Assert.*;

/**
 *
 * @author kalwi
 */
@RunWith(JUnit4TestRunner.class)
public class CombinedCreatorTest extends IntegrationTestBase {

    @Inject
    private BundleContext bundleContext;
    
    private File dir;
    private ResourceDAOFactory factory;
    private ResourceDAO creator;
    
    @Before
    public void setUp() throws InvalidSyntaxException {
        dir = Util.createTempDir();
        
        Bundle[] bs = bundleContext.getBundles();
        
        System.out.println("");
        for (Bundle b : bs ) {
            System.out.println(b.getSymbolicName() + ": " + b.getVersion());
        }

        System.out.println("");
        
            ServiceReference[] srs = bundleContext.getServiceReferences(null, null) ;
            
            for (ServiceReference sr : srs) {
                System.out.println("sr: " + sr + ", " + bundleContext.getService(sr).getClass().getName());
                
            }
            
            ServiceReference sr = bundleContext.getServiceReference(PluginManager.class.getName());
            PluginManager pm = (PluginManager) bundleContext.getService(sr);
            factory = pm.getPlugin(ResourceDAOFactory.class);
//    //        factory = new CombinedResourceDAOFactory();
//            assert factory != null : "factory null";
//            assert (factory instanceof CombinedResourceDAOFactory);
            System.out.println(pm.toString());
            System.out.println("factory: " + factory);
//            factory.hashCode();
//            creator = factory.getResourceDAO();
//            assert creator != null : "ResourceCreator is null";
    }

    @Configuration
    public Option[] configuration() {
        return options(
                provision(
                mavenBundle().groupId("org.osgi").artifactId("org.osgi.core"),
                mavenBundle().groupId("org.osgi").artifactId("org.osgi.compendium"),
                mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.dependencymanager").versionAsInProject(),
                mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.shell"),
                mavenBundle().groupId("org.apache.felix").artifactId("org.osgi.service.obr"),
                mavenBundle().groupId("org.apache.ace").artifactId("ace-obr-storage"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-api"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-plugin-api"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-repository-api"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-metafile")
                ));
    }

    @After
    public void tearDown() {
        Util.deleteDir(dir);
        factory = null;
    }

    @Test
    public void example() throws Exception {
        System.out.println("#################################################");
        System.out.println("#################################################");
        System.out.println("#################################################");
        
            ServiceReference sr = bundleContext.getServiceReference(PluginManager.class.getName());
            PluginManager pm = (PluginManager) bundleContext.getService(sr);
            factory = pm.getPlugin(ResourceDAOFactory.class);
            System.out.println(pm.toString());
            System.out.println("factory: " + factory);
        assertNotNull(pm);
        assertNotNull(factory);
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
//                String value = (String) c.getProperties().getResource("some.name");
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
