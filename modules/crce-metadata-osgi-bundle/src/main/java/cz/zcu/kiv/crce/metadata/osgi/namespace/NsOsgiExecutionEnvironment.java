package cz.zcu.kiv.crce.metadata.osgi.namespace;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface NsOsgiExecutionEnvironment {

    String NAMESPACE__OSGI_EXECUTION_ENVIRONMENT = "osgi.ee";

    AttributeType<String> ATTRIBUTE__EXECUTION_ENVIRONMENT =
            new SimpleAttributeType<>("ee", String.class);

}
