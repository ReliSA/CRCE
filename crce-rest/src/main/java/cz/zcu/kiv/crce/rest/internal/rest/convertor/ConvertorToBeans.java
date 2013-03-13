package cz.zcu.kiv.crce.rest.internal.rest.convertor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		
	private final Logger log = LoggerFactory.getLogger(ConvertorToBeans.class);
	
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
	 */
	private void addAttribute(List<Object> list, String name, String type, String value) {
		
		Tattribute newAttributte = new Tattribute();
		
		if(name != null) newAttributte.setName(name);
		if(type != null) newAttributte.setType(type);
		if(value != null) newAttributte.setValue(value);
		
		list.add(newAttributte);
				
	}
	
	/**
	 * Returns capability with osgi.identity.
	 * @param resource resource
	 * @return capability with osgi.identity
	 */
	private Tcapability prepareOsgiIdentity(Resource resource) {
		
		Tcapability osgiIdentity= new Tcapability();
		osgiIdentity.setNamespace("osgi.identity");
		List<Object> osgiIdentityAttrs = osgiIdentity.getDirectiveOrAttributeOrCapability();
		
		addAttribute(osgiIdentityAttrs, "name", null, resource.getSymbolicName());
		addAttribute(osgiIdentityAttrs, "version", "Version", resource.getVersion().toString());
		
		
		return osgiIdentity;
	}
	

	
	/**
	 * Returns capability with osgi.content
	 * @param resource resource 
	 * @return capability with osgi.content
	 */
	private Tcapability prepareOsgiContent(Resource resource) {
		
		Tcapability osgiContent = new Tcapability();
		osgiContent.setNamespace("osgi.content");
		List<Object> attributes = osgiContent.getDirectiveOrAttributeOrCapability();
		
		addAttribute(attributes, "hash", null, getSHA(resource));
		addAttribute(attributes, "url", null, getURL(resource));
		addAttribute(attributes, "size", "Long", Long.toString(resource.getSize()));
		addAttribute(attributes, "mime", null, "not implemented yet");
		addAttribute(attributes, "crce.original-file-name", null, getFileName(resource));
		
		
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
		
		log.info("Category To string: {}", categoryStr);
		
		return categoryStr;
	}
	
	
	/**
	 * Returns capability with crce.identity
	 * @param resource resource 
	 * @return capability with crce.identity
	 */
	private Tcapability prepareCrceIdentity(Resource resource) {
		
		Tcapability crceIdentity = new Tcapability();
		crceIdentity.setNamespace("crce.identity");
		List<Object> attributes = crceIdentity.getDirectiveOrAttributeOrCapability();
		
		addAttribute(attributes, "name", null, resource.getId());
		addAttribute(attributes, "crce.categories", "List<String>", categoriesToString(resource.getCategories()));
		addAttribute(attributes, "crce.status", null, "stored");
		
		return crceIdentity;
		
	}
	
	

	//TODO : implement filterName filtration
	/**
	 * Add all capabilities with package wiring to the resource
	 * 
	 * @param capabilities
	 *            list of capabilities of the resourceBean
	 * @param resource
	 *            resource
	 * @param filterName
	 *            if is not null, capabilities only with this name should be
	 *            added to the list. If is null, all capabilities should be
	 *            added to the list.
	 */
	private void addCapabilityWirings(List<Tcapability> capabilities, Resource resource, String filterName) {
		Capability[] caps =  resource.getCapabilities("package");
		for (Capability cap : caps) {

			Tcapability newCapBean = new Tcapability();
			List<Object> attributes = newCapBean.getDirectiveOrAttributeOrCapability();
			newCapBean.setNamespace("osgi.wiring.package");

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
	 * Get name of operation from string before character '='.
	 * There are 3 supported operations:
	 * 
	 * <ul>
	 * <li> '>' (full >=) - greater-than
	 * <li> '<' (full <=) - lower than
	 * <li> null (full =) - equal
 	 * </ul>
	 * 
	 * @param operation ">", "<" or null (null mean =)
	 * @return name of operation
	 */
	private String operationToString(String operation) {
		if(operation==null) return "equal";
		else {
			switch (operation) {
			case ">":
				return "greater-than";
			case "<":
				return "lower-than";
			default:
				return null;
			}
			
		}
		
	}
	
	/**
	 * Parse filter in of osgi requirement wirings.
	 * Filter look like this: (&(osgi.wiring.package=cz.zcu.kiv.obcc.example.carpark.arrivals)(version&gt;=1.0.0))
	 * From this string should be parsed name  (string after 'osgi.wiring.package=' of after 'package='),
	 * and version with operation (version 1.0.0, operation greater-than).
	 * Version with operation can be present multiple time
	 * 
	 * @param filter filter string
	 * @return array of string. Name is always first in this array. 
	 * Its followed by version and operation, that can be present more than one (or can't be present).
	 * If parsing failed, empty array is returned.
	 */
	private String[] parseFilter(String filter) {
		//System.out.println("Parse filter : " + filter);
		Pattern filterPattern = Pattern.compile("\\(&(amp;)?\\((osgi.wiring.)?package=\\s*(.+?)\\s*\\)(\\(version\\s*(.+?)\\s*\\))?\\)");
		
		
		Matcher matcher =filterPattern.matcher(filter);
		if (matcher.matches()) {
			String name = matcher.group(3);
			//System.out.println("Parsed name:  " +  name);
			
			try {
				String versions = matcher.group(4);
				versions = versions.replaceAll("&gt;", ">");
				versions = versions.replaceAll("&lt;", "<");
				//System.out.println("Parsing versios: " + versions);
				String[] version = versions.split("[\\(\\)]");
				
				int versioncount = 0;
				for(String ver:version) {
					if(ver.length()>0) {
						//System.out.println("Parsed version: " + ver);
						versioncount++;
					}
				}
				
				String[] result = new String[1+2*versioncount];
				result[0] = name;
				
				int i = 1;
				for(String ver:version) {
					if(ver.length()>0) {
						Pattern versionPattern = Pattern.compile("version(.)?=(.*)");
						Matcher versionMatcher =versionPattern.matcher(ver);
						if(versionMatcher.matches()) {
							String op = versionMatcher.group(1);
							String vers = versionMatcher.group(2);
							result[i++] = vers;
							result[i++] = operationToString(op);
							
							
						} else {
							//System.out.println("Version cant be parsed from filter");
							String[] nameArray = {name};
							return nameArray;
						}
					}
				}
				
				return result;
				
				
			} catch (NullPointerException e) {
				//System.out.println("Parsed filter has no version");
				String[] nameArray = {name};
				return nameArray;
			}
			
		} else {
			log.warn("Osgi wiring requirement filter dont match the pattern and can't be parsed.");
			return new String[0];
		}
	}
	
	//TODO : implement filterName filtration
	/**
	 * Add all requirements with package wiring to the resource
	 * 
	 * @param requirements
	 *            list of requirements of the resourceBean
	 * @param resource
	 *            resource
	 * @param filterName
	 *            if is not null, requirements only with this name should be
	 *            added to the list. If is null, all requirements should be
	 *            added to the list.
	 */
	private void addRequirementWirings(List<Trequirement> requirements, Resource resource, String filterName) {
		
		Requirement[] reqs  = resource.getRequirements("package");
		for(Requirement req: reqs) {

			Trequirement newReqBean = new Trequirement();
			List<Object> atrDirReq = newReqBean.getDirectiveOrAttributeOrRequirement();
			newReqBean.setNamespace("osgi.wiring.package");

			Tdirective dir = new Tdirective();
			dir.setName("filter");
			dir.setValue(req.getFilter());
			atrDirReq.add(dir);

			requirements.add(newReqBean);
			
			try {
				String[] parsedFilter = parseFilter(req.getFilter());
				addAttribute(atrDirReq, "name", null, parsedFilter[0]);
				
				for(int i = 1; i < parsedFilter.length; i+=2){
					addAttribute(atrDirReq, "version", parsedFilter[i+1], parsedFilter[i]);
				}
				
			} catch (Exception e) {
				log.warn("Exception during parsing wiring requirement filter.");
			}

		}
	}
	
	
	
	/**
	 * Convert {@link Resource} to {@link ResourceBean}.
	 * @param resource resource
	 * @return converted resource
	 */
	public Tresource convertResource(Resource resource, IncludeMetadata include) {
		
		Tresource newResource = new Tresource();
		
		newResource.setId(resource.getId());
		
		List<Tcapability> caps = newResource.getCapability();
		List<Trequirement> reqs = newResource.getRequirement();

		if(include.isIncludeCore()) {
			caps.add(prepareOsgiIdentity(resource));
			caps.add(prepareOsgiContent(resource));
			caps.add(prepareCrceIdentity(resource));
		}
		
		if(include.isIncludeCaps()) {
			addCapabilityWirings(caps, resource, include.getIncludeCapseByName());
		}
		if(include.isIncludeReqs()) {
			addRequirementWirings(reqs, resource, include.getIncludeReqsByName());
		}
		
		
		return newResource;
	}
	
	/**
	 * Prepare RepositoryBean, that will contains metadata from array of resources.
	 * @param resources array of resources
	 * @return Object with metadata from array of resources, that is ready to XML export using JAXB. 
	 */
	public Trepository convertRepository(Resource[] resources, IncludeMetadata include) {
 
		
		Trepository repositoryBean = new Trepository();
		List<Tresource> resourceBeans = repositoryBean.getResource();
		
		for(Resource res: resources) {
			resourceBeans.add(convertResource(res, include));
		}

		return repositoryBean;
	}
	
	/**
	 * Get Tresource with information about deleted resource.
	 * This Tresource contains only id and capability crce.identity with 
	 * attribute crce.status, that is deleted
	 * @param id the resource id
	 * @return information about deleted resource
	 */
	public Tresource getDeletedResource(String id) {
		Tresource resource = new Tresource();
		
		resource.setId(id);
		
		List<Tcapability> caps = resource.getCapability();		
		
		Tcapability crceIdentity = new Tcapability();
		crceIdentity.setNamespace("crce.identity");
		List<Object> attributes = crceIdentity.getDirectiveOrAttributeOrCapability();		
		addAttribute(attributes, "name", null, resource.getId());
		addAttribute(attributes, "crce.status", null, "deleted");
		
		caps.add(crceIdentity);
		
		return resource;
		
	}

}
