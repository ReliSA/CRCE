package cz.zcu.kiv.crce.webservices.indexer.internal;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
     *       <documentation />
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
}
