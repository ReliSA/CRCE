package cz.zcu.kiv.crce.plugin;

import java.util.List;

import javax.annotation.Nonnull;
import org.osgi.framework.Version;

/**
 * Common plugin2 interface. Every plugin2 must implement it.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Plugin extends Comparable<Plugin> {

    /**
     * Configuration property name for plugin2 ID.
     * <p>The value should follow the rules for bundle symbolic name.
     */
    String CFG_ID = "plugin2.id";
    /**
     * Configuration property name for plugin2 version.
     * <p>The value must follow the rules for OSGi version.
     */
    String CFG_VERSION = "plugin2.version";
    /**
     * Configuration property name for plugin2 priority.
     * <p>The value must be an integer or <code>PRIORITY_MAX_VALUE</code> and
     * <code>PRIORITY_MIN_VALUE</code> constants.
     */
    String CFG_PRIORITY = "plugin2.priority";
    /**
     * Configuration property name for plugin2 keywords.
     * <p>The value must be a string or list of strings separated by comma.
     */
    String CFG_KEYWORDS = "plugin2.keywords";
    /**
     * Configuration property name for plugin2 description.
     * <p>The value should be a human readable description including spaces and
     * punctuation.
     */
    String CFG_DESCRIPTION = "plugin2.description";

    /**
     * This value can be set to a plugin2 priority configuration property as
     * a maximal possible value.
     */
    String PRIORITY_MAX_VALUE = "MAX";
    /**
     * This value can be set to a plugin2 priority configuration property as
     * a minimal possible value.
     */
    String PRIORITY_MIN_VALUE = "MIN";

    /**
     * Returns the plugin2 unique identification.
     *
     * @return the plugin2 indentification.
     */
    @Nonnull
    String getPluginId();

    /**
     * Returns the version of plugin2.
     *
     * @return the version of plugin2.
     */
    @Nonnull
    Version getPluginVersion();

    /**
     * Returns plugin2 priority. If more plugins of the same type are present,
     * the one with the highest priority will be used.
     *
     * @return the priority of plugin2.
     */
    int getPluginPriority();

    /**
     * Returns the human-readable description of plugin2.
     *
     * @return the human-readable description of plugin2.
     */
    @Nonnull
    String getPluginDescription();

    /**
     * Returns an optional array of plugin2 keywords or zero-length array if no
     * keyword is specified.
     *
     * <p>Keywords can be used to more precise specifying of plugin2(s) returned
     * by <code>PluginManager</code>.
     *
     * @return an array of keywords.
     */
    @Nonnull
    List<String> getPluginKeywords();
}
