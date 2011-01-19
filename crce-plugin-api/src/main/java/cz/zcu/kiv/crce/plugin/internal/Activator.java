package cz.zcu.kiv.crce.plugin.internal;

import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.plugin.ResourceDAO;
import cz.zcu.kiv.crce.plugin.ResourceDAOFactory;
        
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

/**
 *
 * @author kalwi
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        
        final PluginManager pm = new PluginManagerImpl();
        
        manager.add(createComponent()
                .setInterface(PluginManager.class.getName(), null)
                .setImplementation(pm)
                .add(createServiceDependency().setService(LogService.class).setRequired(false))
                .add(createServiceDependency().setService(Plugin.class).setRequired(false).setCallbacks("add", "remove"))
                .add(createServiceDependency().setService(ResourceDAO.class).setRequired(false).setCallbacks("add", "remove"))
                .add(createServiceDependency().setService(ResourceDAOFactory.class).setRequired(false).setCallbacks("add", "remove"))
                );
        
        
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
//              pm.print();
                
            }
        }).start();
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // nothing yet
    }

}
