package cz.zcu.kiv.crce.metadata.json;

import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.metadata.Entity;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface MetadataJsonMapper {

    String serialize(Entity entity);

    String serialize(Entity entity, boolean prettyPrint);

    Resource deserialize(String json);
}
