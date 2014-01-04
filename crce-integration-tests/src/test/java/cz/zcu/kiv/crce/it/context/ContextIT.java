package cz.zcu.kiv.crce.it.context;

import static junit.framework.TestCase.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.IOException;

import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import org.apache.felix.dm.Component;

import org.ops4j.pax.exam.spi.reactors.PerMethod;

import cz.zcu.kiv.crce.it.Configuration;
import cz.zcu.kiv.crce.it.IntegrationTestBase;
import cz.zcu.kiv.crce.it.Options;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.dao.RepositoryDAO;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Store;

/**
 * Tests application context, starting of components and services, etc.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ContextIT extends IntegrationTestBase {

    private static final Logger logger = LoggerFactory.getLogger(ContextIT.class);

    // injected by dependency manager
    private volatile PluginManager pluginManager;
    private volatile ResourceFactory resourceFactory;
    private volatile ResourceDAO resourceDAO;
    private volatile RepositoryDAO repositoryDAO;
    private volatile MetadataService metadataService;
    private volatile Store store;

    /**
     * Configuration of the OSGi runtime.
     *
     * @return the configuration
     */
    @org.ops4j.pax.exam.Configuration
    public final Option[] configuration() {

        logger.info("Option config");
        return options(
                junitBundles(),
                Options.logging(),
                Options.Felix.dependencyManager(),
                Options.Felix.configAdmin(),
                Options.Felix.eventAdmin(),
                Options.Felix.bundleRepository(),
                Options.Osgi.compendium(),

                mavenBundle("commons-io", "commons-io"),

                Options.Crce.pluginApi(),

                Options.Crce.metadata(),
                Options.Crce.metadataDao(),
                Options.Crce.metadataService(),
                Options.Crce.repository(),

                Options.Crce.metadataIndexerApi(),
                Options.Crce.metadataIndexer()

        );
    }

    @Override
    protected void before() throws IOException {
        Configuration.metadataDao(this);
        Configuration.repository(this);
    }

    @Override
    protected Component[] getDependencies() {
        return new Component[]{
            createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(PluginManager.class).setRequired(true))
                .add(createServiceDependency().setService(ResourceFactory.class).setRequired(true))
                .add(createServiceDependency().setService(ResourceDAO.class).setRequired(true))
                .add(createServiceDependency().setService(RepositoryDAO.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataService.class).setRequired(true))
                .add(createServiceDependency().setService(Store.class).setRequired(true))
            };
    }


    @Test
    @SuppressWarnings("deprecation")
    public void testContext() throws InvalidSyntaxException, InterruptedException {
        logger.info("A Simple OSGi Integration Test");

        assertNotNull(bundleContext);
        assertNotNull(dependencyManager);

        logger.info("INIT");

        logger.info("Property {}: {}", Constants.FRAMEWORK_VENDOR, bundleContext.getProperty(Constants.FRAMEWORK_VENDOR));
        logger.info("Property {}: {}", Constants.FRAMEWORK_VERSION, bundleContext.getProperty(Constants.FRAMEWORK_VERSION));
        logger.info("Property {}: {}", Constants.FRAMEWORK_EXECUTIONENVIRONMENT, bundleContext.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));

        logger.info("SERVICES: [service reference] (bundle symbolic name)");

        for (ServiceReference<?> serviceReference : bundleContext.getAllServiceReferences(null, null)) {
            logger.info("Service reference: {} ({})", serviceReference.toString(), serviceReference.getBundle().getSymbolicName());
        }

        logger.info("BUNDLES: symbolic name (location)");
        for (Bundle bundle : bundleContext.getBundles()) {
            logger.info("Bundle: {} ({})", bundle.getSymbolicName(), bundle.getLocation());
        }

        assertNotNull(pluginManager);
        assertNotNull(resourceFactory);
        assertNotNull(resourceDAO);
        assertNotNull(repositoryDAO);
        assertNotNull(metadataService);
        assertNotNull(store);
    }
}
