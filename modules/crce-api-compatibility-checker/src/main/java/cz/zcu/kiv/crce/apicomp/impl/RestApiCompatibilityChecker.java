package cz.zcu.kiv.crce.apicomp.impl;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.CompatibilityCheckResult;
import cz.zcu.kiv.crce.metadata.Capability;

import java.util.List;
import java.util.Set;

/**
 * Compatibility checker for REST API. Expects metadata structure created by
 * 'crce-restimpl-indexer' module.
 */
public class RestApiCompatibilityChecker implements ApiCompatibilityChecker {

    private static final String IDENTITY_CAPABILITY_NAMESPACE = "restimpl.identity";

    @Override
    public boolean isApiSupported(Set<Capability> apiMetadata) {
        // API is supported if it contains capability with namespace 'restimpl.identity'
        // related constants are in internal package of crce-restimpl-indexer module
        return apiMetadata != null &&
                apiMetadata
                .stream()
                .anyMatch(capability -> IDENTITY_CAPABILITY_NAMESPACE.equals(capability.getNamespace()));
    }

    @Override
    public CompatibilityCheckResult compareApis(Set<Capability> api1, Set<Capability> api2) {
        boolean apiCompatible = false;

        // given the structure of metadata created in crce-restimpl-indexer, both
        // capability sets should contain exactly 1 capability
        if (api1.size() != 1 || !isApiSupported(api1)) {
            // todo: log error
            throw new RuntimeException("API 1 is not supported by this checker!");
        }

        if (api2.size() != 1 || !isApiSupported(api2)) {
            // todo: log error
            throw new RuntimeException("API 2 is not supported by this checker!");
        }

        // root capabilities are expected to have only one type
        // of children - capability which describes the endpoint
        // so it can be assumed all children are endpoints
        Capability api1Root = api1.iterator().next();
        Capability api2Root = api1.iterator().next();


        // compare endpoints and their details
        for(Capability api1Endpoint : api1Root.getChildren()) {

            // find endpoint from other api with same metadata
            Capability api2MatchingEndpoint = findMatchingEndpoint(api1Endpoint, api1Root.getChildren());
            if (api2MatchingEndpoint == null) {
                apiCompatible = false;
                break;
            }

            // compare endpoint parameters
            boolean parameterMatch = compareEndpointParameters(api1Endpoint, api2MatchingEndpoint);
            if (!parameterMatch) {
                apiCompatible = false;
                break;
            }

            // compare endpoint responses
            boolean responseMatch = compareEndpointResponses(api1Endpoint, api2MatchingEndpoint);
            if (!responseMatch) {
                apiCompatible = false;
                break;
            }
        }

        // comparison result
        return new CompatibilityCheckResult(apiCompatible, false);
    }

    /**
     * Tries to find endpoint in otherEndpoints so that its metadata matches
     * the endpointMetadata.
     *
     * @param endpointMetadata Metadata of endpoint to be found in otherEndpoints.
     * @param otherEndpoints List of endpoint metadata to search.
     * @return Endpoint metadata or null if not matching endpoint is found.
     */
    private Capability findMatchingEndpoint(Capability endpointMetadata, List<Capability> otherEndpoints) {
        // todo:
        // todo: find out what metadata must be equal and which are optional
        return null;
    }

    /**
     * Compares parameters of two endpoints. Positive result is returned only if
     * all parameters of both endpoints have same order and same type. That is
     * parameters1[i].type == parameters2[i].type. Assuming both endpoints have same
     * parameter count (if not, false is returned).
     *
     * @param endpoint1 Capability containing metadata of the first endpoint.
     * @param endpoint2 Capability containing metadata of the second endpoint.
     * @return True if the parameters of both endpoints match.
     */
    private boolean compareEndpointParameters(Capability endpoint1, Capability endpoint2) {
        // todo:
       return false;
    }

    /**
     * Compares responses of two endpoints. Positive result is returned only if for every
     * response defined for endpoint 1 exists one response defined for endpoint 2 with
     * same status and data type.
     *
     * @param endpoint1 Capability containing metadata of the first endpoint.
     * @param endpoint2 Capability containing metadata of the second endpoint.
     * @return True if the responses of both endpoints match.
     */
    private boolean compareEndpointResponses(Capability endpoint1, Capability endpoint2) {
        // todo:
        return false;
    }
}
