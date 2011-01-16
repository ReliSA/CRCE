package cz.zcu.kiv.crce.metadata;

import java.net.URI;

/**
 *
 * @author kalwi
 */
public interface ResourceDAOFactory {
    
    /**
     * Creates a resource DAO with the given base URI. Base URI can specify e.g.
     * base dir of repository in case of directory based DAO or URI to DB table
     * in case of DB based implementation.
     * @param baseUri Base URI for newly created ResourceDAO.
     * @return an instance of <code>ResourceDAO</code>.
     */
    public ResourceDAO getResourceDAO(URI baseUri);

}
