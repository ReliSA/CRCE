package cz.zcu.kiv.crce.apicomp.webservice;

import cz.zcu.kiv.crce.apicomp.impl.webservice.EndpointParameterComparator;
import cz.zcu.kiv.crce.apicomp.impl.webservice.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EndpointParameterComparatorTest {

    /**
     * Compare two endpoints with same parameters
     */
    @Test
    public void testCompare_same() {
        Capability endpoint1 = createEndpoint1(),
        endpoint2 = createEndpoint1();

        EndpointParameterComparator comparator = new EndpointParameterComparator(endpoint1, endpoint2);

        List<Diff> diffs = comparator.compare();
        assertFalse("Empty diff list returned!", diffs.isEmpty());
        for(Diff d : diffs) {
            assertEquals("Wrong diff returned!", Difference.NON, d.getValue());
        }
    }

    /**
     * Compare two endpoints with different parameters.
     */
    @Test
    public void testCompare_different() {
        Capability endpoint1 = createEndpoint1(),
                endpoint2 = createEndpoint2();

        EndpointParameterComparator comparator = new EndpointParameterComparator(endpoint1, endpoint2);

        List<Diff> diffs = comparator.compare();
        assertFalse("Empty diff list returned!", diffs.isEmpty());
        for(Diff d : diffs) {
            assertEquals("Wrong diff returned!", Difference.UNK, d.getValue());
        }
    }

    /**
     * Compare endpoint with non-optional parameters to endpoint with optional parameters.
     * Should return GEN.
     */
    @Test
    public void testCompare_optional() {
        Capability endpoint1 = createEndpoint1(),
                endpoint2 = createEndpoint1_GEN();

        EndpointParameterComparator comparator = new EndpointParameterComparator(endpoint1, endpoint2);

        List<Diff> diffs = comparator.compare();
        assertFalse("Empty diff list returned!", diffs.isEmpty());
        for(Diff d : diffs) {
            assertEquals("Wrong diff returned!", Difference.GEN, d.getValue());
        }
    }

    private Capability createEndpoint1() {
        Capability endpoint = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICE_ENDPOINT, "m1");
        TestUtil.addEndpointParameter(endpoint, "param1", "string", 0L, 0L, 0L);
        TestUtil.addEndpointParameter(endpoint, "param2", "integer", 1L, 0L, 0L);
        TestUtil.addEndpointParameter(endpoint, "param3", "boolean", 2L, 0L, 0L);

        return endpoint;
    }

    private Capability createEndpoint1_GEN() {
        Capability endpoint = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICE_ENDPOINT, "m1");
        TestUtil.addEndpointParameter(endpoint, "param1", "string", 0L, 0L, 1L);
        TestUtil.addEndpointParameter(endpoint, "param2", "integer", 1L, 0L, 1L);
        TestUtil.addEndpointParameter(endpoint, "param3", "boolean", 2L, 0L, 1L);

        return endpoint;
    }

    private Capability createEndpoint2() {
        Capability endpoint = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICE_ENDPOINT, "m2");
        TestUtil.addEndpointParameter(endpoint, "parameter-1", "float", 0L, 0L, 0L);
        TestUtil.addEndpointParameter(endpoint, "parameter-2", "char", 1L, 0L, 0L);
        TestUtil.addEndpointParameter(endpoint, "parameter-3", "bool", 2L, 0L, 0L);

        return endpoint;
    }
}
