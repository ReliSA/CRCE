package cz.zcu.kiv.crce.crce_integration_tests.rest.support;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataContainerForTestingPurpose {

    public static final String BUNDLE1 = "src/test/resource/OSGI_without_EFP1.jar";
    public static final String BUNDLE2 = "OSGI_without_EFP2.jar";
    public static final String BUNDLE3 = "OSGI_without_EFP3.jar";
    private static final Logger logger = LoggerFactory.getLogger(DataContainerForTestingPurpose.class);

    //--------------------
    /**
     * Returns URI address format of given fileAbsolutePath.
     */
    public URI getUri(String fileAbsolutePath) {
        String uriText = null;
        if (System.getProperty("os.name").toString().startsWith("Windows")) {
            uriText = "file://localhost/" + fileAbsolutePath;
            uriText = uriText.replace('\\', '/');
        } else {
            uriText = "file:" + fileAbsolutePath;
        }

        URI uri = null;
        try {
            uri = new URI(uriText);
        } catch (URISyntaxException e) {
            logger.info("URISyntaxException during processing URI path of input resource.");
        }
        return uri;
    }
}
