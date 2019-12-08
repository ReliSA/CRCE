package cz.zcu.kiv.crce.apicomp.impl;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.apicomp.result.DifferenceAggregation;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.impl.DefaultDiffImpl;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Compatibility checker for REST API. Expects metadata structure created by
 * 'crce-restimpl-indexer' module.
 */
public class RestApiCompatibilityChecker implements ApiCompatibilityChecker {


    @Override
    public boolean isApiSupported(Set<Capability> apiMetadata) {
        // API is supported if it contains capability with namespace 'restimpl.identity'
        // related constants are in internal package of crce-restimpl-indexer module
        return apiMetadata != null &&
                apiMetadata
                .stream()
                .anyMatch(capability -> RestimplIndexerConstants.IDENTITY_CAPABILITY_NAMESPACE.equals(capability.getNamespace()));
    }

    @Override
    public CompatibilityCheckResult compareApis(Set<Capability> api1, Set<Capability> api2) {
        CompatibilityCheckResult checkResult = new CompatibilityCheckResult();

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
        Capability api2Root = api2.iterator().next();


        // compare endpoints and their details
        // copy is created because we're going to be removing
        // processed endpoints
        List<Capability> api1RootChildren = new ArrayList<>(api1Root.getChildren());
        Iterator<Capability> endpoint1It = api1RootChildren.iterator();
        List<Capability> otherEndpoints = new ArrayList<>(api2Root.getChildren());

        while(endpoint1It.hasNext()) {
            Capability api1Endpoint = endpoint1It.next();

            // find endpoint from other api with same metadata
            Diff endpointDiff = compareEndpoints(api1Endpoint, otherEndpoints);
            checkResult.getDiffDetails().add(endpointDiff);

            // endpoint processed, remove it
            endpoint1It.remove();
        }

        // remaining endpoints
        for (Capability api2Endpoint : otherEndpoints) {
            Diff diff = new DefaultDiffImpl();
            diff.setLevel(DifferenceLevel.OPERATION);
            // api 1 does not contain endpoint from API 2
            // -> insertion
            diff.setValue(Difference.INS);
            diff.setName(api2Endpoint.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_NAME));
            checkResult.getDiffDetails().add(diff);
        }

        return checkResult;
    }

    /**
     * Contains whole logic of comparing 1 endpoint from one API to endpoint from other set.
     * Tries to find a one endpoint in otherEndpoints that would be suitable for comparison. If
     * such endpoint is found, it is removed from otherEndpoints and diff structure is constructed.
     * Otherwise diff with value DEL is used.
     *
     * Returned structure:
     *
     * endpointDiff
     *  - value: final verdict about compatibility of two endpoints
     *  - children:
     *      - metadata diff
     *      - parameter diff
     *      - response diff
     *
     * @param endpoint1
     * @param otherEndpoints
     * @return
     */
    private Diff compareEndpoints(Capability endpoint1, List<Capability> otherEndpoints) {
        Diff endpointDiff = new DefaultDiffImpl();
        endpointDiff.setLevel(DifferenceLevel.OPERATION);
        endpointDiff.setValue(Difference.NON);
        endpointDiff.setName(endpoint1.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_NAME));

        // try to find endpoint for comparision and
        // resolve metadata differences
        List<Diff> metadataDiffs = new ArrayList<>();
        Capability api2MatchingEndpoint = pullMatchingEndpoint(endpoint1, otherEndpoints, metadataDiffs);

        if (api2MatchingEndpoint == null) {
            // nothing found, endpoint1 is defined in API 1 but not in API 2
            endpointDiff.setValue(Difference.DEL);
        } else {
            // possible match found
            DifferenceAggregation differenceAggregation = new DifferenceAggregation();

            // construct metadata, parameter and response diff
            Diff metadataDiff = new DefaultDiffImpl();
            metadataDiff.addChildren(metadataDiffs);
            metadataDiff.setLevel(DifferenceLevel.FIELD);
            metadataDiffs.forEach(d -> differenceAggregation.addDifference(d.getValue()));
            metadataDiff.setValue(differenceAggregation.getResultDifference());
            differenceAggregation.clear();

            // parameter diffs
            Diff parameterDiff = new DefaultDiffImpl();
            parameterDiff.setLevel(DifferenceLevel.FIELD);
            parameterDiff.addChildren(compareEndpointParameters(endpoint1, api2MatchingEndpoint));
            parameterDiff.getChildren().forEach(d -> differenceAggregation.addDifference(d.getValue()));
            parameterDiff.setValue(differenceAggregation.getResultDifference());
            differenceAggregation.clear();

            // response diffs
            Diff responseDiff = new DefaultDiffImpl();
            responseDiff.setLevel(DifferenceLevel.FIELD);
            responseDiff.addChildren(compareEndpointResponses(endpoint1, api2MatchingEndpoint));
            responseDiff.getChildren().forEach(d -> differenceAggregation.addDifference(d.getValue()));
            responseDiff.setValue(differenceAggregation.getResultDifference());
            differenceAggregation.clear();

            // total diff
            endpointDiff.addChild(metadataDiff);
            endpointDiff.addChild(parameterDiff);
            endpointDiff.addChild(responseDiff);
            endpointDiff.getChildren().forEach(d -> differenceAggregation.addDifference(d.getValue()));
            endpointDiff.setValue(differenceAggregation.getResultDifference());
        }

        return endpointDiff;
    }

    /**
     * Tries to find endpoint in otherEndpoints so that its metadata matches
     * the endpointMetadata. If the endpoint is found, it will be removed from otherEndpoints.
     *
     * @param endpointMetadata Metadata of endpoint to be found in otherEndpoints.
     * @param otherEndpoints List of endpoint metadata to search.
     * @param metadataDiffs List to store details about metadata differences in case matching endpoint is found.
     * @return Endpoint metadata or null if not matching endpoint is found.
     */
    // todo: what if there are more matches?
    private Capability pullMatchingEndpoint(Capability endpointMetadata, List<Capability> otherEndpoints, List<Diff> metadataDiffs) {
        Capability match = null;
        Iterator<Capability> otherEndpointsIt = otherEndpoints.iterator();

        while(otherEndpointsIt.hasNext()) {
            Capability otherE = otherEndpointsIt.next();
            List<Diff> diffs = compareEndpointMetadata(endpointMetadata, otherE);

            // diff is valid only in case of no DEL and MUT diffs
            boolean validDiff = !diffs.isEmpty() && diffs.stream()
                    .noneMatch(d -> d.getValue().equals(Difference.DEL)
                            || d.getValue().equals(Difference.MUT));

            if (validDiff) {
                match = otherE;
                otherEndpointsIt.remove();
                metadataDiffs.addAll(diffs);
                break;
            }
        }

        return match;
    }

    /**
     * Compares metadata of two endpoints.
     * Compared metadata: METHOD, PATH, CONSUMES, PRODUCES.
     * Produces following diffs:
     *  NON - no difference.
     *  INS - possible match
     *  DEL - no match (because endpoint2 doesn't containt everything from endpoint1)
     *  MUT - no match, because MUT is combination of INS and DEL
     *
     * @param endpoint1 Endpoint 1.
     * @param endpoint2 Endpoint 2.
     * @return List of diffs. All diffs are NON in case of a total match.
     */
    private List<Diff> compareEndpointMetadata(Capability endpoint1, Capability endpoint2) {
        // attributes to be compared
        // ale of those are typed to List
        List<ListAttributeType> attributeTypes = Arrays.asList(
                RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_METHOD,
                RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_PATH,
                RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_CONSUMES,
                RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_PRODUCES
        );
        List<Diff> result = new ArrayList<>();

        for (ListAttributeType at : attributeTypes) {
            Attribute a1 = endpoint1.getAttribute(at);
            Attribute a2 = endpoint2.getAttribute(at);

            // values of both attributes
            List a1V = new ArrayList((List) a1.getValue());
            List a2V = new ArrayList((List) a2.getValue());


            // go through all the values of attribute of endpoint1
            // try to find the value in the attribute of endpoint2
            // and remove it from e1 and e2
            // if all the values are same (regardless of their order)
            // both a1V and a2V will be empty
            Iterator a1i = a1V.iterator();
            while(a1i.hasNext()) {
                Object obj = a1i.next();
                if (a2V.contains(obj)) {
                    a1i.remove();
                    a2V.remove(obj);
                }
            }

            // if a1V is not empty, some values are missing and it is not true that e1 <: e2
            // because e.g. endpoint with methods [GET, POST] <: endpoint with methods [POST]
            // or similarly endpoint with produces [application/xml, application/json] <: [application/json]
            Diff d = new DefaultDiffImpl();
            d.setName(at.getName());
            d.setLevel(DifferenceLevel.FIELD);
            if (a1V.isEmpty() && a2V.isEmpty()) {
                // ok
                continue;
            } else if (a1V.isEmpty() && !a2V.isEmpty()) {
                // endpoint 2 has something endpoint 1 doesn't have
                d.setValue(Difference.INS);
            } else if (!a1V.isEmpty() && a2V.isEmpty()) {
                // endpoint 1 has something endpoint 2 doesn't have
                d.setValue(Difference.DEL);
            } else {
                // combination of INS and DEL -> MUT
                d.setValue(Difference.MUT);
            }
            result.add(d);
        }

        return result;
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

        List<Diff> diffs = new ArrayList<>();
        Iterator<Property> p1i = endpoint1Params.iterator();
        Iterator<Property> p2i = endpoint2Params.iterator();
        while(p1i.hasNext() && p2i.hasNext()) {
            compareParameters(p1i.next(), p2i.next(), diffs);
            p1i.remove();
            p2i.remove();
        }

        // add INS and DEL parameters to diff
        while(p1i.hasNext()) {
            Property param = p1i.next();
            Diff d = new DefaultDiffImpl();
            d.setValue(Difference.DEL);
            d.setLevel(DifferenceLevel.FIELD);
            d.setName(param.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_NAME));
            diffs.add(d);
        }

        while(p2i.hasNext()) {
            Property param = p2i.next();
            Diff d = new DefaultDiffImpl();
            d.setValue(Difference.INS);
            d.setLevel(DifferenceLevel.FIELD);
            d.setName(param.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_NAME));
            diffs.add(d);
        }

        return diffs;
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
     * Creates a diff of two parameters.
     *
     * Parameter names must be equal: ?param1=5 vs. ?param1_2=5
     * Category must be equal: query vs path parameters
     * Type should be equal but may be just GEN/SPE
     *
     * @param param1
     * @param param2
     * @param parameterDiffs List of parameters to add diff to.
     */
    private void compareParameters(Property param1, Property param2, List<Diff> parameterDiffs) {
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
        // todo: handle cases such as short <: long, float <: double ...
        if (!name1.equals(name2)
            || !cat1.equals(cat2)) {
            diff.setValue(Difference.UNK);
            diff.setNamespace(RestimplIndexerConstants.NS_RESTIMPL_REQUESTPARAMETER);
        } else if (!dt1.equals(dt2)) {
            // data types are not same
            // try to instantiate those and compare them
            diff.setValue(compareTypesByNames(dt1.getStringValue(), dt2.getStringValue()));
        }

        parameterDiffs.add(diff);
    }

    /**
     * Compares non-equal types by their names.
     *
     * This may be a bit tricky because e.g. long <: short and long <: int
     * but in java Long, Short, Integer are subclasses of Number and can't be
     * compared between each other (not assignable, not instance of).
     *
     * For unknown types (those outside java.lang) UNK is returned.
     *
     * @param c1Name Name of the first type.
     * @param c2Name Name of the second type.
     * @return
     */
    private Difference compareTypesByNames(String c1Name, String c2Name) {
        if (c1Name == null || c1Name.isEmpty() || c2Name == null || c2Name.isEmpty()) {
            return Difference.UNK;
        }

        if (!c1Name.startsWith("java.lang") || !c2Name.startsWith("java.lang")) {
            return Difference.UNK;
        }

        try {
            Class<?> c1 = Class.forName(c1Name);
            Class<?> c2 = Class.forName(c2Name);

            if (c1.isAssignableFrom(c2)) {
                // c2 <: c1
                return Difference.SPE;
            } else if (c2.isAssignableFrom(c1)) {
                // c1 <: c2
                return Difference.INS;
            }
        } catch (ClassNotFoundException e) {
            // since this method only works with "java.lang", this
            // exception should not be thrown
            // todo: log exception
            e.printStackTrace();
            return Difference.UNK;
        }
    }
}
