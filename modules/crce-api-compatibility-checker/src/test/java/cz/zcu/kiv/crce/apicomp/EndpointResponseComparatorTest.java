package cz.zcu.kiv.crce.apicomp;

import cz.zcu.kiv.crce.apicomp.impl.restimpl.EndpointFeatureComparator;
import cz.zcu.kiv.crce.apicomp.impl.restimpl.EndpointResponseComparator;
import cz.zcu.kiv.crce.apicomp.impl.restimpl.RestimplIndexerConstants;
import cz.zcu.kiv.crce.apicomp.result.DifferenceAggregation;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test case for response comparison. There are 5 endpoints
 * and expected test results can represented as following matrix:
 *
 * todo: when comparing two different endpoints, it should be UNK but it's actually a combination of INS/DEL and thus MUT
 *
 *      1       2       3       4       5
 * 1    NON     INS     GEN     DEL     UNK
 * 2    DEL     NON     MUT     DEL     DEL
 * 3    SPEC    MUT     NON     MUT     UNK
 * 4    INS     INS     MUT     NON     UNK
 * 5    UNK     INS     UNK     UNK     NON
 *
 */
public class EndpointResponseComparatorTest {

    /**
     * Compares endpoint 1 with endpoints 1,2,3,4,5.
     *
     */
    @Test
    public void testCompareEndpoint1WithOthers() {
        Capability e1 = createEndpoint1();
        Capability[] otherEndpoints = allEndpoints();
        Difference[] expectedResults = new Difference[] {Difference.NON, Difference.INS, Difference.GEN, Difference.DEL, Difference.UNK};


        for (int i = 0; i < otherEndpoints.length -1; i++) {

            // e1 with other
            Difference res = compareTwoEndpoints(e1, otherEndpoints[i]);
            assertEquals("Wrong result when comparing endpoint 1 with endpoint "+(i+1)+"!", expectedResults[i], res);

            // other with e1
            res = compareTwoEndpoints(otherEndpoints[i], e1);
            assertEquals("Wrong result when comparing endpoint "+(i+1)+" with endpoint 1!", expectedResults[i].flip(), res);
        }

    }

    /**
     * Wrapper for comparing two response which just returns the result difference.
     *
     * @return
     */
    private Difference compareTwoEndpoints(Capability endpoint, Capability otherEndpoint) {
        EndpointFeatureComparator comparator = new EndpointResponseComparator(endpoint, otherEndpoint);
        List<Diff> diffs =  comparator.compare();
        return DifferenceAggregation.calculateFinalDifferenceFor(diffs);
    }

    /**
     * All endpoints as array.
     *
     * @return
     */
    private Capability[] allEndpoints() {
        return new Capability[] {
                createEndpoint1(),
                createEndpoint2(),
                createEndpoint3(),
                createEndpoint4(),
                createEndpoint5()
        };
    }

    /**
     * Endpoint with response1.
     *
     * @return
     */
    private Capability createEndpoint1() {
        Capability endpoint = new CapabilityImpl(RestimplIndexerConstants.NS__RESTIMPL_ENDPOINT, "e1");
        addResponse1(endpoint);

        return endpoint;
    }

    /**
     * Endpoint with response1 and response2.
     * @return
     */
    private Capability createEndpoint2() {
        Capability endpoint = new CapabilityImpl(RestimplIndexerConstants.NS__RESTIMPL_ENDPOINT, "e2");
        addResponse1(endpoint);
        addResponse2(endpoint);

        return endpoint;
    }

    /**
     * Endpoint with response3.
     * @return
     */
    private Capability createEndpoint3() {
        Capability endpoint = new CapabilityImpl(RestimplIndexerConstants.NS__RESTIMPL_ENDPOINT, "e3");
        addResponse3(endpoint);

        return endpoint;
    }

    /**
     * Endpoint with response4.
     * @return
     */
    private Capability createEndpoint4() {
        Capability endpoint = new CapabilityImpl(RestimplIndexerConstants.NS__RESTIMPL_ENDPOINT, "e4");
        addResponse4(endpoint);

        return endpoint;
    }

    /**
     * Endpoint with response2.
     * @return
     */
    private Capability createEndpoint5() {
        Capability endpoint = new CapabilityImpl(RestimplIndexerConstants.NS__RESTIMPL_ENDPOINT, "e5");
        addResponse2(endpoint);

        return endpoint;
    }

    /**
     * Adds response with two parameters to endpoint capability.
     *
     * Data types are from java.lang package.
     *
     * @return
     */
    private void addResponse1(Capability endpoint) {
        TestUtil.addResponseMetadata(endpoint, "r1", 0L, "java/lang/Integer", 200L);
        TestUtil.addResponseParameter(endpoint, "r1", "param1", "java/lang/Integer", "FORM", 0L);
        TestUtil.addResponseParameter(endpoint, "r1", "param2", "java/lang/String", "FORM", 0L);
    }

    /**
     * Adds another response with two parameters to endpoint capability.
     *
     * Data types are from custom package.
     *
     * @param endpoint
     */
    private void addResponse2(Capability endpoint) {
        TestUtil.addResponseMetadata(endpoint, "r2", 0L, "org.something.Object", 200L);
        TestUtil.addResponseParameter(endpoint, "r2", "param1", "org.something.Integer", "FORM", 0L);
        TestUtil.addResponseParameter(endpoint, "r2", "param2", "org.something.String", "FORM", 0L);
    }

    /**
     * Adds another response with two parameters to endpoint capability.
     *
     * Data types are from java.lang so that some data types from response1 are subtypes to data types
     * this response (they are more generic).
     *
     * @param endpoint
     */
    private void addResponse3(Capability endpoint) {
        TestUtil.addResponseMetadata(endpoint, "r3", 0L, "java/lang/Number", 200L);
        TestUtil.addResponseParameter(endpoint, "r3", "param1", "java/lang/Number", "FORM", 0L);
        TestUtil.addResponseParameter(endpoint, "r3", "param2", "java/lang/String", "FORM", 0L);
    }

    /**
     * Adds response without any parameters to endpoint capability.
     *
     * The response metadata are same as those of response1.
     *
     * @param endpoint
     */
    private void addResponse4(Capability endpoint) {
        TestUtil.addResponseMetadata(endpoint, "r4", 0L, "java/lang/Integer", 200L);
    }
}
