package cz.zcu.kiv.crce.plugin;

import org.osgi.framework.Version;
import org.osgi.service.cm.ManagedService;

/**
 * Common plugin interface. Every plugin must implement it.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public interface Plugin extends Comparable<Plugin> {

    /**
     * Configuration property name for plugin ID.
     * <p>The value should follow the rules for bundle symbolic name.
     */
    public static final String CFG_ID = "plugin.id";
    /**
     * Configuration property name for plugin version.
     * <p>The value must follow the rules for OSGi version.
     */
    public static final String CFG_VERSION = "plugin.version";
    /**
     * Configuration property name for plugin priority.
     * <p>The value must be an integer or <code>PRIORITY_MAX_VALUE</code> and
     * <code>PRIORITY_MIN_VALUE</code> constants.
     */
    public static final String CFG_PRIORITY = "plugin.priority";
    /**
     * Configuration property name for plugin keywords.
     * <p>The value must be a string or list of strings separated by comma.
     */
    public static final String CFG_KEYWORDS = "plugin.keywords";
    /**
     * Configuration property name for plugin description.
     * <p>The value should be a human readable description including spaces and
     * punctuation.
     */
    public static final String CFG_DESCRIPTION = "plugin.description";
    
    /**
     * This value can be set to a plugin priority configuration property as
     * a maximal possible value.
     */
    public static final String PRIORITY_MAX_VALUE = "MAX";
    /**
     * This value can be set to a plugin priority configuration property as
     * a minimal possible value.
     */
    public static final String PRIORITY_MIN_VALUE = "MIN";

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
    public String[] getPluginKeywords();
}
