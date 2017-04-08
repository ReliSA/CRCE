package cz.zcu.kiv.crce.mvn.plugin.search.impl.aether;

import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenLocator;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.SimpleFoundArtifact;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.wagon.Wagon;
import org.codehaus.plexus.*;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
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
import org.sonatype.nexus.index.*;
import org.sonatype.nexus.index.context.IndexCreator;
import org.sonatype.nexus.index.context.IndexingContext;
import org.sonatype.nexus.index.context.UnsupportedExistingLuceneIndexException;
import org.sonatype.nexus.index.creator.MinimalArtifactInfoIndexCreator;
import org.sonatype.nexus.index.updater.IndexUpdater;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * This locator uses the Eclipse Aether to search through central maven repo for artifacts.
 *
 * Note that FoundArtifacts returned by locate method will have pomUrl and jarUrl set to an empty string.
 * Also when resolving, those properties are not goind to be used.
 *
 * @author Zdendek Vales
 */
// todo: use nexus indexer for searching and aether for resolving artifacts
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

//    private PlexusContainer plexusContainer;
//    private IndexUpdater indexUpdater;
//    private NexusIndexer nexusIndexer;
//    private List<IndexCreator> creators;
//    private Wagon httpWagon;


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

//        try {
//            nexusIndexer.addIndexingContext("def-repo", repositoryId, new File(repositoryUrl), new File("/search/index"), repositoryUrl, null, creators);
//        } catch (IOException | UnsupportedExistingLuceneIndexException e) {
//            logger.error("Initialization of nexus indexer failed: "+e.getMessage());
//            e.printStackTrace();
//        }
    }

//    private void initIndexer() throws ComponentLookupException, PlexusContainerException, IOException, UnsupportedExistingLuceneIndexException {
//        final DefaultContainerConfiguration config = new DefaultContainerConfiguration();
//        config.setClassPathScanning( PlexusConstants.SCANNING_INDEX );
//        this.plexusContainer = new DefaultPlexusContainer( config );
//
//        // lookup the indexer components from plexus
//        nexusIndexer = plexusContainer.lookup( NexusIndexer.class );
//        this.indexUpdater = plexusContainer.lookup( IndexUpdater.class );
//        // lookup wagon used to remotely fetch index
//        this.httpWagon = plexusContainer.lookup( Wagon.class, "http" );
//
//        File centralLocalCache = new File( "target/central-cache" );
//        File centralIndexDir = new File( "target/central-index" );
//
//        // Creators we want to use (search for fields it defines)
//        List<IndexCreator> indexers = new ArrayList<IndexCreator>();
//        indexers.add( plexusContainer.lookup( IndexCreator.class, "min" ) );
//        indexers.add( plexusContainer.lookup( IndexCreator.class, "jarContent" ) );
//        indexers.add( plexusContainer.lookup( IndexCreator.class, "maven-plugin" ) );
//
//        // Create context for central repository index
//        IndexingContext centralContext =
//                nexusIndexer.addIndexingContext( "central-context", "central", centralLocalCache, centralIndexDir,
//                        "http://repo1.maven.org/maven2", null, indexers );
//    }

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

//        BooleanQuery bq = new BooleanQuery();
//        bq.add(new TermQuery(new Term("artifactId", artifactId)), BooleanClause.Occur.MUST);
//        bq.add(new TermQuery(new Term("groupId", groupId)), BooleanClause.Occur.MUST);
//        bq.add(new TermQuery(new Term("version", version)), BooleanClause.Occur.MUST);
//
//        FlatSearchRequest request = new FlatSearchRequest(bq);
//        FlatSearchResponse response = null;
//        try {
//            response = nexusIndexer.searchFlat(request);
//        } catch (IOException e) {
//            logger.error("Unexpected exception occurred while locating artifact.");
//            e.printStackTrace();
//        }
//
//        Set<ArtifactInfo> artifactInfos = response.getResults();
//        if(artifactInfos.isEmpty()) {
//            return null;
//        } else {
//            ArtifactInfo ai = artifactInfos.iterator().next();
//            FoundArtifact result = new SimpleFoundArtifact(ai.groupId, ai.artifactId, ai.version, "", "");
//            return result;
//        }


        RepositorySystem repositorySystem = newRepositorySystem();
        RepositorySystemSession session = newSession(repositorySystem);

//        Artifact artifact = new DefaultArtifact(groupId+":"+artifactId+":"+VersionRangeBuilder.singleVersion(version));
        Artifact artifact = new DefaultArtifact(groupId, artifactId, "jar", version);

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        artifactRequest.setRepositories(newRepositories());

        ArtifactResult artifactResult = null;
        try {
            artifactResult = repositorySystem.resolveArtifact(session, artifactRequest);
        } catch (ArtifactResolutionException e) {
            logger.error("Unexpected error occurred while resolving artifact: "+e.getMessage());
            e.printStackTrace();
            return null;
        }

        FoundArtifact foundArtifact = new SimpleFoundArtifact(artifactResult.getArtifact());

        return foundArtifact;
    }

    @Override
    public Collection<FoundArtifact> locate(String groupId, String artifactId) {
        RepositorySystem repositorySystem = newRepositorySystem();
        RepositorySystemSession session = newSession(repositorySystem);

        Artifact artifact = new DefaultArtifact(groupId, artifactId, "jar", VersionRangeBuilder.allVersions());

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
        RepositorySystem repositorySystem = newRepositorySystem();
        RepositorySystemSession session = newSession(repositorySystem);

        Artifact artifact = new DefaultArtifact(groupId+":"+artifactId+":"+VersionRangeBuilder.versionRange(fromVersion, toVersion));

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
    public Collection<FoundArtifact> locate(String includedPackage) {
//        RepositorySystem repositorySystem = newRepositorySystem();
//        RepositorySystemSession session = newSession(repositorySystem);


        return null;
    }

    @Override
    public FoundArtifact resolve(FoundArtifact artifact) {
        RepositorySystem repositorySystem = newRepositorySystem();
        RepositorySystemSession session = newSession(repositorySystem);

        // resolve artifact
        Artifact toBeResolved = new DefaultArtifact(artifact.getGroupId(), artifact.getArtifactId(), "", artifact.getVersion());

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(toBeResolved);
        artifactRequest.setRepositories(newRepositories());

        ArtifactResult artifactResult = null;
        try {
            artifactResult = repositorySystem.resolveArtifact(session, artifactRequest);
        } catch (ArtifactResolutionException e) {
            logger.error("Unexpected error occurred while resolving artifact: "+e.getMessage());
            e.printStackTrace();
            return null;
        }

        FoundArtifact result = new SimpleFoundArtifact(artifactResult.getArtifact());

        return result;
    }

    @Override
    public Collection<FoundArtifact> resolveArtifacts(Collection<FoundArtifact> artifacts) {
        throw new UnsupportedOperationException("Sorry, not implemented yet.");
    }
}
