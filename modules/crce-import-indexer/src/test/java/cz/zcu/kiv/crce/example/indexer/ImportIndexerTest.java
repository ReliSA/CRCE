package cz.zcu.kiv.crce.example.indexer;

import cz.zcu.kiv.crce.example.indexer.internal.ImportIndexer;
import cz.zcu.kiv.crce.example.indexer.namespace.NsExampleNs;
import cz.zcu.kiv.crce.example.indexer.namespace.NsImportPackage;
import cz.zcu.kiv.crce.metadata.*;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;
import cz.zcu.kiv.crce.metadata.internal.MetadataFactoryImpl;
import cz.zcu.kiv.crce.metadata.internal.ResourceImpl;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.service.internal.MetadataServiceImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.*;

public class ImportIndexerTest {

    private static AbstractResourceIndexer indexer;
    private static MetadataService metadataService;

    @BeforeClass
    public static void beforeClass() throws NoSuchFieldException, IllegalAccessException {
        indexer = new ImportIndexer();
        final MetadataFactory metadataFactory = new MetadataFactoryImpl();
        metadataService = new MetadataServiceImpl();

        // set needed fields via reflection
        Field field = MetadataServiceImpl.class.getDeclaredField("metadataFactory");
        field.setAccessible(true);
        field.set(metadataService, metadataFactory);

        field = ImportIndexer.class.getDeclaredField("metadataFactory");
        field.setAccessible(true);
        field.set(indexer, metadataFactory);
        field = ImportIndexer.class.getDeclaredField("metadataService");
        field.setAccessible(true);
        field.set(indexer, metadataService);
    }

    @Test
    public void testIndexJar() throws FileNotFoundException, URISyntaxException {
        // jar name in resource folder
        final String jarName = "/test-import.jar";
        final File testJar = new File(ImportIndexer.class.getResource(jarName).toURI());
        Resource r = new ResourceImpl("test-resource");
        metadataService.addCategory(r, "zip");

        // make sure that the test jar really exists
        assertTrue("Test jar does not exist!", testJar.exists());

        indexer.index(new FileInputStream(testJar), r);

        // test that jar was indexed correctly
        assertTrue("Missing example category", metadataService.getCategories(r).contains("example-category"));

        List<Property> properties = r.getProperties(NsExampleNs.NAMESPACE__EXAMPLE_NS);
        assertEquals("Missing imported packages count", 1, properties.size());
        assertEquals("Imported packages counter should be 1", "1",
                properties.get(0).getAttribute(NsExampleNs.ATTRIBUTE__IMPORT_COUNT).getValue());

        List<Requirement> requirements = r.getRequirements(NsImportPackage.NAMESPACE__JAR_IMPORT);
        assertEquals("Missing imported packages list", 1, requirements.size());
        assertEquals("There should be only one imported package", 1, requirements.get(0)
                .getAttributes(NsImportPackage.ATTRIBUTE__IMPORTED_PACKAGES).size());
        assertEquals("Missing imported package", "org.apache.commons.lang3", requirements.get(0)
                .getAttributes(NsImportPackage.ATTRIBUTE__IMPORTED_PACKAGES).get(0).getValue().get(0));
    }
}
