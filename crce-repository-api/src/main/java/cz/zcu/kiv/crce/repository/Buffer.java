package cz.zcu.kiv.crce.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Resource;

/**
 * This interface defines a temporary store for uploading artifacts before putting
 * them to the permanent store.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Buffer extends Store {

    /**
     * Puts a new resource into resource buffer.
     *
     * @param name A name of uploaded file.
     * @param resource Uploaded resource.
     * @return
     * @throws IOException
     * @throws RevokedArtifactException
     */
    Resource put(@Nonnull String name, @Nonnull InputStream resource) throws IOException, RevokedArtifactException;

    /**
     * Commits uploaded resources to the configured store.
     * <p>If <code>move</code> is <code>true</code> then resources will be removed
     * from the buffer. Returned list constains resources successfully commited
     * to the store.
     *
     * @param move if <code>true</code>, commited resources are removed from the
     * buffer.
     * @return list of commited resources.
     * @throws IOException
     */
    @Nonnull
    List<Resource> commit(boolean move) throws IOException;

    /**
     * Commits only the given resources to the configured store.
     * <p>If <code>move</code> is <code>true</code> then resources will be removed
     * from the buffer. Returned list constains resources successfully commited
     * to the store.
     * 
     * @param resources
     * @param move
     * @return
     * @throws IOException
     */
    @Nonnull
    List<Resource> commit(@Nonnull List<Resource> resources, boolean move) throws IOException;
}
