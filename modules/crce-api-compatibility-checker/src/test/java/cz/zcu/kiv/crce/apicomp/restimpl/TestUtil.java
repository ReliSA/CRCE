package cz.zcu.kiv.crce.apicomp.restimpl;

import cz.zcu.kiv.crce.apicomp.impl.restimpl.RestimplIndexerConstants;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import cz.zcu.kiv.crce.metadata.internal.PropertyImpl;

import java.util.List;

public class TestUtil {

    public static void addResponseMetadata(Capability endpointCapability, String id, Long isArray, String dataType, Long status) {
        Property metadata = new PropertyImpl(RestimplIndexerConstants.NS_RESTIMPL_RESPONSE, id);
        metadata.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_ID, id);
        metadata.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ARRAY, isArray);
        metadata.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_DATETYPE, dataType);
        metadata.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_STATUS, status);

        endpointCapability.addProperty(metadata);
    }

    public static void addResponseParameter(Capability endpointCapability, String id, String paramName, String dataType, String category, Long isArray) {
        Property parameter = new PropertyImpl(RestimplIndexerConstants.NS_RESTIMPL_RESPONSEPARAMETER, id);
        parameter.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_ID, id);
        parameter.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_NAME, paramName);
        parameter.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_DATETYPE, dataType);
        parameter.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_PARAMETER_CATEGEORY, category);
        parameter.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ARRAY, isArray);

        endpointCapability.addProperty(parameter);
    }

    public static void addEndpointParameter(Capability endpointCapability, String name, String dataType, String category, Long isArray, String defaultValue, Long isOptional) {
        Property parameter = new PropertyImpl(RestimplIndexerConstants.NS_RESTIMPL_REQUESTPARAMETER, name);
        parameter.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_NAME, name);
        parameter.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_DATETYPE, dataType);
        parameter.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_PARAMETER_CATEGEORY, category);
        parameter.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ARRAY, isArray);
        parameter.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_DEFAULT_VALUE, defaultValue);
        parameter.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_OPTIONAL, isOptional);

        endpointCapability.addProperty(parameter);
    }

    public static Capability createEndpointFor(Capability rootCapability, String name, List<String> methods, List<String> paths, List<String> produces, List<String> consumes) {
        Capability endpoint1 = new CapabilityImpl(RestimplIndexerConstants.NS__RESTIMPL_ENDPOINT, "");
        if (rootCapability != null) {
            rootCapability.addChild(endpoint1);
        }

        // endpoint metadata
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_NAME, name);
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_METHOD, methods);
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_PATH, paths);
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_PRODUCES, produces);
        endpoint1.setAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_CONSUMES, consumes);

        return endpoint1;
    }
}
