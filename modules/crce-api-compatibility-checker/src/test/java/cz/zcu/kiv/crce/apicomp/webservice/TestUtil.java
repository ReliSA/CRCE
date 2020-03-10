package cz.zcu.kiv.crce.apicomp.webservice;

import cz.zcu.kiv.crce.apicomp.impl.webservice.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import cz.zcu.kiv.crce.metadata.internal.PropertyImpl;

public class TestUtil {

    public static void addEndpointParameter(Capability endpointCapability, String name, String dataType, Long order, Long isArray, Long isOptional) {
        Property parameter = new PropertyImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICE_ENDPOINT_PARAMETER, name);
        parameter.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__NAME, name);
        parameter.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__TYPE, dataType);

        setAttrIfNotNull(parameter, WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__ORDER, order);
        setAttrIfNotNull(parameter, WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__ARRAY, isArray);
        setAttrIfNotNull(parameter, WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__OPTIONAL, isOptional);

        endpointCapability.addProperty(parameter);
    }

    public static void addEndpointResponse(Capability endpointCapability, String type) {
        Property response = new PropertyImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICE_ENDPOINT_RESPONSE, "response");
        response.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__TYPE, type);

        endpointCapability.addProperty(response);
    }

    private static void setAttrIfNotNull(Property parameter, AttributeType attributeType, Object value) {
        if (value != null) {
            parameter.setAttribute(attributeType, value);
        }
    }

    /**
     * Creates webservices that holds endpoints. Used for testing WSDL.
     *
     * @param name
     * @param type
     * @param parentCapability
     */
    public static Capability createWebServiceCapability(String id, String name, String type, Capability parentCapability) {
        Capability ws = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE, id);

        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__NAME, name);
        ws.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE, type);

        if (parentCapability != null) {
            parentCapability.addChild(ws);
        }

        return ws;
    }

    public static Capability createEndpointCapability(String name, String url, Capability parentCapability) {
        Capability endpointC = new CapabilityImpl(WebserviceIndexerConstants.NAMESPACE__WEBSERVICE_ENDPOINT, name);
        endpointC.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME, name);
        endpointC.setAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__URL, url);

        if (parentCapability != null) {
            parentCapability.addChild(endpointC);
        }

        return endpointC;
    }
}
