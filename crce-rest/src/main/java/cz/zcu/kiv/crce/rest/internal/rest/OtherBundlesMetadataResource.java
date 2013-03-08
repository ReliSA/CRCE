package cz.zcu.kiv.crce.rest.internal.rest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.internal.Activator;
import cz.zcu.kiv.crce.rest.internal.rest.convertor.ConvertorToBeans;
import cz.zcu.kiv.crce.rest.internal.rest.convertor.IncludeMetadata;
import cz.zcu.kiv.crce.rest.internal.rest.generated.ObjectFactory;
import cz.zcu.kiv.crce.rest.internal.rest.generated.Trepository;
import cz.zcu.kiv.crce.rest.internal.rest.generated.Tresource;

/**
 * Server provide metadata about other bundles (repository contents diff).
 * 
 * When the client asks CRCE to provide bundle metadata of those bundles which it does not know about
 * and sends a list of bundle identifiers (= those bundles it knows about)
 * and optionally sends a filter criteria specifying which subset of metadata it is interested in

 * Then CRCE sends that (subset of) metadata only for the following bundles currently available in repository in "stored" state:
 * are not in the list sent by client ("new bundles")
 * are in the list but have been removed from the repository ("deleted bundles" - just a list of bundle identifiers).
 * @author Jan Reznicek
 *
 */
@Path("/other_bundles_metadata")
public class OtherBundlesMetadataResource {
	
	
	/**
     * Create XML String from repository.
     * @param repositoryBean repository contains metadata about resources
     * @return XML String with exported metadata
     * @throws JAXBException XML export failed
     * @throws UnsupportedEncodingException unsupported encoding
     */
	public Trepository createXML(String repository) throws JAXBException,
			UnsupportedEncodingException {

		ClassLoader cl = ObjectFactory.class.getClassLoader();
		JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class
				.getPackage().getName(), cl);

		Unmarshaller unmarshaller = jc.createUnmarshaller();

		InputStream repositoryStream = new ByteArrayInputStream(
				repository.getBytes("UTF-8"));
		Object obj = unmarshaller.unmarshal(repositoryStream);

		JAXBElement<?> jxbE = (JAXBElement<?>) obj;

		Trepository rep = (Trepository) jxbE.getValue();


		return rep;

	}
	
	/**
	 * Create a set of id of all resources from repository.
	 * @param resources repository.
	 * @return set of id
	 */
	private Set<String> createIdSet(Trepository resources) {
		List<Tresource> resList =  resources.getResource();
		Set<String> idSet = new HashSet<>();
		
		for(Tresource res: resList) {
			idSet.add(res.getId());
		}
		
		return idSet;
	}
	
	/**
	 * Create a set of id of all resources from list of resources.
	 * @param resources list of resources.
	 * @return set of id
	 */
	private Set<String> createIdSet(Resource[] resources) {
		
		Set<String> idSet = new HashSet<>();
		
		for(Resource res: resources) {
			idSet.add(res.getId());
		}
		
		return idSet;
	}
	
	/**
	 *  Returns list of bundles, that are in storeResources, but not in clientBundlesIdSet. 
	 *  These bundles are new on store since last update of client bundles.
	 * @param storeResources list of bundles in  the store
	 * @param clientBundlesIdSet set of id of bundles in the client
	 * @return list of bundles, that are new on store.
	 */
	private List<Resource> findNewResources(Resource[] storeResources, Set<String> clientBundlesIdSet) {
		 
		 List<Resource> newResources = new ArrayList<Resource>();
		 
		 for(Resource res: storeResources) {
			 if(!clientBundlesIdSet.contains(res.getId())) {
				 newResources.add(res);
			 }
		 }
		 
		 return newResources;
	}
	
	
	/**
	 * Returns list of bundles, that are in clientResources, but not in storeIdset. 
	 * These bundles was deleted from store since last update of client bundles.
	 * @param clientResources list of bundles in the client
	 * @param storeIdSet set of id of bundles in the store
	 * @return list of bundles, that have was deleted from store.
	 */
	private List<Tresource> findDeletedResources(List<Tresource> clientResources, Set<String> storeIdSet) {
		
		List<Tresource> deletedResources = new ArrayList<Tresource>();
		
		for(Tresource res: clientResources) {
			if(!storeIdSet.contains(res.getId())){
				deletedResources.add(res);
			}
		}
		
		return deletedResources;
	}
	
	
	/**
	 * Return repository with other bundles (new to client).
	 * Other bundles are bundles currently available in repository in "stored" state:
	 * <ul>
     *  <li> are not in the list sent by client ("new bundles")
     *  <li> are in the list but have been removed from the repository ("deleted bundles" - just a list of bundle identifiers).
	 * </ul>
	 * 
	 * @param clientBundles
	 * @return
	 */
	private Trepository findOtherBundles(Trepository clientBundles) {
		
		Resource[] storeResources = Activator.instance().getStore().getRepository().getResources();

		Set<String> clientBundlesIdSet = createIdSet(clientBundles);
		Set<String> storeIdSet = createIdSet(storeResources);

		List<Resource> newResources = findNewResources(storeResources,clientBundlesIdSet);
		List<Tresource> deletedResources = findDeletedResources(clientBundles.getResource(), storeIdSet);

		//convert new resources to repository
		ConvertorToBeans conv = new ConvertorToBeans();
		IncludeMetadata include = new IncludeMetadata();
		include.includeAll();
		Trepository repository = conv.convertRepository(newResources.toArray(new Resource[0]), include);

		//add deleted resources
		repository.getResource().addAll(deletedResources);	 
		
		return repository;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response otherBundles(String konwnBundles) {
		try {
			Trepository clientBundles = createXML(konwnBundles);
			Trepository otherBundles = findOtherBundles(clientBundles);
			
			return Response.ok(Utils.createXML(otherBundles)).build();
			
			
		} catch (JAXBException e) {
			// invalid syntax answer
			e.printStackTrace();
			return Response.status(400).build();
		} catch (UnsupportedEncodingException e) {
			// invalid syntax answer
			e.printStackTrace();
			return Response.status(400).build();
		}
		

	}

}
