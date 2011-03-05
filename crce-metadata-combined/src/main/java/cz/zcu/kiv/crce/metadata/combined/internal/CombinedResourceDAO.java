package cz.zcu.kiv.crce.metadata.combined.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.plugins.AbstractResourceDAO;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAO;
import java.io.IOException;
import java.net.URI;

/**
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class CombinedResourceDAO extends AbstractResourceDAO {

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

            m_staticResourceDAO.save(cres.getStaticResource());
            m_writableResourceDAO.save(cres.getWritableResource());
        } else {
            throw new IllegalStateException("Not a CombinedResourceImpl"); // XXX
        }
        
    }

    @Override
    public void copy(Resource resource, URI uri) throws IOException {
        m_staticResourceDAO.copy(resource, uri);
        m_writableResourceDAO.copy(resource, uri);
    }

    @Override
    public Resource getResource(URI uri) throws IOException {
        Resource staticResource = m_staticResourceDAO.getResource(uri);
        Resource writableResource = m_writableResourceDAO.getResource(uri);
        
        if (staticResource == null && writableResource == null) {
            return null;
        }
        
        if (staticResource == null) {
            staticResource = Activator.instance().getResourceCreator().createResource();
//            staticResource.setSymbolicName(createSymbolicname(uri));
        }
        if (writableResource == null) {
            writableResource = Activator.instance().getResourceCreator().createResource();
        }
        if (staticResource.getSymbolicName() == null) {
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
        return 10;
    }
}
