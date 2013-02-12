package cz.zcu.kiv.crce.rest.internal.rest.bean;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "attribute")
public class AttributeBean 
{
	private String name = null;
	private String type = null;
	private String value = null;

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	

	@XmlAttribute	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	} 
	
	public void print()
	{
		System.out.println("Name: " + name);
		System.out.println("Type: " + type);
		System.out.println("Value: " + value);
	}
}
