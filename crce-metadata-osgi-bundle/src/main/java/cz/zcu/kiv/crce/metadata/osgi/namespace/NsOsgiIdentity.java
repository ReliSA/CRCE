package cz.zcu.kiv.crce.metadata.osgi.namespace;

import java.util.List;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface NsOsgiIdentity {
    String NAMESPACE__OSGI_IDENTITY = "osgi.identity";

    AttributeType<String> ATTRIBUTE__ID =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.ID, String.class);

    AttributeType<String> ATTRIBUTE__SYMBOLIC_NAME =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.SYMBOLIC_NAME, String.class);

    AttributeType<String> ATTRIBUTE__VERSION =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.VERSION, String.class);

    AttributeType<String> ATTRIBUTE__PRESENTATION_NAME =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.PRESENTATION_NAME, String.class);

    AttributeType<String> ATTRIBUTE__DESCRIPTION =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.DESCRIPTION, String.class);

    AttributeType<String> ATTRIBUTE__LICENSE_URI =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.LICENSE_URI, String.class);

    AttributeType<String> ATTRIBUTE__COPYRIGHT =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.COPYRIGHT, String.class);

    AttributeType<String> ATTRIBUTE__DOCUMENTATION_URI =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.DOCUMENTATION_URI, String.class);

    AttributeType<String> ATTRIBUTE__SOURCE_URI =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.SOURCE_URI, String.class);

    AttributeType<List<String>> ATTRIBUTE__CATEGORY =
            new ListAttributeType(org.apache.felix.bundlerepository.Resource.CATEGORY);
}
