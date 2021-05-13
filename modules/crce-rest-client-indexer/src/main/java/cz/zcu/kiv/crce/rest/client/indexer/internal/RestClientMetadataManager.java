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
            logger.info("Client call:" + endpoint.toString());
            if (endpoint.getPath() == null) {
                continue;
            }
            requirements.add(createEndpointRequirement(endpoint));
        }
        return requirements;

    }

    /**
     * Converts Headers into set of strings
     * 
     * @param consumes
     * @return Set of strings
     */
    private Set<String> convertHeadersToStrings(Set<Header> consumes) {
        Set<String> stringSet = new HashSet<>();

        for (final Header header : consumes) {
            stringSet.add(header.getValue());
        }
        return stringSet;
    }

    /**
     * Converts endpoint parameters int set of strings
     * 
     * @param params
     * @return Set of strings
     */
    private Set<String> convertParametersToStringSet(Set<EndpointParameter> params) {
        Set<String> stringSet = new HashSet<>();
        for (final EndpointParameter param : params) {
            stringSet.add(param.getDataTypeS());
        }
        return stringSet;
    }

    /**
     * Converts request bodies into set of strings
     * 
     * @param bodies Request bodies
     * @return Set of strings
     */
    private Set<String> convertRequestBodiesToStringSet(Set<EndpointRequestBody> bodies) {
        Set<String> stringSet = new HashSet<>();
        for (final EndpointRequestBody body : bodies) {
            stringSet.add(body.getStructure());
        }
        return stringSet;
    }

    /**
     * Converts set of Http methods into set of strings
     * 
     * @param enums Http methods
     * @return Stringified http methods
     */
    private Set<String> convertHTTPEnumsToStrings(Set<Endpoint.HttpMethod> enums) {
        Set<String> stringSet = new HashSet<>();
        for (Endpoint.HttpMethod enum_ : enums) {
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
        Requirement endpointRequirement = metadataFactory
                .createRequirement(RestClientMetadataConstants.NS__REST_CLIENT_ENDPOINT);
        // set attributes (name, paths, methods, consumes, produces)
        setIfSet(endpointRequirement, RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_URL,
                endpoint.getUrl());
        setIfSet(endpointRequirement, RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_METHOD,
                new ArrayList<>(convertHTTPEnumsToStrings(endpoint.getHttpMethods())));
        setIfSet(endpointRequirement,
                RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_CONSUMES,
                new ArrayList<>(convertHeadersToStrings(endpoint.getConsumes())));
        setIfSet(endpointRequirement,
                RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_PRODUCES,
                new ArrayList<>(convertHeadersToStrings(endpoint.getProduces())));
        setIfSet(endpointRequirement,
                RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_EXPECTS,
                new ArrayList<>(convertRequestBodiesToStringSet(endpoint.getExpectedResponses())));
        setIfSet(endpointRequirement, RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_SENDS,
                new ArrayList<>(convertRequestBodiesToStringSet(endpoint.getRequestBodies())));
        setIfSet(endpointRequirement,
                RestClientMetadataConstants.ATTR__REST_CLIENT_ENDPOINT_PARAMETERS,
                new ArrayList<>(convertParametersToStringSet(endpoint.getParameters())));

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

    protected <T> boolean setIfSet(Requirement requirement, ListAttributeType attribute,
            List<String> values) {
        if (requirement != null && values != null) {
            requirement.addAttribute(attribute, values);
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
