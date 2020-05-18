package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.apicomp.impl.mov.AbstractMovDetector;
import cz.zcu.kiv.crce.apicomp.impl.mov.ApiDescription;
import cz.zcu.kiv.crce.apicomp.impl.mov.JsonWspWadlMovDetector;
import cz.zcu.kiv.crce.apicomp.impl.mov.MovDetectionResult;
import cz.zcu.kiv.crce.apicomp.impl.webservice.mov.JsonWspEndpointMetadataMovComparator;
import cz.zcu.kiv.crce.metadata.Capability;

import java.net.MalformedURLException;

/**
 * Contains logic for comparing apis described by Json-WSP.
 *
 * Possible final diffs:
 * NON - no difference between APIs.
 * GEN - API 2 is more generic than API 1. Could be because of endpoint parameter GEN or
 *       there is at least one optional endpoint parameter in API 2 that was required in API 1.
 * SPE - API 2 is less generic than API 1. Caused by data types in endpoint responses and/or parameters.
 * INS - API 2 contains endpoint that API 1 does not.
 * DEL - API 1 contains endpoint that API 2 does not.
 * MUT - Combination of INS/DEL and/or GEN/SPEC.
 * UNK - Could not determine diff, could be because of uncomparable data types (endpoint parameters/responses)
 *       or communication patterns do not match.
 */
public class JsonWspCompatibilityChecker extends WadlCompatibilityChecker {

    public static final String CATEGORY = "json-wsp";

    /**
     * URL of the first API.
     */
    private String api1Url;

    /**
     * URL of the second API.
     */
    private String api2Url;

    @Override
    protected void extractAdditionalInfoFromRoots(Capability root1, Capability root2) {
        api1Url = root1.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__URI);
        api2Url = root1.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__URI);
    }
    @Override
    protected EndpointFeatureComparator getEndpointParameterComparatorInstance(Capability endpoint1, Capability endpoint2) {
        return new EndpointParameterComparator(endpoint1, endpoint2);
    }

    @Override
    protected EndpointFeatureComparator getEndpointMetadataComparatorInstance(Capability endpoint1, Capability endpoint2, MovDetectionResult movDetectionResult) {
        return new JsonWspEndpointMetadataMovComparator(endpoint1, endpoint2, movDetectionResult, api1Url, api2Url);
    }

    @Override
    protected AbstractMovDetector getMovDetector(Capability root1, Capability root2) throws MalformedURLException {
        return new JsonWspWadlMovDetector(ApiDescription.fromJsonWsp(root1), ApiDescription.fromJsonWsp(root2));
    }

    @Override
    protected String getApiCategory() {
        return CATEGORY;
    }
}
