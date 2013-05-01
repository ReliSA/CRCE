package cz.zcu.kiv.crce.metadata.osgi.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipException;

import org.apache.felix.bundlerepository.RepositoryAdmin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;
import cz.zcu.kiv.crce.metadata.legacy.LegacyMetadataHelper;

/**
 * implementation of <code>ResourceIndexer</code> which provides support for
 * indexing OSGi components metadata. It uses Apache OBR internaly.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class OsgiManifestBundleIndexer extends AbstractResourceIndexer {

    private static final Logger logger = LoggerFactory.getLogger(OsgiManifestBundleIndexer.class);

    private volatile RepositoryAdmin repositoryAdmin;  /* injected by dependency manager */
    private volatile ResourceFactory resourceFactory;  /* injected by dependency manager */

    @Override
    @SuppressWarnings("unchecked")
    public List<String> index(final InputStream input, Resource resource) {

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
            fres = repositoryAdmin.getHelper().createResource(new URL("none", "none", 0, "none", handler));
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Unexpected MalformedURLException", e);
        } catch (ZipException e) {
            logger.warn("Zip file is corrupted: {}", resource.getId(), e);
            LegacyMetadataHelper.addCategory(resourceFactory, resource, "corrupted");
            return Collections.singletonList("corrupted");
        } catch (IOException ex) {
            logger.error("I/O error on indexing resource: {}", resource.getId(), ex);
            return Collections.emptyList();
        } catch (IllegalArgumentException e) {
            // not a bundle
            return Collections.emptyList();
        }

        if (fres == null) {
            return Collections.emptyList();
        }

        LegacyMetadataHelper.setSymbolicName(resourceFactory, resource, fres.getSymbolicName());
        LegacyMetadataHelper.setVersion(resourceFactory, resource, fres.getVersion());
        LegacyMetadataHelper.setPresentationName(resourceFactory, resource, fres.getPresentationName());
        // size is not set
        for (org.apache.felix.bundlerepository.Capability fcap : fres.getCapabilities()) {
            Capability cap = resourceFactory.createCapability(fcap.getName());
            for (org.apache.felix.bundlerepository.Property fprop : fcap.getProperties()) {
                ObrType type = ObrType.getValue(fprop.getType());
                cap.setAttribute(fprop.getName(), (Class<Object>) type.getTypeClass(), ObrType.propertyValueFromString(type, fprop.getValue()));
            }
            resource.addCapability(cap);
        }
        for (org.apache.felix.bundlerepository.Requirement freq : fres.getRequirements()) {
            Requirement req = resourceFactory.createRequirement(freq.getName());

            req.setDirective("comment", freq.getComment());
            req.setDirective("extend", String.valueOf(freq.isExtend()));
            req.setDirective("filter", freq.getFilter());
            req.setDirective("multiple", String.valueOf(freq.isMultiple()));
            req.setDirective("optional", String.valueOf(freq.isOptional()));

            resource.addRequirement(req);
        }

        for (String category : fres.getCategories()) {
            LegacyMetadataHelper.addCategory(resourceFactory, resource, category);
        }

        // TODO properties, if necessary
        LegacyMetadataHelper.addCategory(resourceFactory, resource, "osgi");

        return Collections.singletonList("osgi");
    }

    @Override
    public List<String> getProvidedCategories() {
        List<String> result = new ArrayList<>();
        Collections.addAll(result, "osgi", "corrupted");
        return result;
    }
}
