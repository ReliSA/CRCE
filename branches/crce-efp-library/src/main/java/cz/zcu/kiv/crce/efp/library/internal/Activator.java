package cz.zcu.kiv.crce.efp.library.internal;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;


public class Activator extends DependencyActivatorBase {

	@Override
	public void init(BundleContext context, DependencyManager manager) throws Exception {

		System.out.println("------------START ACTIVATOR CRCE-EFP Library ------------");

	}

	@Override
	public void destroy(BundleContext context, DependencyManager manager) throws Exception {
		// do nothing
	}
}
