package cz.zcu.kiv.crce.metadata.osgi.namespace;

import cz.zcu.kiv.crce.metadata.type.Version;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface NsOsgiBundle {

    String NAMESPACE__OSGI_BUNDLE = "osgi.wiring.bundle";

    AttributeType<String> ATTRIBUTE__SYMBOLIC_NAME = new SimpleAttributeType<>("symbolic-name", String.class);

    AttributeType<String> ATTRIBUTE__PRESENTATION_NAME = new SimpleAttributeType<>("presentation-name", String.class);

    AttributeType<Version> ATTRIBUTE__VERSION =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.VERSION, Version.class);

    AttributeType<String> ATTRIBUTE__MANIFEST_VERSION = new SimpleAttributeType<>("manifest-version", String.class);

}
