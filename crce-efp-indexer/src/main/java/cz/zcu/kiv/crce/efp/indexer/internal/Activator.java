package cz.zcu.kiv.crce.efp.indexer.internal;

import cz.zcu.kiv.crce.plugin.MetadataIndexingResultService;
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

	@Override
	public final void init(final BundleContext context, final DependencyManager manager) throws Exception {

		/*m_instance = this;*/

		manager.add(createComponent()
				.setInterface(Plugin.class.getName(), null)
				.setImplementation(ResourceActionHandler.class)
				.add(createServiceDependency().setRequired(true).setService(PluginManager.class))
				.add(createServiceDependency().setRequired(false).setService(LogService.class))
				.add(createServiceDependency().setRequired(false).setService(MetadataIndexingResultService.class))
				);
	}

	@Override
	public void destroy(final BundleContext context, final DependencyManager manager)
			throws Exception {
		// do nothing
	}
}
