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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WsdlCompatibilityCheckerTest {

    /**
     * Compare API with itself.
     */
    @Test
    public void testCompare_same() {
        Resource api1 = createWS1(),
                api2 = createWS1();

        ApiCompatibilityChecker checker = new WsdlCompatibilityChecker();

        CompatibilityCheckResult result = checker.compareApis(api1, api2);

        assertNotNull("Null compatibility returned!", result);
        assertEquals("Wrong difference!", Difference.NON, result.getDiffValue());
    }

    /**
     * Compare two different WS.
     */
    @Test
    public void testCompare_different() {
        Resource api1 = createWS1(),
                api2 = createWS2();

        ApiCompatibilityChecker checker = new WsdlCompatibilityChecker();

        CompatibilityCheckResult result = checker.compareApis(api1, api2);

        assertNotNull("Null compatibility returned!", result);
        assertEquals("Wrong difference!", Difference.MUT, result.getDiffValue());
    }

    /**
     * Compare API with version of itself that has one extra WS.
     */
    @Test
    public void testCompare_differenceWsINS() {
        Resource api1 = createWS1(),
                api2 = createWS1_wsINS();

        ApiCompatibilityChecker checker = new WsdlCompatibilityChecker();

        CompatibilityCheckResult result = checker.compareApis(api1, api2);

        assertNotNull("Null compatibility returned!", result);
        assertEquals("Wrong difference!", Difference.INS, result.getDiffValue());
    }

    /**
     * Compare API with version of itself that has one extra endpoint.
     */
    @Test
    public void testCompare_differenceEndpointINS() {
        Resource api1 = createWS1(),
                api2 = createWS1_eINS();

        ApiCompatibilityChecker checker = new WsdlCompatibilityChecker();

        CompatibilityCheckResult result = checker.compareApis(api1, api2);

        assertNotNull("Null compatibility returned!", result);
        assertEquals("Wrong difference!", Difference.INS, result.getDiffValue());
    }

    @Test
    public void testCompare_differenceEndpointParamType() {
        Resource api1 = createWS1(),
                api2 = createWS1_paramType();

        ApiCompatibilityChecker checker = new WsdlCompatibilityChecker();

        CompatibilityCheckResult result = checker.compareApis(api1, api2);


        assertNotNull("Null compatibility returned!", result);
        assertEquals("Wrong difference!", Difference.UNK, result.getDiffValue());
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

        Capability e2 = TestUtil.createEndpointCapability("insertTitul", "https://stag-ws.zcu.cz/ws/services/soap/ciselniky", ws1);
        TestUtil.addEndpointParameter(e2, "parameters", "tns:insertTitul", 1L, null, null);
        TestUtil.addEndpointResponse(e2, "tns:insertTitulResponse");

        Resource r = new ResourceImpl(wsId);
        r.addRootCapability(wsRoot);
        return r;
    }

    /**
     * WS1 but with one extra WS.
     * @return
     */
    private Resource createWS1_wsINS() {
        String wsId = "ws1-wsINS";
        Capability wsRoot = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_IDENTITY, wsId);
        wsRoot.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__IDL_VERSION, "1.1");

        // webservice capability containing the endpoints
        Capability ws1 = TestUtil.createWebServiceCapability("ws1", "CiselnikyServiceImplService", "rpc/messaging", wsRoot);

        Capability e1 = TestUtil.createEndpointCapability("getSeznamDomen", "https://stag-ws.zcu.cz/ws/services/soap/ciselniky", ws1);
        TestUtil.addEndpointParameter(e1, "parameters", "tns:getSeznamDomen", 1L, null, null);
        TestUtil.addEndpointResponse(e1, "tns:getSeznamDomenResponse");

        Capability e2 = TestUtil.createEndpointCapability("insertTitul", "https://stag-ws.zcu.cz/ws/services/soap/ciselniky", ws1);
        TestUtil.addEndpointParameter(e2, "parameters", "tns:insertTitul", 1L, null, null);
        TestUtil.addEndpointResponse(e2, "tns:insertTitulResponse");

        Capability ws2 = TestUtil.createWebServiceCapability("ws2", "MistnostiServiceImplService", "rpc/messaging", wsRoot);
        Capability e3 = TestUtil.createEndpointCapability("getSeznamMistnosti", "https://stag-ws.zcu.cz/ws/services/soap/ciselniky", ws2);
        TestUtil.addEndpointParameter(e3, "parameters", "tns:getSeznamMistnosti", 1L, null, null);
        TestUtil.addEndpointResponse(e3, "tns:getSeznamMistnostiResponse");

        Resource r = new ResourceImpl(wsId);
        r.addRootCapability(wsRoot);
        return r;
    }

    /**
     * WS1 but with one extra endpoint.
     * @return
     */
    private Resource createWS1_eINS() {
        String wsId = "ws1-wsINS";
        Capability wsRoot = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_IDENTITY, wsId);
        wsRoot.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__IDL_VERSION, "1.1");

        // webservice capability containing the endpoints
        Capability ws1 = TestUtil.createWebServiceCapability("ws1", "CiselnikyServiceImplService", "rpc/messaging", wsRoot);

        Capability e1 = TestUtil.createEndpointCapability("getSeznamDomen", "https://stag-ws.zcu.cz/ws/services/soap/ciselniky", ws1);
        TestUtil.addEndpointParameter(e1, "parameters", "tns:getSeznamDomen", 1L, null, null);
        TestUtil.addEndpointResponse(e1, "tns:getSeznamDomenResponse");

        Capability e2 = TestUtil.createEndpointCapability("insertTitul", "https://stag-ws.zcu.cz/ws/services/soap/ciselniky", ws1);
        TestUtil.addEndpointParameter(e2, "parameters", "tns:insertTitul", 1L, null, null);
        TestUtil.addEndpointResponse(e2, "tns:insertTitulResponse");

        Capability e3 = TestUtil.createEndpointCapability("getCasovaRada", "https://stag-ws.zcu.cz/ws/services/soap/ciselniky", ws1);
        TestUtil.addEndpointParameter(e3, "parameters", "tns:getCasovaRada", 1L, null, null);
        TestUtil.addEndpointResponse(e3, "tns:getCasovaRadaResponse");


        Resource r = new ResourceImpl(wsId);
        r.addRootCapability(wsRoot);
        return r;
    }

    /**
     * WS1 but with different parameter type.
     * @return
     */
    private Resource createWS1_paramType() {
        String wsId = "ws1";
        Capability wsRoot = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_IDENTITY, wsId);
        wsRoot.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__IDL_VERSION, "1.1");

        // webservice capability containing the endpoints
        Capability ws1 = TestUtil.createWebServiceCapability("ws1", "CiselnikyServiceImplService", "rpc/messaging", wsRoot);

        Capability e1 = TestUtil.createEndpointCapability("getSeznamDomen", "https://stag-ws.zcu.cz/ws/services/soap/ciselniky", ws1);
        TestUtil.addEndpointParameter(e1, "parameters", "tns:getSeznamDomen", 1L, null, null);
        TestUtil.addEndpointResponse(e1, "tns:getSeznamDomenResponse");

        Capability e2 = TestUtil.createEndpointCapability("insertTitul", "https://stag-ws.zcu.cz/ws/services/soap/ciselniky", ws1);
        TestUtil.addEndpointParameter(e2, "parameters", "tns:insertTitul2", 1L, null, null);
        TestUtil.addEndpointResponse(e2, "tns:insertTitulResponse");

        Resource r = new ResourceImpl(wsId);
        r.addRootCapability(wsRoot);
        return r;
    }





    /**
     * Creates WS based on STAG's ciselniky WS that is different from WS1.
     * @return
     */
    private Resource createWS2() {
        String wsId = "ws2";
        Capability wsRoot = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_IDENTITY, wsId);
        wsRoot.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__IDL_VERSION, "1.1");

        // webservice capability containing the endpoints
        Capability ws1 = TestUtil.createWebServiceCapability("ws1", "CiselnikyServiceImplService", "rpc/messaging", wsRoot);

        Capability e1 = TestUtil.createEndpointCapability("getCasovaRada", "https://stag-ws.zcu.cz/ws/services/soap/ciselniky", ws1);
        TestUtil.addEndpointParameter(e1, "parameters", "tns:getCasovaRada", 1L, null, null);
        TestUtil.addEndpointResponse(e1, "tns:getCasovaRadaResponse");

        Capability e2 = TestUtil.createEndpointCapability("getSeznamPracovist", "https://stag-ws.zcu.cz/ws/services/soap/ciselniky", ws1);
        TestUtil.addEndpointParameter(e2, "parameters", "tns:getSeznamPracovist", 1L, null, null);
        TestUtil.addEndpointResponse(e2, "tns:getSeznamPracovistResponse");

        Resource r = new ResourceImpl(wsId);
        r.addRootCapability(wsRoot);
        return r;
    }
}
