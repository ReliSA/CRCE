package cz.zcu.kiv.crce.apicomp.webservice;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.webservice.WadlCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.webservice.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import cz.zcu.kiv.crce.metadata.internal.ResourceImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WadlCompatibilityCheckerTest {

    /**
     * Compare same APIs.
     */
    @Test
    public void testCompare_same() {
        Resource api1 = createWS1(),
                api2 = createWS1();

        ApiCompatibilityChecker compatibilityChecker = new WadlCompatibilityChecker();

        CompatibilityCheckResult res = compatibilityChecker.compareApis(api1, api2);

        assertNotNull("Null result returned!", res);
        assertEquals("Wrong difference!", Difference.NON, res.getDiffValue());
    }

    /**
     * Compare different APIs.
     */
    @Test
    public void testCompare_different() {
        Resource api1 = createWS1(),
                api2 = createWS2();

        ApiCompatibilityChecker compatibilityChecker = new WadlCompatibilityChecker();

        CompatibilityCheckResult res = compatibilityChecker.compareApis(api1, api2);

        assertNotNull("Null result returned!", res);
        assertEquals("Wrong difference!", Difference.MUT, res.getDiffValue());
    }

    /**
     * Compare API with other API that has one extra endpoint.
     */
    @Test
    public void testCompare_differenceINS() {
        Resource api1 = createWS1(),
                api2 = createWS1_INS();

        ApiCompatibilityChecker compatibilityChecker = new WadlCompatibilityChecker();

        CompatibilityCheckResult res = compatibilityChecker.compareApis(api1, api2);

        assertNotNull("Null result returned!", res);
        assertEquals("Wrong difference!", Difference.INS, res.getDiffValue());
    }

    /**
     * Compare API with other API that is missing one endpoint.
     */
    @Test
    public void testCompare_differenceDEL() {
        Resource api1 = createWS1_INS(),
                api2 = createWS1();

        ApiCompatibilityChecker compatibilityChecker = new WadlCompatibilityChecker();

        CompatibilityCheckResult res = compatibilityChecker.compareApis(api1, api2);

        assertNotNull("Null result returned!", res);
        assertEquals("Wrong difference!", Difference.DEL, res.getDiffValue());
    }

    /**
     * Compare API with its generalized version.
     */
    @Test
    public void testCompare_differenceGEN() {
        CompatibilityCheckResult res = compareApis(
                createWS1(),
                createWS1_GEN()
        );

        // result should be SPE because of contravariance
        assertNotNull("Null result returned!", res);
        assertEquals("Wrong difference!", Difference.SPE, res.getDiffValue());
    }

    /**
     * Compare API with its specialized version.
     */
    @Test
    public void testCompare_differenceSPE() {
        CompatibilityCheckResult res = compareApis(
                createWS1_GEN(),
                createWS1()
        );

        // result should be GEN because of contravariance
        assertNotNull("Null result returned!", res);
        assertEquals("Wrong difference!", Difference.GEN, res.getDiffValue());
    }

    @Test
    public void testCompare_parameterOrder() {
        CompatibilityCheckResult res = compareApis(
                createWS1(),
                createWS1_paramOrder()
        );

        // result should be GEN because of contravariance
        assertNotNull("Null result returned!", res);
        assertEquals("Wrong difference!", Difference.NON, res.getDiffValue());
    }

    private CompatibilityCheckResult compareApis(Resource api1, Resource api2) {
        ApiCompatibilityChecker compatibilityChecker = new WadlCompatibilityChecker();

        return compatibilityChecker.compareApis(api1, api2);
    }


    /**
     * Based on Fuel Economy WADL.
     * @return
     */
    private Resource createWS1() {
        Capability ws = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE, "ws1");
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE, "rest");

        Capability e1 = TestUtil.createEndpointCapability("getEmissionsInfo (GET)", "https://www.fueleconomy.gov/ws/rest//vehicle/newemissions", ws);
        TestUtil.addEndpointParameter(e1, "year", "xsd:int", null, null, null);
        TestUtil.addEndpointParameter(e1, "state", "xs:string", null, null, null);
        TestUtil.addEndpointParameter(e1, "id", "xs:string", null, null, null);


        Capability e2 = TestUtil.createEndpointCapability("exportAll (GET)", "https://www.fueleconomy.gov/ws/rest//vehicle/export/all", ws);

        Capability e3 = TestUtil.createEndpointCapability("record (GET)", "https://www.fueleconomy.gov/ws/rest//ftr", ws);
        TestUtil.addEndpointParameter(e3, "app", "xs:string", null, null, null);

        Resource r = new ResourceImpl("ws1");
        r.addRootCapability(ws);
        TestUtil.addIdentityCapabilityWithCategory(r, "wadl");

        return r;
    }

    /**
     * WS1 but endpoint has different parameter order.
     * @return
     */
    private Resource createWS1_paramOrder() {
        Capability ws = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE, "ws1");
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE, "rest");

        Capability e1 = TestUtil.createEndpointCapability("getEmissionsInfo (GET)", "https://www.fueleconomy.gov/ws/rest//vehicle/newemissions", ws);
        TestUtil.addEndpointParameter(e1, "state", "xs:string", null, null, null);
        TestUtil.addEndpointParameter(e1, "id", "xs:string", null, null, null);
        TestUtil.addEndpointParameter(e1, "year", "xsd:int", null, null, null);


        Capability e2 = TestUtil.createEndpointCapability("exportAll (GET)", "https://www.fueleconomy.gov/ws/rest//vehicle/export/all", ws);

        Capability e3 = TestUtil.createEndpointCapability("record (GET)", "https://www.fueleconomy.gov/ws/rest//ftr", ws);
        TestUtil.addEndpointParameter(e3, "app", "xs:string", null, null, null);

        Resource r = new ResourceImpl("ws1");
        r.addRootCapability(ws);
        TestUtil.addIdentityCapabilityWithCategory(r, "wadl");

        return r;
    }

    /**
     * WS1 with one extra endpoint
     * @return
     */
    private Resource createWS1_INS() {
        String wsId = "ws1-ins";
        Capability ws = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE, wsId);
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE, "rest");

        Capability e1 = TestUtil.createEndpointCapability("getEmissionsInfo (GET)", "https://www.fueleconomy.gov/ws/rest//vehicle/newemissions", ws);
        TestUtil.addEndpointParameter(e1, "year", "xsd:int", null, null, null);
        TestUtil.addEndpointParameter(e1, "state", "xs:string", null, null, null);
        TestUtil.addEndpointParameter(e1, "id", "xs:string", null, null, null);


        Capability e2 = TestUtil.createEndpointCapability("exportAll (GET)", "https://www.fueleconomy.gov/ws/rest//vehicle/export/all", ws);

        Capability e3 = TestUtil.createEndpointCapability("record (GET)", "https://www.fueleconomy.gov/ws/rest//ftr", ws);
        TestUtil.addEndpointParameter(e3, "app", "xs:string", null, null, null);

        Capability e4 = TestUtil.createEndpointCapability("getVehicleMenuModelNoEv (GET)", "https://www.fueleconomy.gov/ws/rest//vehicle/menu/modelNoEv", ws);
        TestUtil.addEndpointParameter(e4, "make", "xs:string", null, null, null);
        TestUtil.addEndpointParameter(e4, "year", "xs:string", null, null, null);

        Resource r = new ResourceImpl(wsId);
        r.addRootCapability(ws);
        TestUtil.addIdentityCapabilityWithCategory(r, "wadl");

        return r;
    }

    /**
     * WS1 with some parameters generalized.
     * @return
     */
    private Resource createWS1_GEN() {
        String wsId = "ws1-gen";
        Capability ws = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE, wsId);
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE, "rest");

        Capability e1 = TestUtil.createEndpointCapability("getEmissionsInfo (GET)", "https://www.fueleconomy.gov/ws/rest//vehicle/newemissions", ws);
        TestUtil.addEndpointParameter(e1, "year", "xsd:long", null, null, null);
        TestUtil.addEndpointParameter(e1, "state", "xs:string", null, null, null);
        TestUtil.addEndpointParameter(e1, "id", "xs:string", null, null, null);


        Capability e2 = TestUtil.createEndpointCapability("exportAll (GET)", "https://www.fueleconomy.gov/ws/rest//vehicle/export/all", ws);

        Capability e3 = TestUtil.createEndpointCapability("record (GET)", "https://www.fueleconomy.gov/ws/rest//ftr", ws);
        TestUtil.addEndpointParameter(e3, "app", "xs:string", null, null, null);

        Resource r = new ResourceImpl(wsId);
        r.addRootCapability(ws);
        TestUtil.addIdentityCapabilityWithCategory(r, "wadl");

        return r;
    }

    /**
     * Based on Fuel Economy WADL
     *
     * @return
     */
    private Resource createWS2() {
        Capability ws = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE, "ws2");
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE, "rest");

        Capability e1 = TestUtil.createEndpointCapability("getVehicleMenuOptions (GET)", "https://www.fueleconomy.gov/ws/rest//menu/options", ws);
        TestUtil.addEndpointParameter(e1, "year", "xs:string", null, null, null);
        TestUtil.addEndpointParameter(e1, "make", "xs:string", null, null, null);
        TestUtil.addEndpointParameter(e1, "model", "xs:string", null, null, null);


        Capability e2 = TestUtil.createEndpointCapability("getFuelPrices (GET)", "https://www.fueleconomy.gov/ws/rest//fuelprices", ws);

        Capability e3 = TestUtil.createEndpointCapability("getVehicleMenuModelNoEv (GET)", "https://www.fueleconomy.gov/ws/rest//vehicle/menu/modelNoEv", ws);
        TestUtil.addEndpointParameter(e3, "make", "xs:string", null, null, null);
        TestUtil.addEndpointParameter(e3, "year", "xs:string", null, null, null);

        Resource r = new ResourceImpl("ws2");
        r.addRootCapability(ws);
        TestUtil.addIdentityCapabilityWithCategory(r, "wadl");

        return r;
    }
}
