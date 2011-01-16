package cz.zcu.kiv.crce.metadata.combined;

import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author kalwi
 */
public interface CombinedResource extends Resource {

    Resource getStaticResource();

    Resource getWritableResource();
}
