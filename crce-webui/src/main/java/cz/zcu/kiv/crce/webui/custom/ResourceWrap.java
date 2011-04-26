package cz.zcu.kiv.crce.webui.custom;

import java.net.URI;

import org.osgi.framework.Version;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

abstract class ResourceWrap extends ResourceAdapter{
	protected Resource r;
	
	protected ResourceWrap(Resource r){
		this.r=r;
	}
	
	@Override
	public Property[] getProperties() {		
		return r.getProperties();
	}
	@Override
	
	public Capability[] getCapabilities() {		
		return r.getCapabilities();
	}
	
	@Override
	public String[] getCategories() {		
		return r.getCategories();
	}
	
	@Override
	public Requirement[] getRequirements() {
		
		return r.getRequirements();
	}
	@Override
	public String getSymbolicName() {
		return r.getSymbolicName();
	}
	@Override
	public String getId() {		
		return r.getId();
	}
	@Override
	public Version getVersion() {		
		return r.getVersion();
	}
	@Override
	public String getPresentationName() {		
		return r.getPresentationName();
	}
	@Override
	public URI getUri() {		
		return r.getUri();
	}
	@Override
	public URI getRelativeUri() {		
		return r.getRelativeUri();
	}
	@Override
	public long getSize() {		
		return r.getSize();
	}
}
