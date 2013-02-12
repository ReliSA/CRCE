package cz.zcu.kiv.crce.rest.internal.rest.convertor;

import java.util.ArrayList;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.internal.rest.bean.AttributeBean;
import cz.zcu.kiv.crce.rest.internal.rest.bean.CapabilityBean;
import cz.zcu.kiv.crce.rest.internal.rest.bean.RequirementBean;
import cz.zcu.kiv.crce.rest.internal.rest.bean.ResourceBean;

public class ConvertorToBeans {
	
	
	private void addToAttribute(ArrayList<AttributeBean> attributes, String name, String type, String value) {
		
		AttributeBean newAttributte = new AttributeBean();
		
		if(name != null) newAttributte.setName(name);
		if(type != null) newAttributte.setType(type);
		if(value != null) newAttributte.setValue(value);
		
		attributes.add(newAttributte);
				
	}
	
	private CapabilityBean getOsgiIdentity(Resource resource) {
		
		CapabilityBean osgiIdentity= new CapabilityBean();
		osgiIdentity.setNamespace("osgi.identity");
		ArrayList<AttributeBean> osgiIdentityAttrs = new ArrayList<AttributeBean>();
		
		addToAttribute(osgiIdentityAttrs, "osgi.identity", null, resource.getSymbolicName());
		addToAttribute(osgiIdentityAttrs, "version", "Version", resource.getVersion().toString());
		
		osgiIdentity.setAttributes(osgiIdentityAttrs);
		
		return osgiIdentity;
	}
	
	private CapabilityBean getOsgiContent(Resource resource) {
		
		CapabilityBean osgiContent = new CapabilityBean();
		osgiContent.setNamespace("osgi.content");
		ArrayList<AttributeBean> attributes = new ArrayList<AttributeBean>();
		
		addToAttribute(attributes, "osgi.content", null, "not implemented yet");
		addToAttribute(attributes, "url", null, "not implemented yet");
		addToAttribute(attributes, "size", "Long", Long.toString(resource.getSize()));
		addToAttribute(attributes, "mime", null, "not implemented yet");
		addToAttribute(attributes, "crce.original-file-name", null, resource.getPresentationName());
		
		osgiContent.setAttributes(attributes);
		
		return osgiContent;
		
		
	}

	public ResourceBean convertResource(Resource resource) {
		
		ResourceBean newBean = new ResourceBean();
		
		newBean.setCrceId(resource.getId());
		
		ArrayList<CapabilityBean> caps = new ArrayList<CapabilityBean>();
		ArrayList<RequirementBean> reqs = new ArrayList<RequirementBean>();	

		
		caps.add(getOsgiIdentity(resource));
		caps.add(getOsgiContent(resource));		
		
		newBean.setCapabilities(caps);
		newBean.setRequirements(reqs);
		
		return newBean;
	}
}
