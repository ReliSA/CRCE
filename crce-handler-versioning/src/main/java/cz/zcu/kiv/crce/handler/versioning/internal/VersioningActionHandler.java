package cz.zcu.kiv.crce.handler.versioning.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAO;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAOFactory;
import cz.zcu.kiv.osgi.versionGenerator.exceptions.BundlesIncomparableException;
import cz.zcu.kiv.osgi.versionGenerator.exceptions.VersionGeneratorException;
import cz.zcu.kiv.osgi.versionGenerator.service.VersionService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.osgi.framework.Version;
import org.osgi.service.log.LogService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class VersioningActionHandler extends AbstractActionHandler implements ActionHandler {

    private volatile VersionService m_versionService; /* injected by dependency manager */
    private volatile PluginManager m_pluginManager; /* injected by dependency manager */
    private volatile LogService m_log; /* injected by dependency manager */
    
    
    private int BUFFER_SIZE = 8 * 1024;
    
    @Override
    public Resource onUploadToBuffer(Resource resource, Buffer buffer, String name) {
        if (resource.hasCategory("osgi") && !resource.hasCategory("versioned")) {
            Resource cand = null;
            
            String ext = name.substring(name.lastIndexOf("."));
            Version oldVersion = resource.getVersion();
            
            // TODO zmenit na neco inteligentnejsiho (buffer.getInnerRepository().get(filter))
            for (Resource i : buffer.getRepository().getResources()) {
                if (i.getSymbolicName().equals(resource.getSymbolicName())) {
                    if (cand == null || cand.getVersion().compareTo(i.getVersion()) < 0) {
                        cand = i;
                    }
                }
            }
            if (cand != null) {
                try {
                    InputStream in = new FileInputStream(new File(cand.getUri()));
                    OutputStream output = null;
                    File source;
                    try {
                        source = File.createTempFile("source", ".jar");
                        output = new FileOutputStream(source);
                        byte[] readBuffer = new byte[BUFFER_SIZE];
                        for (int count = in.read(readBuffer); count != -1; count = in.read(readBuffer)) {
                            output.write(readBuffer, 0, count);
                        }
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            // nothing to do
                        }
                        if (output != null) {
                            try {
                                output.flush();
                                output.close();
                            } catch (IOException e) {
                                // nothing to do
                            }
                        }
                    }

                    m_versionService.updateVersion(source, new File(resource.getUri()));
                } catch (IOException ex) {
                    m_log.log(LogService.LOG_ERROR, "Could not update version due to I/O error", ex);
                } catch (VersionGeneratorException ex) {
                    m_log.log(LogService.LOG_ERROR, "Could not update version due to VersionGeneratorException", ex);
                } catch (BundlesIncomparableException ex) {
                    m_log.log(LogService.LOG_ERROR, "Could not update version (incomparable bundles)", ex);
                }
                
                ResourceDAO creator = m_pluginManager.getPlugin(ResourceDAOFactory.class).getResourceDAO();
                
                try {
                    resource = creator.getResource(resource.getUri());   // reload changed resource
                } catch (IOException ex) {
                    m_log.log(LogService.LOG_ERROR, "Could not reload changed resource", ex);
                }
            }
            resource.addCategory("versioned");
            
            Capability[] caps = resource.getCapabilities("file");
            Capability cap = (caps.length == 0 ? resource.createCapability("file") : caps[0]);
            cap.setProperty("original-name", name);
            cap.setProperty("name", resource.getSymbolicName() + "-" + resource.getVersion() + ext);
            
            caps = resource.getCapabilities("bundle");
            cap = (caps.length == 0 ? resource.createCapability("bundle") : caps[0]);
            cap.setProperty("original-version", oldVersion);
            
            
        }
        return resource;
    }

    @Override
    public boolean isModifying() {
        return true;
    }
}
