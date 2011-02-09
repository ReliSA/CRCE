package cz.zcu.kiv.crce.plugin;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public abstract class AbstractPlugin implements Plugin {

    @Override
    public String getPluginId() {
        return getClass().getName();
    }

    @Override
    public int getPluginPriority() {
        return 0;
    }

    @Override
    public String getPluginDescription() {
        if (this instanceof ResourceDAO) {
            return "ResourceDAO plugin implementation";
        }
        if (this instanceof ResourceDAOFactory) {
            return "ResourceDAOFactory plugin implementation";
        }
        return "Unknown plugin: " + getClass().getName();
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

    
    // needed ?
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
        if (this.getPluginPriority() != other.getPluginPriority()) {
            return false;
        }
        return true;
    }

    // needed ?
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.getPluginId() != null ? this.getPluginId().hashCode() : 0);
        hash = 37 * hash + this.getPluginPriority();
        return hash;
    }

}
