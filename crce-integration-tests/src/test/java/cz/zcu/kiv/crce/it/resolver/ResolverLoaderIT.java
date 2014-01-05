package cz.zcu.kiv.crce.it.resolver;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.IOException;

import org.apache.felix.dm.Component;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.it.Configuration;
import cz.zcu.kiv.crce.it.IntegrationTestBase;
import cz.zcu.kiv.crce.it.Options;
import cz.zcu.kiv.crce.resolver.ResourceLoader;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ResolverLoaderIT extends IntegrationTestBase {

    private static final Logger logger = LoggerFactory.getLogger(ResolverLoaderIT.class);

    private volatile ResourceLoader resourceLoader;

    @Override
    protected void before() throws IOException {
        Configuration.metadataDao(this);
    }

    @org.ops4j.pax.exam.Configuration
    public final Option[] configuration() {

        logger.info("Option config");
        return options(
                junitBundles(),
                Options.logging(),
                Options.Osgi.compendium(),
                Options.Felix.dependencyManager(),
                Options.Felix.configAdmin(),
                Options.Felix.eventAdmin(),
                Options.Felix.bundleRepository(),

                Options.Crce.pluginApi(),

                Options.Crce.metadata(),
                Options.Crce.metadataDao(),
                Options.Crce.metadataService(),
                Options.Crce.resolver()
        );
    }

    @Override
    protected Component[] getDependencies() {
        return new Component[]{
            createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(ResourceLoader.class).setRequired(true))
            };
    }

    @Test
    public void testContext() {
        assertNotNull(resourceLoader);
    }
}
