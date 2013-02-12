package cz.zcu.kiv.crce.rest.internal.rest.bean;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public abstract class ResourceProperty 
{
	private String namespace;
	private ArrayList<AttributeBean> attributes;

	@XmlAttribute
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	 
	@XmlElement(name = "attribute")
	public ArrayList<AttributeBean> getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList<AttributeBean> attributes) {
		this.attributes = attributes;
	}
	
	public void print()
	{
		System.out.println("Namespace: " + getNamespace());
		System.out.println("Attributes: ");
		for (AttributeBean atr : attributes) 
		{
			atr.print();
		}
	}
}
