package cz.zcu.kiv.crce.efp.indexer.internal;

import cz.zcu.kiv.crce.efp.indexer.EfpIndexerResultService;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

/**
 * CRCE-EFP-Indexer activator class.
 */
public class Activator extends DependencyActivatorBase {

	/*
	private static volatile Activator m_instance;

	private volatile LogService m_log;
	private volatile EfpIndexerLogService mEfpIndexer;    //* injected by dependency manager * /

    public LogService getLog() {
        return m_log;
    }

    public EfpIndexerLogService getEfpIndexerLog() {
		return efpIndexerLogService;
	}

    public static Activator instance() {
        return m_instance;
    }
    */

	/** This instance of EfpIndexerResultService implementation will be used in creating a service below. */
	private EfpIndexerResultService efpIndexerResult = new EfpIndexerResultServiceImpl();

	@Override
	public final void init(final BundleContext context, final DependencyManager manager) throws Exception {

		/*m_instance = this;

		manager.add(createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setRequired(false).setService(EfpIndexerLogService.class))
                );*/

		manager.add(createComponent()
				.setInterface(EfpIndexerResultService.class.getName(), null)
				.setImplementation(efpIndexerResult)
				);

		manager.add(createComponent()
				.setInterface(Plugin.class.getName(), null)
				.setImplementation(ResourceActionHandler.class)
				.add(createServiceDependency().setRequired(true).setService(PluginManager.class))
				.add(createServiceDependency().setService(LogService.class).setRequired(false))
				.add(createServiceDependency().setRequired(false).setService(EfpIndexerResultService.class))
				);
	}

	@Override
	public void destroy(final BundleContext context, final DependencyManager manager)
			throws Exception {
		// do nothing
	}
}
