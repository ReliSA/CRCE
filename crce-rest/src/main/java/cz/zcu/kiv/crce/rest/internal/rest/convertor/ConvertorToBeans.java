package cz.zcu.kiv.crce.rest.internal.rest.convertor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.internal.rest.bean.AttributeBean;
import cz.zcu.kiv.crce.rest.internal.rest.bean.CapabilityBean;
import cz.zcu.kiv.crce.rest.internal.rest.bean.RequirementBean;
import cz.zcu.kiv.crce.rest.internal.rest.bean.ResourceBean;

public class ConvertorToBeans {
	
	
	private void addToAttribute(List<AttributeBean> attributes, String name, String type, String value) {
		
		AttributeBean newAttributte = new AttributeBean();
		
		if(name != null) newAttributte.setName(name);
		if(type != null) newAttributte.setType(type);
		if(value != null) newAttributte.setValue(value);
		
		attributes.add(newAttributte);
				
	}
	
	private CapabilityBean getOsgiIdentity(Resource resource) {
		
		CapabilityBean osgiIdentity= new CapabilityBean();
		osgiIdentity.setNamespace("osgi.identity");
		List<AttributeBean> osgiIdentityAttrs = new ArrayList<AttributeBean>();
		
		addToAttribute(osgiIdentityAttrs, "osgi.identity", null, resource.getSymbolicName());
		addToAttribute(osgiIdentityAttrs, "version", "Version", resource.getVersion().toString());
		
		osgiIdentity.setAttributes(osgiIdentityAttrs);
		
		return osgiIdentity;
	}
	
	private String getFileName(Resource resource) {
		Capability[] caps = resource.getCapabilities("file");
		
		if (caps.length > 0) {
			return caps[0].getPropertyString("name");
		} else {
			return null;
		}
		
	}
	
	//TODO - made get URI host independent
	private String getURL(Resource resource) {
		return "http://localhost:8080/rest/bundle/" + resource.getId();
	}
	
	private String getSHA(Resource resource) {
		FileInputStream fis = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			fis = new FileInputStream(new File(resource.getUri()));
			
			byte[] dataBytes = new byte[1024];
 
			int nread = 0; 
			while ((nread = fis.read(dataBytes)) != -1) {
			  md.update(dataBytes, 0, nread);
			};
			byte[] mdbytes = md.digest();
 
			
			//convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++) {
			  sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			}		    
			
			return sb.toString();
			
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if(fis!=null) {
				try {
					fis.close();
				} catch (IOException e) {
					//do nothing
				}
			}
		}
	}
	
	private CapabilityBean getOsgiContent(Resource resource) {
		
		CapabilityBean osgiContent = new CapabilityBean();
		osgiContent.setNamespace("osgi.content");
		List<AttributeBean> attributes = new ArrayList<AttributeBean>();
		
		addToAttribute(attributes, "osgi.content", null, getSHA(resource));
		addToAttribute(attributes, "url", null, getURL(resource));
		addToAttribute(attributes, "size", "Long", Long.toString(resource.getSize()));
		addToAttribute(attributes, "mime", null, "not implemented yet");
		addToAttribute(attributes, "crce.original-file-name", null, getFileName(resource));
		
		osgiContent.setAttributes(attributes);
		
		return osgiContent;
		
		
	}

	public ResourceBean convertResource(Resource resource) {
		
		ResourceBean newBean = new ResourceBean();
		
		newBean.setCrceId(resource.getId());
		
		List<CapabilityBean> caps = new ArrayList<CapabilityBean>();
		List<RequirementBean> reqs = new ArrayList<RequirementBean>();	

		
		caps.add(getOsgiIdentity(resource));
		caps.add(getOsgiContent(resource));		
		
		newBean.setCapabilities(caps);
		newBean.setRequirements(reqs);
		
		return newBean;
	}
}
