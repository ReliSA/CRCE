package cz.zcu.kiv.crce.efpAssignment.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

/**
 * CRCE - EfpAssignment activator class.
 */
public class Activator extends DependencyActivatorBase {

	@Override
	public final void init(final BundleContext context, final DependencyManager manager) throws Exception {
		// do nothing
	}

	@Override
	public void destroy(final BundleContext context, final DependencyManager manager)
			throws Exception {
		// do nothing
	}
}
