package cz.zcu.kiv.crce.metadata.osgi.internal;

import org.osgi.framework.Version;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface NsOsgiFragment {

    String NAMESPACE__OSGI_FRAGMENT = "osgi.fragment";

    AttributeType<String> ATTRIBUTE__HOST =
            new SimpleAttributeType<>("host", String.class);

    AttributeType<Version> ATTRIBUTE__VERSION =
            new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.VERSION, Version.class);
}
