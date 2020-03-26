package cz.zcu.kiv.crce.apicomp.impl.restimpl;

import cz.zcu.kiv.crce.apicomp.internal.DiffUtils;
import cz.zcu.kiv.crce.apicomp.result.DifferenceAggregation;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.impl.DefaultDiffImpl;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class used to compare responses of two endpoints. Also better for testability.
 *
 * New instance should be created for every comparison.
 */
public class EndpointResponseComparator extends EndpointFeatureComparator {

    /**
     * Copy of the list of properties containing metadata of
     * responses of the first endpoint. Copy is needed because
     * items will be removed from this list during the process.
     */
    private List<Property> endpoint1Responses = new ArrayList<>();

    /**
     * Copy of the list of properties containing metadata of
     * responses of the second endpoint. Copy is needed because
     * items will be removed from this list during the process.
     */
    private List<Property> endpoint2Responses = new ArrayList<>();

    public EndpointResponseComparator(Capability endpoint1, Capability endpoint2) {
        super(endpoint1, endpoint2);

        endpoint1Responses.addAll(endpoint1.getProperties(RestimplIndexerConstants.NS_RESTIMPL_RESPONSE));
        endpoint2Responses.addAll(endpoint2.getProperties(RestimplIndexerConstants.NS_RESTIMPL_RESPONSE));
    }

    /**
     * Compares responses of two endpoints. Positive result is returned only if for every
     * response defined for endpoint 1 exists one response defined for endpoint 2 with
     * same status and data type.
     *
     * @return Diffs between endpoint responses. Empty collection if the endpoints have same responses.
     */
    public List<Diff> compare() {
        List<Diff> responseDiffs = new ArrayList<>();

        // response consists of metadata (property) and parameters (property)
        // both are identified by the same id
        // response parameters are optional

        Iterator<Property> e1Pi = endpoint1Responses.iterator();

        while(e1Pi.hasNext()) {
            // first response
            Property resp1 = e1Pi.next();

            pullAndCompareMatchingResponse(resp1, endpoint2Responses, responseDiffs);
        }

        // remaining responses of the second endpoint
        for (Property response2 : endpoint2Responses) {
            responseDiffs.add(DiffUtils.createINSDiff(
                    RestimplIndexerConstants.NS_RESTIMPL_RESPONSE + ":" +response2.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_ID),
                    DifferenceLevel.FIELD
            ));
        }



        return responseDiffs;
    }


    /**
     * Searches the otherProperties list and tries to find a property such
     * that its metadata match the one of response1.
     *
     * If a matching property is found, it is removed from otherProperties
     * and further comparison is done. Otherwise Diff with DEL value is added
     * to the result list.
     *
     * All results are added into responseDiffs list.
     *
     * @param response1 Property containing response metadata.
     * @param otherResponses List of other responses to search through.
     * @param responseDiffs List to store results in.
     */
    private void pullAndCompareMatchingResponse(Property response1, List<Property> otherResponses, List<Diff> responseDiffs) {
        Iterator<Property> opi = otherResponses.iterator();
        Diff d = new DefaultDiffImpl();
        d.setLevel(DifferenceLevel.FIELD);
        d.setName(RestimplIndexerConstants.NS_RESTIMPL_RESPONSE + ":" +response1.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_ID));

        Property otherResponse = null;

        // try to find matching response to compare response1 with
        while (opi.hasNext()) {
            Property nextR = opi.next();
            Diff metadataDiff = compareResponseMetadata(response1, nextR);

            if (!metadataDiff.getValue().equals(Difference.UNK)) {
                // not UNK -> suitable for further comparison
                opi.remove();
                otherResponse = nextR;
                d.addChild(metadataDiff);
                break;
            }
        }


        // compare response parameters
        if (otherResponse != null) {
            List<Property> resp1Parameters = findPropertiesById(response1.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_ID),
                    RestimplIndexerConstants.NS_RESTIMPL_RESPONSEPARAMETER,
                    endpoint1);

            List<Property> resp2Parameters = new ArrayList<>();
            resp2Parameters.addAll(findPropertiesById(otherResponse.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_ID),
                    RestimplIndexerConstants.NS_RESTIMPL_RESPONSEPARAMETER,
                    endpoint2));

            d.addChild(compareResponseParameters(resp1Parameters, resp2Parameters));

        }

        // calculate final value for response diff
        if (otherResponse != null) {
            DifferenceAggregation.calculateAndSetFinalDifferenceValueFor(d);
        } else {
            d.setValue(Difference.DEL);
        }

        responseDiffs.add(d);
    }

    /**
     * Compares metadata of two responses and returns diff describing the difference between response metadata.
     *
     * Possible values:
     * NON: all attributes are equal
     * GEN/SPEC: all attributes are equal except for data type attribute (which is generalization/specialization)
     * UNK: some attributes are not equal
     *
     * Diff with value NON, GEN/SPEC means responses are suitable for further comparison.
     *
     * @param response1
     * @param response2
     * @return
     */
    private Diff compareResponseMetadata(Property response1, Property response2) {
        Attribute r1Status = response1.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_STATUS);
        Attribute r2Status = response2.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_STATUS);

        Attribute r1IsArray = response1.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ARRAY);
        Attribute r2IsArray = response2.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ARRAY);

        Diff d = new DefaultDiffImpl();
        d.setName(RestimplIndexerConstants.NS_RESTIMPL_RESPONSE);

        if (r1Status != null && r1Status.equals(r2Status)
                && r1IsArray != null && r1IsArray.equals(r2IsArray)) {
            // status and isArray are equal, compare data types
            d.setValue(compareDateTypeAttributes(response1, response2));

        } else {
            d.setValue(Difference.UNK);
        }

        return d;
    }


    /**
     * Returns all properties with given id and namespace.
     *
     * @param id Id of property.
     * @param namespace Namespace.
     * @param propertyParent Capability which holds the properties.
     * @return Found properties.
     */
    private List<Property> findPropertiesById(String id, String namespace, Capability propertyParent) {
        return propertyParent
                .getProperties(namespace)
                .stream()
                .filter(p -> p.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_ID).equals(id))
                .collect(Collectors.toList());
    }

    /**
     * Compares parameters of two responses.
     *
     * Parameter order has to be the same and for each parameter:
     *
     *  - name must be the same
     *  - category must be the same
     *  - isArray must be the same
     *  - dataType must be either same or SPEC/GEN
     *
     *
     * @param resp1Parameters Parameters of the first response.
     * @param resp2Parameters Parameters of the second response.
     * @return Diff with details (one child diff for each parameter)
     */
    private Diff compareResponseParameters(List<Property> resp1Parameters, List<Property> resp2Parameters) {
        Diff parameterDiff = new DefaultDiffImpl();
        parameterDiff.setLevel(DifferenceLevel.FIELD);

        Iterator<Property> r1Pi = resp1Parameters.iterator();
        Iterator<Property> r2Pi = resp2Parameters.iterator();

        while(r1Pi.hasNext() && r2Pi.hasNext()) {
            parameterDiff.addChild(compareTwoResponseParameters(r1Pi.next(), r2Pi.next()));
        }

        // remaining parameters in response1 or response 2
        while(r1Pi.hasNext()) {
            Property param = r1Pi.next();
            parameterDiff.addChild(DiffUtils.createDELDiff(
                    param.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_NAME),
                    DifferenceLevel.FIELD
            ));
        }

        while (r2Pi.hasNext()) {
            Property param = r2Pi.next();
            parameterDiff.addChild(DiffUtils.createINSDiff(
                    param.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_NAME),
                    DifferenceLevel.FIELD
            ));
        }


        DifferenceAggregation.calculateAndSetFinalDifferenceValueFor(parameterDiff);
        return parameterDiff;
    }

    /**
     * Compares two response parameters.
     * - name must be the same
     * - category must be the same
     * - isArray must be the same
     * - dataType must be either same or SPEC/GEN
     *
     * @param parameter1
     * @param parameter2
     * @return Either NON, SPEC/GEN or UNK diff.
     */
    private Diff compareTwoResponseParameters(Property parameter1, Property parameter2) {
        Diff paramDiff = new DefaultDiffImpl();
        paramDiff.setLevel(DifferenceLevel.FIELD);

        Attribute p1Name = parameter1.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_NAME);
        Attribute p2Name = parameter2.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_NAME);

        Attribute p1Category = parameter1.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_PARAMETER_CATEGEORY);
        Attribute p2Category = parameter2.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_PARAMETER_CATEGEORY);

        Attribute p1IsArray = parameter1.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ARRAY);
        Attribute p2IsArray = parameter2.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ARRAY);

        if (p1Name != null && p1Name.equals(p2Name)
                && p1Category != null && p1Category.equals(p2Category)
                && p1IsArray != null && p1IsArray.equals(p2IsArray)
        ) {
            // compare data types
            paramDiff.setValue(compareDateTypeAttributes(parameter1, parameter2));
        } else {
            paramDiff.setValue(Difference.UNK);
        }

        return paramDiff;
    }
}
