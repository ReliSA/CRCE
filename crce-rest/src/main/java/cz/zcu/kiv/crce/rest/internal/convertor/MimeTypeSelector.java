package cz.zcu.kiv.crce.rest.internal.convertor;

import java.util.List;
import javax.ws.rs.core.MediaType;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 * Determine MIME type of a resource
 * @author Jan Reznicek
 *
 */
@Component(provides = MimeTypeSelector.class)
public class MimeTypeSelector {

	private static final String OSGI_CATEGORY = "osgi";

    @ServiceDependency private volatile MetadataService metadataService;

	/**
	 * OSGI bundle MIME type
	 */
	public static final String APPLICATION_OSGI_BUNDLE = "application/vnd.osgi.bundle";


	/**
	 * Determine MIME type of the resource.
	 * @param resource resource, whose MIME type is returned
	 * @return MIME type of the resource
	 */
    public String selectMimeType(Resource resource) {
        List<String> categories = metadataService.getCategories(resource);
        if (categories.contains(OSGI_CATEGORY)) {
            return APPLICATION_OSGI_BUNDLE;
        } else {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

}
