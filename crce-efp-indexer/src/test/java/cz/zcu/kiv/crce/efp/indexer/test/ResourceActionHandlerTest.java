package cz.zcu.kiv.crce.efp.indexer.test;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import cz.zcu.kiv.crce.efp.indexer.test.support.DataContainerForTestingPurpose;
import cz.zcu.kiv.crce.efp.indexer.test.support.DataModelHelperExtImpl;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.Resource;

import org.osgi.service.log.LogService;

import junit.framework.*;

import cz.zcu.kiv.crce.metadata.internal.ResourceCreatorImpl;
import cz.zcu.kiv.crce.metadata.metafile.DataModelHelperExt;
import cz.zcu.kiv.crce.metadata.metafile.internal.MetafileResourceDAO;

/**
 * Testing class for ResourceActionHandler class.
 */
public class ResourceActionHandlerTest extends TestCase {

	private DataContainerForTestingPurpose dctp = new DataContainerForTestingPurpose();

	//private volatile MetafileResourceDAO metaResDao; // Will be used with container.
	
	/**
	 * Initial method for testing the handleNewResource(Resource resource) method.
	 */
		public void testHandleNewResource() {

		dctp.getRah().setmLog(dctp.getTestLogService());
		dctp.getRah().setmEfpIndexer(dctp.getEirs());

		File fil = new File(dctp.PATH_TO_ARTIFACT_CORRESPONDING_TO_THE_META_FILE);
		String uriText = "file:" + fil.getAbsolutePath();

		Resource res4Test = new ResourceCreatorImpl().createResource();
		try {
			res4Test.setUri(new URI(uriText));
		} catch (URISyntaxException e) {
			dctp.getTestLogService().log(LogService.LOG_ERROR, "URISyntaxException during processing URI path of input resource.");
		}
		
		dctp.getRah().handleNewResource(res4Test);
		// This is tested method.

		File filMeta = new File(dctp.PATH_TO_META);
		String uriTextMeta = "file:" + filMeta.getAbsolutePath();
		Resource resFromMeta = getResourceFromMetaUri(uriTextMeta);
		// Resource which is created by methods of crce-metadata-metafile module.

		displayResourceObrMetadata(resFromMeta);
		displayResourceObrMetadata(res4Test);

		compareRequirements(resFromMeta, res4Test);
		compareCapabilities(resFromMeta, res4Test);
	}
	 
	//========================================================================
	//		Auxiliary, untested methods called from testHandleNewResource():
	//========================================================================

	/**
	 * Methods handles with exceptions which can occur during calling getResource() method.
	 * @param uriText - String of URI address to META file.
	 * @return Resource which is created from META file.
	 */
	public final Resource getResourceFromMetaUri(final String uriText) {

		Resource res = null;
		try {
			res = getResource(new URI(uriText));
		} catch (IOException e) {
			dctp.getTestLogService().log(LogService.LOG_ERROR, "IOException during processing URI path of input resource.");
		} catch (URISyntaxException e) {
			dctp.getTestLogService().log(LogService.LOG_ERROR, "URISyntaxException during processing URI path of input resource.");
		}

		return res;
	}

	/**
	 * Method ensures comparing Requirement metadata of two given resources.
	 * @param resFromMeta - Resource which was created from META file by crce-metadata-metafile module.
	 * @param res4Test - Resource which was created by methods of crce-efp-indexer module.
	 */
	private void compareRequirements(final Resource resFromMeta, final Resource res4Test) {
		dctp.getTestLogService().log(LogService.LOG_DEBUG, "\n\ncompareRequirements:");
		Requirement [] reqyMeta = resFromMeta.getRequirements();
		Requirement [] reqy4Test = res4Test.getRequirements();

		// Resources in testing process should have positive number of Requirements.
		assertTrue(reqyMeta.length > 0);
		assertTrue(reqy4Test.length > 0);
		
		int i = 0;
		for (Requirement req : reqy4Test) {
			i++;

			int i2 = 0;
			boolean match = false;
			for (Requirement req2 : reqyMeta) {
				i2++;
				if (req.getFilter().equals(req2.getFilter()) == true) {
					dctp.getTestLogService().log(LogService.LOG_DEBUG, "agree: " + i + " a " + i2);
					dctp.getTestLogService().log(LogService.LOG_DEBUG, req.getFilter());
					dctp.getTestLogService().log(LogService.LOG_DEBUG, req2.getFilter());
					dctp.getTestLogService().log(LogService.LOG_DEBUG, "");
					match = true;
					break;
				}
				//else
				//System.out.println("disagree");
			}
			if(match == false){
				dctp.getTestLogService().log(LogService.LOG_ERROR, "Following Requirement has no match: "+req.getFilter());
			}
			assertEquals(true, match);
		}
	}

	/**
	 * Method ensures comparing Capability metadata of two given resources.
	 * @param resFromMeta - Resource which was created from META file by crce-metadata-metafile module.
	 * @param res4Test - Resource which was created by methods of crce-efp-indexer module.
	 */
	private void compareCapabilities(final Resource resFromMeta, final Resource res4Test) {
		dctp.getTestLogService().log(LogService.LOG_DEBUG, "\n\ncompareCapabilities:");
		Capability [] capyMeta = resFromMeta.getCapabilities();
		Capability [] capy4Test = res4Test.getCapabilities();

		// Resources in testing process should have positive number of Capabilities.
		assertTrue(capyMeta.length > 0);
		assertTrue(capy4Test.length > 0);
		
		int i = 0;
		for (Capability cap : capy4Test) {
			i++;

			int i2 = 0;
			boolean match = false;
			for (Capability cap2 : capyMeta) {
				i2++;
				if (compareCapaProperties(cap2, cap) == true){
					dctp.getTestLogService().log(LogService.LOG_DEBUG, "agree: " + i + " a " + i2);
					displayCapDataAt1Line(cap);
					dctp.getTestLogService().log(LogService.LOG_DEBUG, "\n");
					match = true;
					break;
				}
			}
			if(match == false){
				dctp.getTestLogService().log(LogService.LOG_ERROR, "Following Capability has no match: "+cap.getPropertyString("efp-name"));
				displayCapDataAt1Line(cap);
			}
			assertEquals(true, match);
		}

	}

	/**
	 * Every Capability can have many Properties. 
	 * This method is used for comparing Capability Properties of two given Capabilities.
	 * @param capFromMeta - Capability of Resource which was created from META file.
	 * @param cap4Test - Capability of Resource which was created by crce-efp-indexer module.
	 * @return Result whether two Capabilities are same (true) or not (false).
	 */
	private boolean compareCapaProperties(final Capability capFromMeta, final Capability cap4Test) {

		Property [] propyMeta = capFromMeta.getProperties();
		Property [] propy4Test = cap4Test.getProperties();

		ArrayList<Property> propListM = new ArrayList<Property>();
		for (Property propM : propyMeta){
			propListM.add(propM);
		}

		// SOME DEBUG MESSAGES OF THIS METHOD IS COMMENTED OUT 
		// BECAUSE THEY ARE TOO MUCH LOW LEVEL DEBUG MESSAGES 
		// AND IT IS NOT IMPORTANT TO SHOW THEM IN USUAL DEBUG CASES.
		//int i = 0;
		boolean totalMatch = true;
		for (Property propT : propy4Test) {
			//i++;

			//int i2 = 0;
			boolean match = false;
			for (Property propM : propListM) {
				//i2++;
				if (propT.getName().equals(propM.getName()) == true) {
					if (propT.getValue().equals(propM.getValue()) == true) {
						//System.out.println(propT.getName()+ "\t\t" + propT.getType() + "\t\t" +propT.getValue());
						propListM.remove(propM);
						//System.out.println("\nagree: " + i + " a " + i2);
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
		//System.out.println("disagree");

		return totalMatch;
	}

	/**
	 * Method displays Capability data and Properties at one line.
	 * Method is used in debug process. 
	 * @param cap - Given Capability for displaying its Properties.
	 */
	private void displayCapDataAt1Line(final Capability cap) {
		dctp.getTestLogService().log(LogService.LOG_DEBUG, "CAP=" + cap.getName());
		Property [] propy = cap.getProperties();
		for (Property prop : propy) {
			dctp.getTestLogService().log(LogService.LOG_DEBUG, "\t" + prop.getName() + "=" + prop.getType() + "=" + prop.getValue());
		}
	}

	/**
	 * Method display Capabilities and Requirements metadata of given Resource.
	 * Method is used in debug process. 
	 * @param res - given Resource.
	 */
	public void displayResourceObrMetadata(Resource res){

		dctp.getTestLogService().log(LogService.LOG_DEBUG, "\nvysypData:");

		Capability [] capy = res.getCapabilities();
		for (Capability cap : capy) {

			dctp.getTestLogService().log(LogService.LOG_DEBUG, "===== Cap: " + cap.getName());

			Property [] propy = cap.getProperties();
			for (Property prop : propy) {
				dctp.getTestLogService().log(LogService.LOG_DEBUG, prop.getName() + " "
						+ prop.getType() + " " + prop.getValue());
			}
		}

		Requirement[] reqy = res.getRequirements();
		for (Requirement cap : reqy) {
			dctp.getTestLogService().log(LogService.LOG_DEBUG, "===== REQ: " + cap.getName());
			dctp.getTestLogService().log(LogService.LOG_DEBUG, cap.getFilter());
		}

		dctp.getTestLogService().log(LogService.LOG_DEBUG, capy.length + "");
		dctp.getTestLogService().log(LogService.LOG_DEBUG, reqy.length + "");
	}

	
	/*
	 * Code from here to down is reused from MetafileResourceDAO class.
	 * This code is used in this class only for temporary time 
	 * till testing container will be working properly.
	 * 
	 * This implementation of ResourceDAO reads/writes metadata from/to a file,
	 * whose name (URI path) is created by resource's URI path and '.meta' extension.
	 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
	 * 
	 */
	
	/**
	 * Methods creates resource instance from given META file.
	 * @param uri - URI address to META file.
	 * @return Resource which is created from META file.
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
			dctp.getTestLogService().log(LogService.LOG_ERROR, "Can not parse XML data (probably corrupted content): " + e.getMessage());
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

}