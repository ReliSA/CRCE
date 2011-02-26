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
    boolean Contains(Resource resource);

    /**
     * Tries to add a resource to the repository. If repository already contains
     * a resource with the same symbolic name and version, the given resource
     * is not added and <code>false</code> is be returned. Otherwise, given
     * resource is be added and <code>true</code> is be returned.
     * 
     * @param resource A resource to be added to the repository.
     * @return <code>true</code> if resource was added to the repository.
     */
    public boolean addResource(Resource resource);
    
    /**
     * Tries to add a resource to the repository. If repository already contains
     * a resource with the same symbolic name and version, then the resource
     * will be added only if the value of <code>force</code> is
     * <code>true</code>. If the value of <code>force</code> is
     * <code>false</code>, then the resource will not be added.
     * 
     * <p> This method returns previously contained resource with the same name
     * and version or <code>null</code>, if the repository does not contains
     * such a resource.
     * 
     * @param resource A resource to be added to the repository.
     * @param force Forces to add given resource to the repository if it already
     * contains a resource with the same symbolic name and version.
     * @return Previously contained resource with the same symbolic name and
     * version.
     */
    public Resource addResource(Resource resource, boolean force);
}