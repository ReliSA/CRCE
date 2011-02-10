package cz.zcu.kiv.crce.metadata.osgi.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
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

    private volatile RepositoryAdmin m_repoAdmin;  /* injected by dependency manager */

    @Override
    public String[] index(final InputStream input, Resource resource) {

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
            return new String[0];
        } catch (IllegalArgumentException e) {
            // not a bundle
            return new String[0];
        }

        if (fres == null) {
            return new String[0];
        }
        
        resource.setSymbolicName(fres.getSymbolicName());
        resource.setVersion(fres.getVersion());
        resource.setPresentationName(fres.getPresentationName());
        // size is not set
        for (org.apache.felix.bundlerepository.Capability fcap : fres.getCapabilities()) {
            Capability cap = resource.createCapability(fcap.getName());
            for (org.apache.felix.bundlerepository.Property fprop : fcap.getProperties()) {
                cap.setProperty(fprop.getName(), fprop.getValue(), Type.getValue(fprop.getType()));
            }
        }
        for (org.apache.felix.bundlerepository.Requirement freq : fres.getRequirements()) {
            Requirement req = resource.createRequirement(freq.getName());
            req.setComment(freq.getComment());
            req.setExtend(freq.isExtend());
            req.setFilter(freq.getFilter());
            req.setMultiple(freq.isMultiple());
            req.setOptional(freq.isOptional());
        }

        for (String category : fres.getCategories()) {
            resource.addCategory(category);
        }

        // TODO properties, if necessary
        resource.addCategory("osgi");
        
        return new String[] {"osgi"};
    }

    @Override
    public String[] getProvidedCategories() {
        return new String[] {"osgi"};
    }
}
