package cz.zcu.kiv.crce.efp.indexer.test.support;

import java.util.Dictionary;

import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.log.LogService;

import cz.zcu.kiv.crce.efp.indexer.EfpIndexerResultService;
import cz.zcu.kiv.crce.efp.indexer.internal.ResourceActionHandler;

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
	
	//--------------------
	
	/** Implemented interface of LogService used during test process. */
	private LogService testLogService = new LogService() {

		private int printedMessagesLevel = 3;
		private int predchoziLevel=0;

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

			if (level == 5) {
				if (predchoziLevel == 4 || predchoziLevel == 1) {
					System.out.print(message);
				}
				return;
			} else if (printedMessagesLevel < level) {
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

			System.out.println("testLog: (" + levelS + "): " + message);

			predchoziLevel=level;
		}
	};

	private EfpIndexerResultService ers = new EfpIndexerResultService() {

		@Override
		public void updated(Dictionary properties) throws ConfigurationException {
			// TODO Auto-generated method stub
		}

		@Override
		public void setMessage(String message) {
			// TODO Auto-generated method stub
		}

		@Override
		public String getMessage() {
			// TODO Auto-generated method stub
			return null;
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
	public final EfpIndexerResultService getErs() {
		return ers;
	}
	
}
