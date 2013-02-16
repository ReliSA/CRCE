package cz.zcu.kiv.crce.rest.internal.rest.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public abstract class ResourceProperty 
{
	private String namespace;
	private List<AttributeBean> attributes;
	private List<AttributeBean> directives;

	@XmlAttribute
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	 
	@XmlElement(name = "attribute")
	public List<AttributeBean> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<AttributeBean> attributes) {
		this.attributes = attributes;
	}
	
	
	@XmlElement(name = "directive")
	public List<AttributeBean> getDirectives() {
		return directives;
	}

	public void setDirectives(List<AttributeBean> directives) {
		this.directives = directives;
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
