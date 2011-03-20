package cz.zcu.kiv.crce.plugin;

import org.osgi.framework.Version;

/**
 * Common plugin interface. Every plugin must implement it.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface Plugin extends Comparable<Plugin> {

    /**
     * Returns the plugin unique identification.
     * 
     * @return the plugin indentification.
     */
    public String getPluginId();

    /**
     * Returns the version of plugin.
     * 
     * @return the version of plugin.
     */
    public Version getPluginVersion();

    /**
     * Returns plugin priority. If more plugins of the same type are present,
     * the one with the highest priority will be used.
     * 
     * @return the priority of plugin.
     */
    public int getPluginPriority();

    /**
     * Returns the human-readable description of plugin.
     * 
     * @return the human-readable description of plugin.
     */
    public String getPluginDescription();

    /**
     * Returns an optional array of plugin keywords or zero-length array if no
     * keyword is specified.
     * 
     * <p>Keywords can be used to more precise specifying of plugin(s) returned
     * by <code>PluginManager</code>.
     * 
     * @return an array of keywords.
     */
    public String[] getKeywords();
}
