package cz.zcu.kiv.crce.crce_external_repository.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.apache.maven.index.ArtifactInfo;
import org.apache.maven.index.ArtifactInfoFilter;
import org.apache.maven.index.Field;
import org.apache.maven.index.Indexer;
import org.apache.maven.index.IteratorSearchRequest;
import org.apache.maven.index.IteratorSearchResponse;
import org.apache.maven.index.MAVEN;
import org.apache.maven.index.context.IndexCreator;
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
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.aether.util.version.GenericVersionScheme;
import org.eclipse.aether.version.Version;

public class MavenIndex {
	private String centralCachePath = "repository-index/central-cache";
	private String centralIndexPath = "repository-index/central-index";

	public String checkIndex(SettingsUrl settings) throws Exception {
		final MavenIndex indexCentralRepository = new MavenIndex();
		return indexCentralRepository.perform(settings);
	}

	public List<ArtifactInfo> searchArtefact(SettingsUrl settings, String group, String artifact, 
			String version, String packaging, String range) throws Exception {
		final MavenIndex indexCentralRepository = new MavenIndex();
		return indexCentralRepository.getArtefact(settings, group, artifact, version, packaging, range);
	}

	private final PlexusContainer plexusContainer;
	private final Indexer indexer;
	private final IndexUpdater indexUpdater;
	private final Wagon httpWagon;
	private IndexingContext centralContext;

	public MavenIndex() throws PlexusContainerException, ComponentLookupException {
		/*final DefaultContainerConfiguration config = new DefaultContainerConfiguration();
        config.setClassPathScanning(PlexusConstants.SCANNING_INDEX);
        this.plexusContainer = new DefaultPlexusContainer(config);*/
		this.plexusContainer = new DefaultPlexusContainer();
		this.indexer = plexusContainer.lookup(Indexer.class);
		this.indexUpdater = plexusContainer.lookup(IndexUpdater.class);
		this.httpWagon = plexusContainer.lookup(Wagon.class, "http");
	}

	private String perform(SettingsUrl settings)
			throws IOException, ComponentLookupException, InvalidVersionSpecificationException {
		File centralLocalCache = new File(centralCachePath);
		File centralIndexDir = new File(centralIndexPath);

		List<IndexCreator> indexers = new ArrayList<IndexCreator>();

		indexers.add(plexusContainer.lookup(IndexCreator.class, "min"));
		indexers.add(plexusContainer.lookup(IndexCreator.class, "jarContent"));
		indexers.add(plexusContainer.lookup(IndexCreator.class, "maven-plugin"));

		// Create context for central repository index

		if (settings == null) {
			SettingsUrl settingsUrl = new SettingsUrl();
			centralContext = indexer.createIndexingContext("central-context", "central", centralLocalCache,
					centralIndexDir, settingsUrl.getCentralMavenUrl(), null, true, true, indexers);
		} else {
			centralContext = indexer.createIndexingContext("central-context", "central", centralLocalCache,
					centralIndexDir, settings.getCentralMavenUrl(), null,
					true, true, indexers);
		}

		System.out.println("Updating Maven Index...");

		TransferListener listener = new AbstractTransferListener() {
			public void transferStarted(TransferEvent transferEvent) {
				System.out.print("  Downloading " + transferEvent.getResource().getName());
			}

			public void transferProgress(TransferEvent transferEvent, byte[] buffer, int length) {

			}

			public void transferCompleted(TransferEvent transferEvent) {
				System.out.println(" - Done");
			}
		};

		ResourceFetcher resourceFetcher = new WagonHelper.WagonFetcher(httpWagon, listener, null, null);
		Date centralContextCurrentTimestamp = centralContext.getTimestamp();

		IndexUpdateRequest updateRequest = new IndexUpdateRequest(centralContext, resourceFetcher);
		IndexUpdateResult updateResult = indexUpdater.fetchAndUpdateIndex(updateRequest);
		
		closeIndexer();
		
		if (updateResult.isFullUpdate()) {
			return "Full update happened!";
		} else if (updateResult.getTimestamp().equals(centralContextCurrentTimestamp)) {
			return "No update needed, index is up to date!";
		} else {
			return "Incremental update happened, change covered " + centralContextCurrentTimestamp + " - "
					+ updateResult.getTimestamp() + " period.";
		}
	}

	private List<ArtifactInfo> getArtefact(SettingsUrl settings, String group, String artifact, String version, String packaging, String range)
			throws IOException, ComponentLookupException, InvalidVersionSpecificationException {
		
		File centralLocalCache = new File(centralCachePath);
		File centralIndexDir = new File(centralIndexPath);

		List<IndexCreator> indexers = new ArrayList<IndexCreator>();

		indexers.add(plexusContainer.lookup(IndexCreator.class, "min"));
		indexers.add(plexusContainer.lookup(IndexCreator.class, "jarContent"));
		indexers.add(plexusContainer.lookup(IndexCreator.class, "maven-plugin"));

		// Create context for central repository index

		if (settings == null) {
			SettingsUrl settingsUrl = new SettingsUrl();
			centralContext = indexer.createIndexingContext("central-context", "central", centralLocalCache,
					centralIndexDir, settingsUrl.getCentralMavenUrl(), null, true, true, indexers);
		} else {
			centralContext = indexer.createIndexingContext("central-context", "central", centralLocalCache,
					centralIndexDir, settings.getCentralMavenUrl(), null,
					true, true, indexers);
		}

		final GenericVersionScheme versionScheme = new GenericVersionScheme();
		
		Version versionIndex = versionScheme.parseVersion(version);

		// construct the query for known GA
		final Query groupIdQ = indexer.constructQuery(MAVEN.GROUP_ID, new SourcedSearchExpression(group));

		final Query artifactIdQ = indexer.constructQuery(MAVEN.ARTIFACT_ID,
				new SourcedSearchExpression(artifact));

		final BooleanQuery query = new BooleanQuery();
		query.add(groupIdQ, Occur.MUST);
		query.add(artifactIdQ, Occur.MUST);
		// we want custom packaging artifacts only
		query.add(indexer.constructQuery(MAVEN.PACKAGING, new SourcedSearchExpression(packaging)), Occur.MUST);
		// we want main artifacts only (no classifier)
		// Note: this below is unfinished API, needs fixing
		query.add(indexer.constructQuery(MAVEN.CLASSIFIER, new SourcedSearchExpression(Field.NOT_PRESENT)),
				Occur.MUST_NOT);

		// construct the filter to express "V greater than"
		final ArtifactInfoFilter versionFilter = new ArtifactInfoFilter() {
			public boolean accepts(final IndexingContext ctx, final ArtifactInfo ai) {
				try {
					final Version aiV = versionScheme.parseVersion(ai.version);
					// Use ">=" if you are INCLUSIVE
					if(range.equals("<=")){
						return aiV.compareTo(versionIndex) <= 0;
					}
					else if(range.equals(">=")){
						return aiV.compareTo(versionIndex) >= 0;
					}
					else{
						return aiV.compareTo(versionIndex) == 0;
					}
					
				} catch (InvalidVersionSpecificationException e) {
					return true;
				}
			}
		};

		final IteratorSearchRequest request = new IteratorSearchRequest(query,
				Collections.singletonList(centralContext), versionFilter);
		final IteratorSearchResponse response = indexer.searchIterator(request);
		List<ArtifactInfo> artifactInfoList = new ArrayList<ArtifactInfo>();
		for (ArtifactInfo ai : response) {
			artifactInfoList.add(ai);
		}
		
		closeIndexer();
		
		return artifactInfoList;
	}
	
	public void closeIndexer() throws IOException{
		indexer.closeIndexingContext(centralContext, false);
	}
}
