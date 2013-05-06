package cz.zcu.kiv.crce.rest.internal.rest.convertor;

import javax.ws.rs.core.MediaType;

import cz.zcu.kiv.crce.metadata.Resource;

/**
 * Determine MIME type of a resource
 * @author Jan Reznicek
 *
 */
public class MimeTypeSelector {
	
	private static final String OSGI_CATEGORY = "osgi";
	
	/**
	 * OSGI bundle MIME type
	 */
	public static final String APPLICATION_OSGI_BUNLDE = "application/vnd.osgi.bundle";
	

	/**
	 * Determine MIME type of the resource.
	 * @param resource resource, whose MIME type is returned
	 * @return MIME type of the resource
	 */
	public String selectMimeType(Resource resource) {
		if(resource.hasCategory(OSGI_CATEGORY)) return APPLICATION_OSGI_BUNLDE;
		else return MediaType.APPLICATION_OCTET_STREAM;
	}

}
