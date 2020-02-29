package cz.zcu.kiv.crce.apicomp.restimpl;

import cz.zcu.kiv.crce.metadata.Capability;

/**
 * Test case for parameter comparison.
 *
 * todo: matrix of endpoints and respective test results
 */
public class EndpointParameterComparatorTest {

    private void addParameter1(Capability endpoint) {
        TestUtil.addEndpointParameter(endpoint,
                "param1",
                "java.lang.Integer",
                "FORM",
                0L,
                "1",
                0L);
    }
}
