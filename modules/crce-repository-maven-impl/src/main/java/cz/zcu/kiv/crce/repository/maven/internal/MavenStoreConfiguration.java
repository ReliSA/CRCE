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

    public static final String CFG__JMX_ENABLED = "jmx.enabled";
    
    public static final String CFG__REPOSITORY_ENABLED = "repository.enabled";
    
    public static final String CFG__REPOSITORY_LOCAL_URI = "repository.local.uri";
    public static final String CFG__REPOSITORY_LOCAL_NAME = "repository.local.name";
    public static final String CFG__REPOSITORY_LOCAL_UPDATE_EXISTING_INDEX = "repository.local.update-existing-index";

    public static final String CFG__REPOSITORY_REMOTE_URI = "repository.remote.uri";
    public static final String CFG__REPOSITORY_REMOTE_NAME = "repository.remote.name";
    public static final String CFG__REPOSITORY_REMOTE_UPDATE_EXISTING_INDEX = "repository.remote.update-existing-index";

    public static final String CFG__REPOSITORY_PRIMARY = "repository.primary";

    public static final String CFG__MAVEN_INDEX_URI = "maven-index.uri";

    public static final String CFG__RESOLUTION_DEPTH = "resolution.depth";
    public static final String CFG__RESOLUTION_METHOD = "resolution.method";
    public static final String CFG__RESOLUTION_STRATEGY = "resolution.strategy";
    public static final String CFG__RESOLUTION_STRATEGY_PARAMETERS = "resolution.strategy.parameters";

    public static final String AR_STRINGS = "gav:groupid:groupid-artifactid:groupid-artifactid-minversion";


    private final RepositoryConfiguration localRepository;
    private final RepositoryConfiguration remoteRepository;

    private final File mavenIndexRootPath;
    private final RepositoryType primaryRepository;
    private final ResolutionDepth resolutionDepth;
    private final ResolutionMethod resolutionMethod;

    private final ResolutionStrategy resolutionStrategy;
    private final String resolutionStrategyParameters;
    private final boolean repositoryEnabled;
    private final boolean jmxEnabled;


    public MavenStoreConfiguration(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
        repositoryEnabled = Boolean.parseBoolean(getProperty(properties, CFG__REPOSITORY_ENABLED));
        
        //Local Repo
        URI uri = getLocalUri(properties);
        String name = getProperty(properties, CFG__REPOSITORY_LOCAL_NAME, pid + "-local");
        Boolean updateOnStartup = Boolean.valueOf(getProperty(properties, CFG__REPOSITORY_LOCAL_UPDATE_EXISTING_INDEX));
        localRepository = new RepositoryConfiguration(uri, name, updateOnStartup, true);

        //Remote Repo
        uri = getRemoteUri(properties);
        name = getProperty(properties, CFG__REPOSITORY_REMOTE_NAME, pid + "-remote");
        updateOnStartup = Boolean.valueOf(getProperty(properties, CFG__REPOSITORY_REMOTE_UPDATE_EXISTING_INDEX));
        remoteRepository = new RepositoryConfiguration(uri, name, updateOnStartup, false);

        mavenIndexRootPath = getMavenIndexRootPath(properties);

        primaryRepository = RepositoryType.valueOfIgnoreCase(getProperty(properties, CFG__REPOSITORY_PRIMARY), null);
        if (primaryRepository == null) {
            throw new ConfigurationException(CFG__REPOSITORY_PRIMARY, "Invalid primary repository specification.");
        }
        resolutionDepth = ResolutionDepth.valueOfIgnoreCase(getProperty(properties, CFG__RESOLUTION_DEPTH), ResolutionDepth.DIRECT);
        resolutionMethod = ResolutionMethod.valueOfIgnoreCase(getProperty(properties, CFG__RESOLUTION_METHOD), ResolutionMethod.POM);

        resolutionStrategy = ResolutionStrategy.fromValue(getProperty(properties, CFG__RESOLUTION_STRATEGY, ResolutionStrategy.NEWEST.getValue()));

        if (AR_STRINGS.contains(resolutionStrategy.getValue().toLowerCase())) {
            resolutionStrategyParameters = getProperty(properties, CFG__RESOLUTION_STRATEGY_PARAMETERS, "");
        } else {
            resolutionStrategyParameters = "";
        }
        
        jmxEnabled = Boolean.parseBoolean(getProperty(properties, CFG__REPOSITORY_ENABLED));
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

    private static File getMavenIndexRootPath(Dictionary<String, ?> properties) throws ConfigurationException {
        String uriString = getProperty(properties, CFG__MAVEN_INDEX_URI, "maven-index");

        try {
            URI uri = new URI(uriString);
            if (uri.getScheme() == null) {
                return new File(uriString).getAbsoluteFile();
            } else if ("file".equals(uri.getScheme())) {
                return new File(uri).getAbsoluteFile();
            } else {
                throw new ConfigurationException(CFG__MAVEN_INDEX_URI, "Wrong URI format: " + uri.getScheme());
            }
        } catch (URISyntaxException ex) {
            logger.error("Wrong URI, check configuration file!", ex);
            return null;
        }
    }

    public RepositoryConfiguration getLocalRepository() {
        return localRepository;
    }

    public RepositoryConfiguration getRemoteRepository() {
        return remoteRepository;
    }

    public File getMavenIndexRootPath() {
        return mavenIndexRootPath;
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

    public ResolutionStrategy getResolutionStrategy() {
        return resolutionStrategy;
    }

    public String getResolutionStrategyParameters() {
        return resolutionStrategyParameters;
    }

    public boolean isRepositoryEnabled() {
        return repositoryEnabled;
    }

    public boolean isJmxEnabled() {
        return jmxEnabled;
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

