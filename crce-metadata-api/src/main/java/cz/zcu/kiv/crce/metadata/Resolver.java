package cz.zcu.kiv.crce.metadata;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface Resolver {
    
    void add(Resource resource);

    Requirement[] getUnsatisfiedRequirements();

    Resource[] getOptionalResources();

    Requirement[] getReason(Resource resource);

    Resource[] getResources(Requirement requirement);

    Resource[] getRequiredResources();

    Resource[] getAddedResources();

    boolean resolve();
}
