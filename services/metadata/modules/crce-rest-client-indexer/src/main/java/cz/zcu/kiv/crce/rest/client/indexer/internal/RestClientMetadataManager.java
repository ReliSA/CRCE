package cz.zcu.kiv.crce.rest.client.indexer.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointBody;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointParameter;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Header;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.StringTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ToJSONTools;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HttpMethod;

/**
 * Inspired by ghessova on 18.04.2018.
 */
public class RestClientMetadataManager {

    private static final Logger logger = LoggerFactory.getLogger(RestClientMetadataManager.class);
    private volatile MetadataFactory metadataFactory;

    /**
     * Constructor for metadataFactory injection.
     * 
     * @param metadataFactory metadata factory
     */
    RestClientMetadataManager(MetadataFactory metadataFactory) {
        this.metadataFactory = metadataFactory;
    }

    /**
     * Convert REST API model represented by a set of endpoint into CRCE metadata and sets them to
     * the resource.
     * 
     * @param resource CRCE resource representing the input component
     * @param endpoints set of REST API endpoint
     * @param framework REST API framework od specification name
     */
    public void setMetadata(Resource resource, Collection<Endpoint> endpoints) {
        // Capability - restimpl identity
        Requirement restClientReq = metadataFactory
                .createRequirement(RestClientMetadataConstants.NS__REST_CLIENT_IDENTITY);
        // restimplIdentityCapability.setAttribute(RestClientMetadataConstants.ATTR__RESTIMPL_FRAMEWORK,
        // framework);
        resource.addRequirement(restClientReq);
        Set<Requirement> endpointRequirements = convertToMetadata(endpoints);
        for (Requirement endpointRequirement : endpointRequirements) {
            resource.addRequirement(endpointRequirement);
            restClientReq.addChild(endpointRequirement);
        }
    }

    /**
     * Converts endpoints into set of requirements
     * 
     * @param endpoints
     * @return Set of reguirements
     */
    private Set<Requirement> convertToMetadata(Collection<Endpoint> endpoints) {
        Set<Requirement> requirements = new HashSet<>();
        for (Endpoint endpoint : endpoints) {
            if (endpoint.getUrl() == null) {
                continue;
            }
            requirements.add(createEndpointRequirement(endpoint));
        }
        return requirements;

    }

    private ArrayList<String> convertEndpointParameterToString(Set<EndpointParameter> parameters) {
        Set<String> output = new HashSet<>();
        for (EndpointParameter parameter : parameters) {
            String json = "{ ";
            if (!StringTools.isEmpty(parameter.getName())) {
                json += "\"key\": " + ToJSONTools.convertString(parameter.getName()) + ", ";
            }
            json += "\"type\": " + ToJSONTools.convertString(parameter.getDataType())
                    + ", \"isArray\": " + parameter.isArray() + " }";
            output.add(json);

        }
        return new ArrayList<>(output);
    }


    private Set<String> convertEndpointParameterToStructures(Set<EndpointParameter> parameters) {
        Set<String> output = new HashSet<>();
        for (EndpointParameter parameter : parameters) {
            String json = parameter.getStructure();
            if (!StringTools.isEmpty(json)) {
                output.add(json);
            }

        }
        return output;
    }

    private Set<String> convertEndpointBodyToStructures(Set<EndpointBody> endpointBodies) {
        Set<String> output = new HashSet<>();
        for (EndpointBody endpointBody : endpointBodies) {
            String json = endpointBody.getStructure();
            if (!StringTools.isEmpty(json)) {
                output.add(json);
            }
        }
        return output;
    }

    private ArrayList<String> convertHeadersToListOfJSON(Set<Header> headers) {
        Set<String> output = new HashSet<>();
        for (Header header : headers) {
            String json = header.getType() + ": " + header.getValue();
            output.add(json);

        }
        return new ArrayList<>(output);
    }

    /**
     * Converts set of Http methods into set of strings
     * 
     * @param enums Http methods
     * @return Stringified http methods
     */
    private Set<String> convertHTTPEnumsToStrings(Set<HttpMethod> enums) {
        Set<String> stringSet = new HashSet<>();
        for (HttpMethod enum_ : enums) {
            stringSet.add(enum_.name());
        }
        return stringSet;
    }

    /**
     * Retrieves requirements from Endpoint variable
     * 
     * @param endpoint Endpoint which will be converted
     * @return Requirement
     */
    private Requirement createEndpointRequirement(Endpoint endpoint) {
        logger.info("New Endpoint=" + endpoint.getUrl() + " numParams="
                + endpoint.getParameters().size());
        Requirement endpointRequirement = metadataFactory
                .createRequirement(RestClientMetadataConstants.NS__REST_CLIENT_ENDPOINT);
        // set attributes (name, paths, methods, consumes, produces)
        setIfSet(endpointRequirement,
                RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_BASEURL,
                endpoint.getBaseUrl());
        setIfSet(endpointRequirement, RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_PATH,
                endpoint.getPath());
        setIfSet(endpointRequirement, RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_METHOD,
                new ArrayList<>(convertHTTPEnumsToStrings(endpoint.getHttpMethods())));

        setIfSet(endpointRequirement,
                RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_HEADER_CONTENT_NEGOTIATION,
                convertHeadersToListOfJSON(endpoint.getContentNegotiation()));

        setIfSet(endpointRequirement,
                RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_HEADER_CONTROLS,
                convertHeadersToListOfJSON(endpoint.getControlsHeaders()));

        setIfSet(endpointRequirement,
                RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_HEADER_AUTHENTICATION_CREDENTIALS,
                convertHeadersToListOfJSON(endpoint.getAuthenticationCredentials()));

        setIfSet(endpointRequirement,
                RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_HEADER_REPRESENTATION,
                convertHeadersToListOfJSON(endpoint.getRepresentation()));

        setIfSet(endpointRequirement,
                RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_HEADER_CONDITIONALS,
                convertHeadersToListOfJSON(endpoint.getConditionals()));

        setIfSet(endpointRequirement,
                RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_HEADER_RESPONSE,
                convertHeadersToListOfJSON(endpoint.getResponseHeaders()));

        setIfSet(endpointRequirement,
                RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_HEADER_REQUEST_CONTEXT,
                convertHeadersToListOfJSON(endpoint.getRequestContext()));

        setIfSet(endpointRequirement,
                RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_REQUEST_BODY_TYPES,
                convertEndpointParameterToString(endpoint.getBodyParameteres()));

        setIfSet(endpointRequirement,
                RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_HEADER_COOKIE,
                convertEndpointParameterToString(endpoint.getCookies()));


        Set<String> structures = new HashSet<>();
        structures.addAll(convertEndpointParameterToStructures(endpoint.getBodyParameteres()));
        structures.addAll(convertEndpointBodyToStructures(endpoint.getExpectedResponses()));
        setIfSet(endpointRequirement,
                RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_HEADER_REQUEST_BODY_STRUCTURES,
                new ArrayList<>(structures));
        setIfSet(endpointRequirement,
                RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_CALLED_FROM,
                new ArrayList<>(endpoint.getDependency()));
        return endpointRequirement;
    }

    /**
     * This function sets <code>attribute</code> of {@link cz.zcu.kiv.crce.metadata.Capability} to a
     * <code>value</code> provided that passed <code>value</code> is not <code>null</code>.
     *
     * @param <T> Datatype of attribute-value pair.
     * @param capability {@link cz.zcu.kiv.crce.metadata.Capability} for which
     *        <code>attribute</code> will be set.
     * @param attribute <code>attribute</code> to set.
     * @param value <code>value</code> to set.
     * @return Returns <code>true</code> if <code>value</code> was set. Returns <code>false</code>
     *         otherwise.
     */
    protected <T> boolean setIfSet(Requirement requirement, AttributeType<T> attribute, T value) {
        if (requirement != null && value != null) {
            requirement.addAttribute(attribute, value);
            return true;
        }
        return false;
    }

    /**
     * This function sets <code>attribute</code> of {@link cz.zcu.kiv.crce.metadata.Property} to a
     * <code>value</code> provided that passed <code>value</code> is not <code>null</code>.
     *
     * @param <T> Datatype of attribute-value pair.
     * @param property {@link cz.zcu.kiv.crce.metadata.Property} for which <code>attribute</code>
     *        will be set.
     * @param attribute <code>attribute</code> to set.
     * @param value <code>value</code> to set.
     * @return Returns <code>true</code> if <code>value</code> was set. Returns <code>false</code>
     *         otherwise.
     */
    protected <T> boolean setIfSet(Property property, AttributeType<T> attribute, T value) {
        if (property != null && value != null) {
            property.setAttribute(attribute, value);
            return true;
        }
        return false;
    }

}
