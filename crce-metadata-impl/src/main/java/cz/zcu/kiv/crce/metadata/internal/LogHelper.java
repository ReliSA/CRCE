package cz.zcu.kiv.crce.metadata.internal;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.json.MetadataJsonMapper;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@Component(provides = LogHelper.class)
public class LogHelper {

    @ServiceDependency(required = false)
    private MetadataJsonMapper metadataJsonMapper;

    public String toString(Resource resource) {
        if (metadataJsonMapper != null) {
            return metadataJsonMapper.serialize(resource);
        }
        return "ResourceImpl{" + "id=" + resource.getId() + '}';
    }
}
