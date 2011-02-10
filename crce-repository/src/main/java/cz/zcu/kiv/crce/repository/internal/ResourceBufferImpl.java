package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAO;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAOFactory;
import cz.zcu.kiv.crce.repository.ResourceBuffer;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;
import cz.zcu.kiv.osgi.versionGenerator.exceptions.BundlesIncomparableException;
import cz.zcu.kiv.osgi.versionGenerator.exceptions.VersionGeneratorException;
import cz.zcu.kiv.osgi.versionGenerator.service.VersionService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.service.cm.ConfigurationException;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class ResourceBufferImpl implements ResourceBuffer {
    
    private volatile VersionService m_versionService;

    private int BUFFER_SIZE = 8 * 1024;
    
//    private volatile ResourceDAOFactory m_resourceDAOFactory;   
    private volatile PluginManager m_pluginManager; /* Injected by dependency manager */

    private volatile BundleContext m_context; /* Injected by dependency manager */

    private File m_baseDir;
    private List<Resource> m_resources = new ArrayList<Resource>(); // TODO remove, make repository.xml instead

    private void setUpBaseDir() {
        m_baseDir = m_context.getDataFile("buffer");
        if (!m_baseDir.exists()) {
            m_baseDir.mkdir();
        } else if (!m_baseDir.isDirectory()) {
            m_baseDir.delete();
            m_baseDir.mkdir();
        }

    }

    @Override
    public synchronized Resource put(String name, InputStream artifact) throws IOException {
        if (name == null || artifact == null || "".equals(name)) {
            return null;
        }
        if (m_baseDir == null) {
            setUpBaseDir();
        }
        FileOutputStream output = null;
        File file = null;
        Resource out = null;
        try {
            file = File.createTempFile("res", ".tmp", m_baseDir);
            output = new FileOutputStream(file);
            byte[] buffer = new byte[BUFFER_SIZE];
            for (int count = artifact.read(buffer); count != -1; count = artifact.read(buffer)) {
                output.write(buffer, 0, count);
            }

        } finally {
            if (output != null) {
                output.flush();
                output.close();
            }
        }
        
        System.out.println("\n############################");
        System.out.println(m_pluginManager);
        
        ResourceDAOFactory factory = m_pluginManager.getPlugin(ResourceDAOFactory.class);
        
        ResourceDAO creator;
        if (factory == null) {
            creator = m_pluginManager.getPlugin(ResourceDAO.class);
        } else {
            creator = factory.getResourceDAO();
        }

        Resource resource = creator.getResource(file.toURI());
        
//        m_pluginManager.getActionHandler().onUploaded(out, name);
        
        // TODO move to some plugin
        resource.createCapability("file").setProperty("name", name);
        resource.setSymbolicName(name);
        
        //TODO move versioning to some plugin
        // <editor-fold defaultstate="collapsed" desc="versioning">
        
        if (resource.hasCategory("osgi")) {
            Resource cand = null;
            
            String ext = name.substring(name.lastIndexOf("."));
            Version oldVersion = resource.getVersion();
            
            for (Resource i : m_resources) {
                if (i.getSymbolicName().equals(resource.getSymbolicName())) {
                    if (cand == null || cand.getVersion().compareTo(i.getVersion()) < 0) {
                        cand = i;
                    }
                }
            }
            if (cand != null) {
                try {
                    InputStream in = new FileInputStream(new File(cand.getUri()));
                    File source;
                    try {
                        source = File.createTempFile("source", ".jar");
                        output = new FileOutputStream(source);
                        byte[] buffer = new byte[BUFFER_SIZE];
                        for (int count = in.read(buffer); count != -1; count = in.read(buffer)) {
                            output.write(buffer, 0, count);
                        }
                    } finally {
                        in.close();
                        if (output != null) {
                            output.flush();
                            output.close();
                        }
                    }

                    m_versionService.updateVersion(source, file);
                } catch (VersionGeneratorException ex) {
                    throw new IllegalStateException(ex.getMessage(), ex);
                } catch (BundlesIncomparableException ex) {
                    throw new IllegalStateException(ex.getMessage(), ex);
                }
                resource = creator.getResource(file.toURI());   // reload changed resource

                resource.addCategory("versioned");
            }
            
            Capability[] caps = resource.getCapabilities("file");
            Capability cap = (caps.length == 0 ? resource.createCapability("file") : caps[0]);
            cap.setProperty("original-name", name);
            cap.setProperty("name", resource.getSymbolicName() + "-" + resource.getVersion() + ext);
            
            caps = resource.getCapabilities("bundle");
            cap = (caps.length == 0 ? resource.createCapability("bundle") : caps[0]);
            cap.setProperty("original-version", oldVersion);
            
            
        }
        // </editor-fold>
        
        m_pluginManager.getPlugin(ActionHandler.class).onUpload(resource, name, this);
        
        creator.save(resource);
        m_resources.add(resource);

        out = resource;
        
        return out;
    }

    @Override
    public synchronized void commit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void runTestsOnComponent(Object component) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Resource[] getStoredResources() {
        return m_resources.toArray(new Resource[0]);
    }

    @Override
    public void executeOnStored(Plugin[] plugins) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updated(Dictionary dctnr) throws ConfigurationException {
        // do nothing yet
    }
}
