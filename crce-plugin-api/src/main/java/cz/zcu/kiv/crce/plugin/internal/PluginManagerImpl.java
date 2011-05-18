package cz.zcu.kiv.crce.plugin.internal;

import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class PluginManagerImpl implements PluginManager {

    private static final String ROOT_CLASS = "java.lang.Object";
    private volatile LogService m_log; /* injected by dependency manager */
    private volatile EventAdmin m_eventAdmin; /* injected by dependency manager */

    /**
     * Map of maps containing sets of plugins associated with a keyword.
     * The key to outer map is a plugin type, value is inner map.
     * The key to inner map is a keyword associated to a set of plugins.
     */
    private final Map<Class, Map<String, Set<Plugin>>> m_plugins = new HashMap<Class, Map<String, Set<Plugin>>>();

    @Override
    public synchronized Plugin[] getPlugins() {
        return getPlugins(Plugin.class);
    }

    @Override
    public <T> T[] getPlugins(Class<T> type) {
        return getPluginsIncl(type, new String[]{null});
    }

    @Override
    public <T> T[] getPlugins(Class<T> type, String keyword) {
        return getPluginsIncl(type, keyword);
    }

    /**
     * Implementation of getPlugins() with inclusive multiple keywords
     * (returning all plugins of specified type with <b>at least one</b> of
     * given keywords).
     * 
     * @param <T>
     * @param type
     * @param keywords
     * @return 
     */
    @SuppressWarnings("unchecked")
    private synchronized <T> T[] getPluginsIncl(Class<T> type, String... keywords) {
        if (keywords == null || keywords.length == 0) {
            keywords = new String[]{null};
        }
        Map<String, Set<Plugin>> map = m_plugins.get(type);
        if (map == null) {
            return (T[]) java.lang.reflect.Array.newInstance(type, 0);
        }

        Set<Plugin> out = new HashSet<Plugin>();
        for (String keyword : keywords) {
            Set<Plugin> set = map.get(keyword);
            if (set != null) {
                out.addAll(set);
            }
        }
        T[] array = (T[]) java.lang.reflect.Array.newInstance(type, out.size());
        T[] sorted = out.toArray(array);
        Arrays.sort(sorted);
        return sorted;
    }

    @Override
    public <T> T getPlugin(Class<T> type) {
        return getPlugin(type, null);
    }

    @Override
    public synchronized <T> T getPlugin(Class<T> type, String keyword) {
        Map<String, Set<Plugin>> map = m_plugins.get(type);
        if (map == null) {
            return null;
        }
        Set<Plugin> set = map.get(keyword);
        if (set == null || set.isEmpty()) {
            return null;
        }
        Set<Plugin> sorted = new TreeSet<Plugin>(set);
        @SuppressWarnings("unchecked")
        T t = (T) sorted.iterator().next();
        return t;
    }

    /**
     * Callback method called on adding new plugin.
     * @param plugin 
     */
    synchronized void register(Plugin plugin) {
        Set<String> types = new HashSet<String>();
        addRecursive(plugin.getClass(), plugin, types);

        m_log.log(LogService.LOG_INFO, "Plugin registered: " + plugin.getPluginId());

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PROPERTY_PLUGIN_ID, plugin.getPluginId());
        properties.put(PROPERTY_PLUGIN_VERSION, plugin.getPluginVersion().toString());
        properties.put(PROPERTY_PLUGIN_PRIORITY, plugin.getPluginPriority());
        properties.put(PROPERTY_PLUGIN_DESCRIPTION, plugin.getPluginDescription());
        properties.put(PROPERTY_PLUGIN_KEYWORDS, plugin.getPluginKeywords().toString());
        properties.put(PROPERTY_PLUGIN_TYPES, types.toString());
        
        m_eventAdmin.sendEvent(new Event(TOPIC_PLUGIN_REGISTERED, properties));
    }

    /**
     * Callback method called on removing existing plugin.
     * @param plugin 
     */
    synchronized void unregister(Plugin plugin) {
        Set<String> types = new HashSet<String>();
        removeRecursive(plugin.getClass(), plugin, types);
        
        m_log.log(LogService.LOG_INFO, "Plugin unregistered: " + plugin.getPluginId());
        
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PROPERTY_PLUGIN_ID, plugin.getPluginId());
        properties.put(PROPERTY_PLUGIN_VERSION, plugin.getPluginVersion().toString());
        properties.put(PROPERTY_PLUGIN_PRIORITY, plugin.getPluginPriority());
        properties.put(PROPERTY_PLUGIN_DESCRIPTION, plugin.getPluginDescription());
        properties.put(PROPERTY_PLUGIN_KEYWORDS, plugin.getPluginKeywords().toString());
        properties.put(PROPERTY_PLUGIN_TYPES, types.toString());

        m_eventAdmin.sendEvent(new Event(TOPIC_PLUGIN_REGISTERED, properties));
    }

    private void removeRecursive(Class clazz, Plugin plugin, Set<String> types) {
        if (ROOT_CLASS.equals(clazz.getName())) {
            return;
        }
        for (Class iface : clazz.getInterfaces()) {
            types.add(clazz.getName());
            Map<String, Set<Plugin>> map = m_plugins.get(iface);
            if (map == null) {
                continue;
            }
            Set<Plugin> set;
            if (plugin.getPluginKeywords().length == 0) {
                if ((set = map.get(NO_KEYWORDS)) != null) {
                    set.remove(plugin);
                }
            } else {
                for (String keyword : plugin.getPluginKeywords()) {
                    if ((set = map.get(keyword)) != null) {
                        set.remove(plugin);
                    }
                }
            }
            if ((set = map.get(null)) != null) {
                set.remove(plugin);
            }
        }
        removeRecursive(clazz.getSuperclass(), plugin, types);
    }

    private void addRecursive(Class clazz, Plugin plugin, Set<String> types) {
        if (ROOT_CLASS.equals(clazz.getName())) {
            return;
        }
        for (Class iface : clazz.getInterfaces()) {
            types.add(iface.getName());
            if (plugin.getPluginKeywords().length == 0) {
                add(iface, plugin, NO_KEYWORDS);
            } else {
                for (String keyword : plugin.getPluginKeywords()) {
                    add(iface, plugin, keyword);
                }
            }
            add(iface, plugin, null);
        }
        addRecursive(clazz.getSuperclass(), plugin, types);
    }

    private void add(Class iface, Plugin plugin, String keyword) {
        Map<String, Set<Plugin>> map = m_plugins.get(iface);
        if (map == null) {
            map = new HashMap<String, Set<Plugin>>();
            m_plugins.put(iface, map);
        }
        Set<Plugin> set = map.get(keyword);
        if (set == null) {
            set = new HashSet<Plugin>();
            map.put(keyword, set);
        }
        set.add(plugin);
    }

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Plugins:\n");
        for (Class clazz : m_plugins.keySet()) {
            sb.append("  ").append(clazz.getName()).append(":\n");
            Map<String, Set<Plugin>> map = m_plugins.get(clazz);
            for (String keyword : map.keySet()) {
                sb.append("    ").append(keyword == null ? "[null]" : (NO_KEYWORDS.equals(keyword)) ? "[none]" : keyword).append(":\n");
                for (Plugin plugin : map.get(keyword)) {
                    sb.append("      ").append(plugin.getPluginId()).append("\n");
                }
            }
        }

        return sb.toString();
    }
}
