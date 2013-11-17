/**
 *
 */
package cz.zcu.kiv.crce.it.container;

//import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;

import java.io.File;

import javax.inject.Inject;

import junit.framework.TestCase;

import org.apache.felix.dm.DependencyManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import cz.zcu.kiv.crce.metadata.dao.internal.ResourceDAOImpl;
//import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
//import cz.zcu.kiv.crce.metadata.internal.RequirementImpl;
//import cz.zcu.kiv.crce.metadata.internal.ResourceImpl;

import cz.zcu.kiv.crce.it.Activator;
import cz.zcu.kiv.crce.it.support.DataContainerForTestingPurpose;
import java.util.logging.Level;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.util.tracker.ServiceTracker;

import org.junit.Ignore;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

/**
 * Class starts testing container.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
@Ignore
public class ContainerRunnerTest extends TestCase {

    @Inject
    private BundleContext bc;
    private DependencyManager manager;
    /**
     * Container includes and provides these packages when container is started.
     */
    private String systemPackages =
            "com.sun.*,javax.xml.*,com.sun.org.apache.xerces.internal.*,"
            + "javax.accessibility,javax.annotation,javax.inject,javax.jmdns,javax.jms,javax.mail,"
            + "javax.mail.internet,javax.management,javax.management.modelmbean,javax.management.remote,"
            + "javax.microedition.io,javax.naming,javax.script,javax.security.auth.x500,javax.servlet,"
            + "javax.servlet.http,javax.servlet.jsp,javax.sql,"
            + "org.w3c.dom,org.xml.sax,org.xml.sax.ext,org.xml.sax.helpers,"
            + "org.w3c.dom.xpath,sun.io,org.w3c.dom.ls,"
            + "com.sun.java_cup.internal,com.sun.xml.internal.bind.v2";

    /**
     * Configuration of the OSGi runtime.
     *
     * @return the configuration
     */
    @Configuration
    public final Option[] config() {

        System.out.println("Option config");
        return options(
                systemPackage(systemPackages),
                junitBundles(),
//                felix(),
                // DS support
                mavenBundle("org.apache.felix", "org.apache.felix.scr", "1.6.0"),
                mavenBundle("org.apache.felix", "org.apache.felix.dependencymanager"),
                mavenBundle("org.apache.felix", "org.apache.felix.bundlerepository"),
                mavenBundle("org.apache.felix", "org.osgi.service.obr"),
                mavenBundle("org.apache.ace", "org.apache.ace.obr.metadata"),
                mavenBundle("org.apache.ace", "org.apache.ace.obr.storage"),
                mavenBundle("org.apache.felix", "org.apache.felix.shell"),
                mavenBundle("org.osgi", "org.osgi.compendium"),
                mavenBundle("org.slf4j", "slf4j-api"),
                mavenBundle("ch.qos.logback", "logback-core"),
                mavenBundle("ch.qos.logback", "logback-classic"),
                mavenBundle("com.sun.jersey", "jersey-core", "1.17"),
                mavenBundle("com.sun.jersey", "jersey-server", "1.17"),
                mavenBundle("com.sun.jersey", "jersey-servlet", "1.17"),
                //                mavenBundle("org.codehaus.groovy", "groovy-all"),
                //                mavenBundle("org.codehaus.janino", "commons-compiler"),

                //mavenBundle().groupId("log4j").artifactId("log4j").version("1.2.16"),

                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-results-api").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-results-impl").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-api").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-plugin-api").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-repository-api").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-repository-impl").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-api").version("2.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-impl").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-metafile").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-dao-api").version("2.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-efp-indexer").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-efp-assignment").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-rest").version("1.0.0-SNAPSHOT").classifier("classes"));
    }
    //private volatile PluginManager m_pluginManager;     /* injected by dependency manager */
    /**
     * Data container is used for storing paths to testing artifacts and some
     * instances which are used during testing process.
     */
    private DataContainerForTestingPurpose dctp = new DataContainerForTestingPurpose();
    //private Buffer m_buffer;
    //private Store store;
    private static final Logger logger = LoggerFactory.getLogger(ContainerRunnerTest.class);

    /**
     * Really simple and short test to prove that test container runs.
     *
     * @param bundleContext for access to services.
     * @throws InvalidSyntaxException
     */
    @Test
    public void testMethod1() throws InvalidSyntaxException {
        System.out.println("A Simple OSGi Integration Test");
        assertEquals(true, true); // Jen pro otestovani, ze se jiz spousti metody s testy.
        try {
            //testOsgiPlatformStarts(bc);
            //testOsgiEnvironment(bc);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ContainerRunnerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param bundleContext for access to services.
     * @throws InvalidSyntaxException
     */
    @Test
    public void testMethod3() throws InvalidSyntaxException {
        //logger.info("ContainerTestResourceActionHandler ---------------------------");
        System.out.println("test3");

        init(bc, manager);
        /*
         ContainerTestRestHandler ctrh = new ContainerTestRestHandler();
         ctrh.testGetMetadatat();*/

        System.out.println("konec testu 3");
        //logger.info( "ContainerTestResourceActionHandler finished!");
    }

    //==================================================
    //		Auxiliary methods with non test prefix:
    //==================================================
    public final boolean addResourceToRepository(File file) {
        boolean success = true;
        /*
        try {
            FileInputStream fis = new FileInputStream(file);

            try {
                if (m_buffer.put(file.getName(), fis) == null) {
                    success = false;
                    m_buffer.commit(false);
                }
            } catch (RevokedArtifactException ex) {
                logger.warn("Artifact revoked: ", ex.getMessage());
                success = false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            success = false;
        } catch (IOException e) {
            success = false;
        }
        */
        return success;

    }

    public final void prepareRepository() {

        File f = new File(DataContainerForTestingPurpose.BUNDLE1);
        System.out.println("FILE:" + f.getAbsolutePath());

        addResourceToRepository(new File(DataContainerForTestingPurpose.BUNDLE1));
    }

    /**
     * Method sets used important instances by dependency injection.
     *
     * @param bundleContext for access to services.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public final void init(final BundleContext bundleContext, DependencyManager dm) {
        logger.info("INIT method ---------------------------");
        System.out.println("init");
        assertNotNull(bundleContext);

        System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VENDOR));
        System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VERSION));
        System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));

        try {

            System.out.println("SERVICES\n");
            ServiceReference<?>[] sr = bundleContext.getAllServiceReferences(null, null);
            for (ServiceReference<?> ser : sr) {
                System.out.println(ser.getBundle().getSymbolicName() + " , " + ser.toString());
            }

            System.out.println("\nBUNDLES\n");
            Bundle[] bbs = bc.getBundles();
            for (Bundle b : bbs) {
                System.out.println(b.getLocation() + ", " + b.toString());
            }
            System.out.println("\n");

        } catch (InvalidSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("GET buffer from activator");

        //if (m_buffer == null) {
          //  System.out.println("Fuck null.");
        //}

        if (Activator.instance() != null) {
            prepareRepository();
            //buffer = Activator.instance().getBuffer();
            //assertNotNull(buffer);
        } else {
            System.out.println("Activator null.");
        }
    }

    public void testOsgiPlatformStarts(BundleContext bundleContext) throws Exception {
        System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VENDOR));
        System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VERSION));
        System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));
    }

    public void testOsgiEnvironment(BundleContext bundleContext) throws Exception {
        Bundle[] bundles = bundleContext.getBundles();
        for (int i = 0; i < bundles.length; i++) {
            System.out.print(bundles[i]);
            System.out.print(", ");
        }
        System.out.println();
    }
    //private volatile PluginManager m_pluginManager;

    @Test
    public void testResourceDAO() {

        //cz.zcu.kiv.crce.repository.impl, cz.zcu.kiv.crce.repository.impl
        //mvn:cz.zcu.kiv.crce/crce-repository-impl/1.0.0-SNAPSHOT
        // crce-metadata-dao-api/2.0.0-SNAPSHOT
        // mvn:cz.zcu.kiv.crce/crce-metadata-dao-api/2.0.0-SNAPSHOT

        //cz.zcu.kiv.crce.plugin.MetadataIndexingResultService
        //cz.zcu.kiv.crce.plugin

        //cz.zcu.kiv.crce.metadata.ResourceCreator
        //cz.zcu.kiv.crce.metadata.impl

        Bundle bundle = bc.getBundle("mvn:cz.zcu.kiv.crce/crce-metadata-dao-api/2.0.0-SNAPSHOT");
        /*
         try {
         bundle.start();
         } catch (BundleException e) {
         fail(e.toString());
         }
         */

        //ResourceDAO resourceDAO = waitForService(bundle, ResourceDAO.class);

        //ResourceFactoryImpl factory = new ResourceFactoryImpl();
        //Resource r = factory.createResource();

        // m_pluginManager.getPlugin(ResourceDAO.class).saveResource(r);

        /*
        try {
            resourceDAO.saveResource(r);
            resourceDAO.loadResource(null);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ContainerRunnerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        //Properties r = buffer2.getProperties();
        //for(Resource rr : r.values()){
        //  System.out.println(rr.getPresentationName());
        //}
    }

    protected <T> T waitForService(Bundle b, Class<T> clazz) {
        try {
            BundleContext bc2 = b.getBundleContext();
            ServiceTracker st = new ServiceTracker(bc2, clazz.getName(), null);
            st.open();
            Object service = st.waitForService(30 * 1000);
            assertNotNull("No service of the type " + clazz.getName()
                    + " was registered.", service);
            st.close();
            return (T) service;
        } catch (Exception e) {
            fail("Failed to register services for " + b.getSymbolicName()
                    + e.getMessage());
            return null;
        }
    }

    //private ResourceFactory resourceFactory;

    //@Test
//    public void testResourceDAOImp() throws IOException {
//
//        String resourceID = "jedna";
//
//        ResourceImpl r = new ResourceImpl(resourceID);
//
//        CapabilityImpl cap = new CapabilityImpl("nameSpace", "1");
//        r.addCapability(cap);
//
//        RequirementImpl req = new RequirementImpl("nameSpace", "1");
//        r.addRequirement(req);
//
//        ResourceDAOImpl impl = new ResourceDAOImpl();
//        impl.saveResource(r);
//        Resource r2 = impl.loadResource(null);
//
//        assertTrue(r.equals(r2));
//
//    }
}
