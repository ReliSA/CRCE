package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.mov.MovDiff;
import cz.zcu.kiv.crce.apicomp.internal.DiffUtils;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.apicomp.result.DifferenceAggregation;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.impl.DefaultDiffImpl;
import cz.zcu.kiv.crce.metadata.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Checker for crce-webservices-indexer
 *
 */
public abstract class WebservicesCompatibilityChecker extends ApiCompatibilityChecker {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getRootCapabilityNamespace() {
        return WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE;
    }

    @Override
    public CompatibilityCheckResult compareApis(Resource api1, Resource api2) {

        CompatibilityCheckResult checkResult = new CompatibilityCheckResult(api1, api2);

        // given the structure of metadata created in crce-restimpl-indexer, both
        // capability sets should contain exactly 1 capability
        if (!isApiSupported(api1)) {
            logger.warn("API 1 is not supported by this checker!");
            throw new RuntimeException("API 1 is not supported by this checker!");
        }

        if (!isApiSupported(api2)) {
            logger.warn("API 2 is not supported by this checker!");
            throw new RuntimeException("API 2 is not supported by this checker!");
        }

        Capability root1 = getOneRootCapability(api1);
        Capability root2 = getOneRootCapability(api2);

        if (root1  == null || root2 == null) {
            logger.warn("Root capability with API metadata not present in the first and/or the second API.");
            checkResult.setFinalDifference(Difference.UNK);
            return checkResult;
        }

        compare(checkResult, root1, root2);
        checkResult.recalculateFinalDifference();

        return checkResult;
    }

    /**
     * Returns the root capability containing the API meta-data tree.
     *
     * @param resource Resource with the root capability.
     *
     * @return Capability or null if resource has no such root capability.
     */
    protected abstract Capability getOneRootCapability(Resource resource);

    /**
     * Returns the attribute type that is sued to get communication pattern attribute from API root capability.
     *
     * @return
     */
    protected abstract AttributeType getCommunicationPatternAttributeName();


    /**
     * Actual comparison logic.
     *
     * @param checkResult Result object. Implementing method should only add child diffs.
     * @param root1 Root capability with metadata of the first WS.
     * @param root2 Root capability with metadata of the second WS.
     */
    protected abstract void compare(CompatibilityCheckResult checkResult, Capability root1, Capability root2);

    /**
     * Different WS may have different response formats and thus require different logic for comparing those.
     *
     * For these cases, implementing class should override this method and provide its own response comparator.
     *
     * @return Comparator to be used for responses.
     */
    protected EndpointFeatureComparator getEndpointResponseComparatorInstance(Capability endpoint1, Capability endpoint2) {
        return new EndpointResponseComparator(endpoint1, endpoint2);
    }

    /**
     * Override if you need custom logic for comparing endpoint parameters (usually because of different format).
     * @param endpoint1
     * @param endpoint2
     * @return
     */
    protected EndpointFeatureComparator getEndpointParameterComparatorInstance(Capability endpoint1, Capability endpoint2) {
        return new EndpointParameterComparator(endpoint1, endpoint2);
    }

    /**
     * Compares endpoint sets of api1 and api2. Endpoints are assumed to be all child capabilities of
     * api1 and api2.
     *
     * @param api1 Capability that contains endpoints of the first API.
     * @param api2 Capability that contains endpoints of the second API.
     * @param endpointsDiff Object used to store diffs between endpoints. New diff should be created for every
     *                      endpoint comparison. Correct Difference value is set by this method after all endpoints
     *                      are evaluated.
     */
    protected void compareEndpointsFromRoot(Capability api1, Capability api2, Diff endpointsDiff) {
        List<Capability> api1Endpoints = new ArrayList<>(api1.getChildren());
        Iterator<Capability> it1 = api1Endpoints.iterator();
        List<Capability> api2Endpoints = new ArrayList<>(api2.getChildren());


        while(it1.hasNext()) {
            Capability api1Method = it1.next();

            // find endpoint from other service with same metadata and compare it
            Diff endpointDiff = compareEndpointsPickBest(api1Method, api2Endpoints);
            endpointsDiff.addChild(endpointDiff);

            // endpoint processed, remove it
            it1.remove();
        }

        // remaining endpoints
        for (Capability api2Endpoint : api2Endpoints) {
            // api 1 does not contain endpoint defined in api 2 -> INS
            Diff diff = DiffUtils.createDiff(
                    api2Endpoint.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME),
                    DifferenceLevel.OPERATION,
                    Difference.INS);
            endpointsDiff.addChild(diff);
        }

        endpointsDiff.setValue(DifferenceAggregation.calculateFinalDifferenceFor(endpointsDiff.getChildren()));
    }

    /**
     * Tries to compare as many endpoints as possible (metadata match+MOV flag) and keeps the best result.
     * Contains whole logic of finding method in api2 suitable for comparison with method 1 and actual comparison.
     *
     *
     * Structure of returned object:
     *
     * methodDiff
     *  - value: final verdict about compatibility of two methods.
     *  - children:
     *      - metadata diff - now this is *theoretically* not needed but lets keep it here in case of
     *                         future changes
     *      - parameter diff
     *      - response diff
     *
     * @param endpoint1
     * @param otherEndpoints
     * @return
     */
    private Diff compareEndpointsPickBest(Capability endpoint1, List<Capability> otherEndpoints) {
        // diff -> compared endpoint
        Map<Diff, Capability> results = new HashMap<>();
        boolean stopCond = false;
        Diff endpointDiff = null;

        while(!stopCond) {
            List<Diff> metadataDiffs = new ArrayList<>();
            Capability matchingEndpoint = pullMatchingEndpoint(endpoint1, otherEndpoints, metadataDiffs);

            if (matchingEndpoint == null) {
                // nothing found, method 1 is in api 1 but not in api 2 -> DEL
                endpointDiff = DiffUtils.createDiff(
                        endpoint1.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME),
                        DifferenceLevel.OPERATION,
                        Difference.DEL);
                stopCond = true;
            } else {
                // possible match found
                Diff metadataDiff = DiffUtils.createDiffMov(
                        "metadata",
                        DifferenceLevel.FIELD,
                        metadataDiffs
                );

                endpointDiff = compareEndpointDetails(endpoint1, matchingEndpoint, metadataDiff);

                if (!DiffUtils.isDangerous(endpointDiff)) {
                    // okish endpoint found
                    stopCond = true;
                } else {
                    if (MovDiff.isMovDiff(endpointDiff)) {
                        // dangerous endpoint found and MOV is set, add it to the results
                        // and keep comparing other endpoints
                        results.put(endpointDiff, matchingEndpoint);
                    } else {
                        // dangerous endpoint found but MOV is not set -> incompatibility
                        stopCond = true;
                    }
                }
            }

            if (!stopCond && otherEndpoints.isEmpty()) {
                // no more endpoints to search and only dangerous
                // ones were found, pick one
                // the results map cannot be empty at this point
                stopCond = true;
                endpointDiff = results.keySet().iterator().next();
                results.remove(endpointDiff);
            }
        }

        // return remaining endpoint to the otherEndpoints list
        otherEndpoints.addAll(results.values());

        return endpointDiff;
    }

    /**
     * Compares details of two endpoints that were already deemed comparable.
     *
     * @param endpoint1
     * @param endpoint2
     * @param metadataDiff Result of pullMatchingEndpoint(), may be MovDiff.
     * @return
     */
    private Diff compareEndpointDetails(Capability endpoint1, Capability endpoint2, Diff metadataDiff) {
        Diff endpointDiff = DiffUtils.createDiff(
                endpoint1.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME),
                DifferenceLevel.OPERATION,
                Difference.NON);


        // parameter diff
        EndpointFeatureComparator parameterComparator = getEndpointParameterComparatorInstance(
                endpoint1,
                endpoint2
        );
        Diff parameterDiff = DiffUtils.createDiff(
                "parameters",
                DifferenceLevel.FIELD,
                parameterComparator.compare()
        );

        // response diff
        EndpointFeatureComparator responseComparator = getEndpointResponseComparatorInstance(
                endpoint1,
                endpoint2);
        Diff responseDiff = DiffUtils.createDiff(
                "responses",
                DifferenceLevel.FIELD,
                responseComparator.compare()
        );


        // put it all together
        endpointDiff.addChild(metadataDiff);
        endpointDiff.addChild(parameterDiff);
        endpointDiff.addChild(responseDiff);
        endpointDiff.setValue(DifferenceAggregation.calculateFinalDifferenceFor(endpointDiff.getChildren()));

        return endpointDiff;
    }

    private Capability pullMatchingEndpoint(Capability api1Endpoint, List<Capability> api2Endpoints, List<Diff> metadataDiffs) {
        Capability match = null;
        Iterator<Capability> otherEndpointsIt = api2Endpoints.iterator();

        while(otherEndpointsIt.hasNext()) {
            Capability otherE = otherEndpointsIt.next();

            boolean parameterCountMatch = compareEndpointParameterCounts(api1Endpoint, otherE);

            if (!parameterCountMatch) {
                continue;
            }

            // endpoints with matching parameter count found -> compare their metadata
            List<Diff> diffs = compareEndpointMetadata(api1Endpoint, otherE);

            // diff is valid only in case of no DEL and MUT diffs
            boolean validDiff = !diffs.isEmpty() && diffs.stream()
                    .noneMatch(d -> d.getValue().equals(Difference.UNK));

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

    /**
     * Returns the number of required parameters of given endpoint.
     *
     * If the model type does not support optional parameters, parameter count should be returned.
     *
     * @param endpoint
     * @return Number of required parameters.
     */
    protected int countRequiredParameters(Capability endpoint) {
        int count = 0;
        Long notOptional = 0L;
        for (Property parameter : endpoint.getProperties(WebserviceIndexerConstants.NAMESPACE__WEBSERVICE_ENDPOINT_PARAMETER)) {
            if (parameter.getAttributesMap().containsKey(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__OPTIONAL.getName())) {
                Attribute<Long> isOptional = parameter.getAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__OPTIONAL);
                if (notOptional.equals(isOptional.getValue())) {
                    count++;
                }
            } else {
                count++;
            }
        }

        return count;
    }


    /**
     * Compares metadata (type, url) of two endpoints and returns list
     * of diffs with each diff describing the difference between metadata.
     *
     * So, one diff for type and one diff for url.
     *
     * TODO: MOV endpoint URL and name contains all information regarding the MOV flag
     *
     * @param api1Endpoint
     * @param otherEndpoint
     * @return
     */
    private List<Diff> compareEndpointMetadata(Capability api1Endpoint, Capability otherEndpoint) {
        List<AttributeType> attributeTypes = Arrays.asList(
                WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME,
                WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__URL
        );

        List<Diff> metadataDiffs = new ArrayList<>();
        for (AttributeType at : attributeTypes) {
            Attribute a1 = api1Endpoint.getAttribute(at);
            Attribute a2 = otherEndpoint.getAttribute(at);

            if (a1 != null && a1.equals(a2)) {
                metadataDiffs.add(DiffUtils.createDiff(at.getName(), DifferenceLevel.FIELD, Difference.NON));
            } else {
                metadataDiffs.add(DiffUtils.createDiff(at.getName(), DifferenceLevel.FIELD, Difference.UNK));
            }
        }

        return metadataDiffs;
    }

    /**
     * Compares communication patterns of two APIs represented by their root capabilities.
     *
     * Returns either NON if communication patterns match or MUT if they do no.
     *
     * @param root1
     * @param root2
     * @return Diff containing info about communication pattern.
     */
    protected Diff compareCommunicationPatterns(Capability root1, Capability root2) {
        String type1 = root1.getAttributeStringValue(getCommunicationPatternAttributeName());
        String type2 = root2.getAttributeStringValue(getCommunicationPatternAttributeName());

        Diff commDiff = new DefaultDiffImpl();
        commDiff.setName("communication pattern");
        commDiff.setLevel(DifferenceLevel.TYPE);
        commDiff.setValue(type1 != null && type1.equals(type2) ? Difference.NON : Difference.MUT);

        return commDiff;
    }
}
