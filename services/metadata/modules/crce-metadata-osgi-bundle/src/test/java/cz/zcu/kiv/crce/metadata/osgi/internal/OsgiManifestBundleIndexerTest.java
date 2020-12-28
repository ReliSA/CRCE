package cz.zcu.kiv.crce.metadata.osgi.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;
import cz.zcu.kiv.crce.metadata.internal.MetadataFactoryImpl;
import cz.zcu.kiv.crce.metadata.internal.ResourceImpl;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.service.internal.MetadataServiceImpl;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Simple test to check that {@link OsgiManifestBundleIndexer} is capable
 * of indexing generic jars.
 */
public class OsgiManifestBundleIndexerTest {

    private static AbstractResourceIndexer indexer;
    private static MetadataService metadataService;

    @BeforeClass
    public static void beforeClass() throws NoSuchFieldException, IllegalAccessException {
        indexer = new OsgiManifestBundleIndexer();
        final MetadataFactory metadataFactory = new MetadataFactoryImpl();
        metadataService = new MetadataServiceImpl();

        // set needed fields via reflection
        Field field = MetadataServiceImpl.class.getDeclaredField("metadataFactory");
        field.setAccessible(true);
        field.set(metadataService, metadataFactory);

        field = OsgiManifestBundleIndexer.class.getDeclaredField("metadataFactory");
        field.setAccessible(true);
        field.set(indexer, metadataFactory);
        field = OsgiManifestBundleIndexer.class.getDeclaredField("metadataService");
        field.setAccessible(true);
        field.set(indexer, metadataService);

    }

    /**
     * Test jar contains only a manifest file with following parameters:
     * Manifest-Version: 1.0
     * Test-property: test
     */
    @Test
    @Ignore // OsgiManifestBundleIndexer currently only index osgi bundles
    public void testIndexGenericJar() throws FileNotFoundException, URISyntaxException {
        // jar name in resource folder
        final String jarName = "/test.jar";
        final File testJar = new File(OsgiManifestBundleIndexerTest.class.getResource(jarName).toURI());
//        final String testPropName = "Test-property";
//        final String expTestPropValue = "test";
        Resource r = new ResourceImpl("test-resource");

        // make sure that the test jar really exists
        assertTrue("Test jar does not exist!", testJar.exists());

        indexer.index(new FileInputStream(testJar), r);

        // test that jar was indexed correctly
        List<Capability> capabilityList = r.getRootCapabilities();
        assertFalse("No capabilities indexed for test jar!", capabilityList.isEmpty());
        assertFalse("No child capabilities indexed for test jar!", capabilityList.get(0).getChildren().isEmpty());
    }

}
