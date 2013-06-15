package cz.zcu.kiv.crce.metadata.dao.internal;

import org.osgi.framework.BundleContext;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;

import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {

        manager.add(createComponent()
                .setInterface(ResourceDAO.class.getName(), null)
                .setImplementation(ResourceDAOImpl.class)
                .add(createServiceDependency().setRequired(true).setService(MetadataService.class))
                .add(createServiceDependency().setRequired(true).setService(ResourceFactory.class)));
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // TODO logging
    }
}
