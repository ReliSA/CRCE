package cz.zcu.kiv.crce.crce_webui_vaadin.repository.classes;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.type.Version;

public class ResourceBean{
	private String presentationName;
	private String symbolicName;
	private String version;
	private String categories;
	private Resource resource;
	
	public ResourceBean(String presentationName, String symbolicName, Version version, 
			String[] categories, Resource resource) {
		this.presentationName = presentationName;
		this.symbolicName = symbolicName;
		if(version == null){
			this.version = "unknown-version";
		}
		else{
			this.version = version.toString();
		}
		for(String s : categories){
			this.categories = s + ",";
		}
		if(!(categories.length == 0)){
			this.categories = this.categories.substring(0, this.categories.length() - 1);
		}
		else{
			this.categories = "unknown-categories";
		}
		this.resource = resource;
	}

	public String getPresentationName() {
		return presentationName;
	}
	
	public String getSymbolicName() {
		return symbolicName;
	}

	public Resource getResource() {
		return resource;
	}
	
	public String getVersion() {
		return version;
	}

	public String getCategories() {
		return categories;
	}

	@Override
	public String toString(){
		return presentationName;
	}
	
}
