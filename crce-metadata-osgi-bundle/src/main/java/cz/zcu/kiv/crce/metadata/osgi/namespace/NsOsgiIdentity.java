package cz.zcu.kiv.crce.metadata.osgi.namespace;

import java.net.URI;
import java.util.List;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.type.Version;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface NsOsgiIdentity {
    String NAMESPACE__OSGI_IDENTITY = "osgi.identity";

    AttributeType<String> ATTRIBUTE__NAME =
            new SimpleAttributeType<>("name", String.class);

    AttributeType<String> ATTRIBUTE__SYMBOLIC_NAME =
            new SimpleAttributeType<>("symbolic-name", String.class);

    AttributeType<Version> ATTRIBUTE__VERSION =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.VERSION, Version.class);

    AttributeType<String> ATTRIBUTE__PRESENTATION_NAME =
            new SimpleAttributeType<>("presentation-name", String.class);

    AttributeType<String> ATTRIBUTE__DESCRIPTION =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.DESCRIPTION, String.class);

    AttributeType<URI> ATTRIBUTE__LICENSE_URI =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.LICENSE_URI, URI.class);

    AttributeType<String> ATTRIBUTE__COPYRIGHT =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.COPYRIGHT, String.class);

    AttributeType<URI> ATTRIBUTE__DOCUMENTATION_URI =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.DOCUMENTATION_URI, URI.class);

    AttributeType<URI> ATTRIBUTE__SOURCE_URI =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.SOURCE_URI, URI.class);

    AttributeType<List<String>> ATTRIBUTE__CATEGORY =
            new ListAttributeType(org.apache.felix.bundlerepository.Resource.CATEGORY);
}
