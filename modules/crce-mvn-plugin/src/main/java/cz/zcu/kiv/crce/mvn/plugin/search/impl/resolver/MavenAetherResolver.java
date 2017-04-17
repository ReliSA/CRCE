package cz.zcu.kiv.crce.mvn.plugin.search.impl.resolver;

import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenResolver;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.SimpleFoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.aether.MavenAetherLocator;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.aether.VersionRangeBuilder;
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
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * This implementation uses the Aether library to resolve artifacts from maven repositories.
 *
 * Created by Zdenek Vales on 9.4.2017.
 */
public class MavenAetherResolver implements MavenResolver {

    private static final Logger logger = LoggerFactory.getLogger(MavenAetherLocator.class);

    private static final String REPOSITORY_ID_DEF = "central";
    private static final String REPOSITORY_TYPE_DEF = "default";
    private static final String REPOSITORY_URL_DEF = "http://repo1.maven.org/maven2/";

    private static final String CONFIG_FILE_NAME = "/search/mavenAetherLocator.properties";
    public static final String REPOSITORY_ID_PROPERTY_NAME = "repository.id";
    public static final String REPOSITORY_TYPE_PROPERTY_NAME = "repository.type";
    public static final String REPOSITORY_URL_PROPERTY_NAME = "repository.url";

    private String repositoryId;
    private String repositoryType;
    private String repositoryUrl;

    private List<RemoteRepository> repositories;
    private RepositorySystem repositorySystem;
    private RepositorySystemSession repositorySystemSession;


    public MavenAetherResolver() {
        init();
    }

    /**
     * Creates a list of repositories containing central repository.
     * @return
     */
    private List<RemoteRepository> newRepositories()
    {
        return new ArrayList<RemoteRepository>( Arrays.asList( newCentralRepository() ) );
    }

    /**
     * Creates a new central repository object.
     * @return
     */
    private RemoteRepository newCentralRepository()
    {
        return new RemoteRepository.Builder(repositoryId, repositoryType, repositoryUrl).build();
    }

    /**
     * Initializes a new repository system for aether.
     * @return Repository system.
     */
    private RepositorySystem newRepositorySystem() {
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
    private RepositorySystemSession newSession(RepositorySystem system )
    {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        LocalRepository localRepo = new LocalRepository( "target/local-repo" );
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );

        return session;
    }

    /**
     * Initializes repository id, type and url from resource config file. If no file or values are found,
     * default ones are used.
     */
    private void init() {
        InputStream is = MavenAetherLocator.class.getResourceAsStream(CONFIG_FILE_NAME);
        Properties properties = new Properties();
        logger.info("Initializing "+MavenAetherLocator.class.getName()+".");

//        nexusIndexer = new DefaultNexusIndexer();
//        creators = new ArrayList<>();
//        creators.add(new MinimalArtifactInfoIndexCreator());

        if (is == null) {
            logger.debug("No properties file found.");
            // no file
            repositoryId = REPOSITORY_ID_DEF;
            repositoryType = REPOSITORY_TYPE_DEF;
            repositoryUrl = REPOSITORY_URL_DEF;
        } else {
            try {
                properties.load(is);
                repositoryId = properties.getProperty(REPOSITORY_ID_PROPERTY_NAME, REPOSITORY_ID_DEF);
                repositoryType = properties.getProperty(REPOSITORY_TYPE_PROPERTY_NAME, REPOSITORY_TYPE_DEF);
                repositoryUrl = properties.getProperty(REPOSITORY_URL_PROPERTY_NAME, REPOSITORY_URL_DEF);
            } catch (IOException e) {
                logger.error("Exception while configuring "+MavenAetherLocator.class.getName()+". "+e.getMessage());
                repositoryId = REPOSITORY_ID_DEF;
                repositoryType = REPOSITORY_TYPE_DEF;
                repositoryUrl = REPOSITORY_URL_DEF;
            }

            try {
                is.close();
            } catch (IOException e) {
                logger.error("Exception while closing the stream: "+e.getMessage());
            }
        }

        repositories = newRepositories();
        repositorySystem = newRepositorySystem();
        repositorySystemSession = newSession(repositorySystem);
    }

    @Override
    public File resolve(FoundArtifact foundArtifact) {
        // todo: tests
        repositorySystem = newRepositorySystem();
        repositorySystemSession = newSession(repositorySystem);
        Artifact artifact = new DefaultArtifact(foundArtifact.getGroupId(),
                                                foundArtifact.getArtifactId(),
                                                "jar",
                                                foundArtifact.getVersion());
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        artifactRequest.setRepositories(newRepositories());

        ArtifactResult artifactResult = null;
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
        // todo: tests
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
