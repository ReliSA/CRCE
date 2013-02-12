package cz.zcu.kiv.crce.rest.internal.rest.bean;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "resource")
public class ResourceBean 
{ 	
	private ArrayList<CapabilityBean> capabilities = new ArrayList<CapabilityBean>();	
	private ArrayList<RequirementBean> requirements = new ArrayList<RequirementBean>();
	private String crceId;
	
	@XmlElement(name = "capability")
	public ArrayList<CapabilityBean> getCapabilities() 
	{
		return capabilities;
	}

	public void setCapabilities(ArrayList<CapabilityBean> capabilities) 
	{
		this.capabilities = capabilities;
	}
	
	@XmlElement(name = "requirement")
	public ArrayList<RequirementBean> getRequirements() {
		return requirements;
	}

	public void setRequirements(ArrayList<RequirementBean> requirements) {
		this.requirements = requirements;
	}
	
	
	@XmlAttribute(namespace = "TBD-CRCE-METADATA-XSD-URI" , name = "id")
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
