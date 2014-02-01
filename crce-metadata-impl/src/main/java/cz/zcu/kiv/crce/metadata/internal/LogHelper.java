package cz.zcu.kiv.crce.metadata.internal;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.json.MetadataJsonMapper;

/**
 * Wrapper for JSON serializer which allows to have an optional OSGi/DM dependency.
 * <p>If optional package 'cz.zcu.kiv.crce.metadata.json' is not present on classpath,
 * then this class is not loaded. So metadata entities implementations
 * should not call this class directly.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@Component(provides = LogHelper.class)
public class LogHelper {

    @ServiceDependency
    private MetadataJsonMapper metadataJsonMapper;

    public String toString(Resource resource) {
        if (metadataJsonMapper != null) {
            return metadataJsonMapper.serialize(resource);
        }
        return "ResourceImpl{" + "id=" + resource.getId() + '}';
    }
}
