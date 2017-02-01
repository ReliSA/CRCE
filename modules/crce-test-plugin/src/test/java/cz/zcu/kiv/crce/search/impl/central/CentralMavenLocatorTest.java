package cz.zcu.kiv.crce.search.impl.central;

import cz.zcu.kiv.crce.search.FoundArtifact;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Zdenek Vales on 1.2.2017.
 */
public class CentralMavenLocatorTest {

    @Test
    public void testLocateArtifact() {
        String groupId = "org.hibernate";
        String artifactId = "hibernate-core";
        String version = "5.2.7.Final";
        String jarDownload = "http://search.maven.org/remotecontent?filepath=org/hibernate/hibernate-core/5.2.7.Final/hibernate-core-5.2.7.Final.jar";

        CentralMavenLocator locator = new CentralMavenLocator();

        FoundArtifact artifact = locator.locate(groupId, artifactId, version);
        assertNotNull("Returned artifact is null!", artifact);
        assertEquals("Wrong group id!", groupId, artifact.getGroupId());
        assertEquals("Wrong artifact id!", artifactId, artifact.getArtifactId());
        assertEquals("Wrong version!", version, artifact.getVersion());
        assertNotNull("Jar download link is null!", artifact.getJarDownloadLink());
        assertEquals("Wrong jar download link!", jarDownload, artifact.getJarDownloadLink().toString());
    }
}
