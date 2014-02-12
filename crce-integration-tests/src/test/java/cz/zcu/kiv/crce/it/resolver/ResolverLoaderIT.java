package cz.zcu.kiv.crce.it.resolver;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.osgi.framework.Version;
import org.apache.commons.io.FileUtils;
import org.apache.felix.dm.Component;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.CoreOptions;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.it.Configuration;
import cz.zcu.kiv.crce.it.IntegrationTestBase;
import cz.zcu.kiv.crce.it.Options;
import cz.zcu.kiv.crce.metadata.EqualityLevel;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.dao.RepositoryDAO;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.json.MetadataJsonMapper;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.resolver.ResourceLoader;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@SuppressWarnings("null")
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ResolverLoaderIT extends IntegrationTestBase {

    private static final Logger logger = LoggerFactory.getLogger(ResolverLoaderIT.class);

    private volatile ResourceLoader resourceLoader;
    private volatile ResourceDAO resourceDAO;
    private volatile RepositoryDAO repositoryDAO;
    private volatile MetadataFactory metadataFactory;
    private volatile MetadataService metadataService;
    private volatile MetadataJsonMapper metadataJsonMapper;

    private Repository repository;
    private Resource resource1;
    private Resource resource2;
    private Resource resource3;

    @Override
    protected void before() throws IOException, URISyntaxException {
        Configuration.metadataDao(this);
    }

    @org.ops4j.pax.exam.Configuration
    public final Option[] configuration() {

        logger.info("Option config");
        return options(
                CoreOptions.systemPackage("org.apache.commons.io"),
                junitBundles(),
                Options.logging(),
                Options.Osgi.compendium(),
                Options.Felix.dependencyManager(),
                Options.Felix.configAdmin(),
                Options.Felix.eventAdmin(),
                Options.Felix.bundleRepository(),

                Options.Crce.pluginApi(),

                Options.Crce.metadataJson(),
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
                .add(createServiceDependency().setService(ResourceDAO.class).setRequired(true))
                .add(createServiceDependency().setService(RepositoryDAO.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataFactory.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataService.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataJsonMapper.class).setRequired(true))
            };
    }

    @Override
    public void setupTest() throws Exception {
        super.setupTest();

        String json = FileUtils.readFileToString(new File("src/test/resources/resolver/Resource1.json"));
        resource1 = metadataJsonMapper.deserialize(json);

        assertNotNull(resource1);

        repository = resource1.getRepository();
        assertNotNull(repository);
        assertNotNull(repository.getURI());

        json = FileUtils.readFileToString(new File("src/test/resources/resolver/Resource2.json"));
        resource2 = metadataJsonMapper.deserialize(json);
        assertNotNull(resource2);
        assertNotNull(resource2.getRepository());
        assertNotNull(resource2.getRepository().getURI());

        assertEquals(repository.getURI(), resource2.getRepository().getURI());
        assertFalse(resource1.equalsTo(resource2, EqualityLevel.KEY));
        assertFalse(resource1.equalsTo(resource2, EqualityLevel.DEEP_WITH_KEY));

        json = FileUtils.readFileToString(new File("src/test/resources/resolver/Resource3.json"));
        resource3 = metadataJsonMapper.deserialize(json);
        assertNotNull(resource3);
        assertNotNull(resource3.getRepository());
        assertNotNull(resource3.getRepository().getURI());

        assertEquals(repository.getURI(), resource3.getRepository().getURI());
        assertFalse(resource1.equalsTo(resource3, EqualityLevel.KEY));
        assertFalse(resource1.equalsTo(resource3, EqualityLevel.DEEP_WITH_KEY));
        assertFalse(resource2.equalsTo(resource3, EqualityLevel.KEY));
        assertFalse(resource2.equalsTo(resource3, EqualityLevel.DEEP_WITH_KEY));

        repositoryDAO.saveRepository(repository);
        resourceDAO.saveResource(resource1);
        resourceDAO.saveResource(resource2);
        resourceDAO.saveResource(resource3);

        List<Resource> resources = resourceDAO.loadResources(repository);
        assertNotNull(resources);
        assertEquals(3, resources.size());
    }

    @Test
    public void testContext() throws Exception {
        assertNotNull(resourceLoader);
        assertNotNull(resourceDAO);
        assertNotNull(repositoryDAO);
        assertNotNull(metadataFactory);
        assertNotNull(metadataJsonMapper);
        assertNotNull(metadataService);
    }

    @Test
    public void testRequirementWithNoAttributes() throws Exception {
        Requirement requirement = metadataFactory.createRequirement("osgi.wiring.package");

        List<Resource> resources = resourceLoader.getResources(repository, requirement);

        assertTrue(resources.contains(resource1));
        assertTrue(resources.contains(resource2));
        assertTrue(resources.contains(resource3));
    }

    @Test
    public void testRequirementWithOneAttribute() throws Exception {
        Requirement requirement = metadataFactory.createRequirement("osgi.wiring.package");

        requirement.addAttribute("name", String.class, "cz.zcu.kiv.test", Operator.EQUAL);

        List<Resource> resources = resourceLoader.getResources(repository, requirement);

        assertTrue(resources.contains(resource1));
        assertTrue(resources.contains(resource2));
        assertFalse(resources.contains(resource3));
    }

    @Test
    public void testRequirementWithTwoAttributes() throws Exception {
        Requirement requirement = metadataFactory.createRequirement("osgi.wiring.package");

        requirement.addAttribute("name", String.class, "cz.zcu.kiv.test", Operator.EQUAL);
        requirement.addAttribute("version", Version.class, new Version("1.0.0"), Operator.EQUAL);

        List<Resource> resources = resourceLoader.getResources(repository, requirement);

        assertTrue(resources.contains(resource1));
        assertFalse(resources.contains(resource2));
        assertFalse(resources.contains(resource3));
    }

    @Test
    public void testRequirementsWithVersion() throws Exception {
        Requirement requirement;
        List<Resource> resources;

        // GREATER
        requirement = metadataFactory.createRequirement("osgi.wiring.package");

        requirement.addAttribute("name", String.class, "cz.zcu.kiv.test.helper", Operator.EQUAL);
        requirement.addAttribute("version", Version.class, new Version("1.0.0"), Operator.GREATER);

        resources = resourceLoader.getResources(repository, requirement);

        assertFalse(resources.contains(resource1));
        assertTrue(resources.contains(resource2));
        assertTrue(resources.contains(resource3));


        // GREATER_EQUAL
        requirement = metadataFactory.createRequirement("osgi.wiring.package");

        requirement.addAttribute("name", String.class, "cz.zcu.kiv.test.helper", Operator.EQUAL);
        requirement.addAttribute("version", Version.class, new Version("1.0.1"), Operator.GREATER_EQUAL);

        resources = resourceLoader.getResources(repository, requirement);

        assertFalse(resources.contains(resource1));
        assertTrue(resources.contains(resource2));
        assertTrue(resources.contains(resource3));


        // LESS
        requirement = metadataFactory.createRequirement("osgi.wiring.package");

        requirement.addAttribute("name", String.class, "cz.zcu.kiv.test.helper", Operator.EQUAL);
        requirement.addAttribute("version", Version.class, new Version("1.1.0"), Operator.LESS);

        resources = resourceLoader.getResources(repository, requirement);

        assertTrue(resources.contains(resource1));
        assertTrue(resources.contains(resource2));
        assertFalse(resources.contains(resource3));


        // LESS_EQUAL
        requirement = metadataFactory.createRequirement("osgi.wiring.package");

        requirement.addAttribute("name", String.class, "cz.zcu.kiv.test.helper", Operator.EQUAL);
        requirement.addAttribute("version", Version.class, new Version("1.0.1"), Operator.LESS_EQUAL);

        resources = resourceLoader.getResources(repository, requirement);

        assertTrue(resources.contains(resource1));
        assertTrue(resources.contains(resource2));
        assertFalse(resources.contains(resource3));


        // NOT_EQUAL
        requirement = metadataFactory.createRequirement("osgi.wiring.package");

        requirement.addAttribute("name", String.class, "cz.zcu.kiv.test.helper", Operator.EQUAL);
        requirement.addAttribute("version", Version.class, new Version("1.0.1"), Operator.NOT_EQUAL);

        resources = resourceLoader.getResources(repository, requirement);

        assertTrue(resources.contains(resource1));
        assertFalse(resources.contains(resource2));
        assertTrue(resources.contains(resource3));


        // APPROX
        requirement = metadataFactory.createRequirement("osgi.wiring.package");

        requirement.addAttribute("name", String.class, "cz.zcu.kiv.test.helper", Operator.EQUAL);
        requirement.addAttribute("version", Version.class, new Version("1.0.0"), Operator.APPROX);

        resources = resourceLoader.getResources(repository, requirement);

        assertTrue(resources.contains(resource1));
        assertTrue(resources.contains(resource2));
        assertFalse(resources.contains(resource3));
    }


    @Test
    public void testRequirementWithOrOperatorDirective() throws Exception {
        Requirement requirement = metadataFactory.createRequirement("osgi.identity");

        requirement.addAttribute("name", String.class, "cz.zcu.kiv.test-bundle-1.0.0", Operator.EQUAL);
        requirement.addAttribute("name", String.class, "cz.zcu.kiv.other-test-bundle-1.0.0", Operator.EQUAL);
        requirement.setDirective("operator", "or");

        List<Resource> resources = resourceLoader.getResources(repository, requirement);

        assertTrue(resources.contains(resource1));
        assertFalse(resources.contains(resource2));
        assertTrue(resources.contains(resource3));
    }

    @Test
    public void testRequirementWithNotOperatorDirective() throws Exception {
        Requirement requirement = metadataFactory.createRequirement("osgi.wiring.package");

        requirement.addAttribute("name", String.class, "cz.zcu.kiv.test.helper", Operator.EQUAL);
        requirement.addAttribute("version", Version.class, new Version("1.0.0"), Operator.EQUAL);
        requirement.setDirective("operator", "not");

        List<Resource> resources = resourceLoader.getResources(repository, requirement);

        assertFalse(resources.contains(resource1));
        assertTrue(resources.contains(resource2));
        assertTrue(resources.contains(resource3));
    }
}
