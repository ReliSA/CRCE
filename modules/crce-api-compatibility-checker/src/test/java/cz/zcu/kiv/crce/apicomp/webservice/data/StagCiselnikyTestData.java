package cz.zcu.kiv.crce.apicomp.webservice.data;

import cz.zcu.kiv.crce.apicomp.impl.webservice.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.apicomp.webservice.TestUtil;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import cz.zcu.kiv.crce.metadata.internal.ResourceImpl;

/**
 * Class containing STAG test data as defined in WSDL resources. These test data are used mainly for fine debugging
 * of 'real' data.
 */
public class StagCiselnikyTestData {

    /**
     * V2 of STAG test API.
     * @return
     */
    public static Resource v2() {
        String wsId = "ws1";
        String url = "https://stag-ws-update.zcu.cz/ws/services/soap/ciselniky";

        Capability wsRoot = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_IDENTITY, wsId);
        wsRoot.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__IDL_VERSION, "1.1");

        // webservice capability containing the endpoints
        Capability ws1 = TestUtil.createWebServiceCapability("ws1", "CiselnikyServiceImplService", "rpc/messaging", wsRoot);

        addWsdlEndpoint(ws1, "insertPracoviste", url);
        addWsdlEndpoint(ws1, "insertTitul", url);
        addTestEndpoint(ws1, url, "xs:int");

        Resource r = new ResourceImpl(wsId);
        TestUtil.addIdentityCapabilityWithCategories(r, "ws-schema","wsdl");
        r.addRootCapability(wsRoot);
        return r;
    }

    /**
     * V5 of STAG test API.
     *
     * @return
     */
    public static Resource v5() {
        String wsId = "ws1";
        String url = "https://stag-ws.zcu.cz/ws/services/soap/ciselniky";

        Capability wsRoot = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_IDENTITY, wsId);
        wsRoot.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__IDL_VERSION, "1.1");

        // webservice capability containing the endpoints
        Capability ws1 = TestUtil.createWebServiceCapability("ws1", "CiselnikyServiceImplService", "rpc/messaging", wsRoot);

        addWsdlEndpoint(ws1, "insertPracoviste", url);
        addWsdlEndpoint(ws1, "insertTitul", url);
        addTestEndpoint(ws1, url, "xs:long");

        Resource r = new ResourceImpl(wsId);
        TestUtil.addIdentityCapabilityWithCategories(r, "ws-schema","wsdl");
        r.addRootCapability(wsRoot);
        return r;
    }

    /**
     * Creates new capability with endpoint and adds it to wsCapability.
     *
     * @param wsCapability Capability to add endpoint to.
     * @param endpointName Name of the endpoint, used to construct the name of dataType for parameters and response.
     */
    private static void addWsdlEndpoint(Capability wsCapability, String endpointName) {
        addWsdlEndpoint(wsCapability, endpointName, "https://stag-ws.zcu.cz/ws/services/soap/ciselniky");
    }

    /**
     * Creates new capability with endpoint and adds it to wsCapability.
     *
     * @param wsCapability Capability to add endpoint to.
     * @param endpointName Name of the endpoint, used to construct the name of dataType for parameters and response.
     */
    private static void addWsdlEndpoint(Capability wsCapability, String endpointName, String endpointUrl) {
        Capability e = TestUtil.createEndpointCapability(endpointName, endpointUrl, wsCapability);
        TestUtil.addEndpointParameter(e, "parameters", "tns:"+endpointName, 1L, null, null);
        TestUtil.addEndpointResponse(e, "tns:"+endpointName+"Response");
    }

    /**
     * Adds 'testOperation' endpoint as defined in source WSDLs.
     * @param wsCapability
     */
    private static void addTestEndpoint(Capability wsCapability, String url, String dataType) {
        Capability e = TestUtil.createEndpointCapability("testOperation", url, wsCapability);
        TestUtil.addEndpointParameter(e, "parameters", dataType, 1L, null, null);
        TestUtil.addEndpointResponse(e, dataType);
    }
}
