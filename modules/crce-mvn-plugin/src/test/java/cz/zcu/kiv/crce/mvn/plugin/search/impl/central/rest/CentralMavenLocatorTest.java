package cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest;

import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class CentralMavenLocatorTest {

    @Test
    public void testLocateArtifact() {
        String groupId = "org.hibernate";
        String artifactId = "hibernate-core";
        String version = "5.2.7.Final";
        String jarDownload = "http://search.maven.org/remotecontent?filepath=org/hibernate/hibernate-core/5.2.7.Final/hibernate-core-5.2.7.Final.jar";
        String pomDownload = "http://search.maven.org/remotecontent?filepath=org/hibernate/hibernate-core/5.2.7.Final/hibernate-core-5.2.7.Final.pom";

        CentralMavenRestLocator locator = new CentralMavenRestLocator();

        FoundArtifact artifact = locator.locate(groupId, artifactId, version);
        assertNotNull("Returned artifact is null!", artifact);
        assertEquals("Wrong group id!", groupId, artifact.getGroupId());
        assertEquals("Wrong artifact id!", artifactId, artifact.getArtifactId());
        assertEquals("Wrong version!", version, artifact.getVersion());
        assertNotNull("Jar download link is null!", artifact.getJarDownloadLink());
        assertEquals("Wrong jar download link!", jarDownload, artifact.getJarDownloadLink());
        assertNotNull("Pom download link is null!", artifact.getPomDownloadLink());
        assertEquals("Wrong pom download link!", pomDownload, artifact.getPomDownloadLink());
    }

    @Test
    public void testLocateArtifacts() {
        String groupId = "org.hibernate";
        String artifactId = "hibernate-core";
        String jarDownloadTmplt = "http://search.maven.org/remotecontent?filepath=org/hibernate/hibernate-core/%s/hibernate-core-%s.jar";
        String pomDownloadTmplt = "http://search.maven.org/remotecontent?filepath=org/hibernate/hibernate-core/%s/hibernate-core-%s.pom";

        CentralMavenRestLocator locator = new CentralMavenRestLocator();

        Collection<FoundArtifact> artifacts = locator.locate(groupId, artifactId);
        for(FoundArtifact artifact : artifacts) {
            assertEquals("Wrong group id!", groupId, artifact.getGroupId());
            assertEquals("Wrong artifact id!", artifactId, artifact.getArtifactId());
            assertNotNull("Null version!", artifact.getVersion());

            String jd = String.format(jarDownloadTmplt, artifact.getVersion(), artifact.getVersion()),
                   pd = String.format(pomDownloadTmplt, artifact.getVersion(), artifact.getVersion());

            assertNotNull("Jar download link is null!", artifact.getJarDownloadLink());
            assertEquals("Wrong jar download link!", jd, artifact.getJarDownloadLink());
            assertNotNull("Pom download link is null!", artifact.getPomDownloadLink());
            assertEquals("Wrong pom download link!", pd, artifact.getPomDownloadLink());
        }
    }

    @Test
    public void testLocateArtifactByGroupId() {
        String groupId = "org.hibernate";

        CentralMavenRestLocator locator = new CentralMavenRestLocator();

        Collection<FoundArtifact> artifacts = locator.locate(groupId, null);
        assertNotNull("Null returned!", artifacts);
        assertFalse("No artifacts found for group id "+groupId, artifacts.isEmpty());
        for(FoundArtifact artifact : artifacts) {
            assertEquals("Wrong group id!", groupId, artifact.getGroupId());
            assertNotNull("Null artifact id!", artifact.getArtifactId());
            assertNotNull("Null version!", artifact.getVersion());
        }
    }

    @Test
    public void testLocateArtifactByIncludedPackage() {
        String packageName = "org.hibernate.dialect.function";

        CentralMavenRestLocator locator = new CentralMavenRestLocator();

        Collection<FoundArtifact> artifacts = locator.locate(packageName);
        assertNotNull("Null returned!", artifacts);
        assertFalse("No artifacts containing package "+packageName+" found!", artifacts.isEmpty());
    }

    @Test
    public void testLocateArtifactByIncludedPackageBad() {
        String badPackageName = "asdasdasdagasd";

        CentralMavenRestLocator locator = new CentralMavenRestLocator();

        Collection<FoundArtifact> artifacts = locator.locate(badPackageName);
        assertNotNull("Null returned!", artifacts);
        assertTrue("Artifacts containing package "+badPackageName+" found!", artifacts.isEmpty());
    }
}
