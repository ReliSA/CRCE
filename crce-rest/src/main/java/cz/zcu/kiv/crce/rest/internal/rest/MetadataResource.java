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
import cz.zcu.kiv.crce.rest.internal.rest.bean.MetadataBean;


@Path("/metadata")
public class MetadataResource {

    
	private String createXML(MetadataBean metabean) throws JAXBException{
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ClassLoader cl = cz.zcu.kiv.crce.rest.internal.rest.bean.ObjectFactory.class.getClassLoader();
		JAXBContext jc = JAXBContext.newInstance("cz.zcu.kiv.crce.rest.internal.rest.bean", cl);

		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(metabean, baos);
		
		return baos.toString();

	}
	
	
	private MetadataBean metadataFromResource(Resource resource) {
		MetadataBean newBean = new MetadataBean();
		
		System.out.println("Detected resource: " + resource.getId());
		
		newBean.setId(resource.getId());
		newBean.setName(resource.getSymbolicName());
		
		return newBean;
	}
	
	private List<MetadataBean> createMetadataList(Resource[] resources) {

		List<MetadataBean> metadataList = new ArrayList<MetadataBean>();

		for (Resource res : resources) {

			metadataList.add(metadataFromResource(res));
		}

		return metadataList;
	}
	
    @GET
    @Produces({MediaType.APPLICATION_XML })
    public String findAll() {
    	Resource[] storeResources;
    	storeResources = Activator.instance().getStore().getRepository().getResources();
    	
    	
    	try {
			if(storeResources.length > 0) {
				MetadataBean mb = metadataFromResource(storeResources[0]);
				return createXML(mb);
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
    public MetadataBean getMetadataById(@PathParam("id") String id) {
    	try {
			Resource[] storeResources;
			String filter = "(id="+id+")";
			storeResources = Activator.instance().getStore().getRepository().getResources(filter);
			
			return metadataFromResource(storeResources[0]);
			
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			return null;
		}    	
        
    }
 

}
