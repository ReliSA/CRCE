package cz.zcu.kiv.crce.repository;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.plugins.Executable;

/**
 * This interface defines permanent store for artifacts.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface Store {

    /**
     * Puts resource to the <code>Store</code>.
     *
     * @param resource
     * @return
     * @throws IOException
     * @throws RefusedArtifactException
     */
    @Nonnull
    Resource put(Resource resource) throws IOException, RefusedArtifactException;

    /**
     * Removes the resource from this buffer and returns <code>true</code>, if
     * the buffer contained the given resource before removing, <code>false</code>
     * otherwise.
     *
     * @param resource Resource to be removed from this buffer.
     * @return <code>true</code>, if the buffer contained the resource before
     * removing.
     * @throws IOException
     */
    boolean remove(Resource resource) throws IOException;

    /**
     * Returns list of resources stored in the Store.
     * @return list of stored resources.
     */
    @Nonnull
    List<Resource> getResources();

    /**
     *
     * @return resource with the given uid or null
     */
    @Nullable
    Resource getResource(String uid, boolean withDetails);

    @Nonnull
    List<Resource> getResources(Requirement requirement, boolean withDetails);

    /**
     *
     * @param requirement set of requirements
     * @return resources that provide the whole set of requirements
     */
    @Nonnull
    List<Resource> getResources(Set<Requirement> requirement, boolean withDetails);

    /**
     *
     * @param requirement set of requirements
     * @return resources that provide at least a portion of the requirements
     */
    @Nonnull
    List<Resource> getPossibleResources(Set<Requirement> requirement, boolean withDetails);


    /**
     * TODO analyze requirements for this method:
     * - add an executable plugin
     * @param resources
     * @param executable
     * @param properties
     */
    void execute(List<Resource> resources, Executable executable, @CheckForNull Properties properties);
}
