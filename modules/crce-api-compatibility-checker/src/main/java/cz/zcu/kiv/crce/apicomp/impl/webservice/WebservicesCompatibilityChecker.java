package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
    protected MethodFeatureComparator getEndpointResponseComparatorInstance(Capability endpoint1, Capability endpoint2) {
        return new MethodResponseComparator(endpoint1, endpoint2);
    }

    /**
     * Override if you need custom logic for comparing endpoint parameters (usually because of different format).
     * @param endpoint1
     * @param endpoint2
     * @return
     */
    protected MethodFeatureComparator getEndpointParameterComparatorInstance(Capability endpoint1, Capability endpoint2) {
        return new MethodParameterComparator(endpoint1, endpoint2);
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
        List<Capability> api1Methods = new ArrayList<>(api1.getChildren());
        Iterator<Capability> it1 = api1Methods.iterator();
        List<Capability> api2Methods = new ArrayList<>(api2.getChildren());


        while(it1.hasNext()) {
            Capability api1Method = it1.next();

            // find method from other service with same metadata and compare it
            Diff methodDiff = compareEndpoints(api1Method, api2Methods);
            endpointsDiff.addChild(methodDiff);

            // method processed, remove it
            it1.remove();
        }

        // remaining methods
        for (Capability api2Method : api2Methods) {
            // api 1 does not contain method defined in api 2 -> INS
            Diff diff = DiffUtils.createDiff(
                    api2Method.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME),
                    DifferenceLevel.OPERATION,
                    Difference.INS);
            endpointsDiff.addChild(diff);
        }

        endpointsDiff.setValue(DifferenceAggregation.calculateFinalDifferenceFor(endpointsDiff.getChildren()));
    }

    /**
     * Contains whole logic of finding method in api2 suitable for comparison with method 1
     * and actual comparison.
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
     * @param api1Endpoint Endpoint from api 1.
     * @param api2Endpoints Set of endpoints from api 2.
     * @return Diff between method 1 and found method from api 2 or just DEL if no method
     *      suitable for comparison is found.
     */
    protected Diff compareEndpoints(Capability api1Endpoint, List<Capability> api2Endpoints) {
        // using endpoint name for diff name improves result readability
        Diff endpointDiff = DiffUtils.createDiff(
                api1Endpoint.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME),
                DifferenceLevel.OPERATION,
                Difference.NON);

        List<Diff> metadataDiffs = new ArrayList<>();
        Capability matchingEndpoint = pullMatchingMethod(api1Endpoint, api2Endpoints, metadataDiffs);

        if (matchingEndpoint == null) {
            // nothing found, method 1 is in api 1 but not in api 2 -> DEL
            endpointDiff.setValue(Difference.DEL);
        } else {
            // possible match found

            // metadata diff
            Diff metadataDiff = DiffUtils.createDiff(
                    "metadata",
                    DifferenceLevel.FIELD,
                    DifferenceAggregation.calculateFinalDifferenceFor(metadataDiffs)
            );

            // parameter diff
            MethodFeatureComparator parameterComparator = getEndpointParameterComparatorInstance(
                    api1Endpoint,
                    matchingEndpoint
            );
            Diff parameterDiff = DiffUtils.createDiff(
                    "parameters",
                    DifferenceLevel.FIELD,
                    parameterComparator.compare()
            );

            // response diff
            MethodFeatureComparator responseComparator = getEndpointResponseComparatorInstance(
                    api1Endpoint,
                    matchingEndpoint);
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
        }

        return endpointDiff;
    }

    private Capability pullMatchingMethod(Capability api1Method, List<Capability> api2Methods, List<Diff> metadataDiffs) {
        Capability match = null;
        Iterator<Capability> otherMethodsIt = api2Methods.iterator();

        while(otherMethodsIt.hasNext()) {
            Capability otherE = otherMethodsIt.next();

            boolean parameterCountMatch = compareEndpointParameterCounts(api1Method, otherE);

            if (!parameterCountMatch) {
                continue;
            }

            // endpoints with matching parameter count found -> compare their metadata
            List<Diff> diffs = compareEndpointMetadata(api1Method, otherE);

            // diff is valid only in case of no DEL and MUT diffs
            boolean validDiff = !diffs.isEmpty() && diffs.stream()
                    .noneMatch(d -> d.getValue().equals(Difference.UNK));

            if (validDiff) {
                match = otherE;
                otherMethodsIt.remove();
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
     * @param api1Method
     * @param otherE
     * @return
     */
    private List<Diff> compareEndpointMetadata(Capability api1Method, Capability otherE) {
        List<AttributeType> attributeTypes = Arrays.asList(
                WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME,
                WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__URL
        );

        List<Diff> metadataDiffs = new ArrayList<>();
        for (AttributeType at : attributeTypes) {
            Attribute a1 = api1Method.getAttribute(at);
            Attribute a2 = otherE.getAttribute(at);

            if (a1 != null && a1.equals(a2)) {
                metadataDiffs.add(DiffUtils.createDiff(at.getName(), DifferenceLevel.FIELD, Difference.NON));
            } else {
                metadataDiffs.add(DiffUtils.createDiff(at.getName(), DifferenceLevel.FIELD, Difference.UNK));
            }
        }

        return metadataDiffs;
    }

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
