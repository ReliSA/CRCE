package cz.zcu.kiv.crce.metadata;

/**
 *
 * @author kalwi
 */
public interface ResourceCreatorFactory {
    
    /**
     * Creates resource creator for a resource with given URI.
     * @return resource creator for specified resource.
     */
    public ResourceCreator getResourceCreator();

}
