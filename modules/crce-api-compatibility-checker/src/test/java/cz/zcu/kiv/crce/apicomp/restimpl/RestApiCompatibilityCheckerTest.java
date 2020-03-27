package cz.zcu.kiv.crce.apicomp.restimpl;

import cz.zcu.kiv.crce.apicomp.impl.restimpl.RestApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.restimpl.RestimplIndexerConstants;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import cz.zcu.kiv.crce.metadata.internal.PropertyImpl;
import cz.zcu.kiv.crce.metadata.internal.ResourceImpl;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;


public class RestApiCompatibilityCheckerTest {

    @Test
    public void testIsApiSupported() {
        RestApiCompatibilityChecker checker = new RestApiCompatibilityChecker();

        Resource supportedResource = new ResourceImpl("api");
        supportedResource.addRootCapability(new CapabilityImpl("restimpl.identity", ""));

        Resource notSupportedResource = new ResourceImpl("api");
        notSupportedResource.addRootCapability(new CapabilityImpl("not.supported", ""));

        assertFalse("Empty set should not be supported!",checker.isApiSupported(new ResourceImpl("")));
        assertFalse("Null set should not be supported!",checker.isApiSupported(null));
        assertFalse("Compatibility set with wrong namespace should not be supported", checker.isApiSupported(notSupportedResource));
        assertTrue("Compatibility set with wrong namespace should be supported", checker.isApiSupported(supportedResource));
    }

    @Test(expected = RuntimeException.class)
    public void testCompareApis_api1notSupported() {
        Resource r1 = new ResourceImpl("api1");
        r1.addRootCapability(new CapabilityImpl("namespace", "id"));
        r1.addRootCapability(new CapabilityImpl("namespace2", "id2"));

        Resource r2 = new ResourceImpl("api2");
        r2.addRootCapability(new CapabilityImpl("namespace", "id"));
        r2.addRootCapability(new CapabilityImpl("namespace2", "id2"));

        RestApiCompatibilityChecker checker = new RestApiCompatibilityChecker();

        checker.compareApis(r1, r2);
        fail("Not supported exception expected!");
    }

    /**
     * Compare two APIs with equal metadata.
     */
    @Test
    public void testCompareApis_sameApi() {
        RestApiCompatibilityChecker checker = new RestApiCompatibilityChecker();
        Resource api1 = createMockApi1();
        Resource api2 = createMockApi1();
        CompatibilityCheckResult result = checker.compareApis(api1, api2);

        assertEquals("APIs should be same!", Difference.NON, result.getDiffValue());
    }

    @Test
    public void testCompareApis_differentApi() {
        RestApiCompatibilityChecker checker = new RestApiCompatibilityChecker();
        Resource api1 = createMockApi1();
        Resource api2 = createMockApi2();
        CompatibilityCheckResult result = checker.compareApis(api1, api2);

        assertEquals("APIs should not be same!", Difference.MUT, result.getDiffValue());
    }

    /**
     * Compare APIs with shuffled order of values of List attributes.
     * This should not affect the result.
     */
    @Test
    public void testCompareApis_shuffled() {
        RestApiCompatibilityChecker checker = new RestApiCompatibilityChecker();
        Resource api1 = createMockApi2();
        Resource api2 = createMockApi2_shuffled();
        CompatibilityCheckResult result = checker.compareApis(api1, api2);

        assertEquals("APIs should be same!", Difference.NON, result.getDiffValue());
    }

    @Test
    public void testCompareApis_diffParameterCount() {
        RestApiCompatibilityChecker checker = new RestApiCompatibilityChecker();
        Resource api1 = createMockApi1();
        Resource api2 = createMockApi1_diffParameterCount();

        CompatibilityCheckResult result = checker.compareApis(api1, api2);

        assertEquals("APIs should not be same!", Difference.MUT, result.getDiffValue());
    }

    @Test
    public void testCompareApis_diffParamType() {
        RestApiCompatibilityChecker checker = new RestApiCompatibilityChecker();
        Resource api1 = createMockApi1_diffParameterCount();
        Resource api2 = createMockApi1_paramType();

        CompatibilityCheckResult result = checker.compareApis(api1, api2);

        assertEquals("APIs should not be same!", Difference.UNK, result.getDiffValue());
    }

    @Test
    public void testCompareApis_GENContravariant() {
        RestApiCompatibilityChecker checker = new RestApiCompatibilityChecker();
        Resource api1 = createMockApi3();
        Resource api2 = createMockApi3_GENparam();

        CompatibilityCheckResult result = checker.compareApis(api1, api2);

        assertEquals("APIs should not be same!", Difference.SPE, result.getDiffValue());
    }


    @Test
    public void testCompareApis_SPEContravariant() {
        RestApiCompatibilityChecker checker = new RestApiCompatibilityChecker();
        Resource api1 = createMockApi3_GENparam();
        Resource api2 = createMockApi3();

        CompatibilityCheckResult result = checker.compareApis(api1, api2);

        assertEquals("APIs should not be same!", Difference.GEN, result.getDiffValue());
    }

    @Test
    public void testCompareApis_pathVersion() {
        RestApiCompatibilityChecker checker = new RestApiCompatibilityChecker();
        checker.setIgnoreVersionInPath(true);

        Resource api1 = createMockApi3();
        Resource api2 = createMockApi3_pathVersion();

        CompatibilityCheckResult result = checker.compareApis(api1, api2);

        assertEquals("APIs should be same!", Difference.NON, result.getDiffValue());
        assertTrue("MOV flag should be set when path version is different!", result.isMov());
    }

    /**
     * Creates mock metadata of API.
     *
     * @return
     */
    private Resource createMockApi1() {
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

        Resource api = new ResourceImpl("");
        api.addRootCapability(apiRoot);
        return api;
    }

    /**
     * Same as API 1 but the endpoint has one parameter.
     * @return
     */
    private Resource createMockApi1_diffParameterCount() {
        Resource api = createMockApi1();
        Capability endpoint = api.getRootCapabilities(RestimplIndexerConstants.IDENTITY_CAPABILITY_NAMESPACE).get(0).getChildren().get(0);

        TestUtil.addEndpointParameter(endpoint, "param1",  "String", "category", 0L, "def", 0L);

        return api;
    }

    /**
     * API 1 with parameter but the endpoint parameter type is different.
     * @return
     */
    private Resource createMockApi1_paramType() {
        Resource api = createMockApi1();
        Capability endpoint = api.getRootCapabilities(RestimplIndexerConstants.IDENTITY_CAPABILITY_NAMESPACE).get(0).getChildren().get(0);

        TestUtil.addEndpointParameter(endpoint, "param1",  "Long", "category", 0L, "def", 0L);

        return api;
    }

    /**
     * Create mock metadata of API (but other than createMockApi1())
     *
     * @return
     */
    private Resource createMockApi2() {
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


        Resource api = new ResourceImpl("");
        api.addRootCapability(apiRoot);
        return api;
    }

    /**
     * Same as createMockApi2() but List attribute have different order.
     *
     * @return
     */
    private Resource createMockApi2_shuffled() {
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


        Resource api = new ResourceImpl("");
        api.addRootCapability(apiRoot);
        return api;
    }

    private Resource createMockApi3() {
        // root capability
        Capability apiRoot = new CapabilityImpl(RestimplIndexerConstants.IDENTITY_CAPABILITY_NAMESPACE, "");

        // endpoint
        Capability endpoint1 = TestUtil.createEndpointFor(apiRoot, "org/kiv/zcu/server/App.testEndpoint",
                Arrays.asList("POST", "GET"),
                Collections.singletonList("/object/test"),
                Arrays.asList("application/xml", "application/json"),
                Collections.emptyList()
        );

        TestUtil.addEndpointParameter(endpoint1, "param1", "java/lang/Long", "params", 0L, "0", 0L);

        // endpoint response
        Property responseProperty = new PropertyImpl(RestimplIndexerConstants.NS_RESTIMPL_RESPONSE, "");
        responseProperty.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_ID, "org/kiv/zcu/server/App.testEndpoint0");
        responseProperty.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_STATUS, 200L);


        Resource api = new ResourceImpl("");
        api.addRootCapability(apiRoot);
        return api;
    }

    private Resource createMockApi3_GENparam() {
        // root capability
        Capability apiRoot = new CapabilityImpl(RestimplIndexerConstants.IDENTITY_CAPABILITY_NAMESPACE, "");

        // endpoint
        Capability endpoint1 = TestUtil.createEndpointFor(apiRoot, "org/kiv/zcu/server/App.testEndpoint",
                Arrays.asList("POST", "GET"),
                Collections.singletonList("/object/test"),
                Arrays.asList("application/xml", "application/json"),
                Collections.emptyList()
        );

        TestUtil.addEndpointParameter(endpoint1, "param1", "java/lang/Number", "params", 0L, "0", 0L);

        // endpoint response
        Property responseProperty = new PropertyImpl(RestimplIndexerConstants.NS_RESTIMPL_RESPONSE, "");
        responseProperty.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_ID, "org/kiv/zcu/server/App.testEndpoint0");
        responseProperty.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_STATUS, 200L);


        Resource api = new ResourceImpl("");
        api.addRootCapability(apiRoot);
        return api;
    }

    /**
     * Same as MockApi3 but contains version in path to endpoint
     * @return
     */
    private Resource createMockApi3_pathVersion() {
        // root capability
        Capability apiRoot = new CapabilityImpl(RestimplIndexerConstants.IDENTITY_CAPABILITY_NAMESPACE, "");

        // endpoint
        Capability endpoint1 = TestUtil.createEndpointFor(apiRoot, "org/kiv/zcu/server/App.testEndpoint",
                Arrays.asList("POST", "GET"),
                Collections.singletonList("/object/v1.1/test"),
                Arrays.asList("application/xml", "application/json"),
                Collections.emptyList()
        );

        TestUtil.addEndpointParameter(endpoint1, "param1", "java/lang/Long", "params", 0L, "0", 0L);

        // endpoint response
        Property responseProperty = new PropertyImpl(RestimplIndexerConstants.NS_RESTIMPL_RESPONSE, "");
        responseProperty.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_ID, "org/kiv/zcu/server/App.testEndpoint0");
        responseProperty.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_STATUS, 200L);


        Resource api = new ResourceImpl("");
        api.addRootCapability(apiRoot);
        return api;
    }
}
