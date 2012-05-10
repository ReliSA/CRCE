package cz.zcu.kiv.crce.efp.indexer.test.support;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import cz.zcu.kiv.crce.efp.indexer.internal.ResourceActionHandler;
import cz.zcu.kiv.crce.plugin.MetadataIndexingResultService;
import cz.zcu.kiv.crce.plugin.internal.MetadataIndexingResultServiceImpl;

public class DataContainerForTestingPurpose {

	/** Path to jar file used in testing. */
	public final String PATH_TO_NON_OSGi = "src/test/resources/non-OSGi.jar";

	/** Path to jar file used in testing. */
	public final String PATH_TO_OSGI_WITHOUT_EFP = "src/test/resources/OSGi_without_EFP.jar";

	/** Path to jar file used in testing. */
	public final String PATH_TO_OSGI_WITH_EFP = "src/test/resources/OSGi_with_EFP.jar";

	/** Path to jar file used in testing. */
	public final String PATH_TO_OSGI_WITH_OLD_EFP_VERSION = "src/test/resources/OSGi_with_old_EFP_version.jar";

	/** URI to meta file used in testing. */
	public final String PATH_TO_META = "src/test/resources/res9131013352758009020.meta";

	/** URI to jar file used in testing. */
	public final String PATH_TO_ARTIFACT_CORRESPONDING_TO_THE_META_FILE = "src/test/resources/res9131013352758009020.jar";

	/** Instance of tested class. */
	private ResourceActionHandler rah = new ResourceActionHandler();

	///** Instance of MetadataIndexingResultService is used during test process. */
	//private MetadataIndexingResultService mirs = new MetadataIndexingResultServiceImpl();

	//--------------------

	/** Implemented interface of LogService used during test process. */
	private LogService testLogService = new LogService() {

		private int printedMessagesLevel = 4;

		@Override
		public void log(final ServiceReference sr, final int level, final String message,
				final Throwable exception) {
		}
		@Override
		public void log(final ServiceReference sr, final int level, final String message) {
		}
		@Override
		public void log(final int level, final String message, final Throwable exception) {
		}

		@Override
		public void log(final int level, final String message) {

			if (printedMessagesLevel < level) {
				return;
			}

			String levelS = null;

			switch (level) {
			case 1:		levelS = "ERROR";
			break;
			case 2:		levelS = "WARNING";
			break;
			case 3:		levelS = "INFO";
			break;
			case 4:		levelS = "DEBUG";
			break;
			}

			System.out.println("crce-efp-indexer testLog: (" + levelS + "): " + message);
		}
	};

	//==============
	
	/**
	 * @return the rah
	 */
	public final ResourceActionHandler getRah() {
		return rah;
	}

	/**
	 * @return the testLogService
	 */
	public final LogService getTestLogService() {
		return testLogService;
	}

	/**
	 * @return the ers
	 */
	/*public final MetadataIndexingResultService getMirs() {
		return mirs;
	}*/
	
}
