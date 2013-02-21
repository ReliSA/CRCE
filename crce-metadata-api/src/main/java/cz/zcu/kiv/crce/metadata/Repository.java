package cz.zcu.kiv.crce.metadata;

import java.net.URI;
import java.util.List;

import javax.annotation.Nonnull;
import org.osgi.framework.InvalidSyntaxException;

/**
 * Represents a set of resources. Repository can be assigned to a physical URI
 * which resources are stored on, but it does not manage physical content.
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
     * Return the resources for this repository.
     * @return 
     */
    @Nonnull
    List<Resource> getResources();

    /**
     * TODO Use other way than OSGi/LDAP filter to get resources.
     * @param filter
     * @return
     * @throws InvalidSyntaxException  
     */
    @Nonnull
    List<Resource> getResources(@Nonnull String filter) throws InvalidSyntaxException;
    
    /**
     * 
     * @param requirements
     * @return 
     */
    @Nonnull
    List<Resource> getResources(@Nonnull List<Requirement> requirements);
    
    /**
     * Return the name of this repository.
     * 
     * TODO PENDING This may be a generic attribute.
     * 
     * @return a non-null name
     */
    @Nonnull
    String getName();

    /**
     * Return the last modification date of this repository
     * 
     * TODO PENDING This may be a generic attribute.
     * 
     * @return the last modification date
     */
    long getLastModified();

    /**
     * TODO
     * @param resource
     * @return 
     */
    boolean contains(@Nonnull Resource resource);

}