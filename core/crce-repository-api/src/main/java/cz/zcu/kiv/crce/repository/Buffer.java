package cz.zcu.kiv.crce.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.metadata.Resource;

/**
 * This interface defines a temporary store for uploading artifacts before putting
 * them to the permanent store.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface Buffer extends Store {

    /**
     * Puts a new resource into resource buffer.
     *
     * @param name A name of uploaded file.
     * @param resource Uploaded resource.
     * @return
     * @throws IOException
     * @throws RefusedArtifactException
     */
    @Nonnull
    Resource put(String name, InputStream resource) throws IOException, RefusedArtifactException;

    /**
     * Puts an existing resource into resource buffer.
     *
     * @param fileName A name of uploaded file.
     * @param fileData A data of uploaded file.
     * @param resource Already preexisting meta-data binded to the uploaded file.
     * @return
     * @throws IOException
     * @throws RefusedArtifactException
     */
    @Nonnull
    Resource put(String fileName, InputStream fileData, Resource resource) throws IOException, RefusedArtifactException;

    /**
     * Commits uploaded resources to the configured store.
     * <p>If <code>move</code> is <code>true</code> then resources will be removed
     * from the buffer. Returned list contains resources successfully committed
     * to the store.
     *
     * @param move if <code>true</code>, committed resources are removed from the
     * buffer.
     * @return list of committed resources.
     * @throws IOException
     */
    @Nonnull
    List<Resource> commit(boolean move) throws IOException;

    /**
     * Commits only the given resources to the configured store.
     * <p>If <code>move</code> is <code>true</code> then resources will be removed
     * from the buffer. Returned list contains resources successfully committed
     * to the store.
     *
     * @param resources
     * @param move
     * @return
     * @throws IOException
     */
    @Nonnull
    List<Resource> commit(List<Resource> resources, boolean move) throws IOException;
}
