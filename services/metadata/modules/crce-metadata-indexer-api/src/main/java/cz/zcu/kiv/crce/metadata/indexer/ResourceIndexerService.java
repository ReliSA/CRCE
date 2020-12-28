package cz.zcu.kiv.crce.metadata.indexer;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.metadata.Resource;

/**
 * Service for indexing of resources metadata.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface ResourceIndexerService {

    /**
     * Indexes the given resource file and returns {@code Resource}
     * containing found metadata.
     *
     * @param resource Resource file to be indexed.
     * @return Metadata resource.
     * @throws IOException If indexing fails on I/O error.
     */
    @Nonnull
    Resource indexResource(File resource) throws IOException;
}
