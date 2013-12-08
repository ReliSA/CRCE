package cz.zcu.kiv.crce.metadata.osgi.namespace;

import org.osgi.framework.Version;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface NsOsgiPackage {

    String NAMESPACE__OSGI_PACKAGE = "osgi.wiring.package";

    AttributeType<String> ATTRIBUTE__NAME = new SimpleAttributeType<>("name", String.class);

    AttributeType<Version> ATTRIBUTE__VERSION =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.VERSION, Version.class);

}
