package cz.zcu.kiv.crce.rest.client.indexer.internal;

import cz.zcu.kiv.crce.metadata.*;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointParameter;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointRequestBody;
import cz.zcu.kiv.crce.rest.client.indexer.config.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by ghessova on 18.04.2018.
 */
public class RestClientMetadataManager {

    private static final Logger logger = LoggerFactory.getLogger(RestClientMetadataManager.class);
    private volatile MetadataFactory metadataFactory;

    /**
     * Constructor for metadataFactory injection.
     * @param metadataFactory metadata factory
     */
    RestClientMetadataManager(MetadataFactory metadataFactory) {
        this.metadataFactory = metadataFactory;
    }

    /**
     * Convert REST API model represented by a set of endpoint into CRCE metadata and sets them to the resource.
     * @param resource CRCE resource representing the input component
     * @param endpoints set of REST API endpoint
     * @param framework REST API framework od specification name
     */
    public void setMetadata(Resource resource, Collection<Endpoint> endpoints) {
        // Capability - restimpl identity
        Requirement restClientReq = metadataFactory.createRequirement(RestClientMetadataConstants.NS__REST_CLIENT_IDENTITY);
        //restimplIdentityCapability.setAttribute(RestClientMetadataConstants.ATTR__RESTIMPL_FRAMEWORK, framework);
        resource.addRequirement(restClientReq);
        Set<Requirement> endpointRequirements = convertToMetadata(endpoints);
        for (Requirement endpointRequirement : endpointRequirements) {
            resource.addRequirement(endpointRequirement);
            restClientReq.addChild(endpointRequirement);
        }
    }

    private Set<Requirement> convertToMetadata(Collection<Endpoint> endpoints) {
        Set<Requirement> capabilities = new HashSet<>();
        for (Endpoint endpoint : endpoints) {
            logger.info("Client call:"+endpoint.toString());
            if (endpoint.getPath() == null){
                continue;
            }
            capabilities.add(createEndpointRequirement(endpoint));
        }
        return capabilities;

    }

    private Set<String> convertHeadersToStrings(Set<Header> consumes){
        Set<String> stringSet = new HashSet<>();

        for (final Header header: consumes){
            stringSet.add(header.getValue());
        }
        return stringSet;
    }

    private Set<String> convertParametersToStringSet(Set<EndpointParameter> params){
        Set<String> stringSet = new HashSet<>();
        for (final EndpointParameter param: params){
            stringSet.add(param.getDataTypeS());
        }
        return stringSet;
    }

    private Set<String> convertRequestBodiesToStringSet(Set<EndpointRequestBody> bodies){
        Set<String> stringSet = new HashSet<>();
        for (final EndpointRequestBody body: bodies){
            stringSet.add(body.getStructure());
        }
        return stringSet;
    }

    private Set<String> convertHTTPEnumsToStrings(Set<Endpoint.HttpMethod> enums){
        Set<String> stringSet = new HashSet<>();
        for (Endpoint.HttpMethod enum_: enums){
            stringSet.add(enum_.name());
        }
        return stringSet;
    }

    private long boolToLong(boolean val){
        return val ? 1 : 0;
    }
    private Requirement createEndpointRequirement(Endpoint endpoint) {
        Requirement endpointCapability = metadataFactory.createRequirement(RestClientMetadataConstants.NS__REST_CLIENT_ENDPOINT);
        // set attributes (name, paths, methods, consumes, produces)
        setIfSet(endpointCapability, RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_URL, endpoint.getUrl());
        setIfSet(endpointCapability, RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_METHOD, new ArrayList<>(convertHTTPEnumsToStrings(endpoint.getHttpMethods())));
        setIfSet(endpointCapability, RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_CONSUMES, new ArrayList<>(convertHeadersToStrings(endpoint.getConsumes())));
        setIfSet(endpointCapability, RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_PRODUCES, new ArrayList<>(convertHeadersToStrings(endpoint.getProduces())));
        setIfSet(endpointCapability, RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_EXPECTS, new ArrayList<>(convertRequestBodiesToStringSet(endpoint.getExpectedResponses())));
        setIfSet(endpointCapability, RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_SENDS, new ArrayList<>(convertRequestBodiesToStringSet(endpoint.getRequestBodies())));
        setIfSet(endpointCapability, RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_PARAMETERS, new ArrayList<>(convertParametersToStringSet(endpoint.getParameters())));

/*        //PARAMETER
        for (EndpointParameter param: endpoint.getParameters()){
            addPropertyIfSet(endpointCapability, createRequestParamProperty(param));
        }

        //BODIES
        for (EndpointRequestBody body: endpoint.getRequestBodies()){
            addPropertyIfSet(endpointCapability, createRequestBodyProperty(body));
        }

        //EXPECTED RESPONSES
        for (EndpointRequestBody body: endpoint.getExpectedResponses()){
            addPropertyIfSet(endpointCapability, createExpectedBodyProperty(body));
        }*/


        return endpointCapability;
    }

    private Property createRequestParamProperty(EndpointParameter parameter) {
        Property property = createParamProperty(parameter, RestClientMetadataConstants.NS_REST_CLIENT_REQUEST_PARAMETER);
        if (parameter == null) {
            return null;
        }
        setIfSet(property, RestClientMetadataConstants.ATTR__REST_CLIENT_PARAMETER_CATEGORY, parameter.getDataType());
        setIfSet(property, RestClientMetadataConstants.ATTR__REST_CLIENT_DATATYPE, parameter.dataTypeToString());
        return property;
    }
    private Property createRequestBodyProperty(EndpointRequestBody body) {
        Property property = createBodyProperty(body, RestClientMetadataConstants.NS_REST_CLIENT_REQUEST_BODY);
        if (property == null) {
            return null;
        }
        return property;
    }
    private Property createExpectedBodyProperty(EndpointRequestBody body) {
        Property property = createBodyProperty(body, RestClientMetadataConstants.NS_REST_CLIENT_RESPONSE_BODY);
        if (property == null) {
            return null;
        }
        return property;
    }
    private Property createParamProperty(EndpointParameter parameter, String propertyNamespace) {
        if (parameter == null) {
            return null;
        }
        Property paramProperty = metadataFactory.createProperty(propertyNamespace);
        //setIfSet(paramProperty, RestimplMetadataConstants.ATTR__RESTIMPL_NAME, parameter.getName());
        setIfSet(paramProperty, RestClientMetadataConstants.ATTR__REST_CLIENT_DATATYPE, parameter.getDataType());
        setIfSet(paramProperty, RestClientMetadataConstants.ATTR__REST_CLIENT_PARAMETER_CATEGORY, parameter.getCategory().name());
        setIfSet(paramProperty, RestClientMetadataConstants.ATTR__REST_CLIENT_IS_ARRAY, boolToLong(parameter.isArray()));
        return paramProperty;
    }
    private Property createBodyProperty(EndpointRequestBody body, String propertyNamespace) {
        if (body == null) {
            return null;
        }
        Property bodyProperty = metadataFactory.createProperty(propertyNamespace);
        setIfSet(bodyProperty, RestClientMetadataConstants.ATTR__REST_CLIENT_STRUCTURE, body.getStructure());
        setIfSet(bodyProperty, RestClientMetadataConstants.ATTR__REST_CLIENT_IS_ARRAY, boolToLong(body.isArray()));
        return bodyProperty;
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
    protected <T> boolean setIfSet(Requirement requirement, AttributeType<T> attribute, T value) {
        if (requirement != null && value != null) {
            requirement.addAttribute(attribute, value);
            return true;
        }
        return false;
    }

    protected <T> boolean setIfSet(Requirement requirement, ListAttributeType attribute, List<String> values) {
        if (requirement != null && values != null) {
            requirement.addAttribute(attribute, values);
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

/*    private boolean addPropertyIfSet(Requirement requirement, Property property) {
        if (requirement != null && property != null) {
            requirement.addProperty(property);
            return true;
        }
        return false;
    }*/

}
