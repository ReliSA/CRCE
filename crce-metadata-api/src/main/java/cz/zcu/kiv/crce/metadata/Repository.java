package cz.zcu.kiv.crce.metadata;

import java.net.URI;

/**
 * Represents a repository.
 * 
 */
public interface Repository {

    /**
     * Return the associated URL for the repository.
     * 
     * @return 
     */
    URI getURI();

    /**
     * Return the resources for this repository.
     * @return 
     */
    Resource[] getResources();

    /**
     * Return the name of this repository.
     * 
     * @return a non-null name
     */
    String getName();

    /**
     * Return the last modification date of this repository
     *
     * @return the last modification date
     */
    long getLastModified();

    /**
     * TODO
     * @param resource
     * @return 
     */
    boolean contains(Resource resource);

}