package cz.zcu.kiv.crce.metadata;

import java.net.URI;

import javax.annotation.Nonnull;

/**
 * Represents a set of resources. Repository can be assigned to a physical URI
 * which resources are stored on, but it does not manage physical content.
 * 
 * TODO Repository (and WritableRepository) would be either merged with RepositoryDAO and ResourceDAO,
 * or leaved as just a repository descriptor without possibility to manipulate with resources (preferred),
 * or placed to independent API as a service layer.
 * 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Repository {

    /**
     * Return the associated URL for the repository.
     * 
     * TODO PENDING This may be a generic attribute.
     * 
     * @return 
     */
    @Nonnull
    URI getURI();

    /**
     * Return the last modification date of this repository
     * 
     * TODO PENDING This may be a generic attribute.
     * 
     * @return the last modification date
     */
    long getIncrement();
}