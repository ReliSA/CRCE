package cz.zcu.kiv.crce.rest.internal.rest.xml;

import java.io.ByteArrayOutputStream;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.internal.Activator;
import cz.zcu.kiv.crce.rest.internal.rest.generated.ObjectFactory;
import cz.zcu.kiv.crce.rest.internal.rest.generated.Trepository;

public class ResourceParent {
	
	protected static int requestId = 0;
	
	private static final Logger log = LoggerFactory.getLogger(ResourceParent.class);
	
	/**
     * Create XML String from repository.
     * @param repositoryBean repository contains metadata about resources
     * @return XML String with exported metadata
     * @throws JAXBException XML export failed
     */
	protected String createXML(Trepository repositoryBean) throws WebApplicationException{
		
		try {
			ObjectFactory objectFactory = new ObjectFactory();
			JAXBElement<?> repository = objectFactory.createRepository(repositoryBean);
			Class<?> clazz = repository.getValue().getClass();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ClassLoader cl = cz.zcu.kiv.crce.rest.internal.rest.generated.ObjectFactory.class.getClassLoader();
			JAXBContext jc = JAXBContext.newInstance(clazz.getPackage().getName(), cl);

			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(repository, baos);
			
			return baos.toString();
			
		} catch (PropertyException e) {
			log.error("Request ({}) - Exception while creating response.", requestId);
			log.error(e.getMessage(), e);
			throw new WebApplicationException(e, 500);
		} catch (JAXBException e) {
			log.error("Request ({}) - Exception while creating response.", requestId);
			log.error(e.getMessage(), e);
			throw new WebApplicationException(e, 500);
		}

	}
	
	/**
	 * Select from array of resources the one with highest version
	 * @param storeResources array of resources
	 * @return resource with highest version
	 */
	protected Resource resourceWithHighestVersion(Resource[] storeResources) {		
		
		
		if(storeResources.length < 1) {
			return null;
		}
		Resource resourceWithHighestVersion = storeResources[0];
		
		for(Resource res: storeResources) {
			if(resourceWithHighestVersion.getVersion().compareTo(res.getVersion()) < 0) {
				resourceWithHighestVersion = res;
			}
		}
		
		log.debug("Request ({}) - Bundle with highest version is: {}.", requestId, resourceWithHighestVersion.getId());
		
		return resourceWithHighestVersion;
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
		try {
			Resource[] storeResources;
			storeResources = Activator.instance().getStore().getRepository().getResources(filter);
			
			if(storeResources.length < 1) {
				log.debug("Request ({}) - Requested bundle was not found in the repository.", requestId );
				throw new WebApplicationException(404);
			} else {
				return storeResources[0];
			}			
			
		} catch (InvalidSyntaxException e) {
			log.debug("Request ({}) - Bad syntax of LDAP filter", requestId);
			throw new WebApplicationException(400);
		} 
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
		try {
			Resource[] storeResources;
			storeResources = Activator.instance().getStore().getRepository().getResources(filter);
			
			if(storeResources.length < 1) {
				log.debug("Request ({}) - Requested bundle was not found in the repository.", requestId );
				throw new WebApplicationException(404);
			}
			
			Resource resource;
			
			if(storeResources.length > 1) {
				log.debug("Request ({}) - More bundles was found, the one with highest version will be selected.", requestId);
				resource = resourceWithHighestVersion(storeResources);
			} else {
				log.debug("Request ({}) - The requested bundle was found.", requestId);
				resource = storeResources[0];
			}
			
			return resource;
			
		} catch (InvalidSyntaxException e) {
			log.debug("Request ({}) - Bad syntax of LDAP filter", requestId);
			throw new WebApplicationException(400);
		} 
	}
	
	
	/**
	 * Find bundles by filter
	 * If the syntax of the LDAP filter is wrong, throw  {@link WebApplicationException} with status 400 - Bad request. 
	 * @param filter LDAP filter
	 * @return founded bundles.
	 * @throws WebApplicationException
	 */
	protected Resource[] findBundlesByFilter(String filter) throws WebApplicationException {
		try {
			Resource[] storeResources;
			storeResources = Activator.instance().getStore().getRepository().getResources(filter);
			
			return storeResources;
			
		} catch (InvalidSyntaxException e) {
			log.debug("Request ({}) - Bad syntax of LDAP filter", requestId);
			throw new WebApplicationException(400);
		} 
	}
	
	
}
