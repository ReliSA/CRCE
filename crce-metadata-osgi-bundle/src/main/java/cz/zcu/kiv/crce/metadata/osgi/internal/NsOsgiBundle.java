package cz.zcu.kiv.crce.metadata.osgi.internal;

import org.osgi.framework.Version;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface NsOsgiBundle {

    String NAMESPACE__OSGI_BUNDLE = "osgi.bundle";

    AttributeType<String> ATTRIBUTE__SYMBOLIC_NAME =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.SYMBOLIC_NAME, String.class);

    AttributeType<String> ATTRIBUTE__PRESENTATION_NAME =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.PRESENTATION_NAME, String.class);

    AttributeType<Version> ATTRIBUTE__VERSION =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.VERSION, Version.class);

    AttributeType<String> ATTRIBUTE__MANIFEST_VERSION =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.MANIFEST_VERSION, String.class);

}
