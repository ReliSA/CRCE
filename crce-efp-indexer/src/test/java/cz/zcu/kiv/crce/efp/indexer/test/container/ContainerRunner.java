/**
 *
 */
package cz.zcu.kiv.crce.efp.indexer.test.container;

import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
//import org.ops4j.pax.exam.spi.PaxExamRuntime;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
//import org.ops4j.pax.exam.spi.container.TestContainerFactory;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;

import cz.zcu.kiv.crce.efp.indexer.test.EfpIndexerResultServiceImplTest;
import cz.zcu.kiv.crce.efp.indexer.test.IndexerHandlerTest;
import cz.zcu.kiv.crce.efp.indexer.test.ResourceActionHandlerTest;


/**
 * NON FINISHED CLASS which should start testing container.
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class ContainerRunner extends TestCase {

	/**
	 * Configuration of the OSGi runtime.
	 * @return the configuration
	 */
	@Configuration
	public Option[] config() {
		return options(
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
				mavenBundle("javax.inject", "javax.inject"),
				// PO PRIDANI mavenBundle("javax.inject", "javax.inject") 
				// VYKAZUJE PRI SPUSTENI CHYBU: 
				// ERROR JUnit4TestRunner Exception
				// org.ops4j.pax.exam.TestContainerException: Problem starting test container.

				// BEZ PRIDANI javax.inject SE POKOUSI O START VSECH NIZE UVEDENYCH BUNDLU,
				// ALE PRI STARTU NEKTERYCH Z NICH NEMUZE DOKONCIT VZAJEMNE NAVAZANI
				// A DOZADUJE SE CHYBEJICIHO BALIKU javax.inject
				
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
                                // 2 means Bundle R4 version
                                .set(Constants.BUNDLE_SYMBOLICNAME,
                                        "TESTOVACI BUNDLE")
                                        .set(Constants.BUNDLE_VERSION, "1.0.0")
                                .set(Constants.BUNDLE_ACTIVATOR,
                                        ActivatorForTestBundle.class.getName())
                                .set(Constants.IMPORT_PACKAGE,
                                        "org.osgi.framework,"
                                                + "org.osgi.framework.hooks.resolver,"
                                                + "org.osgi.framework.hooks.service,"
                                                + "org.slf4j,"
                                                + "cz.zcu.kiv.crce.efp.*," // Important for binding with tested module.
                                                + "junit.framework,"
                                                + "org.osgi.framework.wiring")
                                 .add(ActivatorForTestBundle.class)
                                 .add(EfpIndexerResultServiceImplTest.class)
                                 .add(IndexerHandlerTest.class)
                                 .add(ResourceActionHandlerTest.class)
                                 .build()
                         )
				);
	}


	@Test
	//@SuppressWarnings("unchecked")
	public void testMethod1() throws InvalidSyntaxException {
		assertEquals(true, true); // Jen pro otestovani, ze se jiz spousti metody s testy.
	}

	
	// PREDPOKLADAM, ZE NASLEDUJICI TESTOVACI KOD JE NEPOUZITELNY, PROTOZE LEZI MIMO KONTEJNER
	// A ZE ZPUSOB, JAK SPOUSTET TESTY PRACUJICI S OSTATNIMI MODULY V KONTEJNERU 
	// ZNAMENA NUTNOST VYTVORIT TESTOVACI BUNDLE, KTERY JE NAHRAN TEZ DO KONTEJNERU A JE SCHOPEN
	// PRIPOJIT SE NA SLUZBY, KTERE JSOU UVNITR KONTEJNERU PROVOZOVANY
	
	//private DataContainerForTestingPurpose dctp = new DataContainerForTestingPurpose();

	/* *
	 * Test of the handleNewResource() method.
	 */
	/*	@Test
	public void testFunkcnostiKontejneru() {

		File fil = new File("src/test/resources/OSGi_with_EFP.jar");
		String uriText = "file:" + fil.getAbsolutePath();

		Resource resource = new ResourceCreatorImpl().createResource();
		try {
			resource.setUri(new URI(uriText));
		} catch (URISyntaxException e) {
			//dctp.getTestLogService().log(LogService.LOG_ERROR, "URISyntaxException during processing URI path of input resource.");
		}

		IndexerHandler indexer = new IndexerHandler(null, null);

		boolean result = indexer.indexerInitialization(resource); 

		indexer.getContainer().getAccessor().close();

		assertEquals(true, result);  // ZKRACENO PRO MOZNOST OTESTOVANI SPOUSTENI TESTOVACICH KODU
	}*/

}
