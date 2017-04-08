package cz.zcu.kiv.crce.mvn.plugin;

import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.SimpleFoundArtifact;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.maven.index.*;
import org.apache.maven.index.context.IndexCreator;
import org.apache.maven.index.context.IndexingContext;
import org.apache.maven.index.expr.SourcedSearchExpression;
import org.apache.maven.index.updater.*;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.observers.AbstractTransferListener;
import org.codehaus.plexus.*;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Zdenek Vales on 2.4.2017.
 */
public class NexusIndexerTest {

    @Test
//    @Ignore
    public void testNexusIndexer() throws IOException, ComponentLookupException, PlexusContainerException {

        String artifactId = "hibernate-core";
        String groupId = "org.hibernate";
        String version = "5.2.0.Final";
        String repoId = "central";

        final DefaultContainerConfiguration config = new DefaultContainerConfiguration();
        config.setClassPathScanning( PlexusConstants.SCANNING_INDEX );
        PlexusContainer plexusContainer = new DefaultPlexusContainer( config );

        // lookup the indexer components from plexus
        Indexer nexusIndexer = plexusContainer.lookup( Indexer.class );
        IndexUpdater indexUpdater = plexusContainer.lookup( IndexUpdater.class );
        // lookup wagon used to remotely fetch index
        Wagon httpWagon = plexusContainer.lookup( Wagon.class, "http" );

        File centralLocalCache = new File( "target/central-cache" );
        File centralIndexDir = new File( "target/central-index" );

        // Creators we want to use (search for fields it defines)
        List<IndexCreator> indexers = new ArrayList<IndexCreator>();
        indexers.add( plexusContainer.lookup( IndexCreator.class, "min" ) );
        indexers.add( plexusContainer.lookup( IndexCreator.class, "jarContent" ) );
        indexers.add( plexusContainer.lookup( IndexCreator.class, "maven-plugin" ) );

        // Create context for central repository index
        IndexingContext centralContext =
                nexusIndexer.createIndexingContext( "central-context", "central", centralLocalCache, centralIndexDir,
                        "http://central.maven.org/maven2/", null, true, true, indexers );

        updateIndex(centralContext, httpWagon, indexUpdater);

        System.out.println("Searching for artifact with gav coordinates.");

        BooleanQuery bq = new BooleanQuery();
        bq.add(nexusIndexer.constructQuery(MAVEN.ARTIFACT_ID, new SourcedSearchExpression(artifactId )), BooleanClause.Occur.MUST);
        bq.add(nexusIndexer.constructQuery(MAVEN.GROUP_ID, new SourcedSearchExpression(groupId )), BooleanClause.Occur.MUST);
        bq.add(nexusIndexer.constructQuery(MAVEN.VERSION, new SourcedSearchExpression(version )), BooleanClause.Occur.MUST);
        bq.add(nexusIndexer.constructQuery(MAVEN.PACKAGING, new SourcedSearchExpression("jar" )), BooleanClause.Occur.MUST);

        FlatSearchRequest request = new FlatSearchRequest(bq, centralContext);
        FlatSearchResponse response = nexusIndexer.searchFlat(request);

        Set<ArtifactInfo> artifactInfos = response.getResults();
        System.out.println("Checking...");
        assertFalse("No artifacts found!", artifactInfos.isEmpty());

        ArtifactInfo ai = artifactInfos.iterator().next();
        FoundArtifact result = new SimpleFoundArtifact(ai.groupId, ai.artifactId, ai.version, "", "");

        assertEquals("Wrong artifactId!", artifactId, result.getArtifactId());
        assertEquals("Wrong groupId!", groupId, result.getGroupId());
        assertEquals("Wrong version!", version, result.getVersion());
        System.out.println("Done");


        System.out.println("Searching for artifact by classname.");
        String packageName = "SQLiteDialect";

        bq = new BooleanQuery();
        bq.add(nexusIndexer.constructQuery(MAVEN.CLASSNAMES, new SourcedSearchExpression(packageName)), BooleanClause.Occur.SHOULD);

        request = new FlatSearchRequest(bq, centralContext);
        response = nexusIndexer.searchFlat(request);

        artifactInfos = response.getResults();
        System.out.println("Checking...");
        assertFalse("No artifacts found!", artifactInfos.isEmpty());

        for(ArtifactInfo artifactInfo : artifactInfos) {
            assertTrue("Wrong artifact!", artifactInfo.classNames.contains(packageName));
        }
        System.out.println("Done");
    }

    private void updateIndex(IndexingContext context, Wagon wagon, IndexUpdater indexUpdater) throws IOException {
        System.out.println( "Updating Index..." );
        System.out.println( "This might take a while on first run, so please be patient!" );
        // Create ResourceFetcher implementation to be used with IndexUpdateRequest
        // Here, we use Wagon based one as shorthand, but all we need is a ResourceFetcher implementation
        TransferListener listener = new AbstractTransferListener()
        {
            public void transferStarted( TransferEvent transferEvent )
            {
                System.out.print( "  Downloading " + transferEvent.getResource().getName() );
            }

            public void transferProgress( TransferEvent transferEvent, byte[] buffer, int length )
            {
            }

            public void transferCompleted( TransferEvent transferEvent )
            {
                System.out.println( " - Done" );
            }
        };

        ResourceFetcher resourceFetcher = new WagonHelper.WagonFetcher( wagon, listener, null, null );

        Date centralContextCurrentTimestamp = context.getTimestamp();
        IndexUpdateRequest updateRequest = new IndexUpdateRequest(context, resourceFetcher);
        IndexUpdateResult updateResult = indexUpdater.fetchAndUpdateIndex( updateRequest );
        if ( updateResult.isFullUpdate() )
        {
            System.out.println( "Full update happened!" );
        }
        else if ( updateResult.getTimestamp().equals( centralContextCurrentTimestamp ) )
        {
            System.out.println( "No update needed, index is up to date!" );
        }
        else
        {
            System.out.println(
                    "Incremental update happened, change covered " + centralContextCurrentTimestamp + " - "
                            + updateResult.getTimestamp() + " period." );
        }

        System.out.println();
    }
}
