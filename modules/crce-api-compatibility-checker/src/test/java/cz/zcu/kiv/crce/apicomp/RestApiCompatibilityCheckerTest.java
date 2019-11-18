package cz.zcu.kiv.crce.apicomp;

import cz.zcu.kiv.crce.apicomp.impl.RestApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.RestimplIndexerConstants;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import cz.zcu.kiv.crce.metadata.internal.PropertyImpl;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class RestApiCompatibilityCheckerTest {

    @Test
    public void testIsApiSupported() {
        RestApiCompatibilityChecker checker = new RestApiCompatibilityChecker();

        Capability supportedCapability = new CapabilityImpl("restimpl.identity", "");
        Capability notSupportedCapability = new CapabilityImpl("not.supported", "");

        assertFalse("Empty set should not be supported!",checker.isApiSupported(Collections.emptySet()));
        assertFalse("Null set should not be supported!",checker.isApiSupported(null));
        assertFalse("Compatibility set with wrong namespace should not be supported", checker.isApiSupported(Collections.singleton(notSupportedCapability)));
        assertTrue("Compatibility set with wrong namespace should be supported", checker.isApiSupported(Collections.singleton(supportedCapability)));
    }

    @Test(expected = RuntimeException.class)
    public void testCompareApis_api1notSupported() {
        Set<Capability> api1 = new HashSet<>();
        api1.add(new CapabilityImpl("namespace", "id"));
        api1.add(new CapabilityImpl("namespace2", "id2"));

        Set<Capability> api2 = new HashSet<>();
        api1.add(new CapabilityImpl("namespace", "id"));
        api1.add(new CapabilityImpl("namespace2", "id2"));

        RestApiCompatibilityChecker checker = new RestApiCompatibilityChecker();

        checker.compareApis(api1, api2);
        fail("Not supported exception expected!");
    }

    /**
     * Compare two APIs with equal metadata.
     */
    @Test
    public void testCompareApis_sameApi() {
        RestApiCompatibilityChecker checker = new RestApiCompatibilityChecker();
        Set<Capability> api1 = Collections.singleton(createMockApi1());
        Set<Capability> api2 = Collections.singleton(createMockApi1());
        CompatibilityCheckResult result = checker.compareApis(api1, api2);

        assertEquals("APIs should be same!", Difference.NON, result.getDiffValue());
    }

    @Test
    public void testCompareApis_differentApi() {
        RestApiCompatibilityChecker checker = new RestApiCompatibilityChecker();
        Set<Capability> api1 = Collections.singleton(createMockApi1());
        Set<Capability> api2 = Collections.singleton(createMockApi2());
        CompatibilityCheckResult result = checker.compareApis(api1, api2);

        assertEquals("APIs should not be same!", Difference.UNK, result.getDiffValue());
    }

    /**
     * Compare APIs with shuffled order of values of List attributes.
     * This should not affect the result.
     */
    @Test
    public void testCompareApis_shuffled() {
        RestApiCompatibilityChecker checker = new RestApiCompatibilityChecker();
        Set<Capability> api1 = Collections.singleton(createMockApi2());
        Set<Capability> api2 = Collections.singleton(createMockApi2_shuffled());
        CompatibilityCheckResult result = checker.compareApis(api1, api2);

        assertEquals("APIs should be same!", Difference.NON, result.getDiffValue());
    }

    /**
     * Creates mock metadata of API.
     *
     * @return
     */
    private Capability createMockApi1() {
        // root capability
        Capability apiRoot = new CapabilityImpl(RestimplIndexerConstants.IDENTITY_CAPABILITY_NAMESPACE, "");

        // endpoint
        Capability endpoint1 = new CapabilityImpl(RestimplIndexerConstants.NS__RESTIMPL_ENDPOINT, "");
        apiRoot.addChild(endpoint1);

        // endpoint metadata
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_NAME, "cz/kiv/zcu/server/Server.testEndpoint");
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_METHOD, Collections.singletonList("GET"));
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_PATH, Collections.singletonList("/resource/test"));
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_PRODUCES, Collections.singletonList("application/json"));
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_CONSUMES, Collections.emptyList());

        // endpoint response
        Property responseProperty = new PropertyImpl(RestimplIndexerConstants.NS_RESTIMPL_RESPONSE, "");
        responseProperty.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_ID, "cz/kiv/zcu/server/Server.testEndpoint0");
        responseProperty.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_STATUS, 200L);


        return apiRoot;
    }

    /**
     * Create mock metadata of API (but other than createMockApi1())
     *
     * @return
     */
    private Capability createMockApi2() {
        // root capability
        Capability apiRoot = new CapabilityImpl(RestimplIndexerConstants.IDENTITY_CAPABILITY_NAMESPACE, "");

        // endpoint
        Capability endpoint1 = new CapabilityImpl(RestimplIndexerConstants.NS__RESTIMPL_ENDPOINT, "");
        apiRoot.addChild(endpoint1);

        // endpoint metadata
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_NAME, "org/kiv/zcu/server/App.testEndpoint");
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_METHOD, Arrays.asList("GET", "POST"));
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_PATH, Collections.singletonList("/object/test"));
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_PRODUCES, Arrays.asList("application/json", "application/xml"));
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_CONSUMES, Collections.emptyList());

        // endpoint response
        Property responseProperty = new PropertyImpl(RestimplIndexerConstants.NS_RESTIMPL_RESPONSE, "");
        responseProperty.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_ID, "org/kiv/zcu/server/App.testEndpoint0");
        responseProperty.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_STATUS, 200L);


        return apiRoot;
    }

    /**
     * Same as createMockApi2() but List attribute have different order.
     *
     * @return
     */
    private Capability createMockApi2_shuffled() {
        // root capability
        Capability apiRoot = new CapabilityImpl(RestimplIndexerConstants.IDENTITY_CAPABILITY_NAMESPACE, "");

        // endpoint
        Capability endpoint1 = new CapabilityImpl(RestimplIndexerConstants.NS__RESTIMPL_ENDPOINT, "");
        apiRoot.addChild(endpoint1);

        // endpoint metadata
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_NAME, "org/kiv/zcu/server/App.testEndpoint");
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_METHOD, Arrays.asList("POST", "GET"));
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_PATH, Collections.singletonList("/object/test"));
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_PRODUCES, Arrays.asList("application/xml", "application/json"));
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_CONSUMES, Collections.emptyList());

        // endpoint response
        Property responseProperty = new PropertyImpl(RestimplIndexerConstants.NS_RESTIMPL_RESPONSE, "");
        responseProperty.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_ID, "org/kiv/zcu/server/App.testEndpoint0");
        responseProperty.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_STATUS, 200L);


        return apiRoot;
    }
}
