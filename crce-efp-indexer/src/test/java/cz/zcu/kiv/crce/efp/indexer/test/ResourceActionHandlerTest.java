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

	//private volatile MetafileResourceDAO mao;
	
	
	/**
	 * Test of the handleNewResource(Resource resource) method.
	 */
		public void testHandleNewResource() {

		dctp.getRah().setmLog(dctp.getTestLogService());
		dctp.getRah().setmEfpIndexer(dctp.getErs());

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
		// Resource which was created by methods of crce-metadata-metafile module.

		vysypData(resFromMeta);
		vysypData(res4Test);

		compareRequirements(resFromMeta, res4Test);
		compareCapabilities(resFromMeta, res4Test);
	}
	 
	//==================================================
	//		Auxiliary, untested methods:
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
			dctp.getTestLogService().log(LogService.LOG_ERROR, "IOException during processing URI path of input resource.");
		} catch (URISyntaxException e) {
			dctp.getTestLogService().log(LogService.LOG_ERROR, "URISyntaxException during processing URI path of input resource.");
		}

		return res;
	}

	/**
	 * 
	 * @param resFromMeta
	 * @param res4Test
	 */
	private void compareRequirements(final Resource resFromMeta, final Resource res4Test) {
		dctp.getTestLogService().log(LogService.LOG_DEBUG, "\ncompareRequirements:");
		Requirement [] reqyMeta = resFromMeta.getRequirements();
		Requirement [] reqy4Test = res4Test.getRequirements();

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
					dctp.getTestLogService().log(LogService.LOG_DEBUG, "shoda: " + i + " a " + i2);
					dctp.getTestLogService().log(LogService.LOG_DEBUG, req.getFilter());
					dctp.getTestLogService().log(LogService.LOG_DEBUG, req2.getFilter());
					dctp.getTestLogService().log(LogService.LOG_DEBUG, "");
					match = true;
					break;
				}
				//else
				//System.out.println("neshoda");

			}
			if(match == false){
				dctp.getTestLogService().log(LogService.LOG_ERROR, "Following Requirement has no match: "+req.getFilter());
			}
			assertEquals(true, match);
		}

	}

	/**
	 * 
	 * @param resFromMeta
	 * @param res4Test
	 */
	private void compareCapabilities(final Resource resFromMeta, final Resource res4Test) {
		dctp.getTestLogService().log(LogService.LOG_DEBUG, "\ncompareCapabilities:");
		Capability [] capyMeta = resFromMeta.getCapabilities();
		Capability [] capy4Test = res4Test.getCapabilities();

		assertTrue(capyMeta.length > 0);
		assertTrue(capy4Test.length > 0);
		
		int i = 0;
		for (Capability cap : capy4Test) {
			i++;

			int i2 = 0;
			boolean match = false;
			for (Capability cap2 : capyMeta) {
				i2++;
				if (compareCapa(cap2, cap) == true){
					dctp.getTestLogService().log(LogService.LOG_DEBUG, "shoda: " + i + " a " + i2);
					tiskniCapu(cap);
					dctp.getTestLogService().log(LogService.LOG_DEBUG, "\n");
					match = true;
					break;
				}
			}
			if(match == false){
				dctp.getTestLogService().log(LogService.LOG_ERROR, "Following Capability has no match: "+cap.getPropertyString("efp-name"));
				tiskniCapu(cap);
			}
			assertEquals(true, match);
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
		dctp.getTestLogService().log(5, "CAP=" + cap.getName());
		Property [] propy = cap.getProperties();
		for (Property prop : propy) {
			dctp.getTestLogService().log(5, "\t" + prop.getName() + "=" + prop.getType() + "=" + prop.getValue());
		}

	}

	/**
	 * 
	 * @param res
	 */
	public void vysypData(Resource res){

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