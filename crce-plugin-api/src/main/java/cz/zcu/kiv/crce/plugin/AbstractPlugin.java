package cz.zcu.kiv.crce.plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * This abstract class implements all methods of <code>Plugin</code> interface.
 * It can be extended of any plugin - it's recommended to keep unified
 * behaviour of plugins.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public abstract class AbstractPlugin implements Plugin, Comparable<Plugin>, ManagedService {

    private volatile BundleContext m_context;
    @SuppressWarnings("UseOfObsoleteCollectionType")
    private Dictionary<String, ?> properties = new java.util.Hashtable<>();
    private String id = this.getClass().getName();
    private Version version = new Version("0.0.0");
    private int priority = 0;
    private List<String> keywords = Collections.emptyList();
    private String description = "Abstract plugin";

    @Override
    public String getPluginId() {
        return id;
    }

    @Override
    public Version getPluginVersion() {
        return version;
    }

    @Override
    public int getPluginPriority() {
        return priority;
    }

    @Override
    public String getPluginDescription() {
        return description;
    }

    @Override
    public List<String> getPluginKeywords() {
        return keywords;
    }

    @Override
    public final int compareTo(Plugin o) {
        int thisVal = this.getPluginPriority();
        int anotherVal = o.getPluginPriority();

        if (thisVal == anotherVal) {
            return this.getPluginId().compareTo(o.getPluginId());
        }

        return (thisVal < anotherVal ? 1 : -1);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractPlugin other = (AbstractPlugin) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if (this.version.compareTo(other.version) != 0) {
            return false;
        }
        return this.priority == other.priority;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 37 * hash + this.version.hashCode();
        hash = 37 * hash + this.priority;
        return hash;
    }

    @SuppressWarnings("unused")
    private void init() {
        if (properties.get(CFG_ID) == null) {
            id = getClass().getName();
        }
        if (properties.get(CFG_VERSION) == null) {
            version = m_context.getBundle().getVersion();
        }
        if (properties.get(CFG_DESCRIPTION) == null) {
            description = "Implementation of: " + getClass().getName();
        }
        if (properties.get(CFG_PRIORITY) == null) {
            priority = 0;
        }
        if (properties.get(CFG_KEYWORDS) == null) {
            keywords = Collections.emptyList();
        }
    }

    @Override
    public void updated(Dictionary<String, ? > properties) throws ConfigurationException {
        if (properties != null) {
            try {
                String value;

                // configure ID
                if ((value = (String) properties.get(CFG_ID)) != null) {
                    if ("".equals(value.trim())) {
                        throw new ConfigurationException(CFG_ID, "Plugin ID can not be empty", null);
                    }
                    id = value;
                }

                // configure VERSION
                if ((value = (String) properties.get(CFG_VERSION)) != null) {
                    try {
                        version = Version.parseVersion(value);
                    } catch (IllegalArgumentException e) {
                        throw new ConfigurationException(CFG_VERSION, "Improperly formated version", e);
                    }
                }

                // configure PRIORITY
                if ((value = (String) properties.get(CFG_PRIORITY)) != null) {
                    try {
                        priority = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        switch (value.trim().toUpperCase()) {
                            case PRIORITY_MAX_VALUE:
                                priority = Integer.MAX_VALUE;
                                break;
                            case PRIORITY_MIN_VALUE:
                                priority = Integer.MIN_VALUE;
                                break;
                            default:
                                throw new ConfigurationException(CFG_PRIORITY, "Priority must be an integer", e);
                        }
                    }
                }

                // configure KEYWORDS
                if ((value = (String) properties.get(CFG_KEYWORDS)) != null) {
                    if ("".equals(value.trim())) {
                        keywords = Collections.emptyList();
                    } else {
                        String[] split = value.split(",");
                        keywords = Arrays.asList(split);
                    }
                }

                // configure DESCRIPTION
                if ((value = (String) properties.get(CFG_DESCRIPTION)) != null) {
                    description = value;
                }

            } finally {
                ServiceReference<EventAdmin> ref = m_context.getServiceReference(EventAdmin.class);
                if (ref != null) {
                    EventAdmin eventAdmin = m_context.getService(ref);

                    Map<String, Object> props = new HashMap<>();

                    props.put(PluginManager.PROPERTY_PLUGIN_ID, id);
                    props.put(PluginManager.PROPERTY_PLUGIN_VERSION, version.toString());
                    props.put(PluginManager.PROPERTY_PLUGIN_PRIORITY, priority);
                    props.put(PluginManager.PROPERTY_PLUGIN_DESCRIPTION, description);
                    props.put(PluginManager.PROPERTY_PLUGIN_KEYWORDS, keywords.toString());

                    Set<String> types = new HashSet<>();

                    for (Class<?> cl = this.getClass(); cl != Object.class; cl = cl.getSuperclass()) {
                        for (Class<?> iface : cl.getInterfaces()) {
                            types.add(iface.getName());
                        }
                    }

                    props.put(PluginManager.PROPERTY_PLUGIN_TYPES, types.toString());

                    eventAdmin.sendEvent(new Event(PluginManager.TOPIC_PLUGIN_CONFIGURED, props));
                }
            }
        }

        this.properties = properties;
    }
}
