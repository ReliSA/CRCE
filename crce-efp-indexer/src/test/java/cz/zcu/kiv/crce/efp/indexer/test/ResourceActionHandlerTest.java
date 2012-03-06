package cz.zcu.kiv.crce.efp.indexer.test;

import cz.zcu.kiv.crce.efp.indexer.internal.ResourceActionHandler;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * Testing class for ResourceActionHandler class.
 */
public class ResourceActionHandlerTest {

	  /** Path to jar file used in testing. */
    private static final String PATH_TO_OSGI_WITHOUT_EFP = "./src/test/resources/OSGi_without_EFP.jar";
    /** Path to jar file used in testing. */
    private static final String PATH_TO_OSGI_WITH_EFP = "./src/test/resources/OSGi_with_EFP.jar";
    /** Path to jar file used in testing. */
    private static final String PATH_TO_NON_OSGI = "./src/test/resources/non-OSGi.jar";
    /** Path to jar file used in testing. */
    private static final String PATH_TO_NON_JAR = "./src/test/resources/non-jar.txt";

    /** Instance of tested class. */
    private ResourceActionHandler rah = new ResourceActionHandler();

    /** Test of the jarFileArtefact(String artefactName) method.	*/
	public final void testjarFileArtefact() {
		rah.setmLog(testLogService);
		assert rah.jarFileArtefact(PATH_TO_OSGI_WITH_EFP) == true;
		assert rah.jarFileArtefact(PATH_TO_NON_JAR) == false;
	}

    /** Test of the indexerInitialization(String resourcePath) method.	*/
	public final void testIndexerInitialization() {
		rah.setmLog(testLogService);

		assert rah.indexerInitialization(PATH_TO_OSGI_WITHOUT_EFP) == true;
		assert rah.indexerInitialization(PATH_TO_OSGI_WITH_EFP) == true;
		assert rah.indexerInitialization(PATH_TO_NON_OSGI) == false;
	}

	//--------------------

	/** Implemented interface of LogService used during test process. */
	private LogService testLogService = new LogService() {
		@Override
		public void log(final ServiceReference sr, final int level, final String message,
				final Throwable exception) {
			// TODO Auto-generated method stub
		}
		@Override
		public void log(final ServiceReference sr, final int level, final String message) {
			// TODO Auto-generated method stub

		}
		@Override
		public void log(final int level, final String message, final Throwable exception) {
			// TODO Auto-generated method stub

		}

		@Override
		public void log(final int level, final String message) {
			System.out.println("testLog: (" + level + "): " + message);
		}
	};

}