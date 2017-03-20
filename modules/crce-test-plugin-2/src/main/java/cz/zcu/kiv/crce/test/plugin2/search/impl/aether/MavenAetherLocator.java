package cz.zcu.kiv.crce.test.plugin2.search.impl.aether;

import cz.zcu.kiv.crce.test.plugin2.search.FoundArtifact;
import cz.zcu.kiv.crce.test.plugin2.search.MavenLocator;
import cz.zcu.kiv.crce.test.plugin2.search.impl.SimpleFoundArtifact;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * This locator uses the Eclipse Aether to search through central maven repo for artifacts.
 *
 * @author Zdendek Vales
 */
public class MavenAetherLocator implements MavenLocator {

    private static final Logger logger = LoggerFactory.getLogger(MavenAetherLocator.class);

    private static final String REPOSITORY_ID_DEF = "central";
    private static final String REPOSITORY_TYPE_DEF = "default";
    private static final String REPOSITORY_URL_DEF = "http://repo1.maven.org/maven2/";

    private static final String CONFIG_FILE_NAME = "/search/mavenAetherLocator.properties";
    public static final String REPOSITORY_ID_PROPERTY_NAME = "repository.id";
    public static final String REPOSITORY_TYPE_PROPERTY_NAME = "repository.type";
    public static final String REPOSITORY_URL_PROPERTY_NAME = "repository.url";

    /**
     * Creates a list of repositories containing central repository.
     * @return
     */
    public List<RemoteRepository> newRepositories()
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

    private String repositoryId;
    private String repositoryType;
    private String repositoryUrl;


    public MavenAetherLocator() {
        init();
    }

    /**
     * Initializes repository id, type and url from resource config file. If no file or values are found,
     * default ones are used.
     */
    private void init() {
        InputStream is = MavenAetherLocator.class.getResourceAsStream(CONFIG_FILE_NAME);
        Properties properties = new Properties();
        logger.info("Initializing "+MavenAetherLocator.class.getName()+".");
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
        }
    }

    /**
     * Initializes a new repository system for aether.
     * @return Repository system.
     */
    private RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService( TransporterFactory.class, FileTransporterFactory.class );
        locator.addService( TransporterFactory.class, HttpTransporterFactory.class );

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


    @Override
    public FoundArtifact locate(String groupId, String artifactId, String version) {

        RepositorySystem repositorySystem = newRepositorySystem();
        RepositorySystemSession session = newSession(repositorySystem);

//        Artifact artifact = new DefaultArtifact(groupId+":"+artifactId+":"+VersionRangeBuilder.singleVersion(version));
        Artifact artifact = new DefaultArtifact(groupId+":"+artifactId+":"+version);

        ArtifactDescriptorRequest artifactDescriptorRequest = new ArtifactDescriptorRequest();
        artifactDescriptorRequest.setArtifact(artifact);
        artifactDescriptorRequest.setRepositories(newRepositories());

        ArtifactDescriptorResult artifactDescriptorResult = null;
        try {
            artifactDescriptorResult = repositorySystem.readArtifactDescriptor(session, artifactDescriptorRequest);
        } catch (ArtifactDescriptorException e) {
            logger.error("Unexpected error occurred while resolving artifact: "+e.getMessage());
            e.printStackTrace();
            return null;
        }

        FoundArtifact foundArtifact = new SimpleFoundArtifact(artifactDescriptorResult.getArtifact());

        return foundArtifact;
    }

    @Override
    public Collection<FoundArtifact> locate(String groupId, String artifactId) {
        RepositorySystem repositorySystem = newRepositorySystem();
        RepositorySystemSession session = newSession(repositorySystem);

        Artifact artifact = new DefaultArtifact(groupId+":"+artifactId+":"+VersionRangeBuilder.allVersions());

        VersionRangeRequest versionRangeRequest = new VersionRangeRequest();
        versionRangeRequest.setArtifact(artifact);
        versionRangeRequest.setRepositories(newRepositories());

        VersionRangeResult versionRangeResult = null;
        try {
            versionRangeResult = repositorySystem.resolveVersionRange(session, versionRangeRequest);
        } catch (VersionRangeResolutionException e) {
            logger.error("Unexpected error occurred while resolving artifact: "+e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }


        List<FoundArtifact> res = new ArrayList<>();
        for (Version v : versionRangeResult.getVersions()) {
            res.add(new SimpleFoundArtifact(groupId, artifactId, v.toString(), "", ""));
        }

        return res;
    }

    @Override
    public Collection<FoundArtifact> locate(String groupId, String artifactId, String fromVersion, String toVersion) {
        return null;
    }
}
