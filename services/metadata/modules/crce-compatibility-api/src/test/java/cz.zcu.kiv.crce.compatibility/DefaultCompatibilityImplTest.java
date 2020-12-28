package cz.zcu.kiv.crce.compatibility;

import cz.zcu.kiv.crce.compatibility.impl.DefaultCompatibilityFactoryImpl;
import cz.zcu.kiv.crce.compatibility.impl.DefaultDiffImpl;
import cz.zcu.kiv.crce.metadata.type.Version;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test of default implementation of {@link Compatibility} interface.
 */
public class DefaultCompatibilityImplTest {

    private static CompatibilityFactory factory;

    @BeforeClass
    public static void beforeClass() {
        factory = new DefaultCompatibilityFactoryImpl();
    }

    /**
     * Test that factory returns correct compatibility.
     */
    @Test
    public void factoryTest() {
        final String id = "id";
        final String resourceName = "resourceName";
        final Version resourceVersion = new Version(1,0,0);
        final String baseName = "baseName";
        final Version baseVersion = new Version(1,0,0);
        final Difference diffValue = Difference.NON;
        final List<Diff> diffValues = new ArrayList<>();
        diffValues.add(new DefaultDiffImpl());
        final Contract contract = Contract.SYNTAX;

        // create compatibilities
        Compatibility c1 = factory.createCompatibility(id,
                resourceName, resourceVersion, baseName,
                baseVersion, diffValue, diffValues, contract);

        Compatibility c2 = factory.createCompatibility(id,
                resourceName, resourceVersion, baseVersion,
                diffValue, diffValues, contract);

        // checks
        checkCompatibility(c1, id, resourceName, resourceVersion, baseName,
                baseVersion, diffValue, diffValues, contract);
        checkCompatibility(c2, id, resourceName, resourceVersion, resourceName,
                baseVersion, diffValue, diffValues, contract);
    }

    /**
     * Checks that the compatibility is not null and all necessary fields are set correctly.
     */
    private void checkCompatibility(Compatibility toCheck, String id, String resourceName, Version resourceVersion, String baseName,
                                    Version baseVersion, Difference diffValue, List<Diff> diffValues, Contract contract) {
        assertNotNull("Compatibility is null!", toCheck );
        assertEquals("Wrong id!", id, toCheck.getId());
        assertEquals("Wrong resource name!", resourceName, toCheck.getResourceName());
        assertEquals("Wrong base name!", baseName, toCheck.getBaseResourceName());
        assertEquals("Wrong base version!", baseVersion, toCheck.getBaseResourceVersion());
        assertEquals("Wrong difference value!", diffValue, toCheck.getDiffValue());
        assertEquals("Wrong number of difference values!", diffValues.size(), toCheck.getDiffDetails().size());
        assertEquals("Wrong contract!", contract, toCheck.getContract());
    }
}
