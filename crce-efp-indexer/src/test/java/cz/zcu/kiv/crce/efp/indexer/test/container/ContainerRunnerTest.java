/**
 *
 */
package cz.zcu.kiv.crce.efp.indexer.test.container;

import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;

import cz.zcu.kiv.crce.efp.indexer.test.ContainerTestIndexerHandler;
import cz.zcu.kiv.crce.efp.indexer.test.support.DataContainerForTestingPurpose;

/**
 * Class starts testing container.
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class ContainerRunnerTest extends TestCase {

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
	public Option[] config() {
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

				mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-results-api").version("1.0.0-SNAPSHOT"),
				mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-results-impl").version("1.0.0-SNAPSHOT"),
				mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-api").version("1.0.0-SNAPSHOT"),
				mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-plugin-api").version("1.0.0-SNAPSHOT"),
				mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-repository-api").version("1.0.0-SNAPSHOT"),
				mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-metafile").version("1.0.0-SNAPSHOT"),
				mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-metadata-dao-api").version("1.0.0-SNAPSHOT"),
				mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-efp-indexer").version("1.0.0-SNAPSHOT"),
				mavenBundle().groupId("cz.zcu.kiv.crce").artifactId("crce-efpAssignment").version("1.0.0-SNAPSHOT"),

				provision(
						bundle().set(Constants.BUNDLE_MANIFESTVERSION, "2")
						.set(Constants.BUNDLE_SYMBOLICNAME,
								"TESTING BUNDLE")
								.set(Constants.BUNDLE_VERSION, "1.0.0")
								.set(Constants.BUNDLE_ACTIVATOR,
										Activator.class.getName())
										.set(Constants.IMPORT_PACKAGE,
												"cz.zcu.kiv.crce.metadata;version=\"[1.0,2)\",cz.zcu.kiv."
														+ "crce.metadata.dao;version=\"[1.0,2)\",cz.zcu.kiv.crce.plugin;version=\"["
														+ "1.0,2)\",cz.zcu.kiv.crce.repository;version=\"[1.0,2)\",cz.zcu.kiv.crce."
														+ "repository.plugins;version=\"[1.0,2)\",cz.zcu.kiv.efps.assignment.api,c"
														+ "z.zcu.kiv.efps.assignment.client,cz.zcu.kiv.efps.assignment.core,cz.z"
														+ "cu.kiv.efps.assignment.osgi,cz.zcu.kiv.efps.assignment.types,cz.zcu.k"
														+ "iv.efps.assignment.values,cz.zcu.kiv.efps.types.datatypes,cz.zcu.kiv."
														+ "efps.types.evaluator,cz.zcu.kiv.efps.types.gr,cz.zcu.kiv.efps.types.l"
														+ "r,cz.zcu.kiv.efps.types.properties,"
														+ "javax.xml.parsers,javax.xml.transform,javax.xml.transform.dom,"
														+ "javax.xml.transform.stream,javax.xml.validation,org.apache.felix.dm,"
														+ "org.apache.felix.shell,org.w3c.dom,org.xml.sax,"
														+ "org.osgi.framework,"
														+ "org.osgi.framework.hooks.resolver,"
														+ "org.osgi.framework.hooks.service,"
														+ "org.slf4j,"
														+ "org.osgi.service.log,"
														+ "cz.zcu.kiv.crce.efp.indexer,"
														+ "cz.zcu.kiv.crce.efp.indexer.internal,"
														+ "junit.framework,"
														+ "org.osgi.framework.wiring"
												)
												.add(Activator.class)
												.add(ContainerTestIndexerHandler.class)
												.add(DataContainerForTestingPurpose.class)
												.build()
						)
				);
	}

	
	@Test
	//@SuppressWarnings("unchecked")
	public void testMethod1() throws InvalidSyntaxException {
		assertEquals(true, true); // Jen pro otestovani, ze se jiz spousti metody s testy.
	}
}
