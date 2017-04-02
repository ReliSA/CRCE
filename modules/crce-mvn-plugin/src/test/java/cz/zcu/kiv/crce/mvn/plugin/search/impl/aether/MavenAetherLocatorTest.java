package cz.zcu.kiv.crce.mvn.plugin.search.impl.aether;


import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenLocator;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class MavenAetherLocatorTest {

    @Test
    public void testLocateArtifact() {
        String groupId = "org.hibernate";
        String artifactId = "hibernate-core";
        String version = "5.2.0.Final";

        MavenLocator locator = new MavenAetherLocator();

        FoundArtifact artifact = locator.locate(groupId, artifactId, version);
        assertNotNull("Returned artifact is null!", artifact);
        assertEquals("Wrong group id!", groupId, artifact.getGroupId());
        assertEquals("Wrong artifact id!", artifactId, artifact.getArtifactId());
        assertEquals("Wrong version!", version, artifact.getVersion());
    }

    @Test
    public void testLocateArtifactBad() {
        String groupId = "org.hibernate";
        String artifactId = "asdasdasdasd";
        String version = "5.2.0.Final";

        MavenLocator locator = new MavenAetherLocator();

        FoundArtifact artifact = locator.locate(groupId, artifactId, version);
        assertNull("Non existent artifact is not null!", artifact);
    }

    @Test
    public void testLocateArtifacts() {
        String groupId = "org.hibernate";
        String artifactId = "hibernate-core";

        MavenLocator locator = new MavenAetherLocator();

        Collection<FoundArtifact> artifacts = locator.locate(groupId, artifactId);
        assertNotNull("Null returned!",artifacts);
        assertFalse("No artifacts found!", artifacts.isEmpty());
        for(FoundArtifact artifact : artifacts) {
            assertEquals("Wrong group id!", groupId, artifact.getGroupId());
            assertEquals("Wrong artifact id!", artifactId, artifact.getArtifactId());
            assertNotNull("Null version!", artifact.getVersion());
        }
    }

    @Test
    public void testLocateArtifactsBad() {
        String groupId = "org.hibernate";
        String artifactId = "sdfsdfsdf-core";

        MavenLocator locator = new MavenAetherLocator();

        Collection<FoundArtifact> artifacts = locator.locate(groupId, artifactId);
        assertNotNull("Null returned!",artifacts);
        assertTrue("No artifacts should have been found!", artifacts.isEmpty());
    }

    @Test
    public void testLocateArtifactsVersionRange() {
        String groupId = "org.hibernate";
        String artifactId = "hibernate-core";
        String fromVersion = "5.2.0.Final";
        String toVersion = "5.2.9.Final";
        int expectedCount = 10;

        MavenLocator locator = new MavenAetherLocator();

        Collection<FoundArtifact> artifacts = locator.locate(groupId, artifactId, fromVersion, toVersion);
        assertNotNull("Null returned!",artifacts);
        assertEquals("Wrong count of artifacts found! \n"+collectionToString(artifacts), expectedCount, artifacts.size());
        for(FoundArtifact artifact : artifacts) {
            assertEquals("Wrong group id!", groupId, artifact.getGroupId());
            assertEquals("Wrong artifact id!", artifactId, artifact.getArtifactId());
            assertNotNull("Null version!", artifact.getVersion());
        }
    }

    @Test
    public void testLocateArticactsVersionRangeBad() {
        String groupId = "org.hibernate";
        String artifactId = "asdasasd-core";
        // todo somehow fix the bad version range - maybe check the version string against regexp?
        String fromVersion = "5.2.0.Final";
        String toVersion = "5.2.9.Final";
        int expectedCount = 10;

        MavenLocator locator = new MavenAetherLocator();

        Collection<FoundArtifact> artifacts = locator.locate(groupId, artifactId, fromVersion, toVersion);
        assertNotNull("Null returned!",artifacts);
        assertTrue("No artifacts should have been found for wrong artifactId!\n"+collectionToString(artifacts), artifacts.isEmpty());
    }

    private String collectionToString(Collection collection) {
        StringBuilder sb = new StringBuilder();
        for(Object o : collection) {
            sb.append(o.toString()+"\n");
        }
        return sb.toString();
    }

}
