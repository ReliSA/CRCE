package cz.zcu.kiv.crce.rest.internal.xml;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiBundle;
import cz.zcu.kiv.crce.metadata.type.Version;
import cz.zcu.kiv.crce.rest.internal.Activator;
import cz.zcu.kiv.crce.rest.internal.jaxb.metadata.ObjectFactory;
import cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Repository;

/**
 * Parent class for all resource classes, that implements REST operation.
 * This class contains common methods, that all offsprings can use.
 *
 * @author Jan Reznicek
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
     *
     * @param repository repository contains metadata about resources
     * @return XML String with exported metadata
     * @throws WebApplicationException XML export failed
     */
    protected String createXML(Repository repository) throws WebApplicationException {
        try {
//            ObjectFactory objectFactory = new ObjectFactory();
//            JAXBElement<?> repository = objectFactory.createRepository(repositoryBean);
//            Class<?> clazz = repository.getValue().getClass();
            Class<?> clazz = repository.getClass();

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
     *
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

            if (resVersion != null && highestVersion != null
                    && highestVersion.compareTo(resVersion) < 0) {
                resourceWithHighestVersion = res;
            }
        }

        logger.debug("Request ({}) - Bundle with highest version is: {}.", requestId, resourceWithHighestVersion.getId());

        return resourceWithHighestVersion;
    }


    /**
     * Select from array of resources the one with lowest version
     *
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
     *
     * @param requirement Resource requirement.
     * @return founded bundle.
     * @throws WebApplicationException
     */
    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    protected Resource findSingleBundleByFilter(Requirement requirement) throws WebApplicationException {
        try {
            List<Resource> storeResources = Activator.instance().getStore().getResources(requirement);

            if (storeResources.isEmpty()) {
                logger.debug("Request ({}) - Requested bundle was not found in the repository.", requestId);
                throw new WebApplicationException(404);
            } else {
                return storeResources.get(0);
            }

        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Request ({}) - Could not get resources from store.", requestId, e);
            throw new WebApplicationException(500);
        }
    }

    /**
     * Find a single bundle in repository by LDAP filter.
     * If are more bundle found, return the one with the highest version.
     * If a no bundle was found, throw {@link WebApplicationException} with status 404 - Not found.
     * If the syntax of the LDAP filter is wrong, throw  {@link WebApplicationException} with status 400 - Bad request.
     *
     * @param requirement Resource requirement.
     * @return Found bundle.
     * @throws WebApplicationException
     */
    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    protected Resource findSingleBundleByFilterWithHighestVersion(Requirement requirement) throws WebApplicationException {
        try {
            List<Resource> storeResources = Activator.instance().getStore().getResources(requirement);

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
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Request ({}) - Could not get resources from store.", requestId, e);
            throw new WebApplicationException(500);
        }
    }

    /**
     * Find a single bundle in repository by LDAP filter.
     * If are more bundle found, return the one with the lowest version.
     * If a no bundle was found, throw {@link WebApplicationException} with status 404 - Not found.
     * If the syntax of the LDAP filter is wrong, throw  {@link WebApplicationException} with status 400 - Bad request.
     *
     * @param requirement Resource requirement.
     * @return founded bundle.
     * @throws WebApplicationException
     */
    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    protected Resource findSingleBundleByFilterWithLowestVersion(Requirement requirement) throws WebApplicationException {
        try {
            List<Resource> storeResources = Activator.instance().getStore().getResources(requirement);

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
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Request ({}) - Could not get resources from store.", requestId, e);
            throw new WebApplicationException(500);
        }
    }


    /**
     * Find bundles by filter
     * If the syntax of the LDAP filter is wrong, throw  {@link WebApplicationException} with status 400 - Bad request.
     *
     * @param requirement Resource requirement.
     * @return founded bundles.
     * @throws WebApplicationException
     */
    protected List<Resource> findBundlesByFilter(Requirement requirement) throws WebApplicationException {
        try {
            List<Resource> storeResources = Activator.instance().getStore().getResources(requirement);

            // TODO check list size?

            return storeResources;
        } catch (Exception e) {
            logger.error("Request ({}) - Could not get resources from store.", requestId, e);
            throw new WebApplicationException(500);
        }
    }

    protected List<Resource> findAllBundles() throws WebApplicationException {
        return Activator.instance().getStore().getResources();
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
     *
     * @return id of actual request.
     */
    protected int getRequestId() {
        return requestId;
    }

    // TODO the following methods are candidates for a common OSGi service

    protected Version getBundleVersion(Resource resource) {
        if (resource != null) {
            return Activator.instance().getMetadataService()
                    .getSingletonCapability(resource, NsOsgiBundle.NAMESPACE__OSGI_BUNDLE)
                    .getAttributeValue(NsOsgiBundle.ATTRIBUTE__VERSION);
        }
        return null;
    }

    protected String getBundleSymbolicName(Resource resource) {
        if (resource != null) {
            return Activator.instance().getMetadataService()
                    .getSingletonCapability(resource, NsOsgiBundle.NAMESPACE__OSGI_BUNDLE)
                    .getAttributeValue(NsOsgiBundle.ATTRIBUTE__SYMBOLIC_NAME);
        }
        return null;
    }
}
