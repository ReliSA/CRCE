package cz.zcu.kiv.crce.rest.internal.rest.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "repository")
public class RepositoryBean {
	
	public static final String CRCE_NAMESPACE = "TBD-CRCE-METADATA-XSD-URI";
	public static final String OSGI_NAMESPACE = "http://www.osgi.org/xmlns/repository/v1.0.0";
	
	private List<ResourceBean> resources = new ArrayList<ResourceBean>();

	
	@XmlElement(name = "resource")
	public List<ResourceBean> getResources() {
		return resources;
	}

	public void setResources(List<ResourceBean> resources) {
		this.resources = resources;
	}
	
	
}
