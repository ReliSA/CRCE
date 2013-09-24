package cz.zcu.kiv.crce.metadata;

import java.net.URI;

/**
 * Creates empty OBR entities.
 * 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface ResourceCreator {
    
    /**
     * Creates an empty resource.
     * @return An empty resource.
     */
    Resource createResource();
    
    /**
     * Clone the given resource and return it's deep copy.
     * 
     * <p><i>Don't forget about that the implementation of the given resource
     * could be various, so you don't know whether the given resource is a plain
     * resource or some kind of combined resource (more resources acting as one
     * resource). The clone made by this method (if concrete implementation does
     * not specify it differently) is a plain resource so the inner structure
     * could be another then the structure of the given resource. Use this
     * method only if you are sure that you want to get a plain resource.</i>
     * 
     * @param resource Resource to be clonned.
     * @return deep copy of resource.
     */
    Resource createResource(Resource resource);
    
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
     * @param uri 
     * @return 
     */
    WritableRepository createRepository(URI uri);

    /**
     * Creates a resolver for the given repositories.
     * @param repository
     * @return 
     */
    Resolver createResolver(Repository... repository);
}
