package cz.zcu.kiv.crce.exampleplugin.internal.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.framework.InvalidSyntaxException;

import cz.zcu.kiv.crce.exampleplugin.internal.Activator;
import cz.zcu.kiv.crce.exampleplugin.internal.rest.bean.MetadataBean;
import cz.zcu.kiv.crce.metadata.Resource;


@Path("/metadata")
public class MetadataResource {

    
	private List<MetadataBean> createMetadataList(Resource[] resources) {
		
		List<MetadataBean> metadataList = new ArrayList<MetadataBean>();
		
		for(Resource res: resources) {

			
			metadataList.add(metadataFromResource(res));
		}
		
		return metadataList;
	}
	
	
	private MetadataBean metadataFromResource(Resource resource) {
		MetadataBean newBean = new MetadataBean();
		
		System.out.println("Detected resource: " + resource.getId());
		
		newBean.setId(resource.getId());
		newBean.setName(resource.getSymbolicName());
		
		return newBean;
	}
	
    /*@GET
    @Produces({MediaType.APPLICATION_XML })
    public List<MetadataBean> findAll() {
    	Resource[] storeResources;
    	storeResources = Activator.instance().getStore().getRepository().getResources();
    	
        return createMetadataList(storeResources);

    }*/
    
    @GET
    @Produces({MediaType.TEXT_PLAIN })
    public String findAll() {
    	Resource[] storeResources;
    	storeResources = Activator.instance().getStore().getRepository().getResources();
    	
        List<MetadataBean> resourcesMetadata = createMetadataList(storeResources);
        
        String output = "";
        for(MetadataBean mb:resourcesMetadata) {
        	output+=mb.getId()+"\n";
        }
        return output;

    }
 
    @GET @Path("{id}")
    @Produces({MediaType.APPLICATION_XML })
    public MetadataBean findById(@PathParam("id") String id) {
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
