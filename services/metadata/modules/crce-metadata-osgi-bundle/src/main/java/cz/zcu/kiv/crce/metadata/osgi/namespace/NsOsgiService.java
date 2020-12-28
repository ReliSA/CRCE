package cz.zcu.kiv.crce.metadata.osgi.namespace;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface NsOsgiService {

    String NAMESPACE__OSGI_SERVICE = "osgi.wiring.service";

    AttributeType<String> ATTRIBUTE__NAME = new SimpleAttributeType<>("name", String.class);

}
