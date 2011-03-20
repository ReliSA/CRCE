package cz.zcu.kiv.crce.plugin;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

/**
 * This abstract class implements all methods of <code>Plugin</code> interface.
 * It can be extended of any plugin - it's recommended to keep unified
 * behaviour of plugins.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public abstract class AbstractPlugin implements Plugin, Comparable<Plugin> {
    
    private volatile BundleContext m_context;

    @Override
    public String getPluginId() {
        return getClass().getName();
    }
    
    @Override
    public Version getPluginVersion() {
        return m_context.getBundle().getVersion();
    }

    @Override
    public int getPluginPriority() {
        return 0;
    }

    @Override
    public String getPluginDescription() {
        return "Implementation of: " + getClass().getName();
    }

    @Override
    public String[] getKeywords() {
        return new String[0];
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
        if ((this.getPluginId() == null) ? (other.getPluginId() != null) : !this.getPluginId().equals(other.getPluginId())) {
            return false;
        }
        if (this.getPluginVersion().compareTo(other.getPluginVersion()) != 0) {
            return false;
        }
        if (this.getPluginPriority() != other.getPluginPriority()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.getPluginId() != null ? this.getPluginId().hashCode() : 0);
        hash = 37 * hash + this.getPluginVersion().hashCode();
        hash = 37 * hash + this.getPluginPriority();
        return hash;
    }

}
