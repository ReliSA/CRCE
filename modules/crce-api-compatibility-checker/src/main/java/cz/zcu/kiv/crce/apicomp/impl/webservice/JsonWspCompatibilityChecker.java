package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.apicomp.internal.DiffUtils;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.apicomp.result.DifferenceAggregation;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.impl.DefaultDiffImpl;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

// todo: logging
// todo: document used diff levels
// todo: tests
// todo: should be usable for both WADL and JsonWSP
public class JsonWspCompatibilityChecker extends WebservicesCompatibilityChecker {

    @Override
    protected Capability getOneRootCapability(Resource resource) {
        List<Capability> capabilities = resource.getRootCapabilities(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE);
        if (capabilities.isEmpty()) {
            return null;
        }

        return capabilities.get(0);
    }

    @Override
    protected void compare(CompatibilityCheckResult checkResult, Capability root1, Capability root2) {

        Diff communicationPatternDiff = compareCommunicationPatterns(root1, root2);
        checkResult.getDiffDetails().add(communicationPatternDiff);
        // communication pattern must be same
        if (!communicationPatternDiff.getValue().equals(Difference.NON)) {
            return;
        }

        // start comparing methods in WS
        // new lists are created so that it's safe to remove items
        List<Capability> api1Methods = new ArrayList<>(root1.getChildren());
        Iterator<Capability> it1 = api1Methods.iterator();
        List<Capability> api2Methods = new ArrayList<>(root2.getChildren());


        while(it1.hasNext()) {
            Capability api1Method = it1.next();

            // find method from other service with same metadata and compare it
            Diff methodDiff = compareMethods(api1Method, api2Methods);
            checkResult.getDiffDetails().add(methodDiff);

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
            checkResult.getDiffDetails().add(diff);
        }

        return;
    }

    private Diff compareCommunicationPatterns(Capability root1, Capability root2) {
        String type1 = root1.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE);
        String type2 = root2.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE);

        Diff commDiff = new DefaultDiffImpl();
        commDiff.setName("communication pattern");
        commDiff.setLevel(DifferenceLevel.TYPE);
        commDiff.setValue(type1 != null && type1.equals(type2) ? Difference.NON : Difference.MUT);

        return commDiff;
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
     * @param api1Method Method from api 1.
     * @param api2Methods Set of method from api 2.
     * @return Diff between method 1 and found method from api 2 or just DEL if no method
     *      suitable for comparison is found.
     */
    private Diff compareMethods(Capability api1Method, List<Capability> api2Methods) {
        Diff methodDiff = new DefaultDiffImpl();
        methodDiff.setLevel(DifferenceLevel.OPERATION);
        methodDiff.setValue(Difference.NON);
        methodDiff.setName(api1Method.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME));

        List<Diff> metadataDiffs = new ArrayList<>();
        Capability matchingMethod = pullMatchingMethod(api1Method, api2Methods, metadataDiffs);

        if (matchingMethod == null) {
            // nothing found, method 1 is in api 1 but not in api 2 -> DEL
            methodDiff.setValue(Difference.DEL);
        } else {
            // possible match found

            // metadata diff
            Diff metadataDiff = DiffUtils.createDiff(
                    "metadata",
                    DifferenceLevel.FIELD,
                    DifferenceAggregation.calculateFinalDifferenceFor(metadataDiffs)
            );

            // parameter diff
            MethodParameterComparator parameterComparator = new MethodParameterComparator(
                    api1Method,
                    matchingMethod
            );
            Diff parameterDiff = DiffUtils.createDiff(
                    "parameters",
                    DifferenceLevel.FIELD,
                    parameterComparator.compare()
            );

            // response diff
            MethodResponseComparator responseComparator = new MethodResponseComparator(
                    api1Method,
                    matchingMethod
            );
            Diff responseDiff = DiffUtils.createDiff(
                    "responses",
                    DifferenceLevel.FIELD,
                    responseComparator.compare()
            );


            // put it all together
            methodDiff.addChild(metadataDiff);
            methodDiff.addChild(parameterDiff);
            methodDiff.addChild(responseDiff);
            methodDiff.setValue(DifferenceAggregation.calculateFinalDifferenceFor(methodDiff.getChildren()));
        }

        return methodDiff;
    }

    private Capability pullMatchingMethod(Capability api1Method, List<Capability> api2Methods, List<Diff> metadataDiffs) {
        Capability match = null;
        Iterator<Capability> otherMethodsIt = api2Methods.iterator();

        while(otherMethodsIt.hasNext()) {
            Capability otherE = otherMethodsIt.next();
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
     * Compares metadata (type, url) of two endpoints and returns list
     * of diffs with each diff describing the difference between metadata.
     *
     * So, one diff for type and one diff for url.
     *
     * TODO: MOV
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
}
