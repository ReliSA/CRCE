package cz.zcu.kiv.crce.metadata;

/**
 * A pair of requirement and resource indicating a reason why a resource has
 * been chosen by Resolver.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public interface Reason {

    Resource getResource();

    Requirement getRequirement();
}
