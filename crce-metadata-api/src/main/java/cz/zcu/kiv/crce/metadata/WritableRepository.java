package cz.zcu.kiv.crce.metadata;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
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

}
