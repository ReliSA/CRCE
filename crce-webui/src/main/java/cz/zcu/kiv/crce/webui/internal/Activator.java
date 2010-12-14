package cz.zcu.kiv.crce.webui.internal;

import cz.zcu.kiv.crce.repository.Stack;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

public final class Activator extends DependencyActivatorBase {

    private static volatile Stack m_stack; /* injected */
    private static volatile LogService m_log; /* injected */


    public static Stack getStack() {
        return m_stack;
    }
    
    public static LogService getLog() {
        return m_log;
    }
    
    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        manager.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(Stack.class).setRequired(true))
                .add(createServiceDependency().setService(LogService.class).setRequired(false))
                );
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // nothing to do
    }
}
