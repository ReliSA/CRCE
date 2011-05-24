package cz.zcu.kiv.crce.handler.versioning.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;
import cz.zcu.kiv.osgi.versionGenerator.exceptions.BundlesIncomparableException;
import cz.zcu.kiv.osgi.versionGenerator.exceptions.VersionGeneratorException;
import cz.zcu.kiv.osgi.versionGenerator.service.VersionService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.osgi.service.log.LogService;

/**
 * Implementation of <code>ActionHandler</code> which compares commited OSGi
 * bundle to existing bundles with the same symbolic name and sets a new version
 * based on comparison.
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class VersioningActionHandler extends AbstractActionHandler implements ActionHandler {
    
    private static final String CATEGORY_VERSIONED = "versioned";

    private volatile VersionService m_versionService;   /* injected by dependency manager */
    private volatile PluginManager m_pluginManager;     /* injected by dependency manager */
    private volatile LogService m_log;                  /* injected by dependency manager */

    private int BUFFER_SIZE = 8 * 1024;

    @Override
    public Resource beforePutToStore(Resource resource, Store store) throws RevokedArtifactException {
        if (resource == null || !resource.hasCategory("osgi")) {
            return resource;
        }
        if (!resource.hasCategory(CATEGORY_VERSIONED)) {

            Resource cand = null;

            // TODO improve (by using store.getRepository().getResources(someFilter))
            for (Resource i : store.getRepository().getResources()) {
                if (i.getSymbolicName().equals(resource.getSymbolicName())) {
                    if (cand == null || cand.getVersion().compareTo(i.getVersion()) < 0) {
                        cand = i;
                    }
                }
            }
            String category = null;
            if (cand == null) {
                category = CATEGORY_VERSIONED;
                resource.addCategory("initial-version");
            } else {
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
                    category = CATEGORY_VERSIONED;
                } catch (IOException ex) {
                    m_log.log(LogService.LOG_ERROR, "Could not update version due to I/O error", ex);
                    category = null;
                } catch (VersionGeneratorException ex) {
                    m_log.log(LogService.LOG_WARNING, "Could not update version (VersionGeneratorException): " + ex.getMessage());
                    category = "non-versionable";
                } catch (BundlesIncomparableException ex) {
                    m_log.log(LogService.LOG_WARNING, "Could not update version (incomparable bundles): " + ex.getMessage());
                    category = "non-versionable";
                } catch (Exception e) {
                    m_log.log(LogService.LOG_ERROR, "Could not update version (unknown error)", e);
                    category = null;
                }

                ResourceDAO creator = m_pluginManager.getPlugin(ResourceDAO.class);

                try {
                    resource = creator.getResource(resource.getUri());   // reload changed resource
                } catch (IOException ex) {
                    m_log.log(LogService.LOG_ERROR, "Could not reload changed resource", ex);
                }
            }

            if (category != null) {
                resource.addCategory(category);
            }
        }
        
        return resource;
    }

    @Override
    public Resource onUploadToBuffer(Resource resource, Buffer buffer, String name) {
        if (!resource.hasCategory("osgi")) {
            return resource;
        }
        if (!resource.hasCategory("versioned")) {

            String ext = name.substring(name.lastIndexOf("."));
            
            Capability[] caps = resource.getCapabilities("file");
            Capability cap = (caps.length == 0 ? resource.createCapability("file") : caps[0]);
            cap.setProperty("original-name", name);
            cap.setProperty("name", resource.getSymbolicName() + "-" + resource.getVersion() + ext);

//            caps = resource.getCapabilities("bundle");
//            cap = (caps.length == 0 ? resource.createCapability("bundle") : caps[0]);
            cap.setProperty("original-version", resource.getVersion());

        }
        return resource;
    }

    @Override
    public boolean isExclusive() {
        return true;
    }
}
