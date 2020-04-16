package cz.zcu.kiv.crce.apicomp.webservice;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.webservice.JsonWspCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.webservice.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import cz.zcu.kiv.crce.metadata.internal.ResourceImpl;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test Json-WSP APIs with MOV.
 */
public class JsonWspCompatibilityCheckerMovTest {
    @Test
    public void testCompareApis_Host() {
        CompatibilityCheckResult result = compareApis(
                createApi1(),
                createApi1_changeHost()
        );


        assertNotNull("Null compatibility returned!", result);
        assertEquals("Wrong difference!", Difference.NON, result.getDiffValue());
        assertTrue("MOV flag should be set!", result.isMov());
    }

    @Test
    public void testCompareApis_Path() {
        CompatibilityCheckResult result = compareApis(
                createApi1(),
                createApi1_changePath()
        );

        assertNotNull("Null compatibility returned!", result);
        assertEquals("Wrong difference!", Difference.NON, result.getDiffValue());
        assertTrue("MOV flag should be set!", result.isMov());
    }

    @Test
    public void testCompareApis_HostPath() {
        CompatibilityCheckResult result = compareApis(
                createApi1(),
                createApi1_changeHostPath()
        );

        assertNotNull("Null compatibility returned!", result);
        assertEquals("Wrong difference!", Difference.NON, result.getDiffValue());
        assertTrue("MOV flag should be set!", result.isMov());
    }

    /**
     * Creates compatibility checker and compares two APIs.
     * @param api1
     * @param api2
     * @return
     */
    private CompatibilityCheckResult compareApis(Resource api1, Resource api2) {
        ApiCompatibilityChecker checker = new JsonWspCompatibilityChecker();
        return checker.compareApis(api1, api2);
    }

    /**
     * Creates WS based on example from Wiki.
     * @return
     */
    private Resource createApi1() {
        Capability ws = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE, "ws1");
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE, "rest");
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__URL, "http://testladon.org:80/proxy.php?path=UserService/jsonwsp");

        Capability e1 = TestUtil.createEndpointCapability("listUsers", null, ws);
        TestUtil.addEndpointParameter(e1, "name_filter", "string", 1L, 0L, 0L);

        Resource r = new ResourceImpl("ws1");
        r.addRootCapability(ws);
        TestUtil.addIdentityCapabilityWithCategory(r, "json-wsp");

        return r;
    }

    /**
     * Creates WS based on example from Wiki but changes the host.
     * @return
     */
    private Resource createApi1_changeHost() {
        Capability ws = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE, "ws1");
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE, "rest");
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__URL, "http://some-other-test.com/proxy.php?path=UserService/jsonwsp");

        Capability e1 = TestUtil.createEndpointCapability("listUsers", null, ws);
        TestUtil.addEndpointParameter(e1, "name_filter", "string", 1L, 0L, 0L);

        Resource r = new ResourceImpl("ws1");
        r.addRootCapability(ws);
        TestUtil.addIdentityCapabilityWithCategory(r, "json-wsp");

        return r;
    }

    /**
     * Same as WS1 but the path to endpoint is different
     */
    private Resource createApi1_changePath() {
        Capability ws = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE, "ws1");
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE, "rest");
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__URL, "http://testladon.org:80/path/to/jsonwsp");

        Capability e1 = TestUtil.createEndpointCapability("listUsers", null, ws);
        TestUtil.addEndpointParameter(e1, "name_filter", "string", 1L, 0L, 0L);

        Resource r = new ResourceImpl("ws1");
        r.addRootCapability(ws);
        TestUtil.addIdentityCapabilityWithCategory(r, "json-wsp");

        return r;
    }


    /**
     * Same as WS1 but the host and path are diffrent.
     * @return
     */
    private Resource createApi1_changeHostPath() {
        Capability ws = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE, "ws1");
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE, "rest");
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__URL, "http://some-other-test.com/path/to/jsonwsp");

        Capability e1 = TestUtil.createEndpointCapability("listUsers", null, ws);
        TestUtil.addEndpointParameter(e1, "name_filter", "string", 1L, 0L, 0L);

        Resource r = new ResourceImpl("ws1");
        r.addRootCapability(ws);
        TestUtil.addIdentityCapabilityWithCategory(r, "json-wsp");

        return r;
    }
}
