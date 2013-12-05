package cz.zcu.kiv.crce.rest.internal;

import javax.servlet.http.HttpServletRequest;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

import cz.zcu.kiv.crce.compatibility.service.CompatibilityService;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionRegister;
import cz.zcu.kiv.crce.repository.Store;

public final class Activator extends DependencyActivatorBase {

    private static volatile Activator m_instance;
    private volatile BundleContext m_context;           /* injected by dependency manager */
    private volatile LogService m_log;                  /* injected by dependency manager */
    private volatile Store m_store;                  	/* injected by dependency manager */
    private volatile SessionRegister m_sessionRegister;   /* injected by dependency manager */
    private volatile CompatibilityService m_compatibilityService;  //injected by dependency manager

    public static Activator instance() {
        return m_instance;
    }

    public BundleContext getContext() {
        return m_context;
    }

    public LogService getLog() {
        return m_log;
    }

    public Store getStore() {
        return m_store;
    }

    public CompatibilityService getCompatibilityService() {
        return m_compatibilityService;
    }

    public Buffer getBuffer(HttpServletRequest req) {
        if (req == null) {
            return null;
        }

        String sid = req.getSession(true).getId();
        return m_sessionRegister.getSessionData(sid).getBuffer();
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {

    }

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        m_instance = this;

        manager.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(SessionRegister.class).setRequired(true))
                .add(createServiceDependency().setService(Store.class).setRequired(true))
                .add(createServiceDependency().setService(LogService.class).setRequired(false))
                .add(createServiceDependency().setService(CompatibilityService.class).setRequired(true))
        );
    }
}
