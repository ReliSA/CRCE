package cz.zcu.kiv.crce.rest.internal.rest.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "resource")
public class ResourceBean 
{ 	
	private List<CapabilityBean> capabilities = new ArrayList<CapabilityBean>();	
	private List<RequirementBean> requirements = new ArrayList<RequirementBean>();
	private String crceId;
	
	@XmlElement(name = "capability")
	public List<CapabilityBean> getCapabilities() 
	{
		return capabilities;
	}

	public void setCapabilities(List<CapabilityBean> capabilities) 
	{
		this.capabilities = capabilities;
	}
	
	@XmlElement(name = "requirement")
	public List<RequirementBean> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<RequirementBean> requirements) {
		this.requirements = requirements;
	}
	
	
	@XmlAttribute(namespace = RepositoryBean.CRCE_NAMESPACE , name = "id")
	public String getCrceId() {
		return crceId;
	}

	public void setCrceId(String crceId) {
		this.crceId = crceId;
	}

	@Override
	public String toString()
	{
		System.out.println("Capabilities: ");
		for (CapabilityBean cap : capabilities) 
		{
			cap.print();
		}
		
		System.out.println("Requirements: ");
		for (RequirementBean req : requirements) 
		{
			req.print();
		}
		
		return null;
	}
}
