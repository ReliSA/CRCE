package cz.zcu.kiv.crce.compatibility.dao.internal.mapping;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.DBObject;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.compatibility.CompatibilityFactory;
import cz.zcu.kiv.crce.compatibility.Contract;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.DifferenceRole;
import cz.zcu.kiv.crce.metadata.type.Version;

/**
 * Date: 15.3.14
 *
 * @author Jakub Danek
 */
public class MongoCompatibilityMapperTest {

    /**
     * Test implementation of CompatibilityFactory
     */
    static class CompatibilityFactoryTestImpl implements CompatibilityFactory {
        @Override
        public Compatibility createCompatibility(String id, String resourceName, Version resourceVersion, String baseName, Version baseVersion, Difference diffValue, List<Diff> diffValues, Contract contract) {
            return new CompatibilityTestImpl(id, resourceName, resourceVersion, baseName, baseVersion, diffValue, diffValues, contract);
        }

        @Override
        public Compatibility createCompatibility(String id, String resourceName, Version resourceVersion, Version baseVersion, Difference diffValue, List<Diff> diffValues, Contract contract) {
            return new CompatibilityTestImpl(id, resourceName, resourceVersion, resourceName, baseVersion, diffValue, diffValues, contract);
        }

        @Override
        public Diff createEmptyDiff() {
            return new DiffTestImpl();
        }
    }

    private static CompatibilityFactory factory;

    @BeforeClass
    public static void beforeClass() throws Exception {
        factory = new CompatibilityFactoryTestImpl();
    }

    @Test
    public void consistencyTest() throws Exception {
        Diff root = factory.createEmptyDiff();
        root.setLevel(DifferenceLevel.PACKAGE);
        ;
        root.setNamespace("osgi.wiring.package");
        root.setName("cz.zcu.kiv");
        root.setRole(DifferenceRole.CAPABILITY);
        root.setValue(Difference.MUT);

        Diff child = factory.createEmptyDiff();
        root.setLevel(DifferenceLevel.TYPE);
        ;
        root.setName("cz.zcu.kiv.Clazz1");
        root.setValue(Difference.DEL);
        root.addChild(child);

        child = factory.createEmptyDiff();
        root.setLevel(DifferenceLevel.TYPE);
        ;
        root.setName("cz.zcu.kiv.Clazz2");
        root.setValue(Difference.INS);
        root.addChild(child);

        List<Diff> diffs = new ArrayList<>();
        diffs.add(root);

        Compatibility toMap = factory.createCompatibility(null, "cz.kiv.zcu.TestName",
                new Version(1, 25, 33, "ahoj"), new Version(2, 0, 55), Difference.MUT, diffs, Contract.SYNTAX);

        DBObject obj = MongoCompatibilityMapper.mapToDbObject(toMap);

        Compatibility res = MongoCompatibilityMapper.mapToCompatibility(obj, factory);

        assertEquals(toMap, res);
    }
}
