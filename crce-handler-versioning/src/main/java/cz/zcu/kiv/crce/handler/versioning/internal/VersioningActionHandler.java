package cz.zcu.kiv.crce.handler.versioning.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import cz.zcu.kiv.osgi.versionGenerator.service.VersionService;

/**
 * Implementation of <code>ActionHandler</code> which compares commited OSGi
 * bundle to existing bundles with the same symbolic name and sets a new version
 * based on comparison.
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 * @author Jan Reznicek
 */
public class VersioningActionHandler extends AbstractActionHandler implements ActionHandler {

    private static final Logger logger = LoggerFactory.getLogger(VersioningActionHandler.class);
    
    private static final String CATEGORY_VERSIONED = "versioned";

    private volatile VersionService m_versionService;   /* injected by dependency manager */
    private volatile PluginManager m_pluginManager;     /* injected by dependency manager */

    private int BUFFER_SIZE = 8 * 1024;

    /**
     * Create file from bundle in form of InputSteam.
     * @param bundleAsStream bundle as InputStream
     * @return file with bundle
     * @throws IOException reading from stream or creation of file or write to file failed
     */
    private File fileFromStream(InputStream bundleAsStream) throws IOException {
    	OutputStream output = null;
        File bundleFile;
        try {
            bundleFile = File.createTempFile("source", ".jar");
            output = new FileOutputStream(bundleFile);
            byte[] readBuffer = new byte[BUFFER_SIZE];
            for (int count = bundleAsStream.read(readBuffer); count != -1; count = bundleAsStream.read(readBuffer)) {
                output.write(readBuffer, 0, count);
            }
        } finally {
            try {
                bundleAsStream.close();
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
        
        
        return bundleFile;
    }
    
    /**
     * Create version of the OSGi bundle, before is committed to the Store.
     * Before resource is committed to the Store, check if the resource is unversioned OSGi bundle.
     * If resource is OSGi bundle, generate its version based on difference from previous bundle with same name in repository.
     */
    @Override
    public Resource beforePutToStore(Resource resource, Store store) throws RevokedArtifactException {
    	
    	if (resource == null || !resource.hasCategory("osgi")) {
            return resource;
        }
        if (!resource.hasCategory(CATEGORY_VERSIONED)) {
        	
        	/*candidate for base resource is selected as bundle with same symbolic name 
        	  and highest version in repository.*/
            Resource cand = null;

            try {
				String filter = "(symbolicname=" + resource.getSymbolicName() + ")";
				for (Resource i : store.getRepository().getResources(filter)) {
				        if (cand == null || cand.getVersion().compareTo(i.getVersion()) < 0) {
				            cand = i;
				        }
				}
			} catch (InvalidSyntaxException e1) {
				logger.error("Could not find base bundle, invalid syndax error", e1);
			}
            
            
            String category = null;
            if (cand == null) {
                category = CATEGORY_VERSIONED;
                resource.addCategory("initial-version");
            } else {
            	InputStream versionedBudnleIs = null;
                try {
                	//candidate = base bundle for version generation
                    InputStream candidateInputStream = new FileInputStream(new File(cand.getUri()));
                    
                    InputStream resourceInputStream = new FileInputStream(new File(resource.getUri()));
                    
                    HashMap<String, String> options = new HashMap<String, String>();
                    
                    //micro part of version is generated too
                    options.put("keep-micro-if-identical", "false");

                    versionedBudnleIs =  m_versionService.createVersionedBundle(candidateInputStream, resourceInputStream, options);
                    
                    category = CATEGORY_VERSIONED;
                } catch (IOException ex) {
                    logger.error("Could not update version due to I/O error", ex);
                    category = null;
                } catch (IllegalArgumentException e) {
                    logger.warn("Could not update version (Not osgi bundle): {}", e.getMessage());
                    category = "non-versionable";
                } catch (BundlesIncomparableException e) {
                    logger.warn("Could not update version (incomparable bundles): {}", e.getMessage());
                    category = "non-versionable";
                } catch (Exception e) {
                    logger.error("Could not update version (unknown error)", e);
                    category = null;
                }
                

                ResourceDAO creator = m_pluginManager.getPlugin(ResourceDAO.class);

                try {
                	//create resource from file with bundle with generated version
                	File versionedBundleFile = fileFromStream(versionedBudnleIs);
                	
                    resource = creator.getResource(versionedBundleFile.toURI());   // reload changed resource
                    
                } catch (IOException e) {
                    logger.error("Could not reload changed resource", e);
                }
            }

            if (category != null) {
                resource.addCategory(category);
            }
        }
        
        return resource;
    }

    /**
     * Save an original file name and version to metadata (as resource capability) when resource is uploaded to Buffer.
     * Activate only for unversioned osgi resources
     */
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
