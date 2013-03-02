package cz.zcu.kiv.crce.rest.internal.rest;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.internal.rest.generated.ObjectFactory;
import cz.zcu.kiv.crce.rest.internal.rest.generated.Trepository;

public class Utils {
	
	/**
     * Create XML String from repository.
     * @param repositoryBean repository contains metadata about resources
     * @return XML String with exported metadata
     * @throws JAXBException XML export failed
     */
	public static String createXML(Trepository repositoryBean) throws JAXBException{
		
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

	}
	
	/**
	 * Select from array of resources the one with highest version
	 * @param storeResources array of resources
	 * @return resource with highest version
	 */
	public static Resource resourceWithHighestVersion(Resource[] storeResources) {		
		
		
		if(storeResources.length < 1) {
			return null;
		}
		Resource resourceWithHighestVersion = storeResources[0];
		
		for(Resource res: storeResources) {
			if(resourceWithHighestVersion.getVersion().compareTo(res.getVersion()) < 0) {
				resourceWithHighestVersion = res;
			}
		}
		
		return resourceWithHighestVersion;
	}
	

}
