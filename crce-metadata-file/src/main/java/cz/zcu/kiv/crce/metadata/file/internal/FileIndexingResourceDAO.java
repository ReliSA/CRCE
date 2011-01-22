package cz.zcu.kiv.crce.metadata.file.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.stub.AbstractResourceDAO;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.plugin.ResourceIndexer;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class FileIndexingResourceDAO extends AbstractResourceDAO {

    private volatile PluginManager m_pluginManager;

    @Override
    public Resource getResource(URI uri) throws IOException {
        // TODO optimize via getResourceIndexers(category)

        URL url = uri.toURL();
        
        ResourceIndexer[] indexers = m_pluginManager.getResourceIndexers();

        if (indexers.length == 0) {
            return null;
        }

        Resource resource = indexers[0].index(url.openStream());

        if (indexers.length > 1) {
            for (int i = 1; i < indexers.length; i++) {
                resource = indexers[i].index(url.openStream(), resource);
            }
        }
        
        if ("file".equals(url.getProtocol())) {
            File f = new File(uri);
            resource.setSize(f.length());
        }
        
        resource.setUri(uri.normalize());
        
        System.out.println("URL normalized: " + url.toExternalForm());

        resource.unsetWritable();
        return resource;

    }

    @Override
    public void save(Resource resource) throws IOException {
        // do nothing
    }

    @Override
    public void copy(Resource resource, URI uri) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getPluginPriority() {
        return 10;
    }
}
