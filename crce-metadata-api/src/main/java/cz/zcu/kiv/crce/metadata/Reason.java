package cz.zcu.kiv.crce.metadata;

/**
 * A pair of requirement and resource indicating a reason why a resource has
 * been chosen by Resolver.
 *
 * TODO PENDING Will be this entity needed in Metadata API?
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Reason extends Entity {

    Resource getResource();

    Requirement getRequirement();
}
