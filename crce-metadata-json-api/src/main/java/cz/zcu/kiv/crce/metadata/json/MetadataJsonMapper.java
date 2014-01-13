package cz.zcu.kiv.crce.metadata.json;

import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface MetadataJsonMapper {

    String serialize(Resource resource);

    Resource deserialize(String json);
}
