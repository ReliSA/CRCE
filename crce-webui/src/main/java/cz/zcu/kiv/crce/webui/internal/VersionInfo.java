package cz.zcu.kiv.crce.webui.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides information about application version.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class VersionInfo {

    private static final Logger logger = LoggerFactory.getLogger(VersionInfo.class);

    private static final String MANIFEST = "/META-INF/MANIFEST.MF";
    private static final String PRODUCT_VERSION = "Product-Version";
    private static final String IMPLEMENTATION_BUILD = "Implementation-Build";
    private static final String UNKNOWN = "unknown";

    private static VersionInfo instance = null;
    private String productVersion;
    private String buildRevision;

    /**
     * Returns singleton instance of this class.
     * @param servletContext Servlet context for reading WAR Manifest entries. If null, then the current
     * class classloader will be used to read Manifest entries which can lead to that wrong Manifest will be read.
     * @return Instance of this class.
     */
    public static synchronized VersionInfo getVersionInfo(ServletContext servletContext) {
        if (instance == null) {
            instance = new VersionInfo();
            /*
             * Product version is currently stored in WebUI MANIFEST, which could cause
             * information mismatch if WAR from another build will be deployed.
             * But for now it's enough.
             */
            try (InputStream is
                    = servletContext != null ? servletContext.getResourceAsStream(MANIFEST) : Class.class.getResourceAsStream(MANIFEST)) {
                Properties properties = new Properties();
                properties.load(is);
                instance.productVersion = properties.getProperty(PRODUCT_VERSION);
                instance.buildRevision = properties.getProperty(IMPLEMENTATION_BUILD);
            } catch (IOException e) {
                logger.error("Could not read version info from Manifest.", e);
            }
        }
        return instance;
    }

    public String getProductVersion() {
        return productVersion != null ? productVersion : UNKNOWN;
    }

    public String getBuildRevision() {
        return buildRevision != null ? buildRevision : UNKNOWN;
    }

    private VersionInfo() {
        // singleton
    }
}
