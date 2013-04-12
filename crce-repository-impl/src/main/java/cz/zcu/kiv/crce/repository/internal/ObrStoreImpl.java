package cz.zcu.kiv.crce.repository.internal;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.osgi.service.obr.RepositoryAdmin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.Executable;

/**
 * Implementation of <code>Store</code> which can connect to remote OBR repository.
 * Not implemented yet.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class ObrStoreImpl implements Store {
    public static final String RESOURCE_METADATA_FILE_EXTENSION = ".metadata";

    private volatile RepositoryAdmin repositoryAdmin; /* will be injected by dependencymanager */ // NOPMD

    private static final Logger logger = LoggerFactory.getLogger(ObrStoreImpl.class); // NOPMD

    private PluginManager pluginManager; // NOPMD
    private URL obrBase; // NOPMD

    public ObrStoreImpl(URL obrBase) {
        this.obrBase = obrBase;
    }

    @Override
    public Resource put(Resource resource) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");

////        m_repositoryAdmin.listRepositories()[0].getURL();
//
//        if (m_obrBase == null) {
//            throw new IOException("There is no storage available for this artifact.");
//        }
//
//        InputStream input = null;
//        OutputStream output = null;
//        URL url = null;
//        try {
//            input = resource.getURL().openStream();
//            url = new URL(m_obrBase, new File(resource.getURL().getFile()).getName());
//            URLConnection connection = url.openConnection();
//            connection.setDoOutput(true);
//            connection.setDoInput(true);
//            connection.setUseCaches(false);
////            connection.setRequestProperty("Content-Type", mimetype);  // TODO set mimetype
//            output = connection.getOutputStream();
//            byte[] buffer = new byte[4 * 1024];
//            for (int count = input.read(buffer); count != -1; count = input.read(buffer)) {
//                output.write(buffer, 0, count);
//            }
//            output.close();
//            if (connection instanceof HttpURLConnection) {
//                int responseCode = ((HttpURLConnection) connection).getResponseCode();
//                switch (responseCode) {
//                    case HttpURLConnection.HTTP_OK :
//                        break;
//                    case HttpURLConnection.HTTP_CONFLICT:
//                        throw new IOException("Artifact already exists in storage.");
//                    case HttpURLConnection.HTTP_INTERNAL_ERROR:
//                        throw new IOException("The storage server returned an internal server error.");
//                    default:
//                        throw new IOException("The storage server returned code " + responseCode + " writing to " + url.toString());
//                }
//            }
//        }
//        catch (IOException ioe) {
//            throw new IOException("Error importing artifact " + resource.toString() + ": " + ioe.getMessage());
//        }
//        finally {
//            if (input != null) {
//                try {
//                    input.close();
//                }
//                catch (Exception ex) {
//                    // Not much we can do
//                }
//            }
//            if (output != null) {
//                try {
//                    output.close();
//                }
//                catch (Exception ex) {
//                    // Not much we can do
//                }
//            }
//        }
//
////        return url;

    }

    public Resource[] get(String filter) {
        throw new UnsupportedOperationException("Not supported yet.");
//        org.osgi.service.obr.Resource[] resources = m_repositoryAdmin.discoverResources(filter);

//        Resource[] resources = new Resource[resources.length];

//        for (int i = 0; i < resources.length; i++) {
//            Resource resource;
//            resource = new ResourceExtImpl(resources[i]);

//            String url = resource.getURL().toString() + RESOURCE_METADATA_FILE_EXTENSION;
//            try {
//                resource.setMetadataURL(new URL(url));
//            } catch (MalformedURLException e) {
//                logger.warn("Could not add metadata URL to ResourceExt: " + url, e);
//            }

//            resources[i] = resource;
//        }

//        return resources;
    }

    @Override
    public boolean remove(Resource resource) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void execute(List<Resource> resource, Executable plugin, Properties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Resource> getResources() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Resource> getResources(Requirement requirement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
