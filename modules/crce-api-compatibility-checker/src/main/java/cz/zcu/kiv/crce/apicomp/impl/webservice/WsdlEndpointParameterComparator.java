package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;

import java.util.Arrays;
import java.util.List;

public class WsdlEndpointParameterComparator extends EndpointParameterComparator {

    public WsdlEndpointParameterComparator(Capability method1, Capability method2) {
        super(method1, method2);
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
