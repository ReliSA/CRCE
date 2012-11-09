/**
 *
 */
package cz.zcu.kiv.crce.efp.indexer.test.container;

import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;
import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.efp.indexer.internal.Activator;
import cz.zcu.kiv.crce.efp.indexer.test.ContainerTestIndexerHandler;
import cz.zcu.kiv.crce.efp.indexer.test.ContainerTestResourceActionHandler;
import cz.zcu.kiv.crce.efp.indexer.test.support.DataContainerForTestingPurpose;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.plugin.MetadataIndexingResultService;

/**
 * Class starts testing container.
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class ContainerRunnerTest extends TestCase {

    /** Container includes and provides these packages when container is started. */
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
     * @return the configuration
     */
    @Configuration
    public final Option[] config() {
        return options(
                systemPackage(systemPackages),
                junitBundles(),
                felix(),
                // DS support
                mavenBundle("org.apache.felix", "org.apache.felix.scr", "1.6.0"),
                mavenBundle("org.apache.felix", "org.apache.felix.dependencymanager"),
                mavenBundle("org.apache.felix", "org.apache.felix.bundlerepository"),
                mavenBundle("org.apache.felix", "org.osgi.service.obr"),
                mavenBundle("org.apache.ace", "org.apache.ace.obr.metadata"),
                mavenBundle("org.apache.ace", "org.apache.ace.obr.storage"),
                mavenBundle("org.apache.felix", "org.apache.felix.shell"),
                mavenBundle("org.apache.felix", "org.osgi.compendium"),

                //mavenBundle().groupId("log4j").artifactId("log4j").version("1.2.16"),

                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-results-api").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-results-impl").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-api").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-plugin-api").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-repository-api").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-repository-impl").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-api").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-impl").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-metafile").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-dao-api").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-efp-indexer").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-efpAssignment").version("1.0.0-SNAPSHOT")
                );
    }

    //private volatile PluginManager m_pluginManager;     /* injected by dependency manager */

    /** Data container is used for storing paths to testing artifacts and some instances which are used during testing process.*/
    private DataContainerForTestingPurpose dctp = new DataContainerForTestingPurpose();

    /** For creating a new resource. */
    private ResourceCreator resCreator;

    /** For creating resource from META file. */
    private ResourceDAO resourceDao;
    
    private static final Logger logger = LoggerFactory.getLogger(ContainerRunnerTest.class);

    @Test
    /**
     * Really simple and short test to prove that test container runs.
     * @param bundleContext for access to services.
     * @throws InvalidSyntaxException
     */
    public final void testMethod1(final BundleContext bundleContext) throws InvalidSyntaxException {
        dctp.getTestLogService().log(LogService.LOG_INFO, "TestMethod1  ---------------------------");
        assertEquals(true, true); // Jen pro otestovani, ze se jiz spousti metody s testy.
    }

    @Test
    /**
     * Initial method for testing the indexerInitialization(Resource resource) method.
     * @param bundleContext for access to services.
     * @throws InvalidSyntaxException
     */
    public final void testMethod2(final BundleContext bundleContext) throws InvalidSyntaxException {
        dctp.getTestLogService().log(LogService.LOG_INFO, "ContainerTestIndexerHandler ---------------------------");

        init(bundleContext);

        ContainerTestIndexerHandler ctih = new ContainerTestIndexerHandler(resCreator);
        ctih.testIndexerInitialization();

        logger.info("ContainerTestIndexerHandler finished!");
    }


    /**
     *
     * @param bundleContext for access to services.
     * @throws InvalidSyntaxException
     */
    @Test
    public final void testMethod3(final BundleContext bundleContext) throws InvalidSyntaxException {
        dctp.getTestLogService().log(LogService.LOG_INFO, "ContainerTestResourceActionHandler ---------------------------");

        init(bundleContext);

        ContainerTestResourceActionHandler ctrah = new ContainerTestResourceActionHandler(resourceDao, resCreator);
        ctrah.testHandleNewResource();

        logger.info( "ContainerTestResourceActionHandler finished!");
    }

    //==================================================
    //		Auxiliary methods with non test prefix:
    //==================================================

    /**
     * Method sets used important instances by dependency injection.
     * @param bundleContext for access to services.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public final void init(final BundleContext bundleContext){
        dctp.getTestLogService().log(LogService.LOG_INFO, "INIT method ---------------------------");
        assertNotNull(bundleContext);

        ServiceReference serviceRef = bundleContext.getServiceReference("cz.zcu.kiv.crce.metadata.ResourceCreator");
        Bundle bundle = serviceRef.getBundle();
        resCreator = (ResourceCreator) bundle.getBundleContext().getService(serviceRef);
        assertNotNull(resCreator);

        serviceRef = bundleContext.getServiceReference("cz.zcu.kiv.crce.plugin.MetadataIndexingResultService");
        bundle = serviceRef.getBundle();
        MetadataIndexingResultService mirs = (MetadataIndexingResultService) bundle.getBundleContext().getService(serviceRef);
        assertNotNull(mirs);

        Activator.activatorInstance = new Activator();

        Activator.instance().setmMetadataIndexingResult(mirs);

        serviceRef = bundleContext.getServiceReference("cz.zcu.kiv.crce.metadata.dao.ResourceDAO");
        bundle = serviceRef.getBundle();
        resourceDao = (ResourceDAO) bundle.getBundleContext().getService(serviceRef);
        assertNotNull(resourceDao);
    }
}
