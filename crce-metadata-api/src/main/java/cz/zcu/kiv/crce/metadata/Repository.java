package cz.zcu.kiv.crce.metadata;

import java.net.URI;
import org.osgi.framework.InvalidSyntaxException;

/**
 * Represents a set of resources. Repository can be assigned to a physical URI
 * which resources are stored on, but it does not manage physical content.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
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
     * 
     * @param filter
     * @return
     * @throws InvalidSyntaxException  
     */
    Resource[] getResources(String filter) throws InvalidSyntaxException;
    
    /**
     * 
     * @param requirements
     * @return 
     */
    Resource[] getResources(Requirement[] requirements);
    
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
    boolean contains(Resource resource);

}