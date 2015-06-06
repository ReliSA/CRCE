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


    private final RepositoryWrapper localRepository;
    private final RepositoryWrapper remoteRepository;

    private final String indexingContextPath;
    private final RepositoryType primaryRepository;
    private final ResolutionDepth resolutionDepth;
    private final ResolutionMethod resolutionMethod;

    private final ResolutionStrategy artifactResolve;
    private final String artifactResolveParam;


    public MavenStoreConfiguration(Dictionary<String, ?> properties) throws ConfigurationException {
        //Local Repo
        URI localUri = checkLocalURI(properties.get(CFG__REPOSITORY_LOCAL_URI).toString());
        String localName = properties.get(CFG__REPOSITORY_LOCAL_NAME).toString();
        Boolean localUpdate = toBoolean(properties.get(CFG__REPOSITORY_LOCAL_UPDATE_ON_STARTUP));
        localRepository = new RepositoryWrapper(localUri, localName, localUpdate, true);

        //Remote Repo
        URI remoteUri = checkRemoteURI(properties.get(CFG__REPOSITORY_REMOTE_URI).toString());
        String remoteName = properties.get(CFG__REPOSITORY_REMOTE_NAME).toString();
        Boolean remoteUpdate = toBoolean(properties.get(CFG__REPOSITORY_REMOTE_UPDATE_ON_STARTUP));
        remoteRepository = new RepositoryWrapper(remoteUri, remoteName, remoteUpdate, false);

        String indexContext = properties.get(CFG__INDEXING_CONTEXT_URI).toString();
        indexingContextPath = convertURItoString(indexContext);

        primaryRepository = RepositoryType.valueOfIgnoreCase(getProperty(properties, CFG__REPOSITORY_PRIMARY), null);
        if (primaryRepository == null) {
            throw new ConfigurationException(CFG__REPOSITORY_PRIMARY, "Invalid primary repository specification.");
        }
        resolutionDepth = ResolutionDepth.valueOfIgnoreCase(getProperty(properties, CFG__RESOLUTION_DEPTH), ResolutionDepth.DIRECT);
        resolutionMethod = ResolutionMethod.valueOfIgnoreCase(getProperty(properties, CFG__RESOLUTION_METHOD), ResolutionMethod.POM);

        artifactResolve = ResolutionStrategy.fromValue(properties.get(CFG__RESOLUTION_STRATEGY).toString());

        if (AR_STRINGS.contains(artifactResolve.getValue().toLowerCase())) {
            artifactResolveParam = properties.get(CFG__RESOLUTION_STRATEGY_PARAMETERS).toString();
        } else {
            artifactResolveParam = "";
        }
    }

    private static URI checkLocalURI(String localRepoURI) throws ConfigurationException {
        URI uri;

        try {
            uri = new URI(localRepoURI);
        } catch (URISyntaxException ex) {
            throw new ConfigurationException(CFG__REPOSITORY_LOCAL_URI, "Invalid URI syntax: " + localRepoURI, ex);
        }

        if (uri.getScheme() == null) {
            return new File(localRepoURI).toURI();
        } else if ("file".equals(uri.getScheme())) {
            return uri;
        } else {
            throw new ConfigurationException(CFG__REPOSITORY_LOCAL_URI, "Wrong URI format: " + uri.getScheme());
        }
    }

    private static URI checkRemoteURI(String remoteRepoURI) throws ConfigurationException {
        URI uri;

        try {
            uri = new URI(remoteRepoURI);
        } catch (URISyntaxException ex) {
            throw new ConfigurationException(CFG__REPOSITORY_REMOTE_URI, "Invalid URI syntax: " + remoteRepoURI, ex);
        }

        if ("http".equals(uri.getScheme())) {
            return uri;
        } else {
            throw new ConfigurationException(CFG__REPOSITORY_REMOTE_URI, "Wrong URI format: " + uri.getScheme());
        }

    }

    private static String convertURItoString(String localRepoURI) throws ConfigurationException {
        try {
            File file;
            URI uri = new URI(localRepoURI);
            if (uri.getScheme() == null) {
                file = new File(localRepoURI);
                uri = file.toURI();
                return new File(uri).getAbsolutePath();
            } else if ("file".equals(uri.getScheme())) {
                file = new File(uri);
                return file.getAbsolutePath();

            } else {
                throw new ConfigurationException(CFG__REPOSITORY_LOCAL_URI, "Wrong URI format: " + uri.getScheme());
            }

        } catch (URISyntaxException ex) {
            logger.error("Wrong URI, check configuration file!", ex);
            return null;
        }

    }

    private static boolean toBoolean(Object value) throws ConfigurationException {
        return value != null && value instanceof String && Boolean.valueOf(((String) value).trim());
    }

    public RepositoryWrapper getLocalRepository() {
        return localRepository;
    }

    public RepositoryWrapper getRemoteRepository() {
        return remoteRepository;
    }

    public String getIndexingContextPath() {
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

    private String getProperty(Dictionary<String, ?> properties, String key) {
        if (properties != null) {
            Object value = properties.get(key);
            if (value != null && value instanceof String) {
                return (String) value;
            }
        }
        return null;
    }
}

