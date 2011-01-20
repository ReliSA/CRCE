package cz.zcu.kiv.crce.plugin;

import java.net.URI;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface ResourceDAOFactory extends Plugin {
    
    /**
     * Creates a resource DAO with the given base URI. Base URI can specify e.g.
     * base dir of repository in case of file-based implementation or URI to database
     * in case of DB-based implementation.
     * @param baseUri Base URI for newly created ResourceDAO.
     * @return an instance of <code>ResourceDAO</code>.
     */
    public ResourceDAO getResourceDAO(URI baseUri);

}
