package cz.zcu.kiv.crce.apicomp.webservice.wadl;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.webservice.common.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.apicomp.impl.webservice.wadl.WadlCompatibilityChecker;
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
 * Test WADL APIs with MOV.
 */
public class WadlCompatibilityCheckerMovTest {

    @Test
    public void testCompareApis_Host() {
        CompatibilityCheckResult result = compareApis(
                createApi1(),
                createApi1_changeHost()
        );


        assertNotNull("Null compatibility returned!", result);
        assertEquals("Wrong difference!", Difference.NON, result.getDiffValue());
        assertTrue("MOV flag should be set!", result.movFlagSet());
    }

    @Test
    public void testCompareApis_Path() {
        CompatibilityCheckResult result = compareApis(
                createApi1(),
                createApi1_changePath()
        );

        assertNotNull("Null compatibility returned!", result);
        assertEquals("Wrong difference!", Difference.NON, result.getDiffValue());
        assertTrue("MOV flag should be set!", result.movFlagSet());
    }

    @Test
    public void testCompareApis_HostPath() {
        CompatibilityCheckResult result = compareApis(
                createApi1(),
                createApi1_changeHostPath()
        );

        assertNotNull("Null compatibility returned!", result);
        assertEquals("Wrong difference!", Difference.NON, result.getDiffValue());
        assertTrue("MOV flag should be set!", result.movFlagSet());
    }

    /**
     * Two APIs with DEL difference and
     */
    @Test
    public void testCompareApis_DELNoMov() {
        CompatibilityCheckResult result = compareApis(
                createApi1_INS(),
                createApi1()
        );

        assertNotNull("Null compatibility returned!", result);
        assertEquals("Wrong difference!", Difference.DEL, result.getDiffValue());
        assertFalse("MOV flag should be set!", result.movFlagSet());
    }

    /**
     * Creates compatibility checker and compares two APIs.
     * @param api1
     * @param api2
     * @return
     */
    private CompatibilityCheckResult compareApis(Resource api1, Resource api2) {
        ApiCompatibilityChecker checker = new WadlCompatibilityChecker();
        return checker.compareApis(api1, api2);
    }

    /**
     * Creates WS based on STAG's ciselniky WS.
     * @return
     */
    private Resource createApi1() {
        Capability ws = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE, "ws1");
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE, "rest");

        Capability e1 = TestUtil.createEndpointCapability("getEmissionsInfo (GET)", "https://www.fueleconomy.gov/ws/rest//vehicle/newemissions", ws);
        TestUtil.addEndpointParameter(e1, "year", "xsd:int", null, null, null);
        TestUtil.addEndpointParameter(e1, "state", "xs:string", null, null, null);
        TestUtil.addEndpointParameter(e1, "id", "xs:string", null, null, null);

        Resource r = new ResourceImpl("ws1");
        r.addRootCapability(ws);
        TestUtil.addIdentityCapabilityWithCategory(r, "wadl");

        return r;
    }

    /**
     * Creates WS based on STAG's ciselniky WS but change the host.
     * @return
     */
    private Resource createApi1_changeHost() {
        Capability ws = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE, "ws1");
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE, "rest");

        Capability e1 = TestUtil.createEndpointCapability("getEmissionsInfo (GET)", "https://www.fuel-changed-host.gov/ws/rest//vehicle/newemissions", ws);
        TestUtil.addEndpointParameter(e1, "year", "xsd:int", null, null, null);
        TestUtil.addEndpointParameter(e1, "state", "xs:string", null, null, null);
        TestUtil.addEndpointParameter(e1, "id", "xs:string", null, null, null);

        Resource r = new ResourceImpl("ws1");
        r.addRootCapability(ws);
        TestUtil.addIdentityCapabilityWithCategory(r, "wadl");

        return r;
    }

    /**
     * Same as WS1 but the path to endpoint is different
     */
    private Resource createApi1_changePath() {
        Capability ws = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE, "ws1");
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE, "rest");

        Capability e1 = TestUtil.createEndpointCapability("getEmissionsInfo (GET)", "https://www.fueleconomy.gov/ws/restapi/here//vehicle/newemissions", ws);
        TestUtil.addEndpointParameter(e1, "year", "xsd:int", null, null, null);
        TestUtil.addEndpointParameter(e1, "state", "xs:string", null, null, null);
        TestUtil.addEndpointParameter(e1, "id", "xs:string", null, null, null);

        Resource r = new ResourceImpl("ws1");
        r.addRootCapability(ws);
        TestUtil.addIdentityCapabilityWithCategory(r, "wadl");

        return r;
    }


    /**
     * Same as WS1 but the host and path are diffrent.
     * @return
     */
    private Resource createApi1_changeHostPath() {
        Capability ws = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE, "ws1");
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE, "rest");

        Capability e1 = TestUtil.createEndpointCapability("getEmissionsInfo (GET)", "https://www.fuel-changed-host.gov/ws/restapi/here//vehicle/newemissions", ws);
        TestUtil.addEndpointParameter(e1, "year", "xsd:int", null, null, null);
        TestUtil.addEndpointParameter(e1, "state", "xs:string", null, null, null);
        TestUtil.addEndpointParameter(e1, "id", "xs:string", null, null, null);

        Resource r = new ResourceImpl("ws1");
        r.addRootCapability(ws);
        TestUtil.addIdentityCapabilityWithCategory(r, "wadl");

        return r;
    }

    /**
     * Api 1 with one additional endpoint with same operation name as the first one.
     * @return
     */
    private Resource createApi1_INS() {
        Capability ws = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE, "ws1");
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE, "rest");

        Capability e1 = TestUtil.createEndpointCapability("getEmissionsInfo (GET)", "https://www.fueleconomy.gov/ws/rest//vehicle/newemissions", ws);
        TestUtil.addEndpointParameter(e1, "year", "xsd:int", null, null, null);
        TestUtil.addEndpointParameter(e1, "state", "xs:string", null, null, null);
        TestUtil.addEndpointParameter(e1, "id", "xs:string", null, null, null);

        TestUtil.createEndpointCapability("getEmissionsInfo (GET)", "https://www.fueleconomy.gov/ws/rest//vehicle/oldemissions", ws);

        Resource r = new ResourceImpl("ws1");
        r.addRootCapability(ws);
        TestUtil.addIdentityCapabilityWithCategory(r, "wadl");

        return r;
    }
}
