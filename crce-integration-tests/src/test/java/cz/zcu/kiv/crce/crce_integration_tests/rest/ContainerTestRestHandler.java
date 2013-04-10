package cz.zcu.kiv.crce.crce_integration_tests.rest;


import javax.ws.rs.core.Response;

import junit.framework.TestCase;
import cz.zcu.kiv.crce.crce_integration_tests.rest.support.DataContainerForTestingPurpose;
import cz.zcu.kiv.crce.rest.internal.rest.xml.MetadataResource;

/**
 * Testing class for ResourceActionHandler class.
 */
public class ContainerTestRestHandler extends TestCase {

	/** Data container is used for storing paths to testing artifacts and some instances which are used during testing process.*/
	private DataContainerForTestingPurpose dctp = new DataContainerForTestingPurpose();
	

	/**
	 * Constructor.
	 */
	public ContainerTestRestHandler() {
	}

	
	public final void testGetMetadatat() {
		MetadataResource metadataResource = new MetadataResource();
		
		Response response = metadataResource.getMetadata(null, null, null, null, null, null);
		
		System.out.println(response.getClass().getCanonicalName());
		String xml = (String) response.getEntity();
		System.out.println(xml);
		
	}


}