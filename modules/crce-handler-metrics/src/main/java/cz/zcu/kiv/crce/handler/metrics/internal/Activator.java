package cz.zcu.kiv.crce.handler.metrics.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

import cz.zcu.kiv.crce.concurrency.service.TaskRunnerService;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.dao.MetadataDao;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.service.validation.MetadataValidator;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;


/**
 * Activator of this plugin (indexer).
 *
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {

		String services[] = { Plugin.class.getName(), ActionHandler.class.getName() };

		manager.add(createComponent()
                .setInterface(services, null)
                .setImplementation(MetricsIndexerActionHandler.class)
                .add(createServiceDependency().setRequired(true).setService(TaskRunnerService.class))
                .add(createServiceDependency().setRequired(true).setService(MetadataService.class))
                .add(createServiceDependency().setRequired(true).setService(MetadataValidator.class))
                .add(createServiceDependency().setRequired(true).setService(MetadataFactory.class))
                .add(createServiceDependency().setRequired(true).setService(MetadataDao.class))
                );
	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
		// do nothing
	}
}
