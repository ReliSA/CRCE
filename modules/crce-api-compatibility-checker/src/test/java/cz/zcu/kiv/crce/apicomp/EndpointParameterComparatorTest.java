package cz.zcu.kiv.crce.apicomp;

import cz.zcu.kiv.crce.metadata.Capability;

/**
 * Test case for parameter comparison.
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
