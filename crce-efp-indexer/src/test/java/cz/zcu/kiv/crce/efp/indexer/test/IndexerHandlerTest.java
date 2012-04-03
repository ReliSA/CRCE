package cz.zcu.kiv.crce.efp.indexer.test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.osgi.service.log.LogService;

import cz.zcu.kiv.crce.efp.indexer.internal.IndexerHandler;
import cz.zcu.kiv.crce.efp.indexer.test.support.DataContainerForTestingPurpose;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.internal.ResourceCreatorImpl;
import junit.framework.TestCase;

public class IndexerHandlerTest extends TestCase {
	
	private DataContainerForTestingPurpose dctp = new DataContainerForTestingPurpose();

	
	/** Test of the indexerInitialization(Resource resource) method.	*/
	public void testIndexerInitialization(){

		assertEquals(true, getIndexerInitializationResult(dctp.PATH_TO_OSGI_WITH_EFP));

		assertEquals(false, getIndexerInitializationResult(dctp.PATH_TO_OSGI_WITH_OLD_EFP_VERSION));
		
		assertEquals(false, getIndexerInitializationResult(dctp.PATH_TO_NON_OSGi));
	}

	//==================================================
	//		Auxiliary, untested methods:
	//==================================================
	
	/** Supporting method for test of the indexerInitialization(Resource resource) method.	*/
	public boolean getIndexerInitializationResult(String filePath){

		File fil = new File(filePath);
		String uriText = "file:" + fil.getAbsolutePath();

		Resource resource = new ResourceCreatorImpl().createResource();
		try {
			resource.setUri(new URI(uriText));
		} catch (URISyntaxException e) {
			dctp.getTestLogService().log(LogService.LOG_ERROR, "URISyntaxException during processing URI path of input resource.");
		}

		IndexerHandler indexer = new IndexerHandler(dctp.getTestLogService(), dctp.getErs());

		boolean result = indexer.indexerInitialization(resource); 

		indexer.getContainer().getAccessor().close();

		return result;
	}
}
