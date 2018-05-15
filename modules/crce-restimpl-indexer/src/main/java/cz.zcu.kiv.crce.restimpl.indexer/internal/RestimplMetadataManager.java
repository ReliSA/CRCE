package cz.zcu.kiv.crce.restimpl.indexer.internal;

import cz.zcu.kiv.crce.metadata.*;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.Endpoint;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.EndpointParameter;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.EndpointRequestBody;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.EndpointResponse;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.RequestParameter;

import java.util.*;

/**
 * Created by ghessova on 18.04.2018.
 */
public class RestimplMetadataManager {

    private volatile MetadataFactory metadataFactory;

    /**
     * Constructor for metadataFactory injection.
     * @param metadataFactory metadata factory
     */
    RestimplMetadataManager(MetadataFactory metadataFactory) {
        this.metadataFactory = metadataFactory;
    }

    /**
     * Convert REST API model represented by a set of endpoint into CRCE metadata and sets them to the resource.
     * @param resource CRCE resource representing the input component
     * @param endpoints set of REST API endpoint
     * @param framework REST API framework od specification name
     */
    public void setMetadata(Resource resource, Collection<Endpoint> endpoints, String framework) {
        // Capability - restimpl identity
        Capability restimplIdentityCapability = metadataFactory.createCapability(RestimplMetadataConstants.NS__RESTIMPL_IDENTITY);
        restimplIdentityCapability.setAttribute(RestimplMetadataConstants.ATTR__RESTIMPL_FRAMEWORK, framework);
        resource.addCapability(restimplIdentityCapability);
        resource.addRootCapability(restimplIdentityCapability);
        Set<Capability> endpointCapabilities = convertToMetadata(endpoints);
        for (Capability endpointCapability : endpointCapabilities) {
            resource.addCapability(endpointCapability);
            restimplIdentityCapability.addChild(endpointCapability);

        }
    }

    private Set<Capability> convertToMetadata(Collection<Endpoint> endpoints) {
        Set<Capability> capabilities = new HashSet<>();
        for (Endpoint endpoint : endpoints) {
            capabilities.add(createEndpointCapability(endpoint));
        }
        return capabilities;

    }

    private Capability createEndpointCapability(Endpoint endpoint) {
        Capability endpointCapability = metadataFactory.createCapability(RestimplMetadataConstants.NS__RESTIMPL_ENDPOINT);

        // set attributes (name, paths, methods, consumes, produces)
        setIfSet(endpointCapability, RestimplMetadataConstants.ATTR__RESTIMPL_NAME, endpoint.getName());
        setIfSet(endpointCapability, RestimplMetadataConstants.ATTR__RESTIMPL_ENDPOINT_PATH, endpoint.getPaths());
        setIfSet(endpointCapability, RestimplMetadataConstants.ATTR__RESTIMPL_ENDPOINT_METHOD, new ArrayList<>(endpoint.getHttpMethods()));
        setIfSet(endpointCapability, RestimplMetadataConstants.ATTR__RESTIMPL_ENDPOINT_CONSUMES, new ArrayList<>(endpoint.getConsumes()));
        setIfSet(endpointCapability, RestimplMetadataConstants.ATTR__RESTIMPL_ENDPOINT_PRODUCES, new ArrayList<>(endpoint.getProduces()));

        // add corresponding properties - body, parameters, responses, cookies, headers
        addPropertyIfSet(endpointCapability, createBodyProperty(endpoint.getBody()));

        // request parameters
        for (RequestParameter parameter : endpoint.getParameters()) {
            addPropertyIfSet(endpointCapability, createRequestParamProperty(parameter));
        }

        // responses
        int i = 0;
        for (EndpointResponse response : endpoint.getResponses()) {
            String responseId = endpoint.getName() + i++; // todo not unique
            addPropertyIfSet(endpointCapability, createResponseProperty(response, responseId));
            // response cookies and headers
            for (EndpointParameter parameter : response.getParameters()) {
                addPropertyIfSet(endpointCapability, createResponseParamProperty(parameter, responseId));
            }
        }

        return endpointCapability;
    }

    private Property createBodyProperty(EndpointRequestBody body) {
        if (body == null) {
            return null;
        }
        Property bodyProperty = metadataFactory.createProperty(RestimplMetadataConstants.NS_RESTIMPL_REQUEST_BODY);
        setIfSet(bodyProperty, RestimplMetadataConstants.ATTR__RESTIMPL_DATETYPE, body.getStructure());
        setIfSet(bodyProperty, RestimplMetadataConstants.ATTR__RESTIMPL_ARRAY, body.isArray() ? (long)1 : 0);
        setIfSet(bodyProperty, RestimplMetadataConstants.ATTR__RESTIMPL_OPTIONAL, body.isOptional() ? (long)1 : 0);
        return bodyProperty;
    }

    private Property createRequestParamProperty(RequestParameter parameter) {
        Property property = createParamProperty(parameter, RestimplMetadataConstants.NS_RESTIMPL_REQUESTPARAMETER);
        if (parameter == null) {
            return null;
        }
        setIfSet(property, RestimplMetadataConstants.ATTR__RESTIMPL_DEFAULT_VALUE, parameter.getDefaultValue());
        setIfSet(property, RestimplMetadataConstants.ATTR__RESTIMPL_OPTIONAL, parameter.isOptional() ? (long)1 : 0);
        return property;
    }

    private Property createResponseParamProperty(EndpointParameter parameter, String responseId) {
        Property property = createParamProperty(parameter, RestimplMetadataConstants.NS_RESTIMPL_RESPONSEPARAMETER);
        if (parameter == null) {
            return null;
        }
        setIfSet(property, RestimplMetadataConstants.ATTR__RESTIMPL_RESPONSE_ID, responseId);

        return property;
    }

    private Property createParamProperty(EndpointParameter parameter, String propertyNamespace) {
        if (parameter == null) {
            return null;
        }
        Property paramProperty = metadataFactory.createProperty(propertyNamespace);
        setIfSet(paramProperty, RestimplMetadataConstants.ATTR__RESTIMPL_NAME, parameter.getName());
        setIfSet(paramProperty, RestimplMetadataConstants.ATTR__RESTIMPL_DATETYPE, parameter.getDataType());
        setIfSet(paramProperty, RestimplMetadataConstants.ATTR__RESTIMPL_PARAMETER_CATEGEORY, parameter.getCategory().toString());
        setIfSet(paramProperty, RestimplMetadataConstants.ATTR__RESTIMPL_ARRAY, parameter.isArray() ? (long)1 : 0);
        return paramProperty;
    }

    private Property createResponseProperty(EndpointResponse response, String id) {
        if (response == null) {
            return null;
        }
        Property responseProperty = metadataFactory.createProperty(RestimplMetadataConstants.NS_RESTIMPL_RESPONSE);
        setIfSet(responseProperty, RestimplMetadataConstants.ATTR__RESTIMPL_RESPONSE_ID, id);
        setIfSet(responseProperty, RestimplMetadataConstants.ATTR__RESTIMPL_DATETYPE, response.getStructure());
        setIfSet(responseProperty, RestimplMetadataConstants.ATTR__RESTIMPL_RESPONSE_STATUS, (long)response.getStatus());
        setIfSet(responseProperty, RestimplMetadataConstants.ATTR__RESTIMPL_ARRAY, response.isArray() ? (long)1 : 0);
        return responseProperty;
    }

    /**
     * This function sets <code>attribute</code> of {@link cz.zcu.kiv.crce.metadata.Capability} to a <code>value</code> provided that passed <code>value</code>
     * is not <code>null</code>.
     *
     * @param <T> Datatype of attribute-value pair.
     * @param capability {@link cz.zcu.kiv.crce.metadata.Capability} for which <code>attribute</code> will be set.
     * @param attribute <code>attribute</code> to set.
     * @param value <code>value</code> to set.
     * @return Returns <code>true</code> if <code>value</code> was set. Returns <code>false</code> otherwise.
     */
    protected <T> boolean setIfSet(Capability capability, AttributeType<T> attribute, T value) {
        if (capability != null && value != null) {
            capability.setAttribute(attribute, value);
            return true;
        }
        return false;
    }

    protected <T> boolean setIfSet(Capability capability, ListAttributeType attribute, List<String> values) {
        if (capability != null && values != null) {
            capability.setAttribute(attribute, values);
            return true;
        }
        return false;
    }

    /**
     * This function sets <code>attribute</code> of {@link cz.zcu.kiv.crce.metadata.Property} to a <code>value</code> provided that passed <code>value</code>
     * is not <code>null</code>.
     *
     * @param <T> Datatype of attribute-value pair.
     * @param property {@link cz.zcu.kiv.crce.metadata.Property} for which <code>attribute</code> will be set.
     * @param attribute <code>attribute</code> to set.
     * @param value <code>value</code> to set.
     * @return Returns <code>true</code> if <code>value</code> was set. Returns <code>false</code> otherwise.
     */
    protected <T> boolean setIfSet(Property property, AttributeType<T> attribute, T value) {
        if (property != null && value != null) {
            property.setAttribute(attribute, value);
            return true;
        }
        return false;
    }

    private boolean addPropertyIfSet(Capability capability, Property property) {
        if (capability != null && property != null) {
            capability.addProperty(property);
            return true;
        }
        return false;
    }

}
