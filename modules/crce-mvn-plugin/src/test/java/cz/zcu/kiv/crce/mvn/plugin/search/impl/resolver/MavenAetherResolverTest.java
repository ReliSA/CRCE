package cz.zcu.kiv.crce.mvn.plugin.search.impl.resolver;

import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenLocator;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenResolver;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.aether.MavenAetherLocator;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by valesz on 13.04.2017.
 */
public class MavenAetherResolverTest {

    @Test
    // todo: fix
    public void testResolve() {
        MavenLocator mavenLocator = new MavenAetherLocator();
        MavenResolver mavenResolver = new MavenAetherResolver();

        String groupId = "org.hibernate";
        String artifactId = "hibernate-core";
        String version = "5.2.0.Final";

        FoundArtifact foundArtifact = mavenLocator.locate(groupId, artifactId, version);
        assertNotNull("Artifact not located!", foundArtifact);

        File file = mavenResolver.resolve(foundArtifact);
        assertNotNull("Artifact not resolved!", file);
        assertTrue("Non existent file returned!", file.exists());
    }
}
