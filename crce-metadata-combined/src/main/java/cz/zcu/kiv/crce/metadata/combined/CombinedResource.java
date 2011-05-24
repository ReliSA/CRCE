package cz.zcu.kiv.crce.metadata.combined;

import cz.zcu.kiv.crce.metadata.Resource;

/**
 * Provides an interface for combined <code>Resource </code>implementation which
 * wraps around two other implementations of the same interface, typicaly static
 * and writable.
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public interface CombinedResource extends Resource {

    /**
     * Returns static (read-only) Resource.
     * @return 
     */
    Resource getStaticResource();

    /**
     * Returns writable Resource.
     * @return 
     */
    Resource getWritableResource();
}
