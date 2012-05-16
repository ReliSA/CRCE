package cz.zcu.kiv.crce.efp.indexer.test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.osgi.service.log.LogService;

import cz.zcu.kiv.crce.efp.indexer.internal.Activator;
import cz.zcu.kiv.crce.efp.indexer.internal.IndexerHandler;
import cz.zcu.kiv.crce.efp.indexer.test.support.DataContainerForTestingPurpose;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.internal.ResourceCreatorImpl;
import cz.zcu.kiv.crce.plugin.internal.MetadataIndexingResultServiceImpl;
import junit.framework.TestCase;

/**
 * Testing class for IndexerHandler class.
 */
public class IndexerHandlerTest extends TestCase {

	/** Data container is used for storing paths to testing artifacts and some instances which are used during testing process.*/
	private DataContainerForTestingPurpose dctp = new DataContainerForTestingPurpose();


	/** Initial method for testing the indexerInitialization(Resource resource) method. */
	public final void testIndexerInitialization() {

		assertEquals(true, getIndexerInitializationResult(dctp.PATH_TO_OSGI_WITH_EFP));

		assertEquals(false, getIndexerInitializationResult(dctp.PATH_TO_OSGI_WITH_OLD_EFP_VERSION));

		assertEquals(false, getIndexerInitializationResult(dctp.PATH_TO_NON_OSGi));

		Activator.instance().getLog().log(LogService.LOG_INFO, "IndexerHandlerTest finished!");
	}

	//==================================================
	//     Auxiliary methods with non test prefix:
	//==================================================

	/**
	 * Supporting method for test of the indexerInitialization(Resource resource) method.
	 *
	 * @param filePath - File path to testing artifact.
	 * @return true if EFP metadata was loaded or false if this process failed
	 */
	public final boolean getIndexerInitializationResult(final String filePath) {

		Activator.activatorInstance = new Activator();
		Activator.instance().setmLog(dctp.getTestLogService());
		Activator.instance().setmMetadataIndexingResult(new MetadataIndexingResultServiceImpl());

		File fil = new File(filePath);

		Resource resource = new ResourceCreatorImpl().createResource();
		resource.setUri(dctp.getUri(fil.getAbsolutePath()));

		IndexerHandler indexer = new IndexerHandler();

		boolean result = indexer.indexerInitialization(resource);

		indexer.getContainer().getAccessor().close();

		return result;
	}
}
