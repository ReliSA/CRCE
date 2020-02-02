package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.impl.DefaultDiffImpl;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class used to compare responses of two methods.
 *
 * New instances should be created for every comparison.
 */
public class MethodResponseComparator extends MethodFeatureComparator {

    public MethodResponseComparator(Capability method1, Capability method2) {
        super(method1, method2);
    }

    @Override
    protected String getFeatureNamespace() {
        return WebserviceIndexerConstants.NAMESPACE__WEBSERVICE_ENDPOINT_RESPONSE;
    }

    /**
     * Compares all responses of two methods. Diff is created for every comparison.
     *
     * @return Collection of diffs between responses of two methods.
     */
    public List<Diff> compare() {
        List<Diff> responseDiffs = new ArrayList<>();

        Iterator<Property> response1It = method1Features.iterator();
        Iterator<Property> response2It = method2Features.iterator();

        while (response1It.hasNext()) {
            Property response1 = response1It.next();
            Property response2 = response2It.next();

            compareResponses(response1, response2, responseDiffs);

        }

        return responseDiffs;
    }

    /**
     * Compares metadata of two responses and creates diff based on that comparison.
     *
     * @param response1
     * @param response2
     * @param responseDiffs
     */
    private void compareResponses(Property response1, Property response2, List<Diff> responseDiffs) {
       boolean attributesEqual = areAttributesEqual(response1, response2, WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__TYPE)
                    && areAttributesEqual(response1, response2, WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__ARRAY);

       Diff diff = new DefaultDiffImpl();
       diff.setName("response");
       diff.setValue(attributesEqual ? Difference.NON : Difference.UNK);
       diff.setLevel(DifferenceLevel.FIELD);
       responseDiffs.add(diff);
    }
}
