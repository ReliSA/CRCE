package cz.zcu.kiv.crce.crce_webui_vaadin.repository.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.vaadin.server.WrappedSession;
import cz.zcu.kiv.crce.crce_webui_vaadin.internal.Activator;
import cz.zcu.kiv.crce.crce_webui_vaadin.repository.classes.ResourceBean;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiBundle;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.type.Version;

public class ResourceService implements Serializable{
	private static final long serialVersionUID = 6161700434751771631L;
	private Resource resource;
    private transient MetadataService metadataService;
    
    public ResourceService(MetadataService metadataService){
        this.metadataService = metadataService;
	}
    
	public ResourceService(Resource resource, MetadataService metadataService){
		this.resource = resource;
        this.metadataService = metadataService;
	}
	
	public void setResource(Resource resource){
    	this.resource = resource;
    }
	
	public String getPresentationName() {
        return metadataService.getPresentationName(resource);
    }
	
	public String getSymbolicName() {
        String name = "unknown-symbolic-name";
        List<Capability> capabilities = resource.getCapabilities(NsOsgiBundle.NAMESPACE__OSGI_BUNDLE);
        if (!capabilities.isEmpty()) {
            name = capabilities.get(0).getAttributeValue(NsOsgiBundle.ATTRIBUTE__SYMBOLIC_NAME);
        }
        return name;
    }
	
	public String[] getCategories() {
		return metadataService.getCategories(resource).toArray(new String[0]);
	}
	
	public Version getVersion() {
        Version version = null;
        List<Capability> capabilities = resource.getCapabilities(NsOsgiBundle.NAMESPACE__OSGI_BUNDLE);
        if (!capabilities.isEmpty()) {
            version = capabilities.get(0).getAttributeValue(NsOsgiBundle.ATTRIBUTE__VERSION);
        }
        return version;
    }
	
	public List<ResourceBean> getAllResourceBean(WrappedSession session){
		List<ResourceBean> resources = new ArrayList<ResourceBean>();
		for(Resource resource : Activator.instance().getBuffer(session).getResources()){
			setResource(resource);
			resources.add(new ResourceBean(getPresentationName(), getSymbolicName(), getVersion(), getCategories(), resource));
		}
		return resources;
	}
	
	public List<ResourceBean> getFindResourceBean(WrappedSession session, String stringFilter){
		boolean passesFilter = false;
		ArrayList<ResourceBean> arrayList = new ArrayList<>();
		for (ResourceBean resourceBean : getAllResourceBean(session)){
			if(stringFilter == null || stringFilter.isEmpty()){
				passesFilter = true;
			}
			else{
				passesFilter = resourceBean.toString().toLowerCase().contains(stringFilter.toLowerCase());
			}
			if (passesFilter) {
				arrayList.add(resourceBean);
			}
		}
		Collections.sort(arrayList, new Comparator<ResourceBean>() {
			@Override
			public int compare(ResourceBean o1, ResourceBean o2) {
				return (int) (o2.hashCode() - o1.hashCode());
			}
		});
		return arrayList;
	}
	
}
