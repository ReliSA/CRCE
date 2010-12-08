package cz.zcu.kiv.ccer.internal;

import cz.zcu.kiv.ccer.Repository;
import java.net.URL;
import org.osgi.service.obr.RepositoryAdmin;
import org.osgi.service.obr.Resolver;
import org.osgi.service.obr.Resource;

/**
 *
 * @author kalwi
 */
public class RepositoryImpl implements Repository {
    
    private volatile RepositoryAdmin m_repositoryAdmin; // will be injected by dependency manager
    
    @Override
    public void put(URL resource) {
        throw new UnsupportedOperationException("Not supported yet.");
        // see org.apache.ace.client.services.OBRService
    }

    @Override
    public Resource[] get(String filter) {
        return m_repositoryAdmin.discoverResources(filter);
    }

    @Override
    public URL getNewestVersion(String symbolicName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isCompatible(Resource resource) {
        Resolver resolver = m_repositoryAdmin.resolver();
        resolver.add(resource);
        return resolver.resolve();
    }


}
