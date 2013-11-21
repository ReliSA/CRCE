package cz.zcu.kiv.crce.rest.internal.rest.convertor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.internal.rest.generated.Tattribute;
import cz.zcu.kiv.crce.rest.internal.rest.generated.Tcapability;
import cz.zcu.kiv.crce.rest.internal.rest.generated.Tdirective;
import cz.zcu.kiv.crce.rest.internal.rest.generated.Trepository;
import cz.zcu.kiv.crce.rest.internal.rest.generated.Trequirement;
import cz.zcu.kiv.crce.rest.internal.rest.generated.Tresource;

/**
 * Convert cz.zcu.kiv.crce.metadata.Resource to bean classes with JAXB annotations.
 * These bean classes are ready to export metadata to xml.
 * @author Jan Reznicek
 *
 */
public class ConvertorToBeans {
		
	private static final Logger log = LoggerFactory.getLogger(ConvertorToBeans.class);
	
	public static final String OSGI_IDENTITY_CAP_NAME = "osgi.identity";
	public static final String OSGI_CONTENT_CAP_NAME = "osgi.content";
	public static final String CRCE_IDENTITY_CAP_NAME = "crce.identity";
	public static final String OSGI_WIRING_NAME = "osgi.wiring.package";
	
	/**
	 * Default host, should not be needed, information is gained from request context.
	 * But is there for sure
	 * TODO: add default rest host to CRCE config file
	 */
	public static final String DEFAULT_HOST = "http://localhost:8080/rest/";
	
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
	
	/**
	 * Get URL of resource, that could be uset for REST action GET Bundle.
	 * @param resource resource
	 * @param ui contextual info about URI
	 * @return URL of the resource
	 */
	private String getURL(Resource resource, UriInfo ui) {
		if(ui == null) {
			return DEFAULT_HOST + "bundle/" + resource.getId();
		}
		String url = ui.getBaseUri().toString();
		return url + "bundle/" + resource.getId();
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
			log.warn(e.getMessage(),e);
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
	 * Add a new attribute to the list of objects.
	 * Attribute can contains name, type or value.
	 * These tree are obligatory, if you don't want any of them, set parameter to null.
	 * 
	 * @param list list of objects (attributtes, capabilities, directives)
	 * @param name name or null
	 * @param type type or null
	 * @param value value or null
	 * @param op operation or null
	 */
	private void addAttribute(List<Object> list, String name, String type, String value, String op) {
		
		Tattribute newAttributte = new Tattribute();
		
		if(name != null) newAttributte.setName(name);
		if(type != null) newAttributte.setType(type);
		if(value != null) newAttributte.setValue(value);
		if(op != null) newAttributte.setOp(op);
		
		list.add(newAttributte);
				
	}
	
	/**
	 * Returns capability with osgi.identity.
	 * @param resource resource
	 * @return capability with osgi.identity
	 */
	private Tcapability prepareOsgiIdentity(Resource resource) {
		
		Tcapability osgiIdentity= new Tcapability();
		osgiIdentity.setNamespace(OSGI_IDENTITY_CAP_NAME);
		List<Object> osgiIdentityAttrs = osgiIdentity.getDirectiveOrAttributeOrCapability();
		
		addAttribute(osgiIdentityAttrs, "name", null, resource.getSymbolicName(), null);
		addAttribute(osgiIdentityAttrs, "version", "Version", resource.getVersion().toString(), null);
		
		
		return osgiIdentity;
	}
	

	
	/**
	 * Returns capability with osgi.content
	 * @param resource resource 
	 * @param ui contextual info about URI
	 * @return capability with osgi.content
	 */
	private Tcapability prepareOsgiContent(Resource resource, UriInfo ui) {
		
		Tcapability osgiContent = new Tcapability();
		osgiContent.setNamespace(OSGI_CONTENT_CAP_NAME);
		List<Object> attributes = osgiContent.getDirectiveOrAttributeOrCapability();
		
		MimeTypeSelector mimeTypeSelector = new MimeTypeSelector();
		
		addAttribute(attributes, "hash", null, getSHA(resource), null);
		addAttribute(attributes, "url", null, getURL(resource, ui), null);
		addAttribute(attributes, "size", "Long", Long.toString(resource.getSize()), null);
		addAttribute(attributes, "mime", null, mimeTypeSelector.selectMimeType(resource), null);
		addAttribute(attributes, "crce.original-file-name", null, getFileName(resource), null);
		
		
		return osgiContent;
		
	}
	
	/**
	 * Create from array of strings result string, where are string separated by one comma ','.
	 * @param categories array of strings
	 * @return result string
	 */
	private String categoriesToString(String[] categories) {
		
		String catArrayToStr = Arrays.toString(categories); 
		
		String categoryStr = catArrayToStr.substring(1, catArrayToStr.length()-1).replaceAll(" ", "");
		
		//log.info("Category To string: {}", categoryStr);
		
		return categoryStr;
	}
	
	
	/**
	 * Returns capability with crce.identity
	 * @param resource resource 
	 * @return capability with crce.identity
	 */
	private Tcapability prepareCrceIdentity(Resource resource) {
		
		Tcapability crceIdentity = new Tcapability();
		crceIdentity.setNamespace(CRCE_IDENTITY_CAP_NAME);
		List<Object> attributes = crceIdentity.getDirectiveOrAttributeOrCapability();
		
		addAttribute(attributes, "name", null, resource.getId(), null);
		addAttribute(attributes, "crce.categories", "List<String>", categoriesToString(resource.getCategories()), null);
		addAttribute(attributes, "crce.status", null, "stored", null);
		
		return crceIdentity;
		
	}
	
	

	/**
	 * Add all capabilities with package wiring to the resource
	 * 
	 * @param capabilities
	 *            list of capabilities of the resourceBean
	 * @param resource
	 *            resource
	 */
	private void addCapabilityWirings(List<Tcapability> capabilities, Resource resource) {
		Capability[] caps =  resource.getCapabilities("package");
		for (Capability cap : caps) {

			Tcapability newCapBean = new Tcapability();
			List<Object> attributes = newCapBean.getDirectiveOrAttributeOrCapability();
			newCapBean.setNamespace(OSGI_WIRING_NAME);

			// package attribute
			Property packageProp = cap.getProperty("package");
			if (packageProp != null) {
				Tattribute packAtr = new Tattribute();
				packAtr.setName("name");
				packAtr.setValue(packageProp.getValue());
				attributes.add(packAtr);
			}

			// version attribute
			Property versionProp = cap.getProperty("version");
			if (versionProp != null) {
				Tattribute versAtr = new Tattribute();
				versAtr.setName("version");
				versAtr.setType("Version");
				versAtr.setValue(versionProp.getValue());
				attributes.add(versAtr);
			}
			
			capabilities.add(newCapBean);

		}
	}
	
	
	
	/**
	 * Add all requirements with package wiring to the resource
	 * 
	 * @param requirements
	 *            list of requirements of the resourceBean
	 * @param resource
	 *            resource
	 */
	private void addRequirementWirings(List<Trequirement> requirements, Resource resource) {
		
		Requirement[] reqs  = resource.getRequirements("package");
		for(Requirement req: reqs) {

			Trequirement newReqBean = new Trequirement();
			List<Object> atrDirReq = newReqBean.getDirectiveOrAttributeOrRequirement();
			newReqBean.setNamespace(OSGI_WIRING_NAME);

			Tdirective dir = new Tdirective();
			dir.setName("filter");
			dir.setValue(req.getFilter());
			atrDirReq.add(dir);
			
			String name = null;
			try {
				FilterParser filterParser = new FilterParser();
				
				String[] parsedFilter = filterParser.parseFilter(req.getFilter());
				name = parsedFilter[0];
				addAttribute(atrDirReq, "name", null, name, null);
				
				for(int i = 1; i < parsedFilter.length; i+=2){
					addAttribute(atrDirReq, "version", null ,parsedFilter[i], parsedFilter[i+1]);
				}
				
			} catch (RuntimeException e) {
				log.warn("Exception during parsing wiring requirement filter.");
			}
			
			
			requirements.add(newReqBean);

		}
	}
	
	
	
	/**
	 * Convert {@link Resource} to {@link ResourceBean}.
	 * @param resource resource
	 * @param include what part of metadata should be included
	 * @param ui contextual info about URI
	 * @return converted resource
	 */
	public Tresource convertResource(Resource resource, IncludeMetadata include, UriInfo ui) {
		
		Tresource newResource = new Tresource();
		
		newResource.setId(resource.getId());
		
		List<Tcapability> caps = newResource.getCapability();
		List<Trequirement> reqs = newResource.getRequirement();

		//core capabilities
		if(include.shloudIncludeCap(OSGI_IDENTITY_CAP_NAME)) {
			caps.add(prepareOsgiIdentity(resource));
		}
		if(include.shloudIncludeCap(OSGI_CONTENT_CAP_NAME)) {
			caps.add(prepareOsgiContent(resource, ui));
		}
		if(include.shloudIncludeCap(CRCE_IDENTITY_CAP_NAME)) {
			caps.add(prepareCrceIdentity(resource));
		}
			
		
		//wirings
		if(include.shloudIncludeCap(OSGI_WIRING_NAME)) {
			addCapabilityWirings(caps, resource);
		}
		if(include.shloudIncludeReq(OSGI_WIRING_NAME)) {
			addRequirementWirings(reqs, resource);
		}
		
		
		return newResource;
	}
	
	/**
	 * Prepare RepositoryBean, that will contains metadata from array of resources.
	 * @param resources array of resources
	 * @param include what part of metadata should be included
	 * @param ui contextual info about URI
	 * @return Object with metadata from array of resources, that is ready to XML export using JAXB. 
	 */
	public Trepository convertRepository(Resource[] resources, IncludeMetadata include, UriInfo ui) {
 
		
		Trepository repositoryBean = new Trepository();
		List<Tresource> resourceBeans = repositoryBean.getResource();
		
		for(Resource res: resources) {
			resourceBeans.add(convertResource(res, include, ui));
		}

		return repositoryBean;
	}
	
	/**
	 * Get Tresource with unknown crce.status.
	 * This Tresource contains only id and capability crce.identity with 
	 * attribute crce.status, that is "unknown"
	 * @param id the resource id
	 * @return information about unknown resource
	 */
	public Tresource getResourceWithUnknownStatus(String id) {
		Tresource resource = new Tresource();
		
		resource.setId(id);
		
		List<Tcapability> caps = resource.getCapability();		
		
		Tcapability crceIdentity = new Tcapability();
		crceIdentity.setNamespace("crce.identity");
		List<Object> attributes = crceIdentity.getDirectiveOrAttributeOrCapability();		
		addAttribute(attributes, "name", null, resource.getId(), null);
		addAttribute(attributes, "crce.status", null, "unknown", null);
		
		caps.add(crceIdentity);
		
		return resource;
		
	}

}