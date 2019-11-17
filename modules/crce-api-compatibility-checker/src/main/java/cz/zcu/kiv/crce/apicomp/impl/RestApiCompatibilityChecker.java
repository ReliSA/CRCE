package cz.zcu.kiv.crce.apicomp.impl;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.CompatibilityCheckResult;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.impl.DefaultDiffImpl;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;

import java.util.*;
import java.util.stream.Collectors;

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
        CompatibilityCheckResult checkResult = new CompatibilityCheckResult();
        List<Diff> diffs = new ArrayList<>();

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
        // copy is created because we're going to be removing
        // processed endpoints
        Iterator<Capability> endpoint1It = new ArrayList<>(api1Root.getChildren()).iterator();
        List<Capability> otherEndpoints = new ArrayList<>(api2Root.getChildren());

        while(endpoint1It.hasNext()) {
            Capability api1Endpoint = endpoint1It.next();

            // find endpoint from other api with same metadata
            Capability api2MatchingEndpoint = pullMatchingEndpoint(api1Endpoint, otherEndpoints);
            Diff endpointDiff = new DefaultDiffImpl();
            checkResult.getDiffDetails().add(endpointDiff);
            endpointDiff.setLevel(DifferenceLevel.OPERATION);
            if (api2MatchingEndpoint == null) {
                // API 2 does not contain endpoint from API 1
                // -> deletion
                endpointDiff.setValue(Difference.DEL);
            }
            endpointDiff.setValue(Difference.NON);

            // compare endpoint parameters
            diffs = compareEndpointParameters(api1Endpoint, api2MatchingEndpoint);
            if (!diffs.isEmpty()) {
                endpointDiff.addChildren(diffs);
                endpointDiff.setValue(Difference.MUT);
                apiCompatible = false;
            }

            // compare endpoint responses
            diffs = compareEndpointResponses(api1Endpoint, api2MatchingEndpoint);
            if (!diffs.isEmpty()) {
                endpointDiff.addChildren(diffs);
                endpointDiff.setValue(Difference.MUT);
                apiCompatible = false;
            }


            // difference found ?  -> add diff to result list
            if (!endpointDiff.getValue().equals(Difference.NON)) {
                diffs.add(endpointDiff);
            }

            // endpoint processed, remove it
            endpoint1It.remove();

        }

        // remaining endpoints
        for (Capability api2Endpoint : api1Root.getChildren()) {
            Diff diff = new DefaultDiffImpl();
            diff.setLevel(DifferenceLevel.OPERATION);
            // api 1 does not contain endpoint from API 2
            // -> insertion
            diff.setValue(Difference.INS);

            diffs.add(diff);
        }


        // comparison result
        return new CompatibilityCheckResult();
    }

    /**
     * Tries to find endpoint in otherEndpoints so that its metadata matches
     * the endpointMetadata. If the endpoint is found, it will be removed from otherEndpoints.
     *
     * @param endpointMetadata Metadata of endpoint to be found in otherEndpoints.
     * @param otherEndpoints List of endpoint metadata to search.
     * @return Endpoint metadata or null if not matching endpoint is found.
     */
    // todo: what if there is more matches?
    private Capability pullMatchingEndpoint(Capability endpointMetadata, List<Capability> otherEndpoints) {
        Capability match = null;
        Iterator<Capability> otherEndpointsIt = otherEndpoints.iterator();

        while(otherEndpointsIt.hasNext()) {
            Capability otherE = otherEndpointsIt.next();
            if (endpointMetadataMatch(endpointMetadata, otherE)) {
                match = otherE;
                otherEndpointsIt.remove();
                break;
            }
        }

        return match;
    }

    private boolean endpointMetadataMatch(Capability endpoint1Metadata, Capability endpoint2Metadata) {

        // attributes to be compared
        List<AttributeType> attributeTypes = Arrays.asList(
                RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_METHOD,
                RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_PATH,
                RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_CONSUMES,
                RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_PRODUCES
        );

        for (AttributeType at : attributeTypes) {
            Attribute a1 = endpoint1Metadata.getAttribute(at);
            Attribute a2 = endpoint2Metadata.getAttribute(at);
            if (!a1.equals(a2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Compares parameters of two endpoints. Positive result is returned only if
     * all parameters of both endpoints have same order and same type. That is
     * parameters1[i].type == parameters2[i].type.
     *
     * @param endpoint1 Capability containing metadata of the first endpoint.
     * @param endpoint2 Capability containing metadata of the second endpoint.
     * @return Diffs between endpoint parameters. Empty collection if the endpoints have same parameters
     */
    private List<Diff> compareEndpointParameters(Capability endpoint1, Capability endpoint2) {
        List<Property> endpoint1Params = endpoint1.getProperties().stream()
                .filter(p -> RestimplIndexerConstants.NS_RESTIMPL_REQUESTPARAMETER.equals(p.getNamespace()))
                .collect(Collectors.toList());


        List<Property> endpoint2Params = endpoint2.getProperties().stream()
                .filter(p -> RestimplIndexerConstants.NS_RESTIMPL_REQUESTPARAMETER.equals(p.getNamespace()))
                .collect(Collectors.toList());

        List<Diff> parameterDiffs = new ArrayList<>();

        Iterator<Property> p1i = endpoint1Params.iterator();
        Iterator<Property> p2i = endpoint2Params.iterator();
        while(p1i.hasNext() && p2i.hasNext()) {
            compareParameters(p1i.next(), p2i.next(), parameterDiffs);
            p1i.remove();
            p2i.remove();
        }

        // add INS and DEL parameters to diff
        // todo:

        return parameterDiffs;
    }

    /**
     * Compares responses of two endpoints. Positive result is returned only if for every
     * response defined for endpoint 1 exists one response defined for endpoint 2 with
     * same status and data type.
     *
     * @param endpoint1 Capability containing metadata of the first endpoint.
     * @param endpoint2 Capability containing metadata of the second endpoint.
     * @return Diffs between endpoint responses. Empty collection if the endpoints have same responses.
     */
    private List<Diff> compareEndpointResponses(Capability endpoint1, Capability endpoint2) {
        // todo:
        return Collections.emptyList();
    }

    /**
     * Compares two parameters and adds diff to diffs if they are not same.
     *
     * @param param1
     * @param param2
     */
    private void compareParameters(Property param1, Property param2, List<Diff> diffs) {
        Diff diff = new DefaultDiffImpl();
        diff.setLevel(DifferenceLevel.FIELD);
        diff.setValue(Difference.NON);

        // attributes to compare
        Attribute name1 = param1.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_NAME);
        Attribute name2 = param2.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_NAME);

        Attribute dt1 = param1.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_DATETYPE);
        Attribute dt2 = param2.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_DATETYPE);

        Attribute cat1 = param1.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_PARAMETER_CATEGEORY);
        Attribute cat2 = param2.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_PARAMETER_CATEGEORY);

        // todo: check nulls
        // todo: does it matter that the name is different?
        // todo: handle cases such as short <: long, float <: double ...
        if (!name1.equals(name2)
            || !dt1.equals(dt2)
            || !cat1.equals(cat2)) {
            diff.setValue(Difference.UNK);
            diff.setNamespace(RestimplIndexerConstants.NS_RESTIMPL_REQUESTPARAMETER);
        }

        // add diff if needed
        if (!diff.getValue().equals(Difference.NON)) {
            diffs.add(diff);
        }
    }
}
