package cz.zcu.kiv.crce.mvn.plugin.search.impl.resolver;

import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenLocator;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenResolver;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.CentralMavenRestLocator;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by valesz on 13.04.2017.
 */
public class MavenAetherResolverTest {

    @Test
    public void testResolve() {
        MavenLocator mavenLocator = new CentralMavenRestLocator();
        MavenResolver mavenResolver = new MavenAetherResolver();

        String groupId = "io.airlift";
        String artifactId = "airline";
        String version = "0.6";

        FoundArtifact foundArtifact = mavenLocator.locate(groupId, artifactId, version);
        assertNotNull("Artifact not located!", foundArtifact);

        File file = mavenResolver.resolve(foundArtifact);
        assertNotNull("Artifact not resolved!", file);
        assertTrue("Non existent file returned!", file.exists());
    }

}
