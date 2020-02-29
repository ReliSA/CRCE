package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.apicomp.internal.DiffUtils;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.impl.DefaultDiffImpl;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// todo: implementation
public class WsdlCompatibilityChecker extends WebservicesCompatibilityChecker {

    @Override
    protected Capability getOneRootCapability(Resource resource) {
        return null;
    }

    @Override
    protected void compare(CompatibilityCheckResult checkResult, Capability root1, Capability root2) {
        Diff result = new DefaultDiffImpl();

        // communication pattern must be same
        if (!compareCommunicationPatterns(root1, root2)) {
            result.setValue(Difference.MUT);
            result.setLevel(DifferenceLevel.TYPE);
            return ;
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
            result.addChild(methodDiff);

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
            result.addChild(diff);
        }

        return;
    }

    private boolean compareCommunicationPatterns(Capability root1, Capability root2) {
        String type1 = root1.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE);
        String type2 = root2.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE);

        return type1 != null && type1.equals(type2);
    }

    private Diff compareMethods(Capability api1Method, List<Capability> api2Methods) {
        return null;
    }
}
