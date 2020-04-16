package cz.zcu.kiv.crce.apicomp.impl.webservice.mov;

import cz.zcu.kiv.crce.apicomp.impl.mov.MovDetectionResult;
import cz.zcu.kiv.crce.apicomp.impl.webservice.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.apicomp.internal.DiffUtils;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.metadata.Capability;

public class JsonWspEndpointMetadataMovComparator extends WsEndpointMetadataMovComparator {

    public JsonWspEndpointMetadataMovComparator(Capability endpoint1, Capability endpoint2, MovDetectionResult movDetectionResult) {
        super(endpoint1, endpoint2, movDetectionResult);
    }

    @Override
    protected Diff compareUrls() {
        return DiffUtils.createDiff(
                WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__URL.getName(),
                DifferenceLevel.FIELD,
                Difference.NON);
    }
}
