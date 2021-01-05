package cz.zcu.kiv.crce.webservices.indexer.internal;

import cz.zcu.kiv.crce.webservices.indexer.structures.Webservice;
import cz.zcu.kiv.crce.webservices.indexer.structures.wsdl.WebserviceTypeWsdlBindedOperation;
import cz.zcu.kiv.crce.webservices.indexer.structures.wsdl.WebserviceTypeWsdlBinding;
import cz.zcu.kiv.crce.webservices.indexer.structures.wsdl.WebserviceTypeWsdlInterface;
import cz.zcu.kiv.crce.webservices.indexer.structures.wsdl.WebserviceTypeWsdlOperation;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WebserviceTypeWsdlTest {

    /**
     * Regression test for bug in WebserviceTypeWsdl (#18).
     *
     * Following endpoint is used:
     *   <wsdl:port binding="tns:GeneratedNg_studentiServiceImplServiceSoapBinding" name="GeneratedNg_studentiServiceImplPort">
     *     <soap:address location="https://stag-ws.zcu.cz/ws/services/soap/ng_studenti" />
     *   </wsdl:port>
     *
     * Expected url is "https://stag-ws.zcu.cz/ws/services/soap/ng_studenti"
     */
    @Test
    public void testGetWsdl11EndpointUrl() throws ParserConfigurationException {
        final String expectedUrl = "https://stag-ws.zcu.cz/ws/services/soap/ng_studenti";

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();
        Element endpointElement = document.createElement(WebserviceTypeWsdl.WSDL_SERVICE_ENDPOINT_V_1_1);
        endpointElement.setAttribute("binding", "tns:GeneratedNg_studentiServiceImplServiceSoapBinding");
        endpointElement.setAttribute("name", "GeneratedNg_studentiServiceImplPort");

        Element addressElement = document.createElement(WebserviceTypeWsdl.WSDL_ADDRESS);
        addressElement.setAttribute(WebserviceTypeWsdl.WSDL_V_1_1_LOCATION, expectedUrl);
        endpointElement.appendChild(addressElement);

        String url = new WebserviceTypeWsdl(null, null).getWsdl11EndpointUrl(endpointElement);

        assertNotNull("URL is null!", url);
        assertEquals("Wrong URL returned!", expectedUrl, url);
    }

    /**
     * Regression test for bug in WebserviceTypeWsdl (#18).
     *
     * Following endpoint is used:
     *     <endpoint
     *           name="GeneratedNg_studentiServiceImplPort"
     *           binding="tns:GeneratedNg_studentiServiceImplServiceSoapBinding"
     *           address="https://stag-ws.zcu.cz/ws/services/soap/ng_studenti" >
     *     </endpoint>
     *
     * Expected url is "https://stag-ws.zcu.cz/ws/services/soap/ng_studenti"
     */
    @Test
    public void testGetWsdl2EndpointUrl() throws ParserConfigurationException {
        final String expectedUrl = "https://stag-ws.zcu.cz/ws/services/soap/ng_studenti";

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();
        Element endpointElement = document.createElement(WebserviceTypeWsdl.WSDL_SERVICE_ENDPOINT_V_2_0);
        endpointElement.setAttribute("binding", "tns:GeneratedNg_studentiServiceImplServiceSoapBinding");
        endpointElement.setAttribute("name", "GeneratedNg_studentiServiceImplPort");
        endpointElement.setAttribute(WebserviceTypeWsdl.WSDL_ADDRESS, expectedUrl);

        String url = new WebserviceTypeWsdl(null, null).getWsdl2EndpointUrl(endpointElement);

        assertNotNull("URL is null!", url);
        assertEquals("Wrong URL returned!", expectedUrl, url);
    }

    /**
     * Process simple WSDL 2 webservice:
     *
     * <service>
     *     <endpoint
     *          name="GeneratedNg_studentiServiceImplPort"
     *          binding="tns:GeneratedNg_studentiServiceImplServiceSoapBinding"
     *          address="https://stag-ws.zcu.cz/ws/services/soap/ng_studenti" >
     *      </endpoint>
 *   </service>
     *
     */
    @Test
    public void testProcessWebservice_WSDL2() throws ParserConfigurationException {
        final String endpointUrl = "https://stag-ws.zcu.cz/ws/services/soap/ng_studenti";
        final String endpointName = "GeneratedNg_studentiServiceImplPort";
        final String endpointBinding = "GeneratedNg_studentiServiceImplServiceSoapBinding";
        final int expectedEndpointCount = 1;

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();

        Element serviceElement = document.createElement(WebserviceTypeWsdl.WSDL_SERVICE);
        Element endpointElement = document.createElement(WebserviceTypeWsdl.WSDL_SERVICE_ENDPOINT_V_2_0);
        endpointElement.setAttribute("binding", endpointBinding);
        endpointElement.setAttribute("name", endpointName);
        endpointElement.setAttribute(WebserviceTypeWsdl.WSDL_ADDRESS, endpointUrl);
        serviceElement.appendChild(endpointElement);

        List<Webservice> processedWebservices = new ArrayList<>();
        List<WebserviceTypeWsdlBindedOperation> operations = Collections.singletonList(new WebserviceTypeWsdlBindedOperation(endpointName, "", ""));

        List<WebserviceTypeWsdlBinding> processedBindings = new ArrayList<>();
        processedBindings.add(new WebserviceTypeWsdlBinding(endpointBinding, "_interface", operations, "", ""));

        List<WebserviceTypeWsdlInterface> processedInterfaces = new ArrayList<>();
        processedInterfaces.add(new WebserviceTypeWsdlInterface("_interface", Collections.singletonList(new WebserviceTypeWsdlOperation(endpointName, "", null, "", "", null, ""))));

        WebserviceTypeWsdl webserviceTypeWsdl = new WebserviceTypeWsdl(null, null);

        webserviceTypeWsdl.processWebservice(serviceElement, processedWebservices, processedInterfaces, processedBindings, Collections.emptyList());

        assertEquals("Wrong number of services processed", expectedEndpointCount, processedWebservices.size());
        Webservice ws = processedWebservices.get(0);

        assertEquals("Wrong number of endpoints processed", expectedEndpointCount, ws.getEndpoints().size());
        assertEquals("Wrong endpoint name!", endpointName, ws.getEndpoints().get(0).getName());
        assertEquals("Wrong endpoint url!", endpointUrl, ws.getEndpoints().get(0).getUrl());
    }
}
