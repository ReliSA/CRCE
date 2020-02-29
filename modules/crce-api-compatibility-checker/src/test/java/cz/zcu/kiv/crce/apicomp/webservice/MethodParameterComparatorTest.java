package cz.zcu.kiv.crce.apicomp.webservice;

import cz.zcu.kiv.crce.apicomp.impl.webservice.MethodParameterComparator;
import cz.zcu.kiv.crce.apicomp.impl.webservice.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MethodParameterComparatorTest {

    /**
     * Compare two endpoints with same parameters
     */
    @Test
    public void testCompare_same() {
        Capability method1 = createMethod1(),
        method2 = createMethod1();

        MethodParameterComparator comparator = new MethodParameterComparator(method1, method2);

        List<Diff> diffs = comparator.compare();
        assertFalse("Empty diff list returned!", diffs.isEmpty());
        for(Diff d : diffs) {
            assertEquals("Wrong diff returned!", Difference.NON, d.getValue());
        }
    }

    /**
     * Compare two methods with different parameters.
     */
    @Test
    public void testCompare_different() {
        Capability method1 = createMethod1(),
                method2 = createMethod2();

        MethodParameterComparator comparator = new MethodParameterComparator(method1, method2);

        List<Diff> diffs = comparator.compare();
        assertFalse("Empty diff list returned!", diffs.isEmpty());
        for(Diff d : diffs) {
            assertEquals("Wrong diff returned!", Difference.UNK, d.getValue());
        }
    }

    /**
     * Compare method with non-optional parameters to method with optional parameters.
     * Should return GEN.
     */
    @Test
    public void testCompare_optional() {
        Capability method1 = createMethod1(),
                method2 = createMethod1_GEN();

        MethodParameterComparator comparator = new MethodParameterComparator(method1, method2);

        List<Diff> diffs = comparator.compare();
        assertFalse("Empty diff list returned!", diffs.isEmpty());
        for(Diff d : diffs) {
            assertEquals("Wrong diff returned!", Difference.GEN, d.getValue());
        }
    }

    private Capability createMethod1() {
        Capability method = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICE_ENDPOINT, "m1");
        TestUtil.addMethodParameter(method, "param1", "string", 0L, 0L, 0L);
        TestUtil.addMethodParameter(method, "param2", "integer", 1L, 0L, 0L);
        TestUtil.addMethodParameter(method, "param3", "boolean", 2L, 0L, 0L);

        return method;
    }

    private Capability createMethod1_GEN() {
        Capability method = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICE_ENDPOINT, "m1");
        TestUtil.addMethodParameter(method, "param1", "string", 0L, 0L, 1L);
        TestUtil.addMethodParameter(method, "param2", "integer", 1L, 0L, 1L);
        TestUtil.addMethodParameter(method, "param3", "boolean", 2L, 0L, 1L);

        return method;
    }

    private Capability createMethod2() {
        Capability method = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICE_ENDPOINT, "m2");
        TestUtil.addMethodParameter(method, "parameter-1", "float", 0L, 0L, 0L);
        TestUtil.addMethodParameter(method, "parameter-2", "char", 1L, 0L, 0L);
        TestUtil.addMethodParameter(method, "parameter-3", "bool", 2L, 0L, 0L);

        return method;
    }
}
