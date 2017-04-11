package cz.zcu.kiv.crce.mvn.plugin;

import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Zdenek Vales on 2.4.2017.
 */
public class NexusIndexerTest {

    @Test
    @Ignore
    public void testNexusIndexer() throws IOException, ComponentLookupException, PlexusContainerException {

//        String artifactId = "hibernate-core";
//        String groupId = "org.hibernate";
//        String version = "5.2.0.Final";
//        String repoId = "central";
//
//        final DefaultContainerConfiguration config = new DefaultContainerConfiguration();
//        config.setClassPathScanning( PlexusConstants.SCANNING_INDEX );
//        PlexusContainer plexusContainer = new DefaultPlexusContainer( config );
//
//        // lookup the indexer components from plexus
//        Indexer nexusIndexer = plexusContainer.lookup( Indexer.class );
//        IndexUpdater indexUpdater = plexusContainer.lookup( IndexUpdater.class );
//        // lookup wagon used to remotely fetch index
//        Wagon httpWagon = plexusContainer.lookup( Wagon.class, "http" );
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
//                nexusIndexer.createIndexingContext( "central-context", "central", centralLocalCache, centralIndexDir,
//                        "http://central.maven.org/maven2/", null, true, true, indexers );
//
//        updateIndex(centralContext, httpWagon, indexUpdater);
//
//        System.out.println("Searching for artifact with gav coordinates.");
//
//        BooleanQuery bq = new BooleanQuery();
//        bq.add(nexusIndexer.constructQuery(MAVEN.ARTIFACT_ID, new SourcedSearchExpression(artifactId )), BooleanClause.Occur.MUST);
//        bq.add(nexusIndexer.constructQuery(MAVEN.GROUP_ID, new SourcedSearchExpression(groupId )), BooleanClause.Occur.MUST);
//        bq.add(nexusIndexer.constructQuery(MAVEN.VERSION, new SourcedSearchExpression(version )), BooleanClause.Occur.MUST);
//        bq.add(nexusIndexer.constructQuery(MAVEN.PACKAGING, new SourcedSearchExpression("jar" )), BooleanClause.Occur.MUST);
//
//        FlatSearchRequest request = new FlatSearchRequest(bq, centralContext);
//        FlatSearchResponse response = nexusIndexer.searchFlat(request);
//
//        Set<ArtifactInfo> artifactInfos = response.getResults();
//        System.out.println("Checking...");
//        assertFalse("No artifacts found!", artifactInfos.isEmpty());
//
//        ArtifactInfo ai = artifactInfos.iterator().next();
//        FoundArtifact result = new SimpleFoundArtifact(ai.groupId, ai.artifactId, ai.version, "", "");
//
//        assertEquals("Wrong artifactId!", artifactId, result.getArtifactId());
//        assertEquals("Wrong groupId!", groupId, result.getGroupId());
//        assertEquals("Wrong version!", version, result.getVersion());
//        System.out.println("Done");
//
//
//        System.out.println("Searching for artifact by classname.");
//        String packageName = "SQLiteDialect";
//
//        bq = new BooleanQuery();
//        bq.add(nexusIndexer.constructQuery(MAVEN.CLASSNAMES, new SourcedSearchExpression(packageName)), BooleanClause.Occur.SHOULD);
//
//        request = new FlatSearchRequest(bq, centralContext);
//        response = nexusIndexer.searchFlat(request);
//
//        artifactInfos = response.getResults();
//        System.out.println("Checking...");
//        assertFalse("No artifacts found!", artifactInfos.isEmpty());
//
//        for(ArtifactInfo artifactInfo : artifactInfos) {
//            assertTrue("Wrong artifact!", artifactInfo.classNames.contains(packageName));
//        }
//        System.out.println("Done");
    }

//    private void updateIndex(IndexingContext context, Wagon wagon, IndexUpdater indexUpdater) throws IOException {
//        System.out.println( "Updating Index..." );
//        System.out.println( "This might take a while on first run, so please be patient!" );
//        // Create ResourceFetcher implementation to be used with IndexUpdateRequest
//        // Here, we use Wagon based one as shorthand, but all we need is a ResourceFetcher implementation
//        TransferListener listener = new AbstractTransferListener()
//        {
//            public void transferStarted( TransferEvent transferEvent )
//            {
//                System.out.print( "  Downloading " + transferEvent.getResource().getName() );
//            }
//
//            public void transferProgress( TransferEvent transferEvent, byte[] buffer, int length )
//            {
//            }
//
//            public void transferCompleted( TransferEvent transferEvent )
//            {
//                System.out.println( " - Done" );
//            }
//        };
//
//        ResourceFetcher resourceFetcher = new WagonHelper.WagonFetcher( wagon, listener, null, null );
//
//        Date centralContextCurrentTimestamp = context.getTimestamp();
//        IndexUpdateRequest updateRequest = new IndexUpdateRequest(context, resourceFetcher);
//        IndexUpdateResult updateResult = indexUpdater.fetchAndUpdateIndex( updateRequest );
//        if ( updateResult.isFullUpdate() )
//        {
//            System.out.println( "Full update happened!" );
//        }
//        else if ( updateResult.getTimestamp().equals( centralContextCurrentTimestamp ) )
//        {
//            System.out.println( "No update needed, index is up to date!" );
//        }
//        else
//        {
//            System.out.println(
//                    "Incremental update happened, change covered " + centralContextCurrentTimestamp + " - "
//                            + updateResult.getTimestamp() + " period." );
//        }
//
//        System.out.println();
//    }
}
