package cz.zcu.kiv.crce.rest.internal.rest.bean;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "repository")
public class RepositoryBean {
	private ArrayList<ResourceBean> resources = new ArrayList<ResourceBean>();

	
	@XmlElement(name = "resource")
	public ArrayList<ResourceBean> getResources() {
		return resources;
	}

	public void setResources(ArrayList<ResourceBean> resources) {
		this.resources = resources;
	}
	
	
}
