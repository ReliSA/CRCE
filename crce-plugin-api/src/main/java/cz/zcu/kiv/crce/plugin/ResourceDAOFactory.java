package cz.zcu.kiv.crce.plugin;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface ResourceDAOFactory extends Plugin {
    
    /**
     * Creates an instance of <code>ResourceDAO</code> implementation for the
     * specified URI scheme, e.g. <code>file</code>, <code>http</code> or
     * <code>jdbc</code>.
     * 
     * <p> See the {@link java.net.URI java.net.URI} documentation for more
     * details about schemes.
     * 
     * @param scheme 
     * @return an instance of <code>ResourceDAO</code>.
     * @see java.net.URI java.net.URI
     */
    public ResourceDAO getResourceDAO();

}
