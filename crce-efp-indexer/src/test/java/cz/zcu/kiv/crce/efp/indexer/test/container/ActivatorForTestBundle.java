package cz.zcu.kiv.crce.efp.indexer.test.container;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.efp.indexer.test.EfpIndexerResultServiceImplTest;

/**
 * A test bundle used for testing ResolverHook and FindHook.
 * @author Kamil Ježek [kjezek@kiv.zcu.cz], Jan Řezníček
 *
 * There is an effort to reuse code for purposes of testing crce-efp-indexer module.
 */
public class ActivatorForTestBundle implements BundleActivator, ServiceListener {
	
    /** Logger. */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /** {@inheritDoc} */
    @Override
    public void start(final BundleContext btx) throws Exception {

        logger.debug("start of TestBundleActivator called");

        for(int i=0;i<5;i++){
        	System.out.println("================================");
        }
        
        new EfpIndexerResultServiceImplTest().testEfpIndexerResultServiceImpl();
        
    }

    /** {@inheritDoc} */
    @Override
    public void stop(final BundleContext btx) throws Exception {
    }

    @Override
    public void serviceChanged(final ServiceEvent event) {
        logger.debug("Service Event: {}", event);
    }
}
