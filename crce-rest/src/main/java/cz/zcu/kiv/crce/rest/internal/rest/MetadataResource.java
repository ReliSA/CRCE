package cz.zcu.kiv.crce.rest.internal.rest;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.osgi.framework.InvalidSyntaxException;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.internal.Activator;
import cz.zcu.kiv.crce.rest.internal.rest.bean.RepositoryBean;
import cz.zcu.kiv.crce.rest.internal.rest.bean.ResourceBean;
import cz.zcu.kiv.crce.rest.internal.rest.convertor.ConvertorToBeans;

/**
 * Server will provide a metadata information about resources in the repository.
 * @author Jan Reznicek
 *
 */
@Path("/metadata")
public class MetadataResource {

    /**
     * Create XML String from repository.
     * @param repositoryBean repository contains metadata about resources
     * @return XML String with exported metadata
     * @throws JAXBException XML export failed
     */
	private String createXML(RepositoryBean repositoryBean) throws JAXBException{
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ClassLoader cl = cz.zcu.kiv.crce.rest.internal.rest.bean.ObjectFactory.class.getClassLoader();
		JAXBContext jc = JAXBContext.newInstance("cz.zcu.kiv.crce.rest.internal.rest.bean", cl);

		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(repositoryBean, baos);
		
		return baos.toString();

	}
	
	/**
	 * Prepare RepositoryBean, that will contains metadata from array of resources.
	 * @param resources array of resources
	 * @return Object with metadata from array of resources, that is ready to XML export using JAXB. 
	 */
	private RepositoryBean metadataFromResources(Resource[] resources) {
 
		ConvertorToBeans conv = new ConvertorToBeans();
		
		RepositoryBean repositoryBean = new RepositoryBean();
		List<ResourceBean> resourceBeans = new ArrayList<>();
		
		for(Resource res: resources) {
			resourceBeans.add(conv.convertResource(res));
		}
		
		repositoryBean.setResources(resourceBeans);

		return repositoryBean;
	}
	
	/**
	 * Returns XML with metadata of resources from the store repository.
	 * If the request is without filter query parameter, return all resources.
	 * If the request have filter parameter, return resources that met the filter.
	 * @param filter obligatory LDAP filter
	 * @return XML with metadata of resources from the store repository
	 */
    @GET
    @Produces({MediaType.APPLICATION_XML })
    public Response getMetadata(@QueryParam("filter") String filter) {
    	Resource[] storeResources;
    	
    	try {
        	if(filter != null) {
        		storeResources = Activator.instance().getStore().getRepository().getResources(filter);
        	} else {
        		storeResources = Activator.instance().getStore().getRepository().getResources();
        	}
    		
			if(storeResources.length > 0) {
				RepositoryBean repositoryBean = metadataFromResources(storeResources);
				return Response.ok(createXML(repositoryBean)).build();
			} else {
				return Response.status(404).build();
			}
		} catch (JAXBException e) {
			e.printStackTrace();
			return Response.serverError().build();
		} catch (InvalidSyntaxException e) {
			//Invalid syntax
			e.printStackTrace();
			return Response.status(400).build();
		}

    }
    

    /**
     * Return  xml with metadata of one resource, that is specified by id.
     * @param id id of the resource
     * @return xml with metadata about the resource
     */
    @GET @Path("{id}")
    @Produces({MediaType.APPLICATION_XML })
    public Response getMetadataById(@PathParam("id") String id) {
    	try {
			Resource[] storeResources;
			String filter = "(id="+id+")";
			storeResources = Activator.instance().getStore().getRepository().getResources(filter);
			
	    	try {
				if(storeResources.length > 0) {
					RepositoryBean repositoryBean = metadataFromResources(storeResources);
					return Response.ok(createXML(repositoryBean)).build();
				} else {
					//no resource was found
					return Response.status(404).build();
				}
			} catch (JAXBException e) {
				//xml export of resource metadata failed
				e.printStackTrace();				
				return Response.serverError().build();
			}
			
		} catch (InvalidSyntaxException e) {
			//Invalid syntax of request
			e.printStackTrace();
			return Response.status(400).build();
		}    	
        
    }
 

}
