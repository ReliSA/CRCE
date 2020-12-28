package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.mov.common.AbstractMovDetector;
import cz.zcu.kiv.crce.apicomp.impl.mov.common.MovDetectionResult;
import cz.zcu.kiv.crce.apicomp.impl.mov.common.MovDiff;
import cz.zcu.kiv.crce.apicomp.impl.webservice.common.*;
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

import java.net.MalformedURLException;
import java.util.*;

/**
 * Base compatibility  checker for APIs indexed by crce-webservices-indexer.
 *
 */
public abstract class WebservicesCompatibilityChecker extends ApiCompatibilityChecker {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected MovDetectionResult movDetectionResult;

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

        Diff communicationPatternDiff = compareCommunicationPatterns(root1, root2);
        checkResult.getDiffDetails().add(communicationPatternDiff);
        // communication pattern must be same
        if (!communicationPatternDiff.getValue().equals(Difference.NON)) {
            return checkResult;
        }

        extractAdditionalInfoFromRoots(root1, root2);

        movDetectionResult = detectMov(root1, root2);

        compare(checkResult, root1, root2);
        checkResult.recalculateFinalDifference();
        checkResult.trySetMovIfSafeDiffrence(movDetectionResult, "");

        return checkResult;
    }

    /**
     * Extract additional info from root capabilities before the comparison. What is extracted
     * depends on the implementing method.
     *
     * @param root1 Root capability representing the first API.
     * @param root2 Root capability representing the second API.
     */
    protected void extractAdditionalInfoFromRoots(Capability root1, Capability root2) {
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
     * Returns MOV detector for given API type.
     *
     * @return MOV detector to be used. If null, no MOV detection will be performed.
     */
    protected abstract AbstractMovDetector getMovDetectorInstance(Capability root1, Capability root2) throws MalformedURLException;

    /**
     * Returns a comparator capable of comparing metadata of two endpoints.
     *
     * @param movDetectionResult Object containing info about detectd MOV, should be taken into account by the
     *                           metadata comparator.
     * @return Instance of metadata comparator.
     */
    protected EndpointFeatureComparator getEndpointMetadataComparatorInstance(Capability endpoint1, Capability endpoint2, MovDetectionResult movDetectionResult) {
        return new WsEndpointMetadataMovComparator(endpoint1, endpoint2, movDetectionResult);
    }

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
     * Detects MOV in the current API type.
     *
     * @param root1 Root capability containing metadata of API 1.
     * @param root2 Root capability containing metadata of API 2.
     * @return
     * @throws MalformedURLException
     */
    protected MovDetectionResult detectMov(Capability root1, Capability root2) {
        logger.debug("Detecting MOV flag");
        MovDetectionResult movDetectionResult;
        try {
            AbstractMovDetector movDetector = getMovDetectorInstance(root1, root2);

            if (movDetector == null) {
                logger.debug("No MOV detector provided, skipping MOV detection.");
                return MovDetectionResult.noMov();
            }


            movDetectionResult = movDetector.detectMov();
            if (movDetectionResult.isAnyDiff()) {
                logger.debug("Mov detection result: {}.", movDetectionResult);
            }
        } catch (MalformedURLException ex) {
            logger.error("Could not parse url to endpoint: {}. Skipping MOV detection.", ex);
            movDetectionResult = MovDetectionResult.noMov();
        } catch (Exception ex) {
            logger.error("Unexpected exception when detecting MOV: {}.", ex);
            movDetectionResult = MovDetectionResult.noMov();
        }


        return movDetectionResult;
    }

    /**
     * Compares endpoint sets of api1 and api2. Endpoints are assumed to be all child capabilities of
     * api1 and api2.
     *  @param api1 Capability that contains endpoints of the first API.
     * @param api2 Capability that contains endpoints of the second API.
     * @param endpointsDiff Object used to store diffs between endpoints. New diff should be created for every
 *                      endpoint comparison. Correct Difference value is set by this method after all endpoints
     * @param movDetectionResult
     */
    protected void compareEndpointsFromRoot(Capability api1, Capability api2, Diff endpointsDiff, MovDetectionResult movDetectionResult) {
        logger.debug("Comparing endpoints from root.");
        List<Capability> api1Endpoints = new ArrayList<>(api1.getChildren());
        Iterator<Capability> it1 = api1Endpoints.iterator();
        List<Capability> api2Endpoints = new ArrayList<>(api2.getChildren());


        while(it1.hasNext()) {
            Capability api1Endpoint = it1.next();

            // find endpoint from other service with same metadata and compare it
            Diff endpointDiff = compareEndpointsPickBest(api1Endpoint, api2Endpoints, movDetectionResult);
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

        DifferenceAggregation.calculateAndSetFinalDifferenceValueFor(endpointsDiff);
        logger.debug("Done: {}.", endpointsDiff.getValue());
    }

    /**
     * Tries to compare as many endpoints as possible (metadata match+MOV flag) and keeps the best result.
     * Contains whole logic of finding endpoint in api2 suitable for comparison with endpoint 1 and
     * doing the actual comparison.
     *
     *
     * Structure of returned object:
     *
     * endpointDiff
     *  - value: final verdict about compatibility of two endpoints.
     *  - children:
     *      - metadata diff - now this is *theoretically* not needed but lets keep it here in case of
     *                         future changes
     *      - parameter diff
     *      - response diff
     *
     * @param endpoint1 Endpoint from API 1.
     * @param otherEndpoints Collection of endpoints from API 2.
     * @param movDetectionResult
     * @return
     */
    private Diff compareEndpointsPickBest(Capability endpoint1, List<Capability> otherEndpoints, MovDetectionResult movDetectionResult) {
        logger.debug("Comparing endpoint {} with the best one from other endpoint set.", endpoint1);

        // diff -> compared endpoint
        Map<Diff, Capability> results = new HashMap<>();
        boolean stopCond = false;
        Diff endpointDiff = null;

        while(!stopCond) {
            List<Diff> metadataDiffs = new ArrayList<>();
            Capability matchingEndpoint = pullMatchingEndpoint(endpoint1, otherEndpoints, metadataDiffs, movDetectionResult);

            if (matchingEndpoint == null) {
                logger.debug("No suitable match found in other api set.");
                // nothing found, endpoint 1 is in api 1 but not in api 2 -> DEL
                endpointDiff = DiffUtils.createDiff(
                        endpoint1.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME),
                        DifferenceLevel.OPERATION,
                        Difference.DEL);
                stopCond = true;
            } else {
                logger.debug("Suitable match found: {}.", matchingEndpoint);
                // possible match found
                Diff metadataDiff = DiffUtils.createDiffMov(
                        "metadata",
                        DifferenceLevel.FIELD,
                        metadataDiffs
                );

                endpointDiff = compareEndpointDetails(endpoint1, matchingEndpoint, metadataDiff, movDetectionResult);

                if (!DiffUtils.isDangerous(endpointDiff)) {
                    // okish endpoint found
                    stopCond = true;
                } else {
                    if (MovDiff.isMovDiff(endpointDiff)) {
                        logger.debug("MOV detected for this endpoint pair.");
                        stopCond = DiffUtils.isDiffSafeForMov(endpointDiff.getValue());

                        // dangerous endpoint found and MOV is set, add it to the results
                        // and keep comparing other endpoints
                        if (!stopCond) {
                            results.put(endpointDiff, matchingEndpoint);
                        }
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
     * @param movDetectionResult
     * @return Returns MOV diff if mov was detected for this endpoint.
     */
    private Diff compareEndpointDetails(Capability endpoint1, Capability endpoint2, Diff metadataDiff, MovDetectionResult movDetectionResult) {
        Diff endpointDiff = DiffUtils.createDiff(
                endpoint1.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME),
                DifferenceLevel.OPERATION,
                Difference.NON,
                movDetectionResult.isAnyDiff());


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
        DifferenceAggregation.calculateAndSetFinalDifferenceValueForEndpoint(endpointDiff);

        return endpointDiff;
    }

    private Capability pullMatchingEndpoint(Capability api1Endpoint, List<Capability> api2Endpoints, List<Diff> metadataDiffs, MovDetectionResult movDetectionResult) {
        logger.debug("Pulling matching endpoint for {}.", api1Endpoint.getAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME));
        Capability match = null;
        Iterator<Capability> otherEndpointsIt = api2Endpoints.iterator();

        while(otherEndpointsIt.hasNext()) {
            Capability otherE = otherEndpointsIt.next();

            boolean parameterCountMatch = compareEndpointParameterCounts(api1Endpoint, otherE);

            if (!parameterCountMatch) {
                continue;
            }

            // endpoints with matching parameter count found -> compare their metadata
            List<Diff> diffs = compareEndpointMetadata(api1Endpoint, otherE, movDetectionResult);

            // diff is valid only in case of no UNK
            boolean validDiff = !diffs.isEmpty() && diffs.stream()
                    .noneMatch(d -> d.getValue().equals(Difference.UNK));

            if (validDiff) {
                match = otherE;
                otherEndpointsIt.remove();
                metadataDiffs.addAll(diffs);
                logger.debug("Match found: {}.", match.getAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME));
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
     * @param api1Endpoint
     * @param otherEndpoint
     * @param movDetectionResult
     * @return Returns either NON if the metadata match or UNK if they don't.
     */
    private List<Diff> compareEndpointMetadata(Capability api1Endpoint, Capability otherEndpoint, MovDetectionResult movDetectionResult) {
        EndpointFeatureComparator metadataComparator = getEndpointMetadataComparatorInstance(api1Endpoint, otherEndpoint, movDetectionResult);
        return metadataComparator.compare();
    }

    /**
     * Compares communication patterns of two APIs represented by their root capabilities.
     *
     * Returns either NON if communication patterns match or UNK if they do not.
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
        commDiff.setValue(type1 != null && type1.equals(type2) ? Difference.NON : Difference.UNK);

        return commDiff;
    }
}
