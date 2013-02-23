package cz.zcu.kiv.crce.metadata;

/**
 * Object of this interface type can evaluate dependencies between added resources.
 * 
 * TODO PENDING Will be this entity needed in Metadata API? Anyway, all methods are obsolete and need to be redesigned.
 * 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Resolver {

    boolean isSatisfied(Capability capability);

    // 
    
    void add(Resource resource);

    void clean();

    Reason[] getUnsatisfiedRequirements();

    Resource[] getOptionalResources();

    Reason[] getReason(Resource resource);

    Resource[] getResources(Requirement requirement);

    Resource[] getRequiredResources();

    Resource[] getAddedResources();

    boolean resolve();
}
