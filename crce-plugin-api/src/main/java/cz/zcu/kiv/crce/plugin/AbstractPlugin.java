package cz.zcu.kiv.crce.plugin;

import java.util.Dictionary;
import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

/**
 * This abstract class implements all methods of <code>Plugin</code> interface.
 * It can be extended of any plugin - it's recommended to keep unified
 * behaviour of plugins.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public abstract class AbstractPlugin implements Plugin, Comparable<Plugin>, ManagedService {
    
    private volatile BundleContext m_context;

    private Dictionary properties = new Properties();
    
    private String id = this.getClass().getName();
    private Version version = new Version("0.0.0");
    private int priority = 0;
    private String[] keywords = new String[0];
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
    public String[] getPluginKeywords() {
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
        if (this.priority != other.priority) {
            return false;
        }
        return true;
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
            keywords = new String[0];
        }
    }

    @Override
    public void updated(Dictionary properties) throws ConfigurationException {
        if (properties != null) {
            
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
                    if (PRIORITY_MAX_VALUE.equals(value.trim().toUpperCase())) {
                        priority = Integer.MAX_VALUE;
                    } else if (PRIORITY_MIN_VALUE.equals(value.trim().toUpperCase())) {
                        priority = Integer.MIN_VALUE;
                    } else {
                        throw new ConfigurationException(CFG_PRIORITY, "Priority must be an integer", e);
                    }
                }
            }
            
            // configure KEYWORDS
            if ((value = (String) properties.get(CFG_KEYWORDS)) != null) {
                if ("".equals(value.trim())) {
                    keywords = new String[0];
                } else {
                    String[] split = value.split(",");
                    keywords = new String[split.length];
                    for (int i = 0; i < keywords.length; i++) {
                        keywords[i] = split[i].trim();
                    }
                }
            }
            
            // configure DESCRIPTION
            if ((value = (String) properties.get(CFG_DESCRIPTION)) != null) {
                description = value;
            }

        }
        
        this.properties = properties;
    }
}
