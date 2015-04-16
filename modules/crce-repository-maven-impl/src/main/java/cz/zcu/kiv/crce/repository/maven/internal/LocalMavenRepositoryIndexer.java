package cz.zcu.kiv.crce.repository.maven.internal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.store.FSDirectory;
import org.apache.maven.index.ArtifactInfo;
import org.apache.maven.index.DefaultScannerListener;
import org.apache.maven.index.FlatSearchRequest;
import org.apache.maven.index.FlatSearchResponse;
import org.apache.maven.index.Indexer;
import org.apache.maven.index.IndexerEngine;
import org.apache.maven.index.MAVEN;
import org.apache.maven.index.Scanner;
import org.apache.maven.index.ScanningRequest;
import org.apache.maven.index.context.DefaultIndexingContext;
import org.apache.maven.index.context.IndexCreator;
import org.apache.maven.index.context.IndexUtils;
import org.apache.maven.index.context.IndexingContext;
import org.apache.maven.index.expr.SourcedSearchExpression;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.FileUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.concurrency.model.Task;
import cz.zcu.kiv.crce.repository.maven.internal.metadata.MetadataIndexerCallback;

/**
 *
 * @author Miroslav Brožek
 */
public class LocalMavenRepositoryIndexer extends Task<Object> {

    private static final Logger logger = LoggerFactory.getLogger(LocalMavenRepositoryIndexer.class);

    private final URI uri;
    private final MetadataIndexerCallback metadataIndexerCallback;
    private CloseableIndexingContext indexingContext;
    
    public LocalMavenRepositoryIndexer(URI uri, MetadataIndexerCallback metadataIndexerCallback) {
        super(uri.toString(), "Indexes local maven repository.", "crce-repository-maven-impl");
        this.uri = uri;
        this.metadataIndexerCallback = metadataIndexerCallback;
    }

    @Override
    protected Object run() throws Exception {
        logger.info("Indexing local Maven repository started: {}", uri);

        logger.debug("Updating Maven repository index started.");
        FlatSearchResponse response;
        try {
        	indexingContext = initIndexingContext("localMavenStore", new File(uri), new File("mvn_store_index"), true);
            Indexer indexer = indexingContext.getIndexer();

            BooleanQuery query = new BooleanQuery();
            query.add(indexer.constructQuery(MAVEN.PACKAGING, new SourcedSearchExpression("bundle")), BooleanClause.Occur.MUST);

            response = indexer.searchFlat(new FlatSearchRequest(query, indexingContext));
        } catch (Exception e) {
            logger.error("Error updating Maven repository index.", e);
            return null;
        }
        logger.debug("Updating Maven repository index finished.");

        RepositorySystem repositorySystem = newRepositorySystem(); //Aether
        RepositorySystemSession session = newSession(repositorySystem, uri);

        logger.debug("Indexing artifacts (amount: {}).", response.getTotalHitsCount());

        ArtifactRequest artifactRequest = new ArtifactRequest();
        for (ArtifactInfo ai : response.getResults()) {
            artifactRequest.setArtifact(new DefaultArtifact(ai.groupId + ":" + ai.artifactId + ":" + ai.version));
            ArtifactResult result;
            try {
                result = repositorySystem.resolveArtifact(session, artifactRequest);
                metadataIndexerCallback.index(result.getArtifact(), this);
            } catch (ArtifactResolutionException e) {
                logger.info("Artifact is not present in local repository: " + artifactRequest.toString());
                // TODO optionally download the artifact from a remote repository
                continue;
            }           
            
        }

        logger.info("Indexing local Maven repository finished: {}", uri);
        
        
        return null;
    }


    private static RepositorySystem newRepositorySystem() {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        return locator.getService(RepositorySystem.class);
    }

    private static RepositorySystemSession newSession(RepositorySystem system, URI uri) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        LocalRepository localRepo = new LocalRepository(new File(uri));
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

        return session;
    }


    private CloseableIndexingContext initIndexingContext(String name, File repository, File indexParentDir, boolean update)
            throws PlexusContainerException, ComponentLookupException, IOException {
        logger.debug("Updating index '{}' at '{}' for local repo '{}', update: {}", name, indexParentDir, repository, update);
        if (repository == null || indexParentDir == null) {
        	logger.debug("Mvn repository '{}' or index parent dir '{}' is null. Indexing could not be started!", repository, indexParentDir);
            return null;
        }
 
        if (!repository.exists()) {
            throw new IOException("Repository directory " + repository + " does not exist");
        }

        if (!indexParentDir.exists() && !indexParentDir.mkdirs()) {
            throw new IOException("Cannot create parent directory for indices: " + indexParentDir);
        }

        logger.debug("Initializing Plexus container.");

        PlexusContainer plexusContainer;
        Indexer indexer;
        try {
            ContainerConfiguration configuration = new DefaultContainerConfiguration();
            
            ClassWorld world = new ClassWorld();
            ClassRealm classRealm = new ClassRealm(world, "crce-maven-repo-indexer", getClass().getClassLoader());
            configuration.setRealm(classRealm);

            plexusContainer = new DefaultPlexusContainer(configuration);
            indexer = plexusContainer.lookup(Indexer.class);
//            final IndexUpdater indexUpdater = plexusContainer.lookup(IndexUpdater.class);
        } catch (Exception e) {
            logger.error("Error initializing Plexus container.", e);
            throw new IllegalStateException(e);
        }

        List<IndexCreator> indexers = new ArrayList<>();
        indexers.add(plexusContainer.lookup(IndexCreator.class, "min"));
        indexers.add( plexusContainer.lookup( IndexCreator.class, "maven-archetype" ) );
        indexers.add( plexusContainer.lookup( IndexCreator.class, "osgi-metadatas" ) );       

        logger.info("Creating indexing context of local maven store.");

        IndexingContext indexingContext = indexer.createIndexingContext(
                        name + "-context",
                        name,
                        repository,
                        new File(indexParentDir, name),
                        null,
                        null,
                        true,
                        true,
                        indexers
                );

        // always use temporary context when reindexing
        final File tmpFile = File.createTempFile(indexingContext.getId(), "-tmp", indexParentDir);
        final File tmpDir = new File(indexParentDir, tmpFile.getName() + ".dir");
        if (!tmpDir.mkdirs()) {
            throw new IOException("Cannot create temporary directory: " + tmpDir);
        }

        logger.debug("Temporary dir '{}' created.", tmpDir);


        try {
            Scanner scanner = plexusContainer.lookup(Scanner.class);
            IndexerEngine indexerEngine = plexusContainer.lookup(IndexerEngine.class);

            final FSDirectory directory = FSDirectory.open(tmpDir);
            if (update) {
                IndexUtils.copyDirectory(indexingContext.getIndexDirectory(), directory);
            }

            logger.debug("Creating temporary indexing context.");

            try(@SuppressWarnings("deprecation")
			CloseableIndexingContext tmpContext = new CloseableIndexingContext(
                    new DefaultIndexingContext(
                        indexingContext.getId() + "-tmp",
                        indexingContext.getRepositoryId(),
                        indexingContext.getRepository(),
                        directory,
                        indexingContext.getRepositoryUrl(),
                        indexingContext.getIndexUpdateUrl(),
                        indexingContext.getIndexCreators(),
                        true
                    ),
                    null)
            ) {

                logger.debug("Mvn local store scanning started.");

                ScanningRequest scanningRequest = new ScanningRequest(
                        tmpContext,
                        new DefaultScannerListener(tmpContext, indexerEngine, update, null),
                        null
                );

                long start_time = System.nanoTime();  
                scanner.scan(scanningRequest);
                long end_time = System.nanoTime();
                double difference = (end_time - start_time)/1e6;
                logger.debug("{} nanoseconds to index local maven repository", difference);
                
                tmpContext.updateTimestamp(true);

                logger.debug("Replacing contexts from temporary to origin.");

                indexingContext.replace(tmpContext.getIndexDirectory());
            } catch (Throwable t) {
                logger.error("Error indexing local Maven repository 1.", t);
            }
        } catch (IOException | ComponentLookupException ex) {
            logger.error("Error indexing local Maven repository 2.", ex);
            throw new IOException("Error scanning context " + indexingContext.getId() + ": " + ex, ex);
        } catch (Throwable t) {
                logger.error("Error indexing local Maven repository 3.", t);
        } finally {
            try {
                if (tmpFile.exists()) {
                    tmpFile.delete();
                }
            } finally {
                FileUtils.deleteDirectory(tmpDir); // TODO replace plexus utils
            }
        }

        logger.debug("Indexing local mvn store '{}' finished.", repository);

        return new CloseableIndexingContext(indexingContext, indexer);
    }

}
