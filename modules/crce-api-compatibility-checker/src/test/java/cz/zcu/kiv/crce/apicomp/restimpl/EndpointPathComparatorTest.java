package cz.zcu.kiv.crce.apicomp.restimpl;

import cz.zcu.kiv.crce.apicomp.impl.restimpl.EndpointPathComparator;
import cz.zcu.kiv.crce.apicomp.impl.restimpl.RestimplIndexerConstants;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EndpointPathComparatorTest {

    /**
     * Compare paths /rest/data/pet/{id} and /rest/v1/data/pet/{id}.
     */
    @Test
    public void testCompare_ignoreVersion1() {
        final String path1 = "/rest/data/pet/{id}";
        final String path2 = "/rest/v1/data/pet/{id}";

        compareEndpointsWithPaths(path1, path2, true, Difference.NON);
    }

    /**
     * Compare paths /rest/v2/data/pet/{id} and /rest/v1/data/pet/{id}.
     */
    @Test
    public void testCompare_ignoreVersion2() {
        final String path1 = "/rest/v2/data/pet/{id}";
        final String path2 = "/rest/v1/data/pet/{id}";

        compareEndpointsWithPaths(path1, path2, true, Difference.NON);
    }

    /**
     * Compare paths /rest/v2.2/data/pet/{id} and /rest/V1.3.4/data/pet/{id}.
     */
    @Test
    public void testCompare_ignoreVersion3() {
        final String path1 = "/rest/v2.2/data/pet/{id}";
        final String path2 = "/rest/V1.3.4/data/pet/{id}";

        compareEndpointsWithPaths(path1, path2, true, Difference.NON);
    }

    /**
     * Compare paths /rest/v2.2/data/pet/{id} and /rest/V1.3.4/data/pet/{id}.
     */
    @Test
    public void testCompare_withVersion1() {
        final String path1 = "/rest/v2.2/data/pet/{id}";
        final String path2 = "/rest/V1.3.4/data/pet/{id}";

        compareEndpointsWithPaths(path1, path2, false, Difference.MUT);
    }

    /**
     * Compare paths /rest/v2.2/data/pet/{id} and /rest/V1.3.4/data/pet/{id}.
     */
    @Test
    public void testCompare_withVersion2() {
        final String path1 = "/rest/v2.2/data/pet/{id}";
        final String path2 = "/rest/v2.2/data/pet/{id}";

        compareEndpointsWithPaths(path1, path2, false, Difference.NON);
    }

    /**
     * Try to compare paths with unsupported versions.
     */
    @Test
    public void testCompare_ignoreVersionUnsupportedVersion1() {
        final String path1 = "/rest/data/pet/{id}";
        final String path2 = "/rest/version-1/data/pet/{id}";

        compareEndpointsWithPaths(path1, path2, true, Difference.MUT);
    }

    /**
     * Try to compare paths with unsupported versions.
     */
    @Test
    public void testCompare_ignoreVersionUnsupportedVersion2() {
        final String path1 = "/rest/data/pet/{id}";
        final String path2 = "/rest/v1.1.1.1/data/pet/{id}";

        compareEndpointsWithPaths(path1, path2, true, Difference.MUT);
    }


    private void compareEndpointsWithPaths(String path1, String path2, boolean ignoreApiVersion, Difference expectedDifference) {
        Capability endpoint1 = createEndpointWithPath(path1);
        Capability endpoint2 = createEndpointWithPath(path2);

        List<Diff> diffs = new EndpointPathComparator(endpoint1, endpoint2, ignoreApiVersion).compare();
        assertEquals("Wrong number of diffs returned!", 1, diffs.size());
        assertEquals("Wrong diff returned!", expectedDifference, diffs.get(0).getValue());
    }

    private Capability createEndpointWithPath(String path) {
        Capability endpoint = new CapabilityImpl(RestimplIndexerConstants.NS__RESTIMPL_ENDPOINT, "endpoint");
        endpoint.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_PATH, Collections.singletonList(path));
        return  endpoint;
    }
}
