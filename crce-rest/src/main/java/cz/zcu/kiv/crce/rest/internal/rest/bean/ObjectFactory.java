package cz.zcu.kiv.crce.rest.internal.rest.bean;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

	public ObjectFactory() {
	}
	
	public MetadataBean createMetadataBean() {
		return new MetadataBean();
	}
}
