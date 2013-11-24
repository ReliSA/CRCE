package cz.zcu.kiv.crce.rest.internal.xml;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiBundle;
import cz.zcu.kiv.crce.rest.internal.Activator;
import cz.zcu.kiv.crce.rest.internal.generated.ObjectFactory;
import cz.zcu.kiv.crce.rest.internal.generated.Trepository;

/**
 * Parent class for all resource classes, that implements REST operation.
 * This class contains common methods, that all offsprings can use.
 *
 * @author Jan Reznicek
 *
 */
public abstract class ResourceParent {

	/**
	 * Id of actual HTTP request
	 */
	private int requestId = 0;

	protected static final String DEF_ENCODING = "UTF-8";

	private static final Logger logger = LoggerFactory.getLogger(ResourceParent.class);

	/**
     * Create XML String from repository.
     * @param repositoryBean repository contains metadata about resources
     * @return XML String with exported metadata
     * @throws WebApplicationException XML export failed
     */
    protected String createXML(Trepository repositoryBean) throws WebApplicationException {
        try {
            ObjectFactory objectFactory = new ObjectFactory();
            JAXBElement<?> repository = objectFactory.createRepository(repositoryBean);
            Class<?> clazz = repository.getValue().getClass();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ClassLoader cl = ObjectFactory.class.getClassLoader();
            JAXBContext jc = JAXBContext.newInstance(clazz.getPackage().getName(), cl);

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(repository, baos);

            return baos.toString("UTF-8");

        } catch (JAXBException | UnsupportedEncodingException e) {
            logger.error("Request ({}) - Exception while creating response.", requestId);
            logger.error(e.getMessage(), e);
            throw new WebApplicationException(e, 500);
        }
    }

	/**
	 * Select from array of resources the one with highest version
	 * @param storeResources array of resources
	 * @return resource with highest version
	 */
    protected Resource resourceWithHighestVersion(List<Resource> storeResources) {
        if (storeResources.isEmpty()) {
            return null;
        }
        Resource resourceWithHighestVersion = storeResources.get(0);

        for (Resource res : storeResources) {
            Version highestVersion = getBundleVersion(resourceWithHighestVersion);
            Version resVersion = getBundleVersion(res);

            if (resVersion != null && highestVersion !=null
                    && highestVersion.compareTo(resVersion) < 0) {
                resourceWithHighestVersion = res;
            }
        }

        logger.debug("Request ({}) - Bundle with highest version is: {}.", requestId, resourceWithHighestVersion.getId());

        return resourceWithHighestVersion;
    }


	/**
	 * Select from array of resources the one with lowest version
	 * @param storeResources array of resources
	 * @return resource with highest version
	 */
    protected Resource resourceWithLowestVersion(List<Resource> storeResources) {
        if (storeResources.isEmpty()) {
            return null;
        }
        Resource resourceWithLowestVersion = storeResources.get(0);

        for (Resource res : storeResources) {
            Version lowestVersion = getBundleVersion(resourceWithLowestVersion);
            Version resVersion = getBundleVersion(res);
            if (lowestVersion != null && resVersion != null && lowestVersion.compareTo(resVersion) > 0) {
                resourceWithLowestVersion = res;
            }
        }

        logger.debug("Request ({}) - Bundle with lowest version is: {}.", requestId, resourceWithLowestVersion.getId());

        return resourceWithLowestVersion;
    }


	/**
	 * Find a single bundle in repository by LDAP filter.
	 * If are more bundle found, return first of them.
	 * If a no bundle was found, throw {@link WebApplicationException} with status 404 - Not found.
	 * If the syntax of the LDAP filter is wrong, throw  {@link WebApplicationException} with status 400 - Bad request.
	 * @param filter LDAP filter
	 * @return founded bundle.
	 * @throws WebApplicationException
	 */
	protected Resource findSingleBundleByFilter(String filter) throws WebApplicationException {
//        try {
            List<Resource> storeResources;
//            storeResources = Activator.instance().getStore().getResources(filter);
            logger.warn("OBR filter is not supported in CRCE 2, all resources will be returned."); // TODO API incompatibility
            storeResources = Activator.instance().getStore().getResources();

            if (storeResources.isEmpty()) {
                logger.debug("Request ({}) - Requested bundle was not found in the repository.", requestId);
                throw new WebApplicationException(404);
            } else {
                return storeResources.get(0);
            }

//        } catch (InvalidSyntaxException e) {
//            logger.debug("Request ({}) - Bad syntax of LDAP filter", requestId);
//            throw new WebApplicationException(400);
//        }
	}

	/**
	 * Find a single bundle in repository by LDAP filter.
	 * If are more bundle found, return the one with the highest version.
	 * If a no bundle was found, throw {@link WebApplicationException} with status 404 - Not found.
	 * If the syntax of the LDAP filter is wrong, throw  {@link WebApplicationException} with status 400 - Bad request.
	 * @param filter LDAP filter
	 * @return founded bundle.
	 * @throws WebApplicationException
	 */
    protected Resource findSingleBundleByFilterWithHighestVersion(String filter) throws WebApplicationException {
//        try {
            List<Resource> storeResources;
//            storeResources = Activator.instance().getStore().getResources(filter);
            logger.warn("OBR filter is not supported in CRCE 2, all resources will be returned."); // TODO API incompatibility
            storeResources = Activator.instance().getStore().getResources();

            if (storeResources.isEmpty()) {
                logger.debug("Request ({}) - Requested bundle was not found in the repository.", requestId);
                throw new WebApplicationException(404);
            }

            Resource resource;

            if (storeResources.size() > 1) {
                logger.debug("Request ({}) - More bundles was found, the one with highest version will be selected.", requestId);
                resource = resourceWithHighestVersion(storeResources);
            } else {
                logger.debug("Request ({}) - The requested bundle was found.", requestId);
                resource = storeResources.get(0);
            }

            return resource;

//        } catch (InvalidSyntaxException e) {
//            logger.debug("Request ({}) - Bad syntax of LDAP filter", requestId);
//            throw new WebApplicationException(400);
//        }
    }

	/**
	 * Find a single bundle in repository by LDAP filter.
	 * If are more bundle found, return the one with the lowest version.
	 * If a no bundle was found, throw {@link WebApplicationException} with status 404 - Not found.
	 * If the syntax of the LDAP filter is wrong, throw  {@link WebApplicationException} with status 400 - Bad request.
	 * @param filter LDAP filter
	 * @return founded bundle.
	 * @throws WebApplicationException
	 */
    protected Resource findSingleBundleByFilterWithLowestVersion(String filter) throws WebApplicationException {
//        try {
            List<Resource> storeResources;
//			storeResources = Activator.instance().getStore().getResources(filter);
            logger.warn("OBR filter is not supported in CRCE 2, all resources will be returned."); // TODO API incompatibility
            storeResources = Activator.instance().getStore().getResources();

            if (storeResources.isEmpty()) {
                logger.debug("Request ({}) - Requested bundle was not found in the repository.", requestId);
                throw new WebApplicationException(404);
            }

            Resource resource;

            if (storeResources.size() > 1) {
                logger.debug("Request ({}) - More bundles was found, the one with highest version will be selected.", requestId);
                resource = resourceWithLowestVersion(storeResources);
            } else {
                logger.debug("Request ({}) - The requested bundle was found.", requestId);
                resource = storeResources.get(0);
            }

            return resource;

//        } catch (InvalidSyntaxException e) {
//            logger.debug("Request ({}) - Bad syntax of LDAP filter", requestId);
//            throw new WebApplicationException(400);
//        }
    }


	/**
	 * Find bundles by filter
	 * If the syntax of the LDAP filter is wrong, throw  {@link WebApplicationException} with status 400 - Bad request.
	 * @param filter LDAP filter
	 * @return founded bundles.
	 * @throws WebApplicationException
	 */
	protected List<Resource> findBundlesByFilter(String filter) throws WebApplicationException {
//		try {
			List<Resource> storeResources;
//			storeResources = Activator.instance().getStore().getResources(filter);

            logger.warn("OBR filter is not supported in CRCE 2, all resources will be returned."); // TODO API incompatibility
			storeResources = Activator.instance().getStore().getResources();

			return storeResources;

//		} catch (InvalidSyntaxException e) {
//			log.debug("Request ({}) - Bad syntax of LDAP filter", requestId);
//			throw new WebApplicationException(400);
//		}
	}

	/**
	 * Indicate that new request came to server.
	 * Id of new request is determined.
	 */
	protected void newRequest() {
		requestId++;
	}

	/**
	 * Get id of actual request
	 * @return id of actual request.
	 */
	protected int getRequestId() {
		return requestId;
	}

    // TODO the following methods are candidates for a common OSGi service

    protected Version getBundleVersion(Resource resource) {
        if (resource != null) {
            List<Capability> resCapabilities = resource.getCapabilities(NsOsgiBundle.NAMESPACE__OSGI_BUNDLE);
            if (!resCapabilities.isEmpty()) {
                return resCapabilities.get(0).getAttributeValue(NsOsgiBundle.ATTRIBUTE__VERSION);
            }
        }
        return null;
    }

    protected String getBundleSymbolicName(Resource resource) {
        if (resource != null) {
            List<Capability> resCapabilities = resource.getCapabilities(NsOsgiBundle.NAMESPACE__OSGI_BUNDLE);
            if (!resCapabilities.isEmpty()) {
                return resCapabilities.get(0).getAttributeValue(NsOsgiBundle.ATTRIBUTE__SYMBOLIC_NAME);
            }
        }
        return null;
    }
}
