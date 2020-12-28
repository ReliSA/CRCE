package cz.zcu.kiv.crce.apicomp.webservice.wsdl;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.webservice.common.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.apicomp.impl.webservice.wsdl.WsdlCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.apicomp.webservice.TestUtil;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import cz.zcu.kiv.crce.metadata.internal.ResourceImpl;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test cases related to the MOV flag
 */
public class WsdlCompatibilityCheckerMovTest {

    @Test
    public void testCompareApis_Host() {
        CompatibilityCheckResult result = compareApis(
                createWS1(),
                createWS1_changeHost()
        );


        assertNotNull("Null compatibility returned!", result);
        assertEquals("Wrong difference!", Difference.NON, result.getDiffValue());
        assertTrue("MOV flag should be set!", result.movFlagSet());
    }

    @Test
    public void testCompareApis_Path() {
        CompatibilityCheckResult result = compareApis(
                createWS1(),
                createWS1_changePath()
        );

        assertNotNull("Null compatibility returned!", result);
        assertEquals("Wrong difference!", Difference.NON, result.getDiffValue());
        assertTrue("MOV flag should be set!", result.movFlagSet());
    }

    @Test
    public void testCompareApis_HostPath() {
        CompatibilityCheckResult result = compareApis(
                createWS1(),
                createWS1_changeHostPath()
        );

        assertNotNull("Null compatibility returned!", result);
        assertEquals("Wrong difference!", Difference.NON, result.getDiffValue());
        assertTrue("MOV flag should be set!", result.movFlagSet());
    }

    /**
     * Creates compatibility checker and compares two APIs.
     * @param api1
     * @param api2
     * @return
     */
    private CompatibilityCheckResult compareApis(Resource api1, Resource api2) {
        ApiCompatibilityChecker checker = new WsdlCompatibilityChecker();
        return checker.compareApis(api1, api2);
    }

    /**
     * Creates WS based on STAG's ciselniky WS.
     * @return
     */
    private Resource createWS1() {
        String wsId = "ws1";
        Capability wsRoot = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_IDENTITY, wsId);
        wsRoot.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__IDL_VERSION, "1.1");

        // webservice capability containing the endpoints
        Capability ws1 = TestUtil.createWebServiceCapability("ws1", "CiselnikyServiceImplService", "rpc/messaging", wsRoot);

        Capability e1 = TestUtil.createEndpointCapability("getSeznamDomen", "https://stag-ws.zcu.cz/ws/services/soap/ciselniky", ws1);
        TestUtil.addEndpointParameter(e1, "parameters", "tns:getSeznamDomen", 1L, null, null);
        TestUtil.addEndpointResponse(e1, "tns:getSeznamDomenResponse");

        Resource r = new ResourceImpl(wsId);
        r.addRootCapability(wsRoot);
        TestUtil.addIdentityCapabilityWithCategory(r, "wsdl");
        return r;
    }

    /**
     * Creates WS based on STAG's ciselniky WS but change the host.
     * @return
     */
    private Resource createWS1_changeHost() {
        String wsId = "ws1";
        Capability wsRoot = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_IDENTITY, wsId);
        wsRoot.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__IDL_VERSION, "1.1");

        // webservice capability containing the endpoints
        Capability ws1 = TestUtil.createWebServiceCapability("ws1", "CiselnikyServiceImplService", "rpc/messaging", wsRoot);

        Capability e1 = TestUtil.createEndpointCapability("getSeznamDomen", "https://another-host.zcu.cz/ws/services/soap/ciselniky", ws1);
        TestUtil.addEndpointParameter(e1, "parameters", "tns:getSeznamDomen", 1L, null, null);
        TestUtil.addEndpointResponse(e1, "tns:getSeznamDomenResponse");

        Resource r = new ResourceImpl(wsId);
        r.addRootCapability(wsRoot);
        TestUtil.addIdentityCapabilityWithCategory(r, "wsdl");
        return r;
    }

    /**
     * Same as WS1 but the path to endpoint is different
     */
    private Resource createWS1_changePath() {
        String wsId = "ws1";
        Capability wsRoot = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_IDENTITY, wsId);
        wsRoot.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__IDL_VERSION, "1.1");

        // webservice capability containing the endpoints
        Capability ws1 = TestUtil.createWebServiceCapability("ws1", "CiselnikyServiceImplService", "rpc/messaging", wsRoot);

        Capability e1 = TestUtil.createEndpointCapability("getSeznamDomen", "https://stag-ws.zcu.cz/ws/services-changed/soap-moved/ciselniky", ws1);
        TestUtil.addEndpointParameter(e1, "parameters", "tns:getSeznamDomen", 1L, null, null);
        TestUtil.addEndpointResponse(e1, "tns:getSeznamDomenResponse");

        Resource r = new ResourceImpl(wsId);
        r.addRootCapability(wsRoot);
        TestUtil.addIdentityCapabilityWithCategory(r, "wsdl");
        return r;
    }


    /**
     * Same as WS1 but the host and path are diffrent.
     * @return
     */
    private Resource createWS1_changeHostPath() {
        String wsId = "ws1";
        Capability wsRoot = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_IDENTITY, wsId);
        wsRoot.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__IDL_VERSION, "1.1");

        // webservice capability containing the endpoints
        Capability ws1 = TestUtil.createWebServiceCapability("ws1", "CiselnikyServiceImplService", "rpc/messaging", wsRoot);

        Capability e1 = TestUtil.createEndpointCapability("getSeznamDomen", "https://another-host.zcu.cz/ws/services-changed/soap-moved/ciselniky", ws1);
        TestUtil.addEndpointParameter(e1, "parameters", "tns:getSeznamDomen", 1L, null, null);
        TestUtil.addEndpointResponse(e1, "tns:getSeznamDomenResponse");

        Resource r = new ResourceImpl(wsId);
        r.addRootCapability(wsRoot);
        TestUtil.addIdentityCapabilityWithCategory(r, "wsdl");
        return r;
    }


}
