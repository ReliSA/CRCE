package cz.zcu.kiv.crce.efp.indexer.test;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import cz.zcu.kiv.crce.efp.indexer.internal.ResourceActionHandler;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.Resource;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

//import junit.framework.*;

import cz.zcu.kiv.crce.metadata.internal.ResourceCreatorImpl;
import cz.zcu.kiv.crce.metadata.metafile.DataModelHelperExt;

/**
 * Testing class for ResourceActionHandler class.
 */
//public class ResourceActionHandlerTest extends TestCase {
	public class ResourceActionHandlerTest {

	/** Path to jar file used in testing. */
	private static final String PATH_TO_OSGI_WITHOUT_EFP = "./src/test/resources/OSGi_without_EFP.jar";
	/** Path to jar file used in testing. */
	private static final String PATH_TO_OSGI_WITH_EFP = "./src/test/resources/OSGi_with_EFP.jar";
	/** Path to jar file used in testing. */
	private static final String PATH_TO_NON_OSGI = "./src/test/resources/non-OSGi.jar";
	/** Path to jar file used in testing. */
	private static final String PATH_TO_NON_JAR = "./src/test/resources/non-jar.txt";
	/** URI to meta file used in testing. */
	private static final String URI_TO_META = "file:/media/prostor_/CRCE2/crce-trunk-12-02-17/crce-efp-indexer/src/test/resources/res7212331167601929015.meta";

	/** Instance of tested class. */
	private ResourceActionHandler rah = new ResourceActionHandler();
	
	boolean testFailure = false;

	/** Test of the jarFileArtefact(String artefactName) method.	*/
	public final void testjarFileArtefact() {
		if (testFailure) {
			return;
		}
		
		rah.setmLog(testLogService);
		assert rah.jarFileArtefact(PATH_TO_OSGI_WITH_EFP) == true;
		assert rah.jarFileArtefact(PATH_TO_NON_JAR) == false;
	}

	/** Test of the indexerInitialization(Resource resource) method.	*/
	public final void testIndexerInitialization() {
		if (testFailure) {
			return;
		}
		
		rah.setmLog(testLogService);

		try {

			Resource myRes = new ResourceImplForTestPurpose();
			myRes.setUri(new URI(PATH_TO_OSGI_WITHOUT_EFP));
			boolean result = rah.indexerInitialization(myRes); 
			if(!result){
				testLogService.log(LogService.LOG_ERROR,"TEST FAILURE: testIndexerInitialization() -> indexerInitialization() s PATH_TO_OSGI_WITHOUT_EFP");
				//System.exit(0);
				testFailure = true;
			}
			assert result == true;


			Resource myRes2 = new ResourceImplForTestPurpose();
			myRes2.setUri(new URI(PATH_TO_OSGI_WITH_EFP));
			result = rah.indexerInitialization(myRes2);
			if(!result){
				testLogService.log(LogService.LOG_ERROR,"TEST FAILURE: testIndexerInitialization() -> indexerInitialization() s PATH_TO_OSGI_WITH_EFP");
				//System.exit(0);
				testFailure = true;
			}
			assert result == true;

		} catch (URISyntaxException e) {
			testLogService.log(LogService.LOG_ERROR, "URISyntaxException during processing URI path of input resource.");
		}

	}

	/**
	 * Test of the handleNewResource(Resource resource, String artefactName) method.
	 */
	public void testHandleNewResource() {
		if (testFailure) {
			return;
		}
		
		rah.setmLog(testLogService);

		File fil = new File(PATH_TO_OSGI_WITH_EFP);
		String uriText = "file:" + fil.getAbsolutePath();

		Resource resource = new ResourceCreatorImpl().createResource();
		try {
			resource.setUri(new URI(uriText));
		} catch (URISyntaxException e) {
			testLogService.log(LogService.LOG_ERROR, "URISyntaxException during processing URI path of input resource.");
		}

		Resource res4Test = rah.handleNewResource(resource, resource.getUri().getPath());
		// This is tested method.

		Resource resFromMeta = getResourceFromMetaUri(URI_TO_META);
		// Resource which was created by methods of crce-metadata-metafile module.

		vysypData(resFromMeta);
		vysypData(res4Test);

		compareRequirements(resFromMeta, res4Test);
		compareCapabilities(resFromMeta, res4Test);
	}

	//==================================================
	//		POMOCNE, NETESTOVANE METODY:
	//==================================================

	/**
	 * 
	 * @param uriText
	 * @return
	 */
	public final Resource getResourceFromMetaUri(final String uriText) {

		Resource res = null;
		try {
			res = getResource(new URI(uriText));
		} catch (IOException e) {
			testLogService.log(LogService.LOG_ERROR, "IOException during processing URI path of input resource.");
		} catch (URISyntaxException e) {
			testLogService.log(LogService.LOG_ERROR, "URISyntaxException during processing URI path of input resource.");
		}

		return res;
	}

	/**
	 * 
	 * @param resFromMeta
	 * @param res4Test
	 */
	private void compareRequirements(final Resource resFromMeta, final Resource res4Test) {
		testLogService.log(LogService.LOG_DEBUG, "\ncompareRequirements:");
		Requirement [] reqyMeta = resFromMeta.getRequirements();
		Requirement [] reqy4Test = res4Test.getRequirements();

		int i = 0;
		for (Requirement req : reqy4Test) {
			i++;

			int i2 = 0;
			boolean match = false;
			for (Requirement req2 : reqyMeta) {
				i2++;
				if (req.getFilter().equals(req2.getFilter()) == true) {
					testLogService.log(LogService.LOG_DEBUG, "shoda: " + i + " a " + i2);
					testLogService.log(LogService.LOG_DEBUG, req.getFilter());
					testLogService.log(LogService.LOG_DEBUG, req2.getFilter());
					testLogService.log(LogService.LOG_DEBUG, "");
					match = true;
					break;
				}
				//else
				//System.out.println("neshoda");

			}
			if(match == false){
				testLogService.log(LogService.LOG_ERROR, "Following Requirement has no match: "+req.getFilter());
			}
			assert match == true;
		}

	}

	/**
	 * 
	 * @param resFromMeta
	 * @param res4Test
	 */
	private void compareCapabilities(final Resource resFromMeta, final Resource res4Test) {
		testLogService.log(LogService.LOG_DEBUG, "\ncompareCapabilities:");
		Capability [] capyMeta = resFromMeta.getCapabilities();
		Capability [] capy4Test = res4Test.getCapabilities();

		int i = 0;
		for (Capability cap : capy4Test) {
			i++;

			int i2 = 0;
			boolean match = false;
			for (Capability cap2 : capyMeta) {
				i2++;
				if (compareCapa(cap2, cap) == true){
					testLogService.log(LogService.LOG_DEBUG, "shoda: " + i + " a " + i2);
					tiskniCapu(cap);
					testLogService.log(LogService.LOG_DEBUG, "\n");
					match = true;
					break;
				}
			}
			if(match == false){
				testLogService.log(LogService.LOG_ERROR, "Following Capability has no match: "+cap.getPropertyString("efp-name"));
				tiskniCapu(cap);
			}
			assert match == true;
		}

	}

	/**
	 * 
	 * @param capFromMeta
	 * @param cap4Test
	 * @return
	 */
	private boolean compareCapa(final Capability capFromMeta, final Capability cap4Test) {

		Property [] propyMeta = capFromMeta.getProperties();
		Property [] propy4Test = cap4Test.getProperties();

		ArrayList<Property> propListM = new ArrayList<Property>();
		for (Property propM : propyMeta){
			propListM.add(propM);
		}


		int i = 0;
		boolean totalMatch = true;
		for (Property propT : propy4Test) {
			i++;

			int i2 = 0;
			boolean match = false;
			for (Property propM : propListM) {
				i2++;
				if (propT.getName().equals(propM.getName()) == true) {
					if (propT.getValue().equals(propM.getValue()) == true) {
						//System.out.println(propT.getName()+ "\t\t" + propT.getType() + "\t\t" +propT.getValue());
						propListM.remove(propM);
						//System.out.println("\nshoda: " + i + " a " + i2);
						match = true;
						break;
					}
				}
			}
			if (match == false) {
				totalMatch = false;
			}
		}
		//if(!totalMatch)
		//System.out.println("neshoda");

		return totalMatch;
	}

	/**
	 * 
	 * @param cap
	 */
	private void tiskniCapu(final Capability cap) {
		testLogService.log(5, "CAP=" + cap.getName());
		Property [] propy = cap.getProperties();
		for (Property prop : propy) {
			testLogService.log(5, "\t" + prop.getName() + "=" + prop.getType() + "=" + prop.getValue());
		}

	}

	/**
	 * 
	 * @param res
	 */
	public void vysypData(Resource res){

		testLogService.log(LogService.LOG_DEBUG, "\nvysypData:");

		Capability [] capy = res.getCapabilities();
		for (Capability cap : capy) {

			testLogService.log(LogService.LOG_DEBUG, "===== Cap: " + cap.getName());

			Property [] propy = cap.getProperties();
			for (Property prop : propy) {
				testLogService.log(LogService.LOG_DEBUG, prop.getName() + " "
						+ prop.getType() + " " + prop.getValue());
			}
		}

		Requirement[] reqy = res.getRequirements();
		for (Requirement cap : reqy) {
			testLogService.log(LogService.LOG_DEBUG, "===== REQ: " + cap.getName());
			testLogService.log(LogService.LOG_DEBUG, cap.getFilter());
		}

		testLogService.log(LogService.LOG_DEBUG, capy.length + "");
		testLogService.log(LogService.LOG_DEBUG, reqy.length + "");
	}


	/**
	 * 
	 * @param uri
	 * @return
	 * @throws IOException
	 */
	public Resource getResource(URI uri) throws IOException {
		ResourceCreator resourceCreator = new ResourceCreatorImpl();
		DataModelHelperExt dataModelHelper = new DataModelHelperExtImpl();

		URI metadataUri = getMetafileUri(uri);

		InputStreamReader reader;
		try {
			reader = new InputStreamReader(metadataUri.toURL().openStream());
		} catch (MalformedURLException e) {
			throw new IOException("Malformed URL in URI: " + metadataUri.toString(), e);
		} catch (FileNotFoundException e) {
			Resource resource = resourceCreator.createResource();
			resource.setUri(uri);
			return resource;
		}

		try {
			return dataModelHelper.readMetadata(reader);
		} catch (IOException e) {
			throw new IOException("Can not read XML data", e);
		} catch (Exception e) {
			testLogService.log(LogService.LOG_ERROR, "Can not parse XML data (probably corrupted content): " + e.getMessage());
			return resourceCreator.createResource();
		}
	}

	/** METAFILE_EXTENSION */
	public static final String METAFILE_EXTENSION = ".meta";

	/**
	 * 
	 * @param uri
	 * @return
	 */
	private URI getMetafileUri(URI uri) {
		URI metadataUri = uri;

		if (!uri.toString().toLowerCase().endsWith(METAFILE_EXTENSION)) {
			try {
				metadataUri = new URI(uri.toString() + METAFILE_EXTENSION);
			} catch (URISyntaxException ex) {
				throw new IllegalArgumentException("Invalid URI syntax: " + uri.toString() + METAFILE_EXTENSION, ex);
			}
		}

		return metadataUri;
	}

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

}