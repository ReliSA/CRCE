package cz.zcu.kiv.crce.apicomp.mov;

import cz.zcu.kiv.crce.apicomp.impl.mov.ApiDescription;
import cz.zcu.kiv.crce.apicomp.impl.webservice.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.apicomp.webservice.TestUtil;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.assertTrue;

public class ApiDescriptionTest {

    @Test
    public void testFromWsdl() throws MalformedURLException {
        String protocol = "http://";
        String host = "host.com";
        String path = "/path/to/operation";
        String operationName = "operation";
        Capability endpoint = TestUtil.createEndpointCapability(operationName, protocol+host+path, null);
        Capability ws = wrapEndpointWithWsdlWebService(endpoint);

        ApiDescription apiDescription = ApiDescription.fromWsdl(ws);
        assertTrue("Host not in API description!", apiDescription.containsKey(host));
        assertTrue("Path to endpoint not in API description!", apiDescription.get(host).containsKey(path));
        assertTrue("Operation not in endpoint!", apiDescription.get(host).get(path).contains(operationName));
    }


    private Capability wrapEndpointWithWsdlWebService(Capability endpoint) {
        Capability wsRoot = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_IDENTITY, "ws");
        wsRoot.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__IDL_VERSION, "1.1");

        // webservice capability containing the endpoints
        Capability ws1 = TestUtil.createWebServiceCapability("ws1", "TestService", "rpc/messaging", wsRoot);
        ws1.addChild(endpoint);

        return wsRoot;
    }
}
