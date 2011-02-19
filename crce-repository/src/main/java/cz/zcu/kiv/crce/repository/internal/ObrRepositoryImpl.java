package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Repository;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.osgi.service.log.LogService;
import org.osgi.service.obr.RepositoryAdmin;
//import org.osgi.service.obr.Resource;

/**
 *
 * @author kalwi
 */
public class ObrRepositoryImpl implements Repository {
    public static final String RESOURCE_METADATA_FILE_EXTENSION = ".metadata";
    
    private volatile RepositoryAdmin m_repositoryAdmin; /* will be injected by dependencymanager */
    private volatile LogService m_log; /* will be injected by dependencymanager */
    
    private PluginManager m_pluginManager;
    private URL m_obrBase;
    
    public ObrRepositoryImpl() {
        try {
            m_obrBase = new URL("http://localhost:8080/obr");
        } catch (MalformedURLException ex) {
            m_log.log(LogService.LOG_ERROR, "Malformed url", ex);
        }
    }
    
    @Override
    public void put(Resource resource) throws IOException {
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

    @Override
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
//                m_log.log(LogService.LOG_WARNING, "Could not add metadata URL to ResourceExt: " + url, e);
//            }
            
//            resources[i] = resource;
//        }
        
//        return resources;
    }

    @Override
    public Resource getNewestVersion(String symbolicName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isCompatible(Resource resource) {
        throw new UnsupportedOperationException("Not supported yet.");
//        Resolver resolver = m_repositoryAdmin.resolver();
//        resolver.add(resource);
//        return resolver.resolve();
    }


}
