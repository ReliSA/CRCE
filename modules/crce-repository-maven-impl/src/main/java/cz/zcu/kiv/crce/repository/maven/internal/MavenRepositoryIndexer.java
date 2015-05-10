package cz.zcu.kiv.crce.repository.maven.internal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;
import org.apache.maven.index.ArtifactInfo;
import org.apache.maven.index.ArtifactInfoFilter;
import org.apache.maven.index.ArtifactInfoGroup;
import org.apache.maven.index.DefaultScannerListener;
import org.apache.maven.index.FlatSearchRequest;
import org.apache.maven.index.FlatSearchResponse;
import org.apache.maven.index.GroupedSearchRequest;
import org.apache.maven.index.GroupedSearchResponse;
import org.apache.maven.index.Indexer;
import org.apache.maven.index.IndexerEngine;
import org.apache.maven.index.IteratorSearchRequest;
import org.apache.maven.index.IteratorSearchResponse;
import org.apache.maven.index.MAVEN;
import org.apache.maven.index.Scanner;
import org.apache.maven.index.ScanningRequest;
import org.apache.maven.index.context.DefaultIndexingContext;
import org.apache.maven.index.context.IndexCreator;
import org.apache.maven.index.context.IndexUtils;
import org.apache.maven.index.context.IndexingContext;
import org.apache.maven.index.expr.SourcedSearchExpression;
import org.apache.maven.index.search.grouping.GAGrouping;
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
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;
import org.eclipse.aether.util.version.GenericVersionScheme;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.concurrency.model.Task;
import cz.zcu.kiv.crce.repository.maven.internal.aether.RepositoryFactory;
import cz.zcu.kiv.crce.repository.maven.internal.metadata.MavenArtifactVersion;
import cz.zcu.kiv.crce.repository.maven.internal.metadata.MavenArtifactWrapper;
import cz.zcu.kiv.crce.repository.maven.internal.metadata.MetadataIndexerCallback;

/**
 *
 * @author Miroslav Brožek
 */
public class MavenRepositoryIndexer extends Task<Object> {

    private static final Logger logger = LoggerFactory.getLogger(MavenRepositoryIndexer.class);

    private final URI uri;
    private final MetadataIndexerCallback metadataIndexerCallback;
    private CloseableIndexingContext indexingContext;
    
    private static final String INDEXING_CONTEXT = MavenStoreConfig.getIndexingContextPath();
      
    public MavenRepositoryIndexer(URI uri, MetadataIndexerCallback metadataIndexerCallback) {
        super(uri.toString(), "Indexes local maven repository.", "crce-repository-maven-impl");
        this.uri = uri;
        this.metadataIndexerCallback = metadataIndexerCallback;
    }

	@Override
	protected Object run() throws Exception {
		logger.error("START");
		logger.error("Indexing Maven repository started: {}", uri);
		
		try {
			
			if (!MavenStoreConfig.isRemoteRepoDefault()) {
				RepositoryWrapper lr = MavenStoreConfig.getLocalRepository();
				indexingContext = createLocalRepoIndexingContext(lr.getName(), new File(uri), new File(INDEXING_CONTEXT), lr.isUpdate());
			}

			else {
				RepositoryWrapper rr = MavenStoreConfig.getRemoteRepository();
				indexingContext = createRemoteRepositoryIndexingContext(rr.getName(), uri, new File(INDEXING_CONTEXT), rr.isUpdate());
			}
			logger.error("Indexing Maven repository ended: {}", uri);

			Indexer indexer = indexingContext.getIndexer();
			Set<ArtifactInfo> results = new LinkedHashSet<ArtifactInfo>();
			String arParam = "";

			ArtifactResolve ar = MavenStoreConfig.getArtifactResolve();
			switch (ar) {
			case ALL:
				logger.debug("All Artifact's versions will be processed");				
				results = indexAll(indexer);
				break;

			case NEWEST:
				logger.debug("Only the latest Artifact's versions will be processed");
				//results = filterNewest(results);
				results = latestVersionMI(indexer);
				break;

			case HIGHEST_MAJOR:			
				
			case HIGHEST_MINOR:
				
			case HIGHEST_MICRO:
							
			case LOWEST_MINOR:
							
			case LOWEST_MICRO:
				logger.debug("Only the {} Artifact's versions will be processed",ar);
				results = getHiLowFiltredResults(indexer, indexAll(indexer), ar);
				break;
								
			case GAV:
				arParam = MavenStoreConfig.getArtifactResolveParam();
				logger.debug("Trying process Artifact with GAV: {} ",arParam);
				
				String[] gav = arParam.split(":");

				if (gav.length >= 3) {
					ArtifactInfo ai = new ArtifactInfo("", gav[0].trim(), gav[1].trim(), gav[2].trim(), "");
					results.add(ai);
				} else {
					logger.error("Wrong parameter! String must be in format > groupID:artifactID:versionID! , eg.: 'org.sonatype.nexus:nexus-api:1.5.0'");
				}
				break;
				
			case GROUP_ID:
				arParam = MavenStoreConfig.getArtifactResolveParam();
				logger.debug("Trying process Artifacts with groupID> {} ",arParam);
				results = getArtifactsGAV(indexer, ar);
				break;
			
			case GROUPID_ARTIFACTID:
				arParam = MavenStoreConfig.getArtifactResolveParam();
				logger.debug("Trying process Artifacts > {} ",arParam);
				results = getArtifactsGAV(indexer, ar);
				break;
				
			case GROUPID_ARTIFACTID_FROM_VERSION:
				arParam = MavenStoreConfig.getArtifactResolveParam();
				logger.debug("Trying process Artifacts from specified Version > {} ",arParam);
				results = getArtifactsGAV(indexer, ar);
				break;
				
			default:
				// index all??
				// index none?
				break;
			}

//			logger.debug("Indexing artifacts (amount: {}).", results.size());
			//for debug
			System.out.println("size>"+results.size());		
		
			if(results.size()>0){
				indexResults(results);
			}
			else{
				logger.warn("NO RESULTS! Check configuration file!");
			}
			

		} catch (Exception e) {
			logger.error("Error updating Maven repository index. STOPPING INDEXING artifact's metadata !!", e);
			return null;
		}

		logger.error("END");
		logger.info("Indexing Maven repository metadata finished: {}", uri);
		indexingContext.close();
		return null;
	}

	private void indexResults(Set<ArtifactInfo> results) {
		int counter = 1;
		RepositorySystem system = RepositoryFactory.newRepositorySystem(); // Aether
		DefaultRepositorySystemSession session = RepositoryFactory.newRepositorySystemSession(system);
		
		for (ArtifactInfo ai : results) {
			logger.debug("Processing {}. Artifact: {}",counter, ai.toString());
			

			Artifact a = new DefaultArtifact(ai.groupId + ":" + ai.artifactId + ":" + ai.version);
			ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
			descriptorRequest.setRepositories(RepositoryFactory.newRepositories());
			descriptorRequest.setArtifact(a);

			try {

				// Direct Dependency
				if (!MavenStoreConfig.isDependencyHierarchy()) {

					ArtifactDescriptorResult descriptorResult = system.readArtifactDescriptor(session, descriptorRequest);
					List<Dependency> directD = descriptorResult.getDependencies();

					// Indexing and resolve JAR
					if (MavenStoreConfig.isResolveArtifacts()) {
						ArtifactRequest artifactRequest = new ArtifactRequest();
						artifactRequest.setArtifact(a);
						artifactRequest.setRepositories(descriptorRequest.getRepositories());

						ArtifactResult artifactResult = system.resolveArtifact(session, artifactRequest);
						a = artifactResult.getArtifact();
					}

					// Indexing by POM
					else {
						a = descriptorResult.getArtifact();
						a = setPOMfileToArtifact(a, system, session);
					}

					MavenArtifactWrapper maw = new MavenArtifactWrapper(a, directD, null);
					metadataIndexerCallback.index(maw);
				}

				// Create Hierarchy Dependcies
				else {

					session.setConfigProperty(ConflictResolver.CONFIG_PROP_VERBOSE, true);
					session.setConfigProperty(DependencyManagerUtils.CONFIG_PROP_VERBOSE, true);

					ArtifactDescriptorResult descriptorResult = system.readArtifactDescriptor(session, descriptorRequest);

					CollectRequest collectRequest = new CollectRequest();
					collectRequest.setRootArtifact(descriptorResult.getArtifact());
					collectRequest.setDependencies(descriptorResult.getDependencies());
					collectRequest.setManagedDependencies(descriptorResult.getManagedDependencies());
					collectRequest.setRepositories(descriptorRequest.getRepositories());

					CollectResult collectResult = system.collectDependencies(session, collectRequest);
					List<DependencyNode> hierarchyD = collectResult.getRoot().getChildren();

					// Indexing by POM
					if (!MavenStoreConfig.isResolveArtifacts()) {
						a = collectResult.getRoot().getArtifact();
						a = setPOMfileToArtifact(a, system, session);
					}

					// Indexing and resolve JARs
					else {
						DependencyFilter classpathFlter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE);
						collectRequest.setRoot(new Dependency(a, JavaScopes.COMPILE));
						DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, classpathFlter);
						DependencyResult dr = system.resolveDependencies(session, dependencyRequest);
						a = dr.getRoot().getArtifact();
					}

					MavenArtifactWrapper maw = new MavenArtifactWrapper(a, null, hierarchyD);
					metadataIndexerCallback.index(maw);
				}

			} catch (DependencyCollectionException e) {
				logger.error("Couldn't collect dependendencies...", e);

			} catch (ArtifactResolutionException e) {
				logger.error("Couldn't resolve artifact...", e);

			} catch (ArtifactDescriptorException e) {
				logger.error("Failed to read ArtifactDescriptor...", e);

			} catch (DependencyResolutionException e) {
				logger.error("Couldn't resolve dependendencies...", e);
			}
			
			counter++;
		}
	}

	private Artifact setPOMfileToArtifact(Artifact a, RepositorySystem system, RepositorySystemSession session) throws ArtifactResolutionException {
		File pom = new File(getPathForArtifact(a, true, true));
		
		if (pom.getAbsoluteFile().exists()) {
			a = a.setFile(pom);			
		}
		
		else{
			String g = a.getGroupId().split("\\.")[0];
			String pomS = a.getArtifactId() + "-" + a.getVersion()+".pom";	
			File root = new File(MavenStoreConfig.getLocalRepository().getURItoPath() + "/" + g) ;
			String  newPath = findPOM(pomS, root);
			
			if(newPath== null){
				logger.debug("Can't find POM file...trying resolve whole JAR file... " + a);
				
				ArtifactRequest artifactRequest = new ArtifactRequest();
				artifactRequest.setRepositories(RepositoryFactory.newRepositories());
				artifactRequest.setArtifact(a);
				a = system.resolveArtifact(session, artifactRequest).getArtifact();				
			}
			else{
				logger.debug("POM file found in repository on different place"); 
				a=a.setFile(new File(newPath));
			}
		}		

		return a;
	}

	//fallback
	private String findPOM(String name, File dir) {
		String found = null;		

		File[] list = dir.listFiles();
		if (list != null)
			for (File fil : list) {
				if (fil.isDirectory()) {
					found = findPOM(name, fil);
					if(found!=null){
						break;
					}
				} else if (name.equalsIgnoreCase(fil.getName())) {
					return fil.getAbsolutePath();
				}
			}
		return found;
	}
	
	private String getPathForArtifact(Artifact artifact, boolean local, boolean searchPOM) {
	    StringBuilder path = new StringBuilder(128);
	    path.append(MavenStoreConfig.getLocalRepository().getURItoPath()+"/");
	    path.append(artifact.getGroupId().replace('.', '/')).append('/');
	    path.append(artifact.getArtifactId()).append('/');
	    path.append(artifact.getBaseVersion()).append('/');
	    path.append(artifact.getArtifactId()).append('-');
	    if (local) {
	      path.append(artifact.getBaseVersion());
	    } else {
	      path.append(artifact.getVersion());
	    }
	    if (artifact.getClassifier().length() > 0) {
	      path.append('-').append(artifact.getClassifier());
	    }
	    
	    if(searchPOM){
	    	path.append('.').append("pom");
	    }
	    else if (artifact.getExtension().length() > 0) {
	      path.append('.').append(artifact.getExtension());
	    }
	    return path.toString();
	  }
	

	/**
	 * Main method to get all 'bundles' from indexingContext
	 * 
	 * @param indexer  Indexer from context
	 * @return FlatSearchResponse result due query setting
	 * @throws IOException
	 */
	private Set<ArtifactInfo> indexAll(Indexer indexer) throws IOException {
		FlatSearchResponse response;
		BooleanQuery query = new BooleanQuery();
		query.add(indexer.constructQuery(MAVEN.PACKAGING, new SourcedSearchExpression("bundle")), BooleanClause.Occur.MUST);
		response = indexer.searchFlat(new FlatSearchRequest(query, indexingContext));
		return response.getResults();
	}

	/**
	 * Filter to search latest version of bundle recieved by MavenIndexer 
	 * 
	 * @param indexer maven indexer
	 * @return Set of artifacts
	 * @throws IOException
	 */
	private Set<ArtifactInfo> latestVersionMI(Indexer indexer) throws IOException {		
		BooleanQuery query = new BooleanQuery();
		query.add(indexer.constructQuery(MAVEN.PACKAGING, new SourcedSearchExpression("bundle")), BooleanClause.Occur.MUST);
		GroupedSearchResponse response = indexer.searchGrouped(new GroupedSearchRequest(query, new GAGrouping(), indexingContext));
		Set<ArtifactInfo> res = new LinkedHashSet<ArtifactInfo>();
		
		//debug
		for (Map.Entry<String, ArtifactInfoGroup> entry : response.getResults().entrySet()) {
			ArtifactInfo ai = entry.getValue().getArtifactInfos().iterator().next();
			res.add(ai);

			logger.debug("{} artifact atest version:  {}",ai, ai.version);
		}		 
		return res;
	}

	/**
	 * Filter to search only highest versions Artifacts
	 * @param indexer i maven indexer
	 * @param ai is artifact info
	 * @throws IOException
	 * @throws InvalidVersionSpecificationException 
	 */
	private Set<ArtifactInfo> getHiLowFiltredResults(Indexer indexer, Set<ArtifactInfo> results, ArtifactResolve ars) throws IOException,
			InvalidVersionSpecificationException {
		
		HashSet<String>done = new HashSet<String>();//list of proccesed artifacts...much faster
		LinkedHashSet<String> filtredArtifactsInString = new LinkedHashSet<String>();		
		
		for (ArtifactInfo ai : results) {
			String ga = new String(ai.groupId+":"+ai.artifactId);
			if(done.contains(ga)){
				continue;
			}			
			getMatch(indexer, ai, filtredArtifactsInString, ars);
			done.add(ga);
		}

		LinkedHashSet<ArtifactInfo> filtred = new LinkedHashSet<ArtifactInfo>();
		for (String s : filtredArtifactsInString) {
			String[] coords = s.split(":");
			filtred.add(new ArtifactInfo("", coords[0], coords[1], coords[2], ""));
		}

		return filtred;
	}
	
	/**
	 * Filter to create results by Enum
	 * @param indexer
	 * @param ai
	 * @param filtred
	 * @throws InvalidVersionSpecificationException
	 * @throws IOException
	 */
	private void getMatch(Indexer indexer, ArtifactInfo ai, LinkedHashSet<String> filtred, ArtifactResolve ar) throws InvalidVersionSpecificationException, IOException {
		int highest = 0;
		int lowest = Integer.MAX_VALUE;

		Query gidQ = indexer.constructQuery(MAVEN.GROUP_ID, new SourcedSearchExpression(ai.groupId));
		Query aidQ = indexer.constructQuery(MAVEN.ARTIFACT_ID, new SourcedSearchExpression(ai.artifactId));
		Query pckQ = indexer.constructQuery(MAVEN.PACKAGING, new SourcedSearchExpression("bundle"));

		BooleanQuery bq = new BooleanQuery();
		bq.add(gidQ, Occur.MUST);
		bq.add(aidQ, Occur.MUST);
		bq.add(pckQ, Occur.MUST);

		final GenericVersionScheme versionScheme = new GenericVersionScheme();
		final String versionString = "0.0.0";
		final Version version = versionScheme.parseVersion(versionString);

		// construct the filter to express "V greater than"
		final ArtifactInfoFilter versionFilter = new ArtifactInfoFilter() {
			public boolean accepts(final IndexingContext ctx, final ArtifactInfo ai) {
				try {
					final Version aiV = versionScheme.parseVersion(ai.version);
					// Use ">=" if you are INCLUSIVE
					return aiV.compareTo(version) > 0;
				} catch (InvalidVersionSpecificationException e) {
					// do something here? be safe and include?
					return true;
				}
			}
		};

		logger.debug("Searching all versions for artifact {}-{}", ai.groupId, ai.artifactId);
		final IteratorSearchRequest request = new IteratorSearchRequest(bq, Collections.singletonList((IndexingContext) indexingContext),
				versionFilter);

		IteratorSearchResponse response = indexer.searchIterator(request);

		switch (ar) {
		case HIGHEST_MAJOR:
			for (ArtifactInfo a : response) {
				int major = new MavenArtifactVersion(a.version).getMajorVersion();
				highest = major > highest ? major : highest;
			}
			response.close();

			response = indexer.searchIterator(request);

			for (ArtifactInfo a : response) {
				int major = new MavenArtifactVersion(a.version).getMajorVersion();
				if (major >= highest) {
					filtred.add(a.toString());
				}
			}
			break;

		case HIGHEST_MINOR:
			for (ArtifactInfo a : response) {
				int minor = new MavenArtifactVersion(a.version).getMinorVersion();
				highest = minor > highest ? minor : highest;
			}
			response.close();

			response = indexer.searchIterator(request);

			for (ArtifactInfo a : response) {
				int minor = new MavenArtifactVersion(a.version).getMinorVersion();
				if (minor >= highest) {
					filtred.add(a.toString());
				}
			}
			break;

		case HIGHEST_MICRO:
			for (ArtifactInfo a : response) {
				int micro = new MavenArtifactVersion(a.version).getMicroVersion();
				highest = micro > highest ? micro : highest;
			}
			response.close();

			response = indexer.searchIterator(request);

			for (ArtifactInfo a : response) {
				int micro = new MavenArtifactVersion(a.version).getMicroVersion();
				if (micro >= highest) {
					filtred.add(a.toString());
				}
			}
			break;

		// lowest
		case LOWEST_MINOR:
			for (ArtifactInfo a : response) {
				int minor = new MavenArtifactVersion(a.version).getMinorVersion();	
				if (minor > -1 && minor < lowest) {
					lowest = minor;
				}
			}
			response.close();
			response = indexer.searchIterator(request);

			for (ArtifactInfo a : response) {
				int minor = new MavenArtifactVersion(a.version).getMinorVersion();
				
				if (minor == lowest) {
					filtred.add(a.toString());
				}
			}
			break;

		case LOWEST_MICRO:
			for (ArtifactInfo a : response) {
				int micro = new MavenArtifactVersion(a.version).getMicroVersion();

				if (micro > -1 && micro < lowest) {
					lowest = micro;
				}
			}
			response.close();

			response = indexer.searchIterator(request);

			for (ArtifactInfo a : response) {
				int micro = new MavenArtifactVersion(a.version).getMicroVersion();

				// add '<' to condition if micro is null ?
				if (micro == lowest) {
					filtred.add(a.toString());
				}
			}
			break;

		default:
			break;
		}
	}
	

	private Set<ArtifactInfo> getArtifactsGAV(Indexer indexer, ArtifactResolve ar) throws IOException, ArtifactResolutionException, InvalidVersionSpecificationException, VersionRangeResolutionException {
		
		Set<ArtifactInfo> results = new LinkedHashSet<ArtifactInfo>();	
		String arParam = MavenStoreConfig.getArtifactResolveParam();
		String[] gav = arParam.split(":");
		
	
		BooleanQuery bq = new BooleanQuery();
				
		switch (ar) {		
		case GROUP_ID:			
			Query gidQ = indexer.constructQuery(MAVEN.GROUP_ID, new SourcedSearchExpression(gav[0]));
			Query pckQ = indexer.constructQuery(MAVEN.PACKAGING, new SourcedSearchExpression("bundle"));
						
			bq.add(gidQ, Occur.MUST);
			bq.add(pckQ, Occur.MUST);
			
			FlatSearchResponse response = indexer.searchFlat(new FlatSearchRequest(bq, indexingContext));
			results = response.getResults();
			break;
			
		case GROUPID_ARTIFACTID:
			if(gav.length >= 2){
				results = getArtifactFromVersion(indexer, gav, ar);				
			}
			
			break;
			
		case GROUPID_ARTIFACTID_FROM_VERSION:
			if(gav.length >= 3){
				results = getArtifactFromVersion(indexer, gav, ar);				
			}
			
			break;
	
		default:
			break;
		}
		
		return results;
	}

	private Set<ArtifactInfo> getArtifactFromVersion(Indexer indexer, String[] gav, ArtifactResolve ar) throws InvalidVersionSpecificationException,
			IOException, VersionRangeResolutionException {
		
		Query gidQ;
		Query aidQ;
		Query pckQ;
		gidQ = indexer.constructQuery(MAVEN.GROUP_ID, new SourcedSearchExpression(gav[0]));
		aidQ = indexer.constructQuery(MAVEN.ARTIFACT_ID, new SourcedSearchExpression(gav[1]));				
		pckQ = indexer.constructQuery(MAVEN.PACKAGING, new SourcedSearchExpression("bundle"));
		
		BooleanQuery bq = new BooleanQuery();
		bq.add(gidQ, Occur.MUST);
		bq.add(aidQ, Occur.MUST);			
		bq.add(pckQ, Occur.MUST);
		
		
		final String versionString;
		
		//find artifacts version at 'gav[2] inclusive and higher
		if (ar.equals(ArtifactResolve.GROUPID_ARTIFACTID_FROM_VERSION)) {
			versionString = gav[2];
		
		} else {
			versionString = "0.0.0";			
		}
		
		
		final GenericVersionScheme versionScheme = new GenericVersionScheme();
		final Version version = versionScheme.parseVersion(versionString);

		// construct the filter to express "V greater than"
		final ArtifactInfoFilter versionFilter = new ArtifactInfoFilter() {
			public boolean accepts(final IndexingContext ctx, final ArtifactInfo ai) {
				try {
					final Version aiV = versionScheme.parseVersion(ai.version);
					// Use ">=" if you are INCLUSIVE
					return aiV.compareTo(version) >= 0;
				} catch (InvalidVersionSpecificationException e) {
					// do something here? be safe and include?
					return true;
				}
			}
		};

		logger.debug("Searching all versions for artifact {}-{}",gav[0],gav[1]);
		final IteratorSearchRequest request = new IteratorSearchRequest(bq,
				Collections.singletonList((IndexingContext) indexingContext), versionFilter);
		
		IteratorSearchResponse isr = indexer.searchIterator(request);
		
		LinkedHashSet<ArtifactInfo> results = new LinkedHashSet<ArtifactInfo>();
		for (ArtifactInfo ai : isr) {			
			results.add(ai);
		}			
		
		isr.close();
		
		//fallback, if query doesnt match with any Artifacts in indexing context > using Aether -time consuming
		if (results.size()<1){
			RepositorySystem system = RepositoryFactory.newRepositorySystem(); // Aether
			RepositorySystemSession session = RepositoryFactory.newRepositorySystemSession(system);
			Artifact a = new DefaultArtifact( gav[0]+":"+gav[1]+":["+versionString+",)" );
			
			VersionRangeRequest rangeRequest = new VersionRangeRequest();
		    rangeRequest.setArtifact( a );
		    rangeRequest.setRepositories( RepositoryFactory.newRepositories());
		    VersionRangeResult rangeResult = system.resolveVersionRange( session, rangeRequest );
		    List<Version> versions = rangeResult.getVersions();
		    
		    logger.debug("Aether found Artifacts range > {}",versions);
		    for (Version v : versions) {
				results.add(new ArtifactInfo("", a.getGroupId(), a.getArtifactId(), v.toString(), ""));
			}		    
		}
		
		return results;
	}
	

	
//	//Aether artifacts latest version filter NEEDS rework
//	private Set<ArtifactInfo> filterLatestByAether(Set<ArtifactInfo> all) throws VersionRangeResolutionException {
//		  RepositorySystem system = RepositoryFactory.newRepositorySystem(); // Aether
//			RepositorySystemSession session = RepositoryFactory.newRepositorySystemSession(system);
//			ArtifactRequest artifactRequest = new ArtifactRequest();
//			artifactRequest.setRepositories(RepositoryFactory.newRepositories());
//		
//		for (Iterator<ArtifactInfo> i = all.iterator(); i.hasNext();) {
//		    ArtifactInfo ai = i.next();			
//			Artifact artifact = new DefaultArtifact(ai.groupId + ":" + ai.artifactId + ":[0,)");
//
//			VersionRangeRequest rangeRequest = new VersionRangeRequest();
//			rangeRequest.setArtifact(artifact);
//			rangeRequest.setRepositories(RepositoryFactory.newRepositories());
//			VersionRangeResult rangeResult = system.resolveVersionRange(session, rangeRequest);
//			org.eclipse.aether.version.Version newestVersion = rangeResult.getHighestVersion();
//			List<org.eclipse.aether.version.Version> versions = rangeResult.getVersions();
////			logger.debug("Available versions " + versions);
//			if(newestVersion==null){
//				logger.debug("Removing artifact, which is not available anymore...");
//				i.remove();
//				continue;
//			}
//			Version latest = new MavenArtifactVersion(newestVersion.toString()).convertVersion();
//			Version cand = new MavenArtifactVersion(ai.version).convertVersion();
//
//			if (cand.compareTo(latest) < 0) {
//				i.remove();
//			}
//		}
//		
//		return all;
//	}

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
//        indexers.add( plexusContainer.lookup( IndexCreator.class, "maven-archetype" ) );
//        indexers.add( plexusContainer.lookup( IndexCreator.class, "osgi-metadatas" ) );       

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
