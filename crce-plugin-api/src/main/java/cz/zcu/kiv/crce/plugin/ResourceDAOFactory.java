package cz.zcu.kiv.crce.plugin;

/**
 * Plugin implementing this interface creates an instance of implementation of
 * <code>ResourceDAO</code>. It's useful when <code>ResourceDAO</code> could not
 * be instantiated automaticaly by dependency manager using public parameterless
 * constructor.
 * 
 * <p> Typical usage of <code>ResourceDAOFactory</code> plugin is in case of
 * in-time-changing conditions to create an instance of <code>ResourceDAO</code>.
 * It may for example enclose or wrap other instances of another implementations
 * of <code>ResourceDAO</code> which may be meanwhile removed - in this case
 * this factory ensures that new instance of <code>ResourceDAO</code> will
 * reflect current state of plugins registered in plugin management.
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
     * @return an instance of <code>ResourceDAO</code>.
     * @see java.net.URI java.net.URI
     */
    public ResourceDAO getResourceDAO();

}
