package cz.zcu.kiv.crce.webui.internal;

import cz.zcu.kiv.crce.repository.Stack;
import org.apache.ace.obr.storage.BundleStore;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.service.obr.RepositoryAdmin;

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
        manager.add(createComponent().setImplementation(this).add(createServiceDependency().setService(Stack.class).setRequired(true)).add(createServiceDependency().setService(LogService.class).setRequired(false)));

        final Test t1 = new Test();
        final Test t2 = new Test();

        manager.add(createComponent().setImplementation(t1).add(createServiceDependency().setService(RepositoryAdmin.class).setRequired(true)));
        manager.add(createComponent().setImplementation(t2).add(createServiceDependency().setService(RepositoryAdmin.class).setRequired(true)));

        manager.add(createComponent().setImplementation(t1).add(createServiceDependency().setService(BundleStore.class).setRequired(true)));
        manager.add(createComponent().setImplementation(t2).add(createServiceDependency().setService(BundleStore.class).setRequired(true)));


        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                }

                t1.add("file:///U:/repository.xml");
                t2.add("file:///Q:/DIP/m2repo/repository.xml");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                t1.print();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                t2.print();
            }
        }).start();
    }

    @Override
    public void destroy(BundleContext context, DependencyManager manager) throws Exception {
        // nothing to do
    }
}
