package cz.zcu.kiv.crce.apicomp.impl.webservice.mov;

import cz.zcu.kiv.crce.apicomp.impl.mov.MovDetectionResult;
import cz.zcu.kiv.crce.metadata.Capability;

/**
 * The Json-WSP endpoint metadata do not contain URL attribute and so it has to be passed in separately.
 */
public class JsonWspEndpointMetadataMovComparator extends WsEndpointMetadataMovComparator {

    private String endpoint1Url;
    private String endpoint2Url;

    public JsonWspEndpointMetadataMovComparator(Capability endpoint1, Capability endpoint2, MovDetectionResult movDetectionResult, String endpoint1Url, String endpoint2Url) {
        super(endpoint1, endpoint2, movDetectionResult);
        this.endpoint1Url = endpoint1Url;
        this.endpoint2Url = endpoint2Url;
    }

    @Override
    public String getEndpoint1Url() {
        return endpoint1Url;
    }

    @Override
    protected String getEndpoint2Url() {
        return endpoint2Url;
    }
}
