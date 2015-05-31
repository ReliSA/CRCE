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

    public static final String LOCAL_MAVEN_STORE_URI = "local.maven.store.uri";
    public static final String LOCAL_STORE_NAME = "local.store.name";
    public static final String LOCAL_REPOSITORY_UPDATE = "local.repository.update";

    public static final String REMOTE_MAVEN_STORE_URI = "remote.maven.store.uri";
    public static final String REMOTE_STORE_NAME = "remote.store.name";
    public static final String REMOTE_REPOSITORY_UPDATE = "remote.repository.update";

    public static final String INDEXING_CONTEXT_URI ="indexing.context.uri";
    public static final String REMOTE_STORE_DEFAULT = "use.remote.maven.store.default";
    public static final String DEPENDENCY_HIERARCHY = "aether.find.dependency.hierarchy";
    public static final String RESOLVE_ARTIFACTS = "aether.resolve.artifacts";
    public static final String ARTIFACT_RESOLVE = "artifact.resolve";
    public static final String ARTIFACT_RESOLVE_PARAM = "artifact.resolve.param";
    public static final String AR_STRINGS = "gav:groupid:groupid-artifactid:groupid-artifactid-minversion";


    private static RepositoryWrapper localRepository;
    private static RepositoryWrapper remoteRepository;

    private static String indexingContextPath = "mvn_store_index";
    private static boolean remoteRepoDefault = false;
    private static boolean dependencyHierarchy = false;
    private static boolean resolveArtifacts = false;

    private static ArtifactResolutionStrategy artifactResolve = ArtifactResolutionStrategy.NEWEST;
    private static String artifactResolveParam = "";


    public static void initConfig(Dictionary<String, ?> properties) throws ConfigurationException {
        //Local Repo
        URI localUri = checkLocalURI(properties.get(LOCAL_MAVEN_STORE_URI).toString());
        String localName = properties.get(MavenStoreConfiguration.LOCAL_STORE_NAME).toString();
        Boolean localUpdate = toBoolean(properties.get(LOCAL_REPOSITORY_UPDATE).toString());
        localRepository = new RepositoryWrapper(localUri, localName, localUpdate, true);

        //Remote Repo
        URI remoteUri = checkRemoteURI(properties.get(REMOTE_MAVEN_STORE_URI).toString());
        String remoteName = properties.get(MavenStoreConfiguration.REMOTE_STORE_NAME).toString();
        Boolean remoteUpdate = toBoolean(properties.get(REMOTE_REPOSITORY_UPDATE).toString());
        remoteRepository = new RepositoryWrapper(remoteUri, remoteName, remoteUpdate, false);

        String indexContext = properties.get(INDEXING_CONTEXT_URI).toString();
        setIndexingContextPath(convertURItoString(indexContext));

        setRemoteRepoDefault(toBoolean(properties.get(REMOTE_STORE_DEFAULT).toString()));
        setDependencyHierarchy(toBoolean(properties.get(DEPENDENCY_HIERARCHY).toString()));
        setResolveArtifacts(toBoolean(properties.get(RESOLVE_ARTIFACTS).toString()));

        setArtifactResolve(ArtifactResolutionStrategy.fromValue(properties.get(ARTIFACT_RESOLVE).toString()));

        if (AR_STRINGS.contains(artifactResolve.getValue().toLowerCase())) {
            setArtifactResolveParam(properties.get(ARTIFACT_RESOLVE_PARAM).toString());
        }
    }

    private static URI checkLocalURI(String localRepoURI) throws ConfigurationException {
        URI uri;

        try {
            uri = new URI(localRepoURI);
        } catch (URISyntaxException ex) {
            throw new ConfigurationException(LOCAL_MAVEN_STORE_URI, "Invalid URI syntax: " + localRepoURI, ex);
        }

        if (uri.getScheme() == null) {
            return new File(localRepoURI).toURI();
        } else if ("file".equals(uri.getScheme())) {
            return uri;
        } else {
            throw new ConfigurationException(MavenStoreConfiguration.LOCAL_MAVEN_STORE_URI, "Wrong URI format: " + uri.getScheme());
        }
    }

    private static URI checkRemoteURI(String remoteRepoURI) throws ConfigurationException {
        URI uri;

        try {
            uri = new URI(remoteRepoURI);
        } catch (URISyntaxException ex) {
            throw new ConfigurationException(REMOTE_MAVEN_STORE_URI, "Invalid URI syntax: " + remoteRepoURI, ex);
        }

        if ("http".equals(uri.getScheme())) {
            return uri;
        } else {
            throw new ConfigurationException(REMOTE_MAVEN_STORE_URI, "Wrong URI format: " + uri.getScheme());
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
                throw new ConfigurationException(MavenStoreConfiguration.LOCAL_MAVEN_STORE_URI, "Wrong URI format: " + uri.getScheme());
            }

        } catch (URISyntaxException ex) {
            logger.error("Wrong URI, check configuration file!", ex);
            return null;
        }

    }

    private static boolean toBoolean(String s) throws ConfigurationException {
        if ("true".equalsIgnoreCase(s) || "1".equals(s)) {
            return true;
        } else if ("false".equalsIgnoreCase(s) || "0".equals(s)) {
            return false;
        } else {
            throw new ConfigurationException("CONVERSION FAIL", "Not possible convert value: " + s + " to boolean value. "
                    + "Must be 'true' or '1' or 'false' or '0'");
        }
    }

    public static RepositoryWrapper getLocalRepository() {
        return localRepository;
    }

    public static void setLocalRepository(RepositoryWrapper localRepository) {
        MavenStoreConfiguration.localRepository = localRepository;
    }

    public static RepositoryWrapper getRemoteRepository() {
        return remoteRepository;
    }

    public static void setRemoteRepository(RepositoryWrapper remoteRepository) {
        MavenStoreConfiguration.remoteRepository = remoteRepository;
    }

    public static String getIndexingContextPath() {
        return indexingContextPath;
    }

    public static void setIndexingContextPath(String indexingContexPath) {
        MavenStoreConfiguration.indexingContextPath = indexingContexPath;
    }

    public static boolean isRemoteRepoDefault() {
        return remoteRepoDefault;
    }

    public static void setRemoteRepoDefault(boolean remoteRepoDefault) {
        MavenStoreConfiguration.remoteRepoDefault = remoteRepoDefault;
    }


    public static boolean isDependencyHierarchy() {
        return dependencyHierarchy;
    }

    public static void setDependencyHierarchy(boolean dependencyHierarchy) {
        MavenStoreConfiguration.dependencyHierarchy = dependencyHierarchy;
    }

    public static boolean isResolveArtifacts() {
        return resolveArtifacts;
    }

    public static void setResolveArtifacts(boolean resolveArtifacts) {
        MavenStoreConfiguration.resolveArtifacts = resolveArtifacts;
    }

    public static ArtifactResolutionStrategy getArtifactResolve() {
        return artifactResolve;
    }

    public static void setArtifactResolve(ArtifactResolutionStrategy artifactResolve) {
        MavenStoreConfiguration.artifactResolve = artifactResolve;
    }

    public static String getArtifactResolveParam() {
        return artifactResolveParam;
    }

    public static void setArtifactResolveParam(String artifactResolveParam) {
        MavenStoreConfiguration.artifactResolveParam = artifactResolveParam;
    }
}

