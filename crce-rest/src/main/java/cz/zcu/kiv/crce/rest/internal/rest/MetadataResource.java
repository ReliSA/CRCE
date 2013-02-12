package cz.zcu.kiv.crce.rest.internal.rest;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.osgi.framework.InvalidSyntaxException;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.internal.Activator;
import cz.zcu.kiv.crce.rest.internal.rest.bean.RepositoryBean;
import cz.zcu.kiv.crce.rest.internal.rest.bean.ResourceBean;
import cz.zcu.kiv.crce.rest.internal.rest.convertor.ConvertorToBeans;


@Path("/metadata")
public class MetadataResource {

    
	private String createXML(RepositoryBean repositoryBean) throws JAXBException{
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ClassLoader cl = cz.zcu.kiv.crce.rest.internal.rest.bean.ObjectFactory.class.getClassLoader();
		JAXBContext jc = JAXBContext.newInstance("cz.zcu.kiv.crce.rest.internal.rest.bean", cl);

		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(repositoryBean, baos);
		
		return baos.toString();

	}
	
	
	private RepositoryBean metadataFromResource(Resource[] resources) {
 
		ConvertorToBeans conv = new ConvertorToBeans();
		
		RepositoryBean repositoryBean = new RepositoryBean();
		ArrayList<ResourceBean> resourceBeans = new ArrayList<>();
		
		for(Resource res: resources) {
			resourceBeans.add(conv.convertResource(res));
		}
		
		repositoryBean.setResources(resourceBeans);

		return repositoryBean;
	}
	
    @GET
    @Produces({MediaType.APPLICATION_XML })
    public String findAll() {
    	Resource[] storeResources;
    	storeResources = Activator.instance().getStore().getRepository().getResources();
    	
    	
    	try {
			if(storeResources.length > 0) {
				RepositoryBean repositoryBean = metadataFromResource(storeResources);
				return createXML(repositoryBean);
			} else {
				return "<noresource></noresource>";
			}
		} catch (JAXBException e) {
			e.printStackTrace();
			return "<error></error>";
		}

    }
    

 
    @GET @Path("{id}")
    @Produces({MediaType.APPLICATION_XML })
    public String getMetadataById(@PathParam("id") String id) {
    	try {
			Resource[] storeResources;
			String filter = "(id="+id+")";
			storeResources = Activator.instance().getStore().getRepository().getResources(filter);
			
			return "<notimplementedyet/>";
			
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			return null;
		}    	
        
    }
 

}
