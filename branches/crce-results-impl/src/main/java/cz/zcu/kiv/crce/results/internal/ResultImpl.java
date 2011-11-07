package cz.zcu.kiv.crce.results.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.results.Result;
import java.io.File;
import java.net.URI;
import org.osgi.framework.Version;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class ResultImpl implements Result {

    private URI m_uri;
    private String m_resourceSymbolicName;
    private Version m_resourceVersion;
    private String m_pluginId;
    private Version m_pluginVersion;
    
    public ResultImpl(File file, Resource resource, Plugin plugin) {
        m_uri = file.toURI();
        m_resourceSymbolicName = resource.getSymbolicName();
        m_resourceVersion = resource.getVersion();
        m_pluginId = plugin.getPluginId();
        m_pluginVersion = plugin.getPluginVersion();
    }
    
    @Override
    public URI getResult() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getResourceSymbolicName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Version getResourceVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getPluginId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Version getPluginVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
