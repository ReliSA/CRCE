package cz.zcu.kiv.crce.repository.maven.internal;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Config class holding parameters for CRCE maven store
 *
 * @author M.Brozek
 */
public class MavenStoreConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MavenStoreConfiguration.class);

    public static final String CFG__REPOSITORY_LOCAL_URI = "repository.local.uri";
    public static final String CFG__REPOSITORY_LOCAL_NAME = "repository.local.name";
    public static final String CFG__REPOSITORY_LOCAL_UPDATE_ON_STARTUP = "repository.local.update-on-startup";

    public static final String CFG__REPOSITORY_REMOTE_URI = "repository.remote.uri";
    public static final String CFG__REPOSITORY_REMOTE_NAME = "repository.remote.name";
    public static final String CFG__REPOSITORY_REMOTE_UPDATE_ON_STARTUP = "repository.remote.update-on-startup";

    public static final String CFG__REPOSITORY_PRIMARY = "repository.primary";

    public static final String CFG__INDEXING_CONTEXT_URI ="indexing.context.uri";

    public static final String CFG__RESOLUTION_DEPTH = "resolution.depth";
    public static final String CFG__RESOLUTION_METHOD = "resolution.method";
    public static final String CFG__RESOLUTION_STRATEGY = "resolution.strategy";
    public static final String CFG__RESOLUTION_STRATEGY_PARAMETERS = "resolution.strategy.parameters";

    public static final String AR_STRINGS = "gav:groupid:groupid-artifactid:groupid-artifactid-minversion";


    private final RepositoryConfiguration localRepository;
    private final RepositoryConfiguration remoteRepository;

    private final File indexingContextPath;
    private final RepositoryType primaryRepository;
    private final ResolutionDepth resolutionDepth;
    private final ResolutionMethod resolutionMethod;

    private final ResolutionStrategy artifactResolve;
    private final String artifactResolveParam;


    public MavenStoreConfiguration(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        //Local Repo
        URI uri = getLocalUri(properties);
        String name = getProperty(properties, CFG__REPOSITORY_LOCAL_NAME, pid + "-local");
        Boolean updateOnStartup = toBoolean(properties.get(CFG__REPOSITORY_LOCAL_UPDATE_ON_STARTUP));
        localRepository = new RepositoryConfiguration(uri, name, updateOnStartup, true);

        //Remote Repo
        uri = getRemoteUri(properties);
        name = getProperty(properties, CFG__REPOSITORY_REMOTE_NAME, pid + "-remote");
        updateOnStartup = toBoolean(properties.get(CFG__REPOSITORY_REMOTE_UPDATE_ON_STARTUP));
        remoteRepository = new RepositoryConfiguration(uri, name, updateOnStartup, false);

        indexingContextPath = getIndexingContextPath(properties);

        primaryRepository = RepositoryType.valueOfIgnoreCase(getProperty(properties, CFG__REPOSITORY_PRIMARY), null);
        if (primaryRepository == null) {
            throw new ConfigurationException(CFG__REPOSITORY_PRIMARY, "Invalid primary repository specification.");
        }
        resolutionDepth = ResolutionDepth.valueOfIgnoreCase(getProperty(properties, CFG__RESOLUTION_DEPTH), ResolutionDepth.DIRECT);
        resolutionMethod = ResolutionMethod.valueOfIgnoreCase(getProperty(properties, CFG__RESOLUTION_METHOD), ResolutionMethod.POM);

        artifactResolve = ResolutionStrategy.fromValue(getProperty(properties, CFG__RESOLUTION_STRATEGY, ResolutionStrategy.NEWEST.getValue()));

        if (AR_STRINGS.contains(artifactResolve.getValue().toLowerCase())) {
            artifactResolveParam = getProperty(properties, CFG__RESOLUTION_STRATEGY_PARAMETERS, "");
        } else {
            artifactResolveParam = "";
        }
    }

    private static URI getLocalUri(Dictionary<String, ?> properties) throws ConfigurationException {
        String uriString = getProperty(properties, CFG__REPOSITORY_LOCAL_URI);
        if (uriString == null) {
            throw new ConfigurationException(CFG__REPOSITORY_LOCAL_URI, "Remote repository URI cannot be null.");
        }

        URI uri;
        try {
            uri = new URI(uriString);
        } catch (URISyntaxException ex) {
            throw new ConfigurationException(CFG__REPOSITORY_LOCAL_URI, "Invalid URI syntax: " + uriString, ex);
        }

        if (uri.getScheme() == null) {
            return new File(uriString).toURI();
        } else if ("file".equals(uri.getScheme())) {
            return uri;
        } else {
            throw new ConfigurationException(CFG__REPOSITORY_LOCAL_URI, "Wrong URI format: " + uri.getScheme());
        }
    }

    private static URI getRemoteUri(Dictionary<String, ?> properties) throws ConfigurationException {
        String uriString = getProperty(properties, CFG__REPOSITORY_REMOTE_URI);
        if (uriString == null) {
            throw new ConfigurationException(CFG__REPOSITORY_REMOTE_URI, "Local repository URI cannot be null.");
        }

        URI uri;
        try {
            uri = new URI(uriString);
        } catch (URISyntaxException ex) {
            throw new ConfigurationException(CFG__REPOSITORY_REMOTE_URI, "Invalid URI syntax: " + uriString, ex);
        }

        if ("http".equals(uri.getScheme())) {
            return uri;
        } else {
            throw new ConfigurationException(CFG__REPOSITORY_REMOTE_URI, "Wrong URI format: " + uri.getScheme());
        }
    }

    private static File getIndexingContextPath(Dictionary<String, ?> properties) throws ConfigurationException {
        String uriString = getProperty(properties, CFG__INDEXING_CONTEXT_URI, "maven-index");

        try {
            URI uri = new URI(uriString);
            if (uri.getScheme() == null) {
                return new File(uriString).getAbsoluteFile();
            } else if ("file".equals(uri.getScheme())) {
                return new File(uri).getAbsoluteFile();
            } else {
                throw new ConfigurationException(CFG__INDEXING_CONTEXT_URI, "Wrong URI format: " + uri.getScheme());
            }
        } catch (URISyntaxException ex) {
            logger.error("Wrong URI, check configuration file!", ex);
            return null;
        }
    }

    private static boolean toBoolean(Object value) throws ConfigurationException {
        return value != null && value instanceof String && Boolean.valueOf(((String) value).trim());
    }

    public RepositoryConfiguration getLocalRepository() {
        return localRepository;
    }

    public RepositoryConfiguration getRemoteRepository() {
        return remoteRepository;
    }

    public File getIndexingContextPath() {
        return indexingContextPath;
    }

    public RepositoryType getPrimaryRepository() {
        return primaryRepository;
    }

    public ResolutionDepth getResolutionDepth() {
        return resolutionDepth;
    }

    public ResolutionMethod getResolutionMethod() {
        return resolutionMethod;
    }

    public ResolutionStrategy getArtifactResolve() {
        return artifactResolve;
    }

    public String getArtifactResolveParam() {
        return artifactResolveParam;
    }

    private static String getProperty(Dictionary<String, ?> properties, String key) {
        if (properties != null) {
            Object value = properties.get(key);
            if (value != null && value instanceof String) {
                return (String) value;
            }
        }
        return null;
    }

    private static String getProperty(Dictionary<String, ?> properties, String key, String defaultValue) {
        String value = getProperty(properties, key);
        return (value == null) ? defaultValue : value;
    }
}

