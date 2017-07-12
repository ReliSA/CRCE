package cz.zcu.kiv.crce.metadata.internal;

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Entity;
import cz.zcu.kiv.crce.metadata.json.MetadataJsonMapper;

@Component(provides = {LogHelper.class, ManagedService.class}, properties = {
    @org.apache.felix.dm.annotation.api.Property(name = "service.pid", value = "cz.zcu.kiv.crce.metadata")
})
public class LogHelperImpl implements LogHelper, ManagedService {

    private static final Logger logger = LoggerFactory.getLogger(LogHelperImpl.class);

    public static final String CFG__JSON_TO_STRING_ENABLED = "json-to-string.enabled";
    public static final String CFG__JSON_TO_STRING_PRETTY_PRINT = "json-to-string.pretty-print";

    private boolean available = true;
    private boolean prettyPrint = false;

    @ServiceDependency
    private MetadataJsonMapper metadataJsonMapper;

    @Override
    public String toString(Entity resource) {
        return metadataJsonMapper.serialize(resource, prettyPrint);
    }

    @Override
    public boolean available() {
        return available;
    }

    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        if (properties != null) {
            Object value = properties.get(CFG__JSON_TO_STRING_ENABLED);
            available = value == null || !(value instanceof String) || !"false".equalsIgnoreCase(((String) value).trim()); // default true

            value = properties.get(CFG__JSON_TO_STRING_PRETTY_PRINT);
            prettyPrint = value != null && value instanceof String && Boolean.valueOf(((String) value).trim()); // default false

            logger.info("LogHelper configured: enabled={}, pretty-print={}", available, prettyPrint);
        }
    }
}
