package cz.zcu.kiv.crce.crce_webui_v2.other;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.VaadinServlet;

public class VersionInfo {
	private static final Logger logger = LoggerFactory.getLogger(VersionInfo.class);
	
	private static final String MANIFEST = "/META-INF/MANIFEST.MF";
	private static final String PRODUCT_VERSION = "Bundle-Version";
	private static final String IMPLEMENTATION_BUILD = "Implementation-Build";
	private static final String UNKNOWN = "unknown";

	private static VersionInfo instance = null;
	private String productVersion;
	private String buildRevision;

	public static synchronized VersionInfo getVersionInfo() {
		if (instance == null) {
			instance = new VersionInfo();
			Properties properties = new Properties();
			try (InputStream stream
                    = VaadinServlet.getCurrent().getServletContext().getResourceAsStream(MANIFEST)) {
				properties.load(stream);
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
}
