package cz.zcu.kiv.crce.plugin;

/**
 *
 * @author kalwi
 */
public interface Plugin {

    public String getPluginId();

    public int getPluginPriority();
    
    public String getName();
    
    
}
