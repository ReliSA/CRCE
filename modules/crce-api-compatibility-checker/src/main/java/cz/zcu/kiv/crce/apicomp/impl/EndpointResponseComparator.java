package cz.zcu.kiv.crce.apicomp.impl;

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
public class EndpointResponseComparator {

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

    private Capability endpoint1;

    private Capability endpoint2;

    public EndpointResponseComparator(Capability endpoint1, Capability endpoint2) {
        this.endpoint1 = endpoint1;
        this.endpoint2 = endpoint2;

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
    public List<Diff> compareEndpointResponses() {
        List<Diff> responseDiffs = new ArrayList<>();

        // response consists of metadata (property) and parameters (property)
        // both are identified by the same id
        // response parameters are optional

        Iterator<Property> e1Pi = endpoint1Responses.iterator();

        while(e1Pi.hasNext()) {
            // first response
            Property resp1 = e1Pi.next();
            List<Property> resp1Parameters = findPropertiesById(resp1.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_ID),
                    RestimplIndexerConstants.NS_RESTIMPL_RESPONSEPARAMETER,
                    endpoint1);

            // other response
            Property resp2 = pullMatchingResponse(resp1, endpoint2Responses);
            List<Property> resp2Parameters = new ArrayList<>();
            if (resp2 != null) {
                // matching response found
                resp2Parameters.addAll(findPropertiesById(resp2.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_ID),
                        RestimplIndexerConstants.NS_RESTIMPL_RESPONSEPARAMETER,
                        endpoint2));
                responseDiffs.add(compareResponses(resp1, resp1Parameters, resp2, resp2Parameters));
            } else {
                // no matching response in endpoint 2 found
                Diff d = new DefaultDiffImpl();
                d.setLevel(DifferenceLevel.FIELD);
                d.setName(RestimplIndexerConstants.NS_RESTIMPL_RESPONSE + ":" +resp1.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_ID));
                d.setValue(Difference.DEL);
                responseDiffs.add(d);
            }
        }

        // remaining responses of the second endpoint
        for (Property response2 : endpoint2Responses) {
            Diff d = new DefaultDiffImpl();
            d.setLevel(DifferenceLevel.FIELD);
            d.setName(RestimplIndexerConstants.NS_RESTIMPL_RESPONSE + ":" +response2.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_ID));
            d.setValue(Difference.INS);
            responseDiffs.add(d);
        }



        return responseDiffs;
    }


    /**
     * Searches the otherProperties list and tries to find a property such
     * that its metadata match the one of response1.
     *
     * If a matching property is found, it is removed from otherProperties.
     *
     * @param response1 Property containing response metadata.
     * @param otherResponses List of other responses to search through.
     * @return Found property or null if no is found.
     */
    private Property pullMatchingResponse(Property response1, List<Property> otherResponses) {
        Iterator<Property> opi = otherResponses.iterator();

        while (opi.hasNext()) {
            Property otherResponse = opi.next();

            if (isResponseMetadataMatch(response1, otherResponse)) {
                opi.remove();
                return otherResponse;
            }
        }

        return null;
    }

    /**
     * Checks attributes of both responses and returns true if they're suitable for
     * further comparison.
     *
     * status, datetype and isArray attributes of both responses must match.
     *
     * @param response1
     * @param response2
     * @return
     */
    private boolean isResponseMetadataMatch(Property response1, Property response2) {
        Attribute r1Status = response1.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_STATUS);
        Attribute r2Status = response2.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_RESPONSE_STATUS);

        Attribute r1IsArray = response1.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ARRAY);
        Attribute r2IsArray = response2.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_ARRAY);

        // todo: use compareDateTypesAttribute() method instead of this
        Attribute r1DateType = response1.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_DATETYPE);
        Attribute r2DateType = response2.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_DATETYPE);

        return r1Status != null && r1Status.equals(r2Status)
            && r1IsArray != null && r1IsArray.equals(r2IsArray)
            && r1DateType != null && r1DateType.equals(r2DateType);
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
     * Compares two responses.
     *
     * @param resp1 Metadata of the first response.
     * @param resp1Parameters Parameters of the first response. May be empty.
     * @param resp2 Metadata of the second response.
     * @param resp2Parameters Parameters of the second response. May be empty.
     * @return Diff describing the differencies between two responses.
     */
    private Diff compareResponses(Property resp1, List<Property> resp1Parameters, Property resp2, List<Property> resp2Parameters) {
        // todo:
        return null;
    }
}
