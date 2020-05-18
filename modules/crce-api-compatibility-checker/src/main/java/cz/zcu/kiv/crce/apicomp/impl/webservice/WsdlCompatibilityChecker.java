package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.apicomp.impl.mov.AbstractMovDetector;
import cz.zcu.kiv.crce.apicomp.impl.mov.ApiDescription;
import cz.zcu.kiv.crce.apicomp.impl.mov.MovDetectionResult;
import cz.zcu.kiv.crce.apicomp.impl.mov.WsdlMovDetector;
import cz.zcu.kiv.crce.apicomp.internal.DiffUtils;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.apicomp.result.DifferenceAggregation;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Contains logic for comparing apis described by WSDL.
 *
 * The main difference between WSDL-based APIs and other indexed APIs is that WSDL ones have one extra level in data
 * hierarchy. So while the structure of other APIs looks like this: root -> endpoint -> param, response, ... the
 * structure of WSDL-based API looks like this: root -> webservice -> endpoint -> param, response. So the endpoints
 * are grouped into webservices.
 *
 * Possible differences:
 * NON - APIs are the same
 * GEN/SPE -  The second API has data types that are GEN/SPE of the same types in the first API (e.g. endpoint parameter, response data type, ...)
 * INS/DEL -  The second api has/has not ws or endpoint that is defined in the first API.
 * MUT -  Combination of GEN/SPE, INS/DEL.
 * UNK -  Endpoints with same signature have different response, or some more specific metadata (parameter order, parameter data type, communication patterns) do not match.
 */
public class WsdlCompatibilityChecker extends WebservicesCompatibilityChecker {

    @Override
    public String getRootCapabilityNamespace() {
        return WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_IDENTITY;
    }

    @Override
    protected Capability getOneRootCapability(Resource resource) {
        // wsdls have identity->ws capability
        List<Capability> wsIdentityCapabilities = resource.getRootCapabilities(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_IDENTITY);
        if (wsIdentityCapabilities.isEmpty()) {
            return null;
        }

        return wsIdentityCapabilities.get(0);
    }

    @Override
    protected AttributeType getCommunicationPatternAttributeName() {
        return WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__IDL_VERSION;
    }

    @Override
    protected EndpointFeatureComparator getEndpointResponseComparatorInstance(Capability endpoint1, Capability endpoint2) {
        return new WsdlEndpointResponseComparator(endpoint1, endpoint2);
    }

    @Override
    protected EndpointFeatureComparator getEndpointParameterComparatorInstance(Capability endpoint1, Capability endpoint2) {
        return new WsdlEndpointParameterComparator(endpoint1, endpoint2);
    }

    @Override
    protected void compare(CompatibilityCheckResult checkResult, Capability root1, Capability root2) {
        // start comparing web services defined in WSDL
        // new lists are created so that it's safe to remove items
        List<Capability> api1WebServices = new ArrayList<>(root1.getChildren());
        Iterator<Capability> it1 = api1WebServices.iterator();
        List<Capability> api2WebServices = new ArrayList<>(root2.getChildren());

        // diff for collecting differences from all webservices this API may contain
        Diff webServicesDiff = DiffUtils.createDiff("webservices", DifferenceLevel.PACKAGE, Difference.NON);
        while(it1.hasNext()) {
            Capability api1WebService = it1.next();

            // find webservice from other api with same metadata and compare it
            Diff webServiceDiff = compareWebServices(api1WebService, api2WebServices, movDetectionResult);
            webServicesDiff.addChild(webServiceDiff);

            // webservice processed, remove it
            it1.remove();
        }

        // remaining webservices
        for (Capability api2WebService : api2WebServices) {
            // api 1 does not contain webservice defined in api 2 -> INS
            Diff diff = DiffUtils.createDiff(
                    api2WebService.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__NAME),
                    DifferenceLevel.PACKAGE,
                    Difference.INS);
            webServicesDiff.addChild(diff);
        }
        DifferenceAggregation.calculateAndSetFinalDifferenceValueFor(webServicesDiff);

        checkResult.getDiffDetails().add(webServicesDiff);
        if (movDetectionResult.isPossibleMOV()) {
            checkResult.setMoveFlag("");
        }
    }

    @Override
    protected AbstractMovDetector getMovDetectorInstance(Capability root1, Capability root2) throws MalformedURLException {
        return new WsdlMovDetector(ApiDescription.fromWsdl(root1), ApiDescription.fromWsdl(root2));
    }

    @Override
    protected String getApiCategory() {
        return "wsdl";
    }

    /**
     * Compares web services. Comparable WSs are chosen based on their type and name.
     *
     * @param api1Ws
     * @param api2WebServices
     * @param movDetectionResult Object containing the result of previous MOV detection.
     * @return
     */
    private Diff compareWebServices(Capability api1Ws, List<Capability> api2WebServices, MovDetectionResult movDetectionResult) {
        Diff webserviceDiff = DiffUtils.createDiff(
                api1Ws.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__NAME),
                DifferenceLevel.PACKAGE,
                Difference.NON);

        Diff wsMetadataDiff = DiffUtils.createDiff("metadata", DifferenceLevel.FIELD, Difference.NON);
        Capability otherWs = pullComparableWs(api1Ws, api2WebServices, wsMetadataDiff);

        if (otherWs != null) {
            // comparable WS found
            webserviceDiff.addChild(wsMetadataDiff);

            // start comparing endpoints of two web services
            Diff endpointsDiff = DiffUtils.createDiff("endpoints", DifferenceLevel.PACKAGE, Difference.NON);
            compareEndpointsFromRoot(api1Ws, otherWs, endpointsDiff, movDetectionResult);
            webserviceDiff.addChild(endpointsDiff);

            DifferenceAggregation.calculateAndSetFinalDifferenceValueFor(webserviceDiff);

        } else {
            // no matching WS found
            webserviceDiff.setValue(Difference.DEL);
        }
        return webserviceDiff;
    }

    /**
     * Tries to find capability representing a WSDL webservice in api2WebServices
     * that is comparable with api1Ws. Also sets metadata diffs and diff value in wsMetadataDiff
     *
     * If a comparable WS is found, it is removed from api2WebServices.
     *
     * @param api1Ws WS from the first API.
     * @param api2WebServices Collection of WS from the second API.
     * @param wsMetadataDiff Metadata diff details.
     * @return Found WS or null if nothing is found. If null is returned, wsMetadataDiff should not be used.
     */
    private Capability pullComparableWs(Capability api1Ws, List<Capability> api2WebServices, Diff wsMetadataDiff) {

        Iterator<Capability> ws2It = api2WebServices.iterator();
        Attribute type1 = api1Ws.getAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE);
        Attribute name1 = api1Ws.getAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__NAME);

        if (type1 == null) {
            logger.error("Malformed webservice metadata, missing attribute: '{}'", WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__TYPE.getName());
            throw new IllegalArgumentException("Malformed webservice metadata, missing attribute: "+WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__TYPE.getName());
        }

        if (name1 == null) {
            logger.error("Malformed webservice metadata, missing attribute: '{}'", WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__NAME.getName());
            throw new IllegalArgumentException("Malformed webservice metadata, missing attribute: "+WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__NAME.getName());
        }

        while (ws2It.hasNext()) {
            Capability ws2 = ws2It.next();

            // type and name must be equal
            Attribute type2 = ws2.getAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE);
            Attribute name2 = ws2.getAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__NAME);

            if (type1.equals(type2) && name1.equals(name2)) {
                ws2It.remove();
                return ws2;
            }
        }
        return null;
    }
}
