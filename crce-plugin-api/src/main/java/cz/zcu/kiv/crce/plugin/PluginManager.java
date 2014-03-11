package cz.zcu.kiv.crce.plugin;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Plugin manager is responsible to register, unregister and provide plugins.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface PluginManager {

    String NO_KEYWORDS = "";

    String TOPIC_PLUGIN_REGISTERED = PluginManager.class.getName().replace(".", "/") + "/REGISTERED";
    String TOPIC_PLUGIN_UNREGISTERED = PluginManager.class.getName().replace(".", "/") + "/UNREGISTERED";
    String TOPIC_PLUGIN_CONFIGURED = PluginManager.class.getName().replace(".", "/") + "/CONFIGURED";

    String PROPERTY_PLUGIN_ID = "id";
    String PROPERTY_PLUGIN_VERSION = "version";
    String PROPERTY_PLUGIN_PRIORITY = "priority";
    String PROPERTY_PLUGIN_DESCRIPTION = "description";
    String PROPERTY_PLUGIN_KEYWORDS = "keywords";
    String PROPERTY_PLUGIN_TYPES = "types";

    /**
     * Returns all registered plugins ordered by their priority.
     *
     * @return an array containing all registered plugins.
     */
    @Nonnull
    List<Plugin> getPlugins();

    /**
     * Returns all registered instances of plugins with the specified type.
     *
     * <p>Returned array is sort by plugins priority.
     *
     * <p>Example usage:
     * <blockquote>
     * <pre>
     * PluginManager pm = ... // get instance
     * ResourceDAO[] daos = pm.getPlugins(ResourceDAO.class);
     * </pre>
     * </blockquote>
     *
     * @param <T>
     * @param type the <code>Class</code> object representing the type of
     * plugins in returned array.
     * @return the array with plugins of given type.
     */
    @Nonnull
    <T extends Plugin> List<T> getPlugins(Class<T> type);


        // TODO is the following necessary? should be inclusive or exclusive regarding keywords?
//    /**
//     * Returns all registered instances of plugins with the specified type and
//     * keywords.
//     *
//     * <p>Returned array is sort by plugins' priority and contains plugins
//     * registered with specified keywords only. If keywords are <code>null</code>
//     * or are not specified, then all plugins of given type are returned.
//     * If keyword is a zero-length string, then all plugins of given type with
//     * no keyword are returned.
//     *
//     * @param <T>
//     * @param type the <code>Class</code> object representing the type of
//     * plugins in returned array.
//     * @param keywords an optional array of keywords specifying the selection
//     * of plugins.
//     * @return the array with plugins of given type and keywords.
//     */
//    <T> T[] getPlugins(Class<T> type, String... keywords);

    /**
     * Returns all registered instances of plugins with the specified type and
     * keyword.
     *
     * <p>Returned array is sort by plugins' priority and contains plugins
     * registered with specified keyword only. If keyword is <code>null</code>,
     * then <b>all</b> plugins of given type are returned.
     * If keyword is a <code>NO_KEYWORDS</code> string, then all plugins of given
     * type with <b>no keyword</b> are returned.
     *
     * @param <T>
     * @param type the <code>Class</code> object representing the type of
     * plugins in returned array.
     * @param keyword a keyword specifying the selection of plugins.
     * @return the array with plugins of specified type and with given keyword.
     */
    @Nonnull
    <T extends Plugin> List<T> getPlugins(Class<T> type, @Nullable String keyword);

    /**
     * Returns an instance of preferred plugin implementation of specified type.
     *
     * <p> If more implementations of plugin with given type are present,
     * then the one with the highest priority is returned.
     *
     * @param <T>
     * @param type the <code>Class</code> object representing the type of
     * returned plugin.
     * @return the instance of plugin.
     */
    @Nonnull
    <T extends Plugin> T getPlugin(Class<T> type);

    /**
     * Returns an instance of preferred plugin implementation of specified type
     * and keyword.
     *
     * <p> If more implementations of plugin with given type and keyword are
     * present, then the one with the highest priority is returned.
     *
     * @param <T>
     * @param type the <code>Class</code> object representing the type of
     * returned plugin.
     * @param keyword a keyword specifying the selection of plugins.
     * @return the instance of plugin.
     */
    @Nonnull
    <T extends Plugin> T getPlugin(Class<T> type, @Nullable String keyword);
}
