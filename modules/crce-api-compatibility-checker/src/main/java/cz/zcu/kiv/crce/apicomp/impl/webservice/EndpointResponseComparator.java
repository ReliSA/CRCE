package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.apicomp.internal.DiffUtils;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class used to compare responses of two endpoints. New instances should be created for every comparison.
 *
 * It is possible to extend this class and override compareResponses() method to provide logic for
 * different response formats.
 */
public class EndpointResponseComparator extends EndpointFeatureComparator {

    public EndpointResponseComparator(Capability endpoint1, Capability endpoint2) {
        super(endpoint1, endpoint2);
    }

    @Override
    protected String getFeatureNamespace() {
        return WebserviceIndexerConstants.NAMESPACE__WEBSERVICE_ENDPOINT_RESPONSE;
    }

    /**
     * Compares all responses of two endpoints. Diff is created for every comparison.
     *
     * @return Collection of diffs between responses of two endpoints.
     */
    public List<Diff> compare() {
        List<Diff> responseDiffs = new ArrayList<>();

        Iterator<Property> response1It = endpoint1Features.iterator();
        Iterator<Property> response2It = endpoint2Features.iterator();

        while (response1It.hasNext()) {
            Property response1 = response1It.next();
            Property response2 = response2It.next();

            Diff responseDiff = DiffUtils.createDiff("response", DifferenceLevel.FIELD, Difference.NON);

            compareResponses(response1, response2, responseDiff);
            responseDiffs.add(responseDiff);

        }

        return responseDiffs;
    }

    /**
     * Compares metadata of two responses and creates diff based on that comparison.
     *
     * @param response1 Response 1.
     * @param response2 Response 2.
     * @param responseDiff Implementing method should only set value of this diff (and optionally add children if necessary)
     */
    protected void compareResponses(Property response1, Property response2, Diff responseDiff) {
       boolean attributesEqual = areAttributesEqual(response1, response2, WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__TYPE)
                    && areAttributesEqual(response1, response2, WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__ARRAY);

       responseDiff.setValue(attributesEqual ? Difference.NON : Difference.UNK);
    }
}
