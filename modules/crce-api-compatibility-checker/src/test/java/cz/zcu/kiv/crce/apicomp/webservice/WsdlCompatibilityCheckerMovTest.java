package cz.zcu.kiv.crce.apicomp.webservice;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.webservice.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.apicomp.impl.webservice.WsdlCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
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
        Resource api1 = createWS1(),
                api2 = createWS1_changeHost();

        ApiCompatibilityChecker checker = new WsdlCompatibilityChecker();

        CompatibilityCheckResult result = checker.compareApis(api1, api2);


        assertNotNull("Null compatibility returned!", result);
        assertEquals("Wrong difference!", Difference.NON, result.getDiffValue());
        assertTrue("MOV flag should be set!", result.isMov());
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
        return r;
    }


}
