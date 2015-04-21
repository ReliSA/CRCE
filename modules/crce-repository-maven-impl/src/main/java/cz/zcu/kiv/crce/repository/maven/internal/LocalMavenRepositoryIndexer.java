package cz.zcu.kiv.crce.repository.maven.internal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
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
import org.apache.maven.index.updater.IndexUpdateRequest;
import org.apache.maven.index.updater.IndexUpdateResult;
import org.apache.maven.index.updater.IndexUpdater;
import org.apache.maven.index.updater.ResourceFetcher;
import org.apache.maven.index.updater.WagonHelper;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.observers.AbstractTransferListener;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.FileUtils;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.concurrency.model.Task;
import cz.zcu.kiv.crce.repository.maven.internal.aether.RepositoryFactory;
import cz.zcu.kiv.crce.repository.maven.internal.metadata.MetadataIndexerCallback;

/**
 *
 * @author Miroslav Brožek
 */
public class LocalMavenRepositoryIndexer extends Task<Object> {

    private static final Logger logger = LoggerFactory.getLogger(LocalMavenRepositoryIndexer.class);

    private final URI uri;
    private final MetadataIndexerCallback metadataIndexerCallback;
    private CloseableIndexingContext closeableIndexingContext;
    private static final String INDEXING_CONTEXT = "mvn_store_index";
    
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
        	if(!MavenStoreConfig.isRemoteRepoDefault()){
        		closeableIndexingContext = createLocalRepoIndexingContext(MavenStoreConfig.getStoreName(), new File(uri), new File(INDEXING_CONTEXT), MavenStoreConfig.isUpdateRepository());        		
        	}
        	
        	else{
        		closeableIndexingContext = createRemoteRepositoryIndexingContext(MavenStoreConfig.getStoreName(), uri, new File(INDEXING_CONTEXT), MavenStoreConfig.isUpdateRepository());
        	}
        	
            Indexer indexer = closeableIndexingContext.getIndexer();

            BooleanQuery query = new BooleanQuery();
            query.add(indexer.constructQuery(MAVEN.PACKAGING, new SourcedSearchExpression("bundle")), BooleanClause.Occur.MUST);

            response = indexer.searchFlat(new FlatSearchRequest(query, closeableIndexingContext));
        } catch (Exception e) {
            logger.error("Error updating Maven repository index.", e);
            return null;
        }
        logger.debug("Updating Maven repository index finished.");
        logger.debug("Indexing artifacts (amount: {}).", response.getTotalHitsCount());
        
//		if (MavenStoreConfig.isRemoteRepoDefault()) {
//			for (ArtifactInfo ai : response.getResults()) {
//				DefaultArtifact a = new DefaultArtifact(ai.groupId + ":" + ai.artifactId + ":" + ai.version);			
//				metadataIndexerCallback.index(a, this);
//			}
//		}
		
		
		RepositorySystem system = RepositoryFactory.newRepositorySystem(); // Aether
		// localRepo must be defined even indexing remote repository
		RepositorySystemSession session = RepositoryFactory.newRepositorySystemSession(system);

		ArtifactRequest artifactRequest = new ArtifactRequest();
		for (ArtifactInfo ai : response.getResults()) {
			artifactRequest.setArtifact(new DefaultArtifact(ai.groupId + ":" + ai.artifactId + ":" + ai.version));
			if(MavenStoreConfig.isRemoteRepoDefault()){
				artifactRequest.addRepository(new RemoteRepository.Builder(MavenStoreConfig.getStoreName(), "default", MavenStoreConfig.getRemoteRepoURI()).build());
			}				
			ArtifactResult result;
			try {
				result = system.resolveArtifact(session, artifactRequest);
				metadataIndexerCallback.index(result.getArtifact(), this);
			} catch (ArtifactResolutionException e) {
				logger.debug("Artifact could not be found in local repository: " + artifactRequest.toString());
				// TODO optionally download the artifact from a remote repository or local .m2
				continue;
			}
		}			
		


        logger.info("Indexing local Maven repository finished: {}", uri);
        return null;
    }
 

	private CloseableIndexingContext createLocalRepoIndexingContext(String name, File repository, File indexParentDir, boolean update)
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
            ContainerConfiguration config = new DefaultContainerConfiguration();
            
            ClassWorld world = new ClassWorld();
            ClassRealm classRealm = new ClassRealm(world, "crce-maven-repo-indexer", getClass().getClassLoader());
            config.setRealm(classRealm);

            plexusContainer = new DefaultPlexusContainer(config);
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

                logger.debug("Maven local store scanning started.");

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

        logger.debug("Indexing local maven store '{}' finished.", repository);

        return new CloseableIndexingContext(indexingContext, indexer);
    }
	
	
	private CloseableIndexingContext createRemoteRepositoryIndexingContext(String storeName, URI uri, File indexParentDir, boolean update)
			throws IOException, PlexusContainerException, ComponentLookupException {
		if (!indexParentDir.exists() && !indexParentDir.mkdirs()) {
			throw new IOException("Cannot create parent directory for indices: " + indexParentDir);
		}

		logger.debug("Initializing Plexus container.");

		PlexusContainer plexusContainer;
		Indexer indexer;
		IndexUpdater indexUpdater;
		IndexingContext indexingContext;
		Wagon httpWagon;

		try {
			DefaultContainerConfiguration config = new DefaultContainerConfiguration();
			config.setClassPathScanning(PlexusConstants.SCANNING_INDEX);
			plexusContainer = new DefaultPlexusContainer(config);

			ClassWorld world = new ClassWorld();
			ClassRealm classRealm = new ClassRealm(world, "crce-maven-repo-indexer", getClass().getClassLoader());
			config.setRealm(classRealm);

			plexusContainer = new DefaultPlexusContainer(config);
			indexer = plexusContainer.lookup(Indexer.class);
			indexUpdater = plexusContainer.lookup(IndexUpdater.class);
			httpWagon = plexusContainer.lookup(Wagon.class, "http");

		} catch (Exception e) {
			logger.error("Error initializing Plexus container.", e);
			throw new IllegalStateException(e);
		}
		

		// Creators we want to use (search for fields it defines)
		List<IndexCreator> indexers = new ArrayList<IndexCreator>();
		indexers.add(plexusContainer.lookup(IndexCreator.class, "min"));
		// indexers.add( plexusContainer.lookup( IndexCreator.class,"maven-archetype" ) );
		// indexers.add( plexusContainer.lookup( IndexCreator.class,"osgi-metadatas" ) );

		// Create context for remote repository index
		indexingContext = indexer.createIndexingContext(storeName + "-context", storeName, new File(storeName + "cache"), new File(indexParentDir, storeName),
				uri.toString(), null, true, true, indexers);

		//TODO: replace 'update' for some trigger ...eg once in week after midnight
		if (update) {

			logger.info("Updating Index...");
			logger.info("This might take a while on first run, so please be patient! ... It could take 5 minutes and more");

			TransferListener listener = new AbstractTransferListener() {
				public void transferStarted(TransferEvent transferEvent) {
					logger.info("  Downloading " + transferEvent.getResource().getName());
				}

				public void transferProgress(TransferEvent transferEvent, byte[] buffer, int length) {
				}

				public void transferCompleted(TransferEvent transferEvent) {
					logger.info(" - DONE");
				}
			};

			// always use temporary context when reindexing
			final File tmpFile = File.createTempFile(indexingContext.getId(), "-tmp", indexParentDir);
			final File tmpDir = new File(indexParentDir, tmpFile.getName() + ".dir");
			if (!tmpDir.mkdirs()) {
				throw new IOException("Cannot create temporary directory: " + tmpDir);
			}

			logger.debug("Temporary dir '{}' created.", tmpDir);

			try {

				final FSDirectory directory = FSDirectory.open(tmpDir);
				IndexUtils.copyDirectory(indexingContext.getIndexDirectory(), directory);

				logger.debug("Creating temporary indexing context.");

				try (@SuppressWarnings("deprecation")
				CloseableIndexingContext tmpContext = new CloseableIndexingContext(new DefaultIndexingContext(indexingContext.getId()
						+ "-tmp", indexingContext.getRepositoryId(), indexingContext.getRepository(), directory,
						indexingContext.getRepositoryUrl(), indexingContext.getIndexUpdateUrl(), indexingContext.getIndexCreators(), true),
						null)) {

					logger.debug("Remote maven store indexing started.");
					long start_time = System.nanoTime();

					ResourceFetcher resourceFetcher = new WagonHelper.WagonFetcher(httpWagon, listener, null, null);
					Date indexingContextCurrentTimestamp = tmpContext.getTimestamp();
					IndexUpdateRequest updateRequest = new IndexUpdateRequest(tmpContext, resourceFetcher);
					IndexUpdateResult updateResult = indexUpdater.fetchAndUpdateIndex(updateRequest);
					
					if(updateResult.getTimestamp() == null){
						logger.debug("Index is up to date"); //as it is in DefaultIndexUpdater.class
						updateResult.setTimestamp(indexingContextCurrentTimestamp);
					}
					
					else if (updateResult.isFullUpdate()) {
						logger.debug("Full update happened!");
					}
					
					else if (updateResult.getTimestamp().equals(indexingContextCurrentTimestamp)) {
						logger.debug("No update needed, index is up to date!");
					}
					
					else {
						logger.debug("Incremental update happened, change covered " + indexingContextCurrentTimestamp + " - "
								+ updateResult.getTimestamp() + " period.");
					}

					logger.info("Indexing remote repository finished succesfully!!!");
					long end_time = System.nanoTime();
					double difference = (end_time - start_time) / 1e6;
					logger.debug("Indexing remote repository took {} nanoseconds ", difference);

					tmpContext.updateTimestamp(true);
					
					//TODO: decide if use tempContext
					logger.debug("Replacing contexts from temporary to origin.");
					indexingContext.replace(tmpContext.getIndexDirectory());
					
				} catch (Throwable t) {
					logger.error("Error indexing remote Maven repository 1.", t);
				}
			} catch (IOException ex) {
				logger.error("Error indexing remote Maven repository 2.", ex);
				throw new IOException("Error scanning context " + indexingContext.getId() + ": " + ex, ex);
			} catch (Throwable t) {
				logger.error("Error indexing remote Maven repository 3.", t);
			} finally {
				try {
					if (tmpFile.exists()) {
						tmpFile.delete();
					}
				} finally {
					FileUtils.deleteDirectory(tmpDir); // TODO replace plexus utils														
				}
			}
		}
		
		logger.debug("Indexing remote maven store '{}' finished.", uri);
		return new CloseableIndexingContext(indexingContext, indexer);
	}
}
