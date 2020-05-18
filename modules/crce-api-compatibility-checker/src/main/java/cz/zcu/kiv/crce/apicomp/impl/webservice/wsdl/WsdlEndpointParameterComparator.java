package cz.zcu.kiv.crce.apicomp.impl.webservice.wsdl;

import cz.zcu.kiv.crce.apicomp.impl.webservice.common.EndpointParameterComparator;
import cz.zcu.kiv.crce.apicomp.impl.webservice.common.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;

import java.util.Arrays;
import java.util.List;

/**
 * Class that captures differences in parameters of WSDL-based APIs.
 */
public class WsdlEndpointParameterComparator extends EndpointParameterComparator {

    public WsdlEndpointParameterComparator(Capability endpoint1, Capability endpoint2) {
        super(endpoint1, endpoint2);
    }

    @Override
    protected List<AttributeType> getEqualAttributeTypes() {
        return Arrays.asList(
                WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__NAME,
                WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__ORDER
        );
    }

    @Override
    protected void compareOptionalParameter(Property param1, Property param2, Diff parameterDiff) {
        // do nothing, WSDL does not have optional
    }
}
