package cz.zcu.kiv.crce.test.plugin2.search.impl.aether;


import cz.zcu.kiv.crce.test.plugin2.search.FoundArtifact;
import cz.zcu.kiv.crce.test.plugin2.search.MavenLocator;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class MavenAetherLocatorTest {

    @Test
    public void testLocateArtifact() {
        String groupId = "org.hibernate";
        String artifactId = "hibernate-core";
        String version = "5.2.7.Final";
        String jarDownload = "http://search.maven.org/remotecontent?filepath=org/hibernate/hibernate-core/5.2.7.Final/hibernate-core-5.2.7.Final.jar";
        String pomDownload = "http://search.maven.org/remotecontent?filepath=org/hibernate/hibernate-core/5.2.7.Final/hibernate-core-5.2.7.Final.pom";

        MavenLocator locator = new MavenAetherLocator();

        FoundArtifact artifact = locator.locate(groupId, artifactId, version);
        assertNotNull("Returned artifact is null!", artifact);
        assertEquals("Wrong group id!", groupId, artifact.getGroupId());
        assertEquals("Wrong artifact id!", artifactId, artifact.getArtifactId());
        assertEquals("Wrong version!", version, artifact.getVersion());
        assertNotNull("Jar download link is null!", artifact.getJarDownloadLink());
//        assertEquals("Wrong jar download link!", jarDownload, artifact.getJarDownloadLink());
        assertNotNull("Pom download link is null!", artifact.getPomDownloadLink());
//        assertEquals("Wrong pom download link!", pomDownload, artifact.getPomDownloadLink());
    }

    @Test
    public void testLocateArtifacts() {
        String groupId = "org.hibernate";
        String artifactId = "hibernate-core";
        String jarDownloadTmplt = "http://search.maven.org/remotecontent?filepath=org/hibernate/hibernate-core/%s/hibernate-core-%s.jar";
        String pomDownloadTmplt = "http://search.maven.org/remotecontent?filepath=org/hibernate/hibernate-core/%s/hibernate-core-%s.pom";

        MavenLocator locator = new MavenAetherLocator();

        Collection<FoundArtifact> artifacts = locator.locate(groupId, artifactId);
        assertNotNull("Null returned!",artifacts);
        assertFalse("No artifacts found!", artifacts.isEmpty());
        for(FoundArtifact artifact : artifacts) {
            assertEquals("Wrong group id!", groupId, artifact.getGroupId());
            assertEquals("Wrong artifact id!", artifactId, artifact.getArtifactId());
            assertNotNull("Null version!", artifact.getVersion());

            String jd = String.format(jarDownloadTmplt, artifact.getVersion(), artifact.getVersion()),
                    pd = String.format(pomDownloadTmplt, artifact.getVersion(), artifact.getVersion());

            assertNotNull("Jar download link is null!", artifact.getJarDownloadLink());
//            assertEquals("Wrong jar download link!", jd, artifact.getJarDownloadLink());
            assertNotNull("Pom download link is null!", artifact.getPomDownloadLink());
//            assertEquals("Wrong pom download link!", pd, artifact.getPomDownloadLink());
        }
    }

}
