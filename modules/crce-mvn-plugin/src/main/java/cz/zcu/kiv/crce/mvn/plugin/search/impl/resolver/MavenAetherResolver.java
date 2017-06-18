package cz.zcu.kiv.crce.mvn.plugin.search.impl.resolver;

import cz.zcu.kiv.crce.mvn.plugin.search.Configurable;
import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenResolver;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * This implementation uses the Aether library to resolve artifacts from maven repositories.
 *
 * A list of repositories can be configured in property file like this:
 * reposityN.id=
 * repositoryN.type=
 * repositoryN.url=
 *
 * where N is the number of repository. N needs to start at 0 and always increase by 1.
 * If N is 0,1,3 only respositories 0 and 1 would be used.
 *
 *
 * Created by Zdenek Vales on 9.4.2017.
 */
public class MavenAetherResolver implements MavenResolver, Configurable {

    private static final Logger logger = LoggerFactory.getLogger(MavenAetherResolver.class);

    public static final String REPOSITORY_ID_DEF = "central";
    public static final String REPOSITORY_TYPE_DEF = "default";
    public static final String REPOSITORY_URL_DEF = "http://repo1.maven.org/maven2/";
    public static final String LOCAL_REPOSITORY_PATH_DEF = "target/local-repo";

    private static final String CONFIG_FILE_NAME = "/search/mavenAetherLocator.properties";
    public static final String LOCAL_REPOSITORY_PATH_PROPERTY_NAME = "repository.local.path";
    public static final String REPOSITORY_N_ID_PROPERTY_NAME = "repository%d.id";
    public static final String REPOSITORY_N_TYPE_PROPERTY_NAME = "repository%d.type";
    public static final String REPOSITORY_N_URL_PROPERTY_NAME = "repository%d.url";

    /**
     * Creates a repository object with values loaded in init() method.
     * @return
     */
    private static RemoteRepository newRepository(String repositoryId, String repositoryType, String repositoryUrl)
    {
        return new RemoteRepository.Builder(repositoryId, repositoryType, repositoryUrl).build();
    }

    /**
     * Initializes a new repository system for aether.
     * @return Repository system.
     */
    private static RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService( TransporterFactory.class, HttpTransporterFactory.class );
        locator.addService( TransporterFactory.class, FileTransporterFactory.class );

        return locator.getService(RepositorySystem.class);
    }

    /**
     * Initializes a new repository session for aether. This is used to keep common settings for artifact
     * resolutions.
     * @param system Initialized repository system.
     * @return Repository session.
     */
    private static RepositorySystemSession newSession(RepositorySystem system, String localRepoPath)
    {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository( localRepoPath );
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );

        return session;
    }

    private static String getNRepoId(int n) {
        return String.format(REPOSITORY_N_ID_PROPERTY_NAME, n);
    }

    private static String getNRepoType(int n) {
        return String.format(REPOSITORY_N_TYPE_PROPERTY_NAME, n);
    }

    private static String getNRepoUrl(int n) {
        return String.format(REPOSITORY_N_URL_PROPERTY_NAME, n);
    }

    private String localRepoPath;

    private List<RemoteRepository> repositories;
    private RepositorySystem repositorySystem;
    private RepositorySystemSession repositorySystemSession;


    public MavenAetherResolver() {
        init();
    }

    /**
     * Initializes repository id, type and url from resource config file. If no file or values are found,
     * default ones are used.
     */
    private void init() {
        try {
            reconfigure((File)null);
        } catch (FileNotFoundException e) {
            // this really shouldn't happen
            logger.error("Error while loading configuration from default file: "+e.getMessage());
        }
    }

    public List<RemoteRepository> getRepositories() {
        return repositories;
    }

    public String getLocalRepoPath() {
        return localRepoPath;
    }

    @Override
    public void reconfigure(InputStream sourceStream) {
        // load properties
        repositories = new ArrayList<>();
        Properties properties = new Properties();
        localRepoPath = null;
        try {
            properties.load(sourceStream);

            // load single properties first
            localRepoPath = properties.getProperty(LOCAL_REPOSITORY_PATH_PROPERTY_NAME, LOCAL_REPOSITORY_PATH_DEF);

            // load N repositories
            int n = 0;
            while(properties.getProperty(getNRepoId(n)) != null) {
                String repositoryId = properties.getProperty(getNRepoId(n), REPOSITORY_ID_DEF);
                String repositoryType = properties.getProperty(getNRepoType(n), REPOSITORY_TYPE_DEF);
                String repositoryUrl = properties.getProperty(getNRepoUrl(n), REPOSITORY_URL_DEF);

                repositories.add(newRepository(repositoryId, repositoryType, repositoryUrl));
                n++;
            }

        } catch (IOException e) {
            logger.error("Exception while configuring "+MavenAetherResolver.class.getSimpleName()+". "+e.getMessage()+", using default values.");
            if(repositories.isEmpty()) {
                repositories.add(newRepository(REPOSITORY_ID_DEF, REPOSITORY_TYPE_DEF, REPOSITORY_URL_DEF));
            }

            if(localRepoPath == null) {
                localRepoPath = LOCAL_REPOSITORY_PATH_DEF;
            }
        }

        repositorySystem = newRepositorySystem();
        repositorySystemSession = newSession(repositorySystem, localRepoPath);
    }

    @Override
    public void reconfigure(File sourceFile) throws FileNotFoundException {
        InputStream is;

        // bad file => use the default one
        if(sourceFile == null) {
            logger.debug("No configuration file, using the default one.");
            is = this.getClass().getResourceAsStream(CONFIG_FILE_NAME);
        } else {
            is = new FileInputStream(sourceFile);
        }

        reconfigure(is);

        try {
            is.close();
        } catch (IOException e) {
            logger.error("Exception while closing the stream: "+e.getMessage());
        }

    }

    @Override
    public File resolve(FoundArtifact foundArtifact) {
        Artifact artifact = new DefaultArtifact(foundArtifact.getGroupId(),
                                                foundArtifact.getArtifactId(),
                                                "jar",
                                                foundArtifact.getVersion());
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        artifactRequest.setRepositories(repositories);

        ArtifactResult artifactResult;
        try {
            artifactResult = repositorySystem.resolveArtifact(repositorySystemSession, artifactRequest);
        } catch (ArtifactResolutionException e) {
            logger.error("Unexpected error occurred while resolving artifact: "+e.getMessage());
            return null;
        }

        if(artifactResult.isResolved()) {
            return artifactResult.getArtifact().getFile();
        } else {
            return null;
        }
    }

    @Override
    public Collection<File> resolveArtifacts(Collection<FoundArtifact> foundArtifacts) {
        List<File> res = new ArrayList<>();
        for(FoundArtifact foundArtifact : foundArtifacts) {
            File f = resolve(foundArtifact);
            if(f != null) {
                res.add(f);
            }
        }

        return res;
    }
}
