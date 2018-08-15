package cz.zcu.kiv.crce.webui.internal.custom;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class Plugin implements Serializable {

    private static final long serialVersionUID = 7452244491708601610L;

    private final String pluginId;
    private final String pluginDescription;
    private final String pluginPriority;
    private final String pluginVersion;
    private final List<String> pluginKeywords;

    public Plugin(cz.zcu.kiv.crce.plugin.Plugin plugin) {
        pluginId = plugin.getPluginId();
        pluginDescription = plugin.getPluginDescription();
        pluginPriority = String.valueOf(plugin.getPluginPriority());
        pluginVersion = plugin.getPluginVersion().toString();
        pluginKeywords = plugin.getPluginKeywords();
    }

    public String getPluginId() {
        return pluginId;
    }

    public String getPluginDescription() {
        return pluginDescription;
    }

    public String getPluginPriority() {
        return pluginPriority;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public List<String> getPluginKeywords() {
        return pluginKeywords;
    }
}
