package cz.zcu.kiv.crce.metadata;

/**
 * Object of this interface type can evaluate dependencies between added resources.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public interface Resolver {
    
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
