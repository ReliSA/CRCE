package cz.zcu.kiv.crce.metadata.combined.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.dao.AbstractResourceDAO;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import java.io.IOException;
import java.net.URI;

/**
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class CombinedResourceDAO extends AbstractResourceDAO {

    private volatile ResourceCreator m_resourceCreator; /* injected by dependency manager */

    private ResourceDAO m_staticResourceDAO;
    private ResourceDAO m_writableResourceDAO;

    public CombinedResourceDAO(ResourceDAO staticDAO, ResourceDAO writableDAO) {
        m_staticResourceDAO = staticDAO;
        m_writableResourceDAO = writableDAO;
    }

    @Override
    public void save(Resource resource) throws IOException {

        if (resource instanceof CombinedResourceImpl) {
            CombinedResourceImpl cres = (CombinedResourceImpl) resource;

            try {
                m_staticResourceDAO.save(cres.getStaticResource());
            } finally {
                m_writableResourceDAO.save(cres.getWritableResource());
            }
        } else {
            throw new IllegalStateException("Not a CombinedResourceImpl"); // XXX
        }
        
    }

    @Override
    public Resource moveResource(Resource resource, URI uri) {
        if (!(resource instanceof CombinedResourceImpl)) {
            throw new IllegalStateException("Not a CombinedResourceImpl"); // XXX
        }
        Resource staticResource = m_staticResourceDAO.moveResource(((CombinedResourceImpl) resource).getStaticResource(), uri);
        
        Resource writableResource = m_writableResourceDAO.moveResource(((CombinedResourceImpl) resource).getWritableResource(), uri);

        if (staticResource == null && writableResource == null) {
            return null;
        }
        if (staticResource == null) {
            staticResource = m_resourceCreator.createResource();
            // TODO unsetWritable?
//            staticResource.setSymbolicName(createSymbolicname(uri));
        }
        if (writableResource == null) {
            writableResource = m_resourceCreator.createResource();
        }
        
        return new CombinedResourceImpl(staticResource, writableResource);
    }
    
    @Override
    public void remove(Resource resource) throws IOException {
        try {
            m_staticResourceDAO.remove(resource);
        } finally {
            m_writableResourceDAO.remove(resource);
        }
    }

    @Override
    public Resource getResource(URI uri) throws IOException {
        Resource staticResource = m_staticResourceDAO.getResource(uri);
        Resource writableResource = m_writableResourceDAO.getResource(uri);
        
        if (staticResource == null && writableResource == null) {
            return null;
        }
        
        if (staticResource == null) {
            staticResource = m_resourceCreator.createResource();
            // TODO unsetWritable?
//            staticResource.setSymbolicName(createSymbolicname(uri));
        }
        if (writableResource == null) {
            writableResource = m_resourceCreator.createResource();
        }
        if (staticResource.getSymbolicName() == null && writableResource.getSymbolicName() == null) {
            writableResource.setSymbolicName(createSymbolicname(uri));
        }
        
        // TODO set new resource's URIs ?
        return new CombinedResourceImpl(staticResource, writableResource);
    }
    
    private String createSymbolicname(URI uri) {
            String[] seg = uri.getPath().split("/");
            return seg[seg.length - 1];
    }

    @Override
    public int getPluginPriority() {
        return 10; // TODO configurable
    }
}
