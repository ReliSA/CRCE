package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;

public class WsdlEndpointResponseComparator extends EndpointResponseComparator {

    public WsdlEndpointResponseComparator(Capability endpoint1, Capability endpoint2) {
        super(endpoint1, endpoint2);
    }

    @Override
    protected void compareResponses(Property response1, Property response2, Diff responseDiff) {
        String type1 = response1.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__TYPE);
        String type2 = response1.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__TYPE);

        compareDataTypes(type1, type2, responseDiff);
    }
}
