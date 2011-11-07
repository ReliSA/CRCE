package cz.zcu.kiv.crce.metadata;

/**
 * Extends read-only <code>Repository</code> interface to support of modyfing
 * operations.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public interface WritableRepository extends Repository {

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
    
    /**
     * Removes the resource from repository and returns <code>true</code>, if the
     * repository contained the given resource before removing, <code>false</code>
     * otherwise.
     * 
     * @param resource Resource to be removed from the repository.
     * @return <code>true</code>, if the repository contained the resource
     * before removing.
     */
    public boolean removeResource(Resource resource);

}
