package cz.zcu.kiv.crce.apicomp.impl.restimpl;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.mov.common.MovDiff;
import cz.zcu.kiv.crce.apicomp.internal.DiffUtils;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.apicomp.result.DifferenceAggregation;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.impl.DefaultDiffImpl;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Compatibility checker for REST API. Expects metadata structure created by
 * 'crce-restimpl-indexer' module.
 */
public class RestApiCompatibilityChecker extends ApiCompatibilityChecker {


    /**
     * If set, this checker tries to detect version in path to API and ignore it when picking
     * endpoint suitable for comparison.
     */
    private boolean ignoreVersionInPath = false;

    public RestApiCompatibilityChecker() {
    }

    public boolean isIgnoreVersionInPath() {
        return ignoreVersionInPath;
    }

    public void setIgnoreVersionInPath(boolean ignoreVersionInPath) {
        this.ignoreVersionInPath = ignoreVersionInPath;
    }

    @Override
    public String getRootCapabilityNamespace() {
        return RestimplIndexerConstants.IDENTITY_CAPABILITY_NAMESPACE;
    }

    @Override
    public CompatibilityCheckResult compareApis(Resource api1, Resource api2) {
        CompatibilityCheckResult checkResult = new CompatibilityCheckResult(api1, api2);

        // given the structure of metadata created in crce-restimpl-indexer, both
        // capability sets should contain exactly 1 capability
        if (!isApiSupported(api1)) {
            // todo: log error
            throw new RuntimeException("API 1 is not supported by this checker!");
        }

        if (!isApiSupported(api2)) {
            // todo: log error
            throw new RuntimeException("API 2 is not supported by this checker!");
        }

        // root capabilities are expected to have only one type
        // of children - capability which describes the endpoint
        // so it can be assumed all children are endpoints
        Capability api1Root = api1.getRootCapabilities(getRootCapabilityNamespace()).iterator().next();
        Capability api2Root = api2.getRootCapabilities(getRootCapabilityNamespace()).iterator().next();


        // compare endpoints and their details
        // copy is created because we're going to be removing
        // processed endpoints
        List<Capability> api1RootChildren = new ArrayList<>(api1Root.getChildren());
        Iterator<Capability> endpoint1It = api1RootChildren.iterator();
        List<Capability> otherEndpoints = new ArrayList<>(api2Root.getChildren());

        boolean isMov = false;
        while(endpoint1It.hasNext()) {
            Capability api1Endpoint = endpoint1It.next();

            // find endpoint from other api with same metadata
            MovDiff endpointDiff = compareEndpoints(api1Endpoint, otherEndpoints);
            isMov |= endpointDiff.isMov();
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

        checkResult.recalculateFinalDifference();
        if (isMov) {
            checkResult.setMoveFlag("mov");
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
    private MovDiff compareEndpoints(Capability endpoint1, List<Capability> otherEndpoints) {
        MovDiff endpointDiff = new MovDiff();
        endpointDiff.setLevel(DifferenceLevel.OPERATION);
        endpointDiff.setValue(Difference.NON);
        endpointDiff.setName(endpoint1.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_PATH));

        // try to find endpoint for comparision and
        // resolve metadata differences
        List<Diff> metadataDiffs = new ArrayList<>();
        Capability api2MatchingEndpoint = pullMatchingEndpoint(endpoint1, otherEndpoints, metadataDiffs);

        if (api2MatchingEndpoint == null) {
            // nothing found, endpoint1 is defined in API 1 but not in API 2
            endpointDiff.setValue(Difference.DEL);
        } else {
            // possible match found

            // construct metadata, parameter and response diff
            Diff metadataDiff = DiffUtils.createDiff(
                    "metadata",
                    DifferenceLevel.FIELD,
                    metadataDiffs
            );

            // parameter diffs
            EndpointParameterComparator parameterComparator = new EndpointParameterComparator(
                    endpoint1,
                    api2MatchingEndpoint
            );
            Diff parameterDiff = DiffUtils.createDiff(
                    "parameters",
                    DifferenceLevel.FIELD,
                    parameterComparator.compare()
            );

            // response diffs
            EndpointResponseComparator responseComparator = new EndpointResponseComparator(
                    endpoint1,
                    api2MatchingEndpoint);
            Diff responseDiff = DiffUtils.createDiff(
                    "response",
                    DifferenceLevel.FIELD,
                    responseComparator.compare()
            );

            // total diff
            endpointDiff.addChild(metadataDiff);
            endpointDiff.addChild(parameterDiff);
            endpointDiff.addChild(responseDiff);
            endpointDiff.setMov(metadataDiff.getChildren().stream().anyMatch(d -> d instanceof MovDiff && ((MovDiff)d).isMov()));
            DifferenceAggregation.calculateAndSetFinalDifferenceValueForEndpoint(endpointDiff);
        }

        return endpointDiff;
    }

    /**
     * Tries to find endpoint in otherEndpoints so that its metadata matches
     * the endpointMetadata. If the endpoint is found, it will be removed from otherEndpoints.
     *
     * If versionIgnoring is turned on, metadataDiffs may contain MovDiff with MOV flag raised.
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

            boolean parameterCountMatch = compareEndpointParameterCounts(endpointMetadata, otherE);

            if (!parameterCountMatch) {
                continue;
            }

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
     * Compares counts of required parameters of two endpoints.
     *
     * Endpoints should have the same count of required parameters.
     *
     * @param endpoint1
     * @param endpoint2
     */
    private boolean compareEndpointParameterCounts(Capability endpoint1, Capability endpoint2) {
        int requiredParameterCount1 = countRequiredParameters(endpoint1);
        int requiredParameterCount2 = countRequiredParameters(endpoint2);

        return requiredParameterCount1 == requiredParameterCount2;
    }

    private int countRequiredParameters(Capability endpoint1) {
        int count = 0;
        Long notOptional = 0L;

        for (Property endpointParameter : endpoint1.getProperties(RestimplIndexerConstants.NS_RESTIMPL_REQUESTPARAMETER)) {
            Attribute<Long> isOptional = endpointParameter.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_OPTIONAL);
            if (notOptional.equals(isOptional.getValue())) {
                count++;
            }
        }

        return count;
    }

    /**
     * Compares metadata of two endpoints.
     * Compared metadata: METHOD, PATH, CONSUMES, PRODUCES.
     * Produces following diffs:
     *  NON - no difference.
     *  INS - possible match
     *  DEL - no match (because endpoint2 doesn't contain everything from endpoint1)
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

            // use smart comparator for endpoint paths
            if (at.equals(RestimplIndexerConstants.ATTR__RESTIMPL_ENDPOINT_PATH)) {
                EndpointPathComparator pathComparator = new EndpointPathComparator(endpoint1, endpoint2, this.ignoreVersionInPath);
                result.add(pathComparator.compare().get(0));
            } else {
                result.add(compareListAttributes(endpoint1, endpoint2, at));
            }
        }

        return result;
    }

    /**
     * Compares one list attribute of two endpoints.
     *
     * @param endpoint1
     * @param endpoint2
     * @param attributeType
     */
    private Diff compareListAttributes(Capability endpoint1, Capability endpoint2, ListAttributeType attributeType) {
        Attribute a1 = endpoint1.getAttribute(attributeType);
        Attribute a2 = endpoint2.getAttribute(attributeType);

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
        d.setName(attributeType.getName());
        d.setLevel(DifferenceLevel.FIELD);
        if (a1V.isEmpty() && a2V.isEmpty()) {
            // ok
            d.setValue(Difference.NON);
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

        return d;
    }
}
