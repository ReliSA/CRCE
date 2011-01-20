package cz.zcu.kiv.crce.plugin;

/**
 * Common plugin interface. Every plugin must implement it.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface Plugin {

    /*
     * Returns the plugin identification, e.g. class name.
     * @return the plugin indentification.
     */
    public String getPluginId();

    /**
     * Returns plugin priority. If more plugins of the same type are present,
     * the one with the highest priority will be used.
     * @return the priority of plugin.
     */
    public int getPluginPriority();

    /**
     * Returns the human-readable description of plugin.
     * @return the human-readable description of plugin.
     */
    public String getPluginDescription();
}
