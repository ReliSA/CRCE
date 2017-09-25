package cz.zcu.kiv.crce.crce_webui_vaadin.repository.services;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.vaadin.server.VaadinSession;
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
	
	public long getSize() {
        return metadataService.getSize(resource);
    }
	
	public List<ResourceBean> getAllResourceBeanFromBuffer(WrappedSession session){
		List<ResourceBean> resources = new ArrayList<ResourceBean>();
		for(Resource resource : Activator.instance().getBuffer(session).getResources()){
			setResource(resource);
			resources.add(new ResourceBean(getPresentationName(), getSymbolicName(), getVersion(), getCategories(), getSize(), resource));
		}
		return resources;
	}
	
	public boolean removeResourceFromBuffer(WrappedSession wSession, Resource resource){
		boolean result;
		try {
			result = Activator.instance().getBuffer(wSession).remove(resource);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return result;
	}
	
	public List<ResourceBean> getFindResourceBeanFromBuffer(WrappedSession session, String stringFilter){
		boolean passesFilter = false;
		ArrayList<ResourceBean> arrayList = new ArrayList<>();
		for (ResourceBean resourceBean : getAllResourceBeanFromBuffer(session)){
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
	
	// for store
	public boolean pushResourcesToStore(VaadinSession session){
		try {
			Activator.instance().getBuffer(session.getSession()).commit(true);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public List<ResourceBean> getAllResourceBeanFromStore(VaadinSession session) {
		List<ResourceBean> resourceBeanList = new ArrayList<ResourceBean>();
		for(Resource resource : Activator.instance().getStore(getRepositoryId(session)).getResources()){
			setResource(resource);
			resourceBeanList.add(new ResourceBean(getPresentationName(), getSymbolicName(), getVersion(), getCategories(), getSize(), resource));
		}
		return resourceBeanList ;
	}
	
	public List<ResourceBean> getFindResourceBeanFromStore(VaadinSession session, String stringFilter){
		boolean passesFilter = false;
		ArrayList<ResourceBean> arrayList = new ArrayList<>();
		for (ResourceBean resourceBean : getAllResourceBeanFromStore(session)){
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
	
	public boolean removeResorceFromStore(VaadinSession session, Resource resource){
		boolean result;
		try {
			result = Activator.instance().getStore(getRepositoryId(session)).remove(resource);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return result;
	}

	private String getRepositoryId(VaadinSession session) {
		String id = (String) session.getAttribute("repositoryId");
		if (id == null) {
			Map<String, String> stores = Activator.instance().getRepositories();
			if (stores.isEmpty()) {
				return null;
			}
			id = stores.keySet().iterator().next();
			session.setAttribute("repositoryId", id);
		}
		return id;
	}
}
