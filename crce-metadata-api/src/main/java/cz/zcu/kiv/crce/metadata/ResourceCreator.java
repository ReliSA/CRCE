package cz.zcu.kiv.crce.metadata;

import java.net.URI;

/**
 * Creates empty OBR entities.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface ResourceCreator {
    
    /**
     * Creates an empty resource.
     * @return An empty resource.
     */
    Resource createResource();
    
    /**
     * Creates an empty capability with given name.
     * @param name Name of created capability.
     * @return An empty capability.
     */
    Capability createCapability(String name);
    
    /**
     * Creates an empty requirement with given name.
     * @param name Name of created requirement.
     * @return An empty capability.
     */
    Requirement createRequirement(String name);

    /**
     * TODO
     * @return 
     */
    Repository createRepository(URI uri);
}
