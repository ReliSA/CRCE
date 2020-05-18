package cz.zcu.kiv.crce.apicomp.impl.webservice.common;

import cz.zcu.kiv.crce.apicomp.impl.mov.common.MovDetectionResult;
import cz.zcu.kiv.crce.apicomp.internal.DiffUtils;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Compares metadata of two WSDL/WADL endpoints (=operations) with respect to provided
 * MOV details. Handles endpoints with NAME and URL as metadata.
 *
 * Supports following MOV flags combinations:
 *
 * !host && !path
 * host && !path
 * !host && path
 * host && path
 *
 * and returns UNK for everything else.
 */
public class WsEndpointMetadataMovComparator extends EndpointFeatureComparator {

    protected final static Logger logger = LoggerFactory.getLogger(WsEndpointMetadataMovComparator.class);

    private MovDetectionResult movDetectionResult;

    public WsEndpointMetadataMovComparator(Capability endpoint1, Capability endpoint2, MovDetectionResult movDetectionResult) {
        super(endpoint1, endpoint2);
        this.movDetectionResult = movDetectionResult;
    }

    @Override
    public List<Diff> compare() {
        logger.debug("Comparing metadata of endpoints '{}', '{}.",
                endpoint1.getAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME),
                endpoint2.getAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME)
        );

        List<Diff> metadataDiff = new ArrayList<>();
        // compare endpoint metadata without host part of url
        Attribute a1Name = endpoint1.getAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME);
        Attribute a2Name = endpoint2.getAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME);

        logger.trace("Comparing name attributes.");
        if (a1Name != null && a1Name.equals(a2Name)) {
            logger.trace("Name attributes are equal.");
            metadataDiff.add(DiffUtils.createDiff(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME.getName(), DifferenceLevel.FIELD, Difference.NON));

            // urls are affected by the MOV flag
            metadataDiff.add(compareUrls());

        } else {
            logger.trace("Name attributes are not equal.");
            metadataDiff.add(DiffUtils.createDiff(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME.getName(), DifferenceLevel.FIELD, Difference.UNK));
        }

        logger.debug("Metadata compared.");
        return metadataDiff;
    }

    /**
     * Compares URLs of two endpoints with respect to MOV detection result and returns Diff describing the differences.
     * @return
     */
    protected Diff compareUrls() {
        logger.trace("Comparing URLs");
        Diff urlDiff = DiffUtils.createDiff(
                WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__URL.getName(),
                DifferenceLevel.FIELD,
                Difference.NON);

        String e1Url = getEndpoint1Url();
        String e2Url = getEndpoint2Url();

        if (e1Url != null && e2Url != null) {
            try {
                URL url1 = new URL(e1Url);
                URL url2 = new URL(e2Url);

                if (!movDetectionResult.hostDiff && !movDetectionResult.pathDiff) {
                    urlDiff.setValue(url1.equals(url2) ? Difference.NON : Difference.UNK);
                } else if (movDetectionResult.hostDiff && !movDetectionResult.pathDiff) {
                    boolean pathSame = comparePaths(url1, url2);
                    urlDiff.setValue(pathSame ? Difference.NON : Difference.UNK);
                } else if (!movDetectionResult.hostDiff && movDetectionResult.pathDiff) {
                    boolean hostSame = compareHosts(url1, url2);
                    urlDiff.setValue(hostSame ? Difference.NON : Difference.UNK);
                } else {
                    // if is hostDiff && pathDiff, set the URL value to NON as there's nothing to compare
                    urlDiff.setValue(Difference.NON);
                }


            } catch (MalformedURLException e) {
                urlDiff.setValue(Difference.UNK);
            }
        } else {
            logger.warn("One of the URLs is null.");
            urlDiff.setValue(Difference.UNK);
        }

        logger.trace("Result: {}.", urlDiff.getValue());
        return urlDiff;
    }

    protected String getEndpoint2Url() {
        return endpoint2.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__URL);
    }

    protected String getEndpoint1Url() {
        return endpoint1.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__URL);
    }

    /**
     * Compares paths of two urls.
     * @param url1 Url of the first endpoint.
     * @param url2 Url of the second endpoint.
     * @return True if the paths are same.
     */
    private boolean comparePaths(URL url1, URL url2) {
        return url1.getPath().equals(url2.getPath());
    }

    /**
     * Compares hosts of two urls.
     * @param url1 Url of the first endpoint.
     * @param url2 Url of the second endpoint.
     * @return True if the hosts are same.
     */
    private boolean compareHosts(URL url1, URL url2) {
        return url1.getHost().equals(url2.getHost());
    }

    @Override
    protected String getFeatureNamespace() {
        return null;
    }
}
