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

/**
 * Convert cz.zcu.kiv.crce.metadata.Resource to bean classes with JAXB annotations.
 * These bean classes are ready to export metadata to xml.
 * @author Jan Reznicek
 *
 */
public class ConvertorToBeans {
	

	
	/**
	 * Get original file name from resource or null, if name was not found.
	 * @param resource resource
	 * @return original file name of resource or null.
	 */
	private String getFileName(Resource resource) {
		Capability[] caps = resource.getCapabilities("file");
		
		if (caps.length > 0) {
			for(Capability cap:caps) {
				String orgFileName = cap.getPropertyString("name");
				if (orgFileName != null) {
					return orgFileName;
				}
			}
			return null;
		} else {
			return null;
		}
		
	}
	
	//TODO - made get URI host independent
	/**
	 * Get URL of resource, that could be uset for REST action GET Bundle.
	 * @param resource resource
	 * @return URL of the resource
	 */
	private String getURL(Resource resource) {
		return "http://localhost:8080/rest/bundle/" + resource.getId();
	}
	
	/**
	 * Get hexadecimal SHA-256 of file with resource or null, if error occurred during counting digest. 
	 * @param resource
	 * @return hexadecimal SHA-256 of file with resource or null
	 */
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
 
			
			//convert the byte to hex format
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
	
	/**
	 * Add a new attribute to the list of attributes.
	 * Attribute can contains name, type or value.
	 * These tree are obligatory, if you don't want any of them, set parameter to null.
	 * 
	 * @param attributes list of attributes, new attribute will be add to this list.
	 * @param name name or null
	 * @param type type or null
	 * @param value value or null
	 */
	private void addToAttribute(List<AttributeBean> attributes, String name, String type, String value) {
		
		AttributeBean newAttributte = new AttributeBean();
		
		if(name != null) newAttributte.setName(name);
		if(type != null) newAttributte.setType(type);
		if(value != null) newAttributte.setValue(value);
		
		attributes.add(newAttributte);
				
	}
	
	/**
	 * Get CapabilityBean with osgi.identity.
	 * @param resource resource
	 * @return CapabilityBean with osgi.identity
	 */
	private CapabilityBean getOsgiIdentity(Resource resource) {
		
		CapabilityBean osgiIdentity= new CapabilityBean();
		osgiIdentity.setNamespace("osgi.identity");
		List<AttributeBean> osgiIdentityAttrs = new ArrayList<AttributeBean>();
		
		addToAttribute(osgiIdentityAttrs, "osgi.identity", null, resource.getSymbolicName());
		addToAttribute(osgiIdentityAttrs, "version", "Version", resource.getVersion().toString());
		
		osgiIdentity.setAttributes(osgiIdentityAttrs);
		
		return osgiIdentity;
	}
	

	
	/**
	 * Get CapabilitiBean with osgi.content
	 * @param resource resource 
	 * @return CapabilitiBean with osgi.content
	 */
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

	/**
	 * Convert {@link Resource} to {@link ResourceBean}.
	 * @param resource resource
	 * @return converted resource
	 */
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
