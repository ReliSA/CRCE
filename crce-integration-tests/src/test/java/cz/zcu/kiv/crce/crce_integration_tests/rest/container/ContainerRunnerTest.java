/**
 *
 */
package cz.zcu.kiv.crce.crce_integration_tests.rest.container;

import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;

import junit.framework.TestCase;

import org.apache.felix.dm.DependencyManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.crce_integration_tests.rest.Activator;
import cz.zcu.kiv.crce.crce_integration_tests.rest.support.DataContainerForTestingPurpose;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;
import cz.zcu.kiv.crce.repository.Store;

/**
 * Class starts testing container.
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class ContainerRunnerTest extends TestCase {
	
	@Inject
    private BundleContext bc;
	
	private DependencyManager manager;

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
    	
    	System.out.println("Option config");
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
                mavenBundle("org.osgi", "org.osgi.compendium"),
                mavenBundle("org.slf4j", "slf4j-api"),
                mavenBundle("ch.qos.logback", "logback-core"),
                mavenBundle("ch.qos.logback", "logback-classic"),
                mavenBundle("com.sun.jersey" , "jersey-core" , "1.17"),
                mavenBundle("com.sun.jersey" , "jersey-server" , "1.17"),
                mavenBundle("com.sun.jersey" , "jersey-servlet" , "1.17"),
                
//                mavenBundle("org.codehaus.groovy", "groovy-all"),
//                mavenBundle("org.codehaus.janino", "commons-compiler"),

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
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-efp-assignment").version("1.0.0-SNAPSHOT"),
                mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-rest").version("1.0.0-SNAPSHOT").classifier("classes")
                );
    }

    //private volatile PluginManager m_pluginManager;     /* injected by dependency manager */

    /** Data container is used for storing paths to testing artifacts and some instances which are used during testing process.*/
    private DataContainerForTestingPurpose dctp = new DataContainerForTestingPurpose();

    
    private Buffer buffer;
    
    private Store store;


    private static final Logger logger = LoggerFactory.getLogger(ContainerRunnerTest.class);

    
    /**
     * Really simple and short test to prove that test container runs.
     * @param bundleContext for access to services.
     * @throws InvalidSyntaxException
     */
    @Test
    public void testMethod1() throws InvalidSyntaxException {
        System.out.println("Test method 1");
        assertEquals(true, true); // Jen pro otestovani, ze se jiz spousti metody s testy.
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
		try {
			FileInputStream fis = new FileInputStream(file);

			

			try {
				if (buffer.put(file.getName(), fis) == null) {
					success = false;
					buffer.commit(false);
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
		
		return success;

	}
	
	public final void prepareRepository() {
		addResourceToRepository(new File(DataContainerForTestingPurpose.BUNDLE1));
	}
    
    
    
    /**
     * Method sets used important instances by dependency injection.
     * @param bundleContext for access to services.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public final void init(final BundleContext bundleContext, DependencyManager dm){
        logger.info("INIT method ---------------------------");
        System.out.println("init");
        assertNotNull(bundleContext);
        
        
        try {
			
			ServiceReference<?>[] sr = bundleContext.getAllServiceReferences(null, null);
			
			for(ServiceReference<?> ser: sr) {
				System.out.println(ser.getBundle().getSymbolicName() + " , " + ser.toString());
				
			}
			
			
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println("GET buffer from activator");
        
        if(Activator.instance() == null) {
        	System.out.println("Activator null.");
        }
        //buffer = Activator.instance().getBuffer();
        
        //assertNotNull(buffer);

        prepareRepository();
    }
}
