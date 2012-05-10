package cz.zcu.kiv.crce.efp.indexer.test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.osgi.service.log.LogService;

import cz.zcu.kiv.crce.efp.indexer.internal.IndexerHandler;
import cz.zcu.kiv.crce.efp.indexer.test.support.DataContainerForTestingPurpose;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.internal.ResourceCreatorImpl;
import junit.framework.TestCase;

/**
 * Testing class which should be used in testing container because it requires dependency injection.
 * This class is not finished.
 */
public class ContainerTestIndexerHandler extends TestCase {

    /** Data container is used for storing paths to testing artifacts and some instances which are used during testing process.*/
    private DataContainerForTestingPurpose dctp = new DataContainerForTestingPurpose();

    /** For creating a new resource. */
    private volatile ResourceCreator resCreator;

    /**
     * Constructor.
     * @param resCreator For creating a new resource.
     */
    public ContainerTestIndexerHandler(final ResourceCreator resCreator) {
        this.resCreator = resCreator;
    }

    /** Test of the indexerInitialization(Resource resource) method. */
    public final void testIndexerInitialization() {

        assertEquals(true, getIndexerInitializationResult(dctp.PATH_TO_OSGI_WITH_EFP));

        assertEquals(false, getIndexerInitializationResult(dctp.PATH_TO_OSGI_WITH_OLD_EFP_VERSION));

        assertEquals(false, getIndexerInitializationResult(dctp.PATH_TO_NON_OSGi));
    }

    //==================================================
    //		Auxiliary methods with non test prefix:
    //==================================================

    /**
     * Supporting method for test of the indexerInitialization(Resource resource) method.
     * @param filePath path to artifact file.
     * @return success or fail
     */
    public final boolean getIndexerInitializationResult(final String filePath) {

        File fil = new File(filePath);
        String uriText = "file:" + fil.getAbsolutePath();

        assertNotNull(resCreator);

        Resource resource = resCreator.createResource();

        assertNotNull(resource);

        try {
            resource.setUri(new URI(uriText));
        } catch (URISyntaxException e) {
            dctp.getTestLogService().log(LogService.LOG_ERROR, "URISyntaxException during processing URI path of input resource.");
        }

        IndexerHandler indexer = new IndexerHandler();

        boolean result = indexer.indexerInitialization(resource);

        indexer.getContainer().getAccessor().close();

        return result;
    }
}