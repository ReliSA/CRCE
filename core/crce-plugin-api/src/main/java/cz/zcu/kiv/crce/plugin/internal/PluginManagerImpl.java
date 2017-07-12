package cz.zcu.kiv.crce.plugin.internal;

import static cz.zcu.kiv.crce.plugin.PluginManager.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;

/**
 * Implementation of <code>PluginManager</code>.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class PluginManagerImpl implements PluginManager {

    private static final Logger logger = LoggerFactory.getLogger(PluginManagerImpl.class);

    private static final String ROOT_CLASS = "java.lang.Object";
    private volatile EventAdmin eventAdmin; /* injected by dependency manager */

    /**
     * Map of maps containing sets of plugins associated with a keyword.
     * The key to outer map is a plugin2 type, value is inner map.
     * The key to inner map is a keyword associated to a set of plugins.
     */
    private final Map<Class<?>, Map<String, Set<? extends Plugin>>> plugins = new HashMap<>();

    @Override
    public synchronized List<Plugin> getPlugins() {
        return getPlugins(Plugin.class);
    }

    @Override
    public <T extends Plugin> List<T> getPlugins(Class<T> type) {
        return getPluginsIncl(type, new String[]{null});
    }

    @Override
    public <T extends Plugin> List<T> getPlugins(Class<T> type, String keyword) {
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
    private synchronized <T extends Plugin> List<T> getPluginsIncl(Class<T> type, String... keywords) {
        if (keywords == null || keywords.length == 0) {
            keywords = new String[]{null};
        }
        Map<String, Set<? extends Plugin>> map = plugins.get(type);
        if (map == null) {
            return Collections.emptyList();
        }

        Set<T> out = new TreeSet<>();
        for (String keyword : keywords) {
            Set<T> set = (Set<T>) map.get(keyword);
            if (set != null) {
                out.addAll(set);
            }
        }
//        List<Plugin> result =
//        T[] array = (T[]) java.lang.reflect.Array.newInstance(type, out.size());
//        T[] sorted = out.toArray(array);
//        Arrays.sort(sorted);
        return new ArrayList<>(out);
    }

    @Override
    public <T extends Plugin> T getPlugin(Class<T> type) {
        return getPlugin(type, null);
    }

    @Override
    public synchronized <T extends Plugin> T getPlugin(Class<T> type, String keyword) {
        Map<String, Set<? extends Plugin>> map = plugins.get(type);
        if (map == null) {
            return getProxy(type);
        }
        Set<? extends Plugin> set = map.get(keyword);
        if (set == null || set.isEmpty()) {
            return getProxy(type);
        }
        Set<Plugin> sorted = new TreeSet<>(set);
        @SuppressWarnings("unchecked")
        T t = (T) sorted.iterator().next();
        return t;
    }

    @SuppressWarnings("unchecked")
    private <T> T getProxy(Class<T> type) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }

        });
    }

    /**
     * Callback method called on adding new plugin2.
     * @param plugin
     */
    synchronized void register(Plugin plugin) {
        Set<String> types = new HashSet<>();
        addRecursive(plugin.getClass(), plugin, types);

        logger.info("Plugin registered: {}", plugin.getPluginId());

        Map<String, Object> properties = new HashMap<>();
        properties.put(PROPERTY_PLUGIN_ID, plugin.getPluginId());
        properties.put(PROPERTY_PLUGIN_VERSION, plugin.getPluginVersion().toString());
        properties.put(PROPERTY_PLUGIN_PRIORITY, plugin.getPluginPriority());
        properties.put(PROPERTY_PLUGIN_DESCRIPTION, plugin.getPluginDescription());
        properties.put(PROPERTY_PLUGIN_KEYWORDS, plugin.getPluginKeywords().toString());
        properties.put(PROPERTY_PLUGIN_TYPES, types.toString());

        eventAdmin.sendEvent(new Event(TOPIC_PLUGIN_REGISTERED, properties));
    }

    /**
     * Callback method called on removing existing plugin2.
     * @param plugin
     */
    synchronized void unregister(Plugin plugin) {
        Set<String> types = new HashSet<>();
        removeRecursive(plugin.getClass(), plugin, types);

        logger.info("Plugin unregistered: {}", plugin.getPluginId());

        Map<String, Object> properties = new HashMap<>();
        properties.put(PROPERTY_PLUGIN_ID, plugin.getPluginId());
        properties.put(PROPERTY_PLUGIN_VERSION, plugin.getPluginVersion().toString());
        properties.put(PROPERTY_PLUGIN_PRIORITY, plugin.getPluginPriority());
        properties.put(PROPERTY_PLUGIN_DESCRIPTION, plugin.getPluginDescription());
        properties.put(PROPERTY_PLUGIN_KEYWORDS, plugin.getPluginKeywords().toString());
        properties.put(PROPERTY_PLUGIN_TYPES, types.toString());

        eventAdmin.sendEvent(new Event(TOPIC_PLUGIN_REGISTERED, properties));
    }

    private void removeRecursive(Class<?> clazz, Plugin plugin, Set<String> types) {
        if (ROOT_CLASS.equals(clazz.getName())) {
            return;
        }
        for (Class<?> iface : clazz.getInterfaces()) {
            types.add(clazz.getName());
            Map<String, Set<? extends Plugin>> map = plugins.get(iface);
            if (map == null) {
                continue;
            }
            Set<? extends Plugin> set;
            if (plugin.getPluginKeywords().isEmpty()) {
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

    private void addRecursive(Class<?> clazz, Plugin plugin, Set<String> types) {
        if (ROOT_CLASS.equals(clazz.getName())) {
            return;
        }
        for (Class<?> iface : clazz.getInterfaces()) {
            types.add(iface.getName());
            if (plugin.getPluginKeywords().isEmpty()) {
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

    @SuppressWarnings("unchecked")
    private void add(Class<?> iface, Plugin plugin, String keyword) {
        Map<String, Set<? extends Plugin>> map = plugins.get(iface);
        if (map == null) {
            map = new HashMap<>();
            plugins.put(iface, map);
        }
        Set<? extends Plugin> set = map.get(keyword);
        if (set == null) {
            set = new HashSet<>();
            map.put(keyword, set);
        }
        ((Set<Plugin>) set).add(plugin);
    }

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Plugins:\n");
        for (Class<?> clazz : plugins.keySet()) {
            sb.append("  ").append(clazz.getName()).append(":\n");
            Map<String, Set<? extends Plugin>> map = plugins.get(clazz);
            for (Map.Entry<String, Set<? extends Plugin>> entry : map.entrySet()) {
                String keyword = entry.getKey();
                sb.append("    ").append(keyword == null ? "[null]" : (NO_KEYWORDS.equals(keyword)) ? "[none]" : keyword).append(":\n");
                for (Plugin plugin : entry.getValue()) {
                    sb.append("      ").append(plugin.getPluginId()).append("\n");
                }
            }
        }

        return sb.toString();
    }
}
