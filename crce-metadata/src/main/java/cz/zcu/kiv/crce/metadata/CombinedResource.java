package cz.zcu.kiv.crce.metadata;

/**
 *
 * @author kalwi
 */
public interface CombinedResource extends Resource {

    Resource getStaticResource();

    Resource getWritableResource();
}
