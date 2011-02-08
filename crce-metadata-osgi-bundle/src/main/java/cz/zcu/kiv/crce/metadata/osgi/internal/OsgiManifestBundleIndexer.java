package cz.zcu.kiv.crce.metadata.osgi.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.Type;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import org.apache.felix.bundlerepository.RepositoryAdmin;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class OsgiManifestBundleIndexer extends AbstractResourceIndexer {

    private volatile ResourceCreator m_resourceCreator; /* injected by dependency manager */
    private volatile RepositoryAdmin m_repoAdmin;  /* injected by dependency manager */

    @Override
    public Resource index(InputStream input) {
        return index(input, null);
    }

    @Override
    public Resource index(final InputStream input, Resource resource) {
        Resource res = (resource == null ? m_resourceCreator.createResource() : resource);

        org.apache.felix.bundlerepository.Resource fres;
        
        try {
            URLStreamHandler handler = new URLStreamHandler()  {
                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    return new URLConnection(null)  {
                        @Override
                        public void connect() throws IOException {
                        }
                        @Override
                        public InputStream getInputStream() throws IOException {
                            return input;
                        }
                    };

                }
            };
            fres = m_repoAdmin.getHelper().createResource(new URL("none", "none", 0, "none", handler));
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Unexpected MalformedURLException", e);
        } catch (IOException ex) {
            ex.printStackTrace();   // XXX
            return res;
        } catch (IllegalArgumentException e) {
            // not a bundle
            return res;
        }

        if (fres == null) {
            return res;
        }
        
        res.setSymbolicName(fres.getSymbolicName());
        res.setVersion(fres.getVersion());
        res.setPresentationName(fres.getPresentationName());
        // size is not set
        for (org.apache.felix.bundlerepository.Capability fcap : fres.getCapabilities()) {
            Capability cap = res.createCapability(fcap.getName());
            for (org.apache.felix.bundlerepository.Property fprop : fcap.getProperties()) {
                cap.setProperty(fprop.getName(), fprop.getValue(), Type.getValue(fprop.getType()));
            }
        }
        for (org.apache.felix.bundlerepository.Requirement freq : fres.getRequirements()) {
            Requirement req = res.createRequirement(freq.getName());
            req.setComment(freq.getComment());
            req.setExtend(freq.isExtend());
            req.setFilter(freq.getFilter());
            req.setMultiple(freq.isMultiple());
            req.setOptional(freq.isOptional());
        }

        for (String category : fres.getCategories()) {
            res.addCategory(category);
        }

        // TODO properties, if necessary
        res.addCategory("osgi");
        return res;
    }

    @Override
    public String[] getProvidedCategories() {
        return new String[] {"osgi"};
    }
}
