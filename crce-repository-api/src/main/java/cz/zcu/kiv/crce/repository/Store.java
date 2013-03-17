package cz.zcu.kiv.crce.repository;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.plugins.Executable;

/**
 * This interface defines permanent store for artifacts.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Store {

    /**
     * Puts resource to the <code>Store</code>.
     *
     * @param resource
     * @return
     * @throws IOException
     * @throws RevokedArtifactException
     */
    Resource put(@Nonnull Resource resource) throws IOException, RevokedArtifactException;

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
    boolean remove(@Nonnull Resource resource) throws IOException;

    /**
     * Returns resources stored in buffer.
     * @return array of resources.
     */
    @Nonnull
    Repository getRepository();

    /**
     * TODO analyze requirements for this method:
     * - add an executable plugin
     * @param resources
     * @param executable
     * @param properties
     */
    void execute(@Nonnull List<Resource> resources, @Nonnull Executable executable, @CheckForNull Properties properties);
}
