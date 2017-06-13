package cz.zcu.kiv.crce.mvn.plugin.search.impl.resolver;

import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenLocator;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenResolver;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.CentralMavenRestLocator;
import org.eclipse.aether.repository.RemoteRepository;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by valesz on 13.04.2017.
 */
public class MavenAetherResolverTest {

    @Test
    public void testReconfigure3() throws FileNotFoundException {
        MavenAetherResolver resolver = new MavenAetherResolver();
        File f = new File(getClass().getResource("/mavenAetherLocatorTest.properties").getPath());

        // default config
        List<RemoteRepository> repositories = resolver.getRepositories();
        assertEquals("Only one remote repository expected to be configured!", 1, repositories.size());
        RemoteRepository repo = repositories.get(0);
        assertEquals(repo.getId(), MavenAetherResolver.REPOSITORY_ID_DEF);
        assertEquals(repo.getContentType(), MavenAetherResolver.REPOSITORY_TYPE_DEF);
        assertEquals(repo.getUrl(), MavenAetherResolver.REPOSITORY_URL_DEF);
        assertEquals(resolver.getLocalRepoPath(), MavenAetherResolver.LOCAL_REPOSITORY_PATH_DEF);

        // new config with null file => should be default
        resolver.reconfigure(null);
        repositories = resolver.getRepositories();
        assertEquals("Only one remote repository expected to be configured!", 1, repositories.size());
        repo = repositories.get(0);
        assertEquals(repo.getId(), MavenAetherResolver.REPOSITORY_ID_DEF);
        assertEquals(repo.getContentType(), MavenAetherResolver.REPOSITORY_TYPE_DEF);
        assertEquals(repo.getUrl(), MavenAetherResolver.REPOSITORY_URL_DEF);
        assertEquals(resolver.getLocalRepoPath(), MavenAetherResolver.LOCAL_REPOSITORY_PATH_DEF);

    }

    @Test(expected = FileNotFoundException.class)
    public void testReconfigure2() throws FileNotFoundException {
        MavenAetherResolver resolver = new MavenAetherResolver();

        // default config
        List<RemoteRepository> repositories = resolver.getRepositories();
        assertEquals("Only one remote repository expected to be configured!", 1, repositories.size());
        RemoteRepository repo = repositories.get(0);
        assertEquals(repo.getId(), MavenAetherResolver.REPOSITORY_ID_DEF);
        assertEquals(repo.getContentType(), MavenAetherResolver.REPOSITORY_TYPE_DEF);
        assertEquals(repo.getUrl(), MavenAetherResolver.REPOSITORY_URL_DEF);
        assertEquals(resolver.getLocalRepoPath(), MavenAetherResolver.LOCAL_REPOSITORY_PATH_DEF);

        // new config
        resolver.reconfigure(new File("asasd"));
        fail("Exception should have been thrown for non-existent file!");
    }

    @Test
    public void testReconfigure() throws IOException {
        MavenAetherResolver resolver = new MavenAetherResolver();
        File f = new File(getClass().getResource("/mavenAetherLocatorTest.properties").getPath());
        String repoIdFormat = "central-new-%d";
        String repoTypeFormat = "default-new-%d";
        String repoUrlFormat = "http://repo%d.maven.org/maven2/";

        // default config
        List<RemoteRepository> repositories = resolver.getRepositories();
        assertEquals("Only one remote repository expected to be configured!", 1, repositories.size());
        RemoteRepository repo = repositories.get(0);
        assertEquals(repo.getId(), MavenAetherResolver.REPOSITORY_ID_DEF);
        assertEquals(repo.getContentType(), MavenAetherResolver.REPOSITORY_TYPE_DEF);
        assertEquals(repo.getUrl(), MavenAetherResolver.REPOSITORY_URL_DEF);
        assertEquals(resolver.getLocalRepoPath(), MavenAetherResolver.LOCAL_REPOSITORY_PATH_DEF);

        // new config
        resolver.reconfigure(f);
        assertEquals(resolver.getLocalRepoPath(),"d:/tools/develop/apache-maven/repository/");
        repositories = resolver.getRepositories();
        assertEquals("Three repositories expected to be configured!", 3, repositories.size());
        for (int i = 0; i < repositories.size(); i++) {
            RemoteRepository r = repositories.get(i);
            assertEquals("Wrong id of "+i+" repository!", String.format(repoIdFormat, i), r.getId());
            assertEquals("Wrong type of "+i+" repository!", String.format(repoTypeFormat, i), r.getContentType());
            assertEquals("Wrong id of "+i+" repository!", String.format(repoUrlFormat, i), r.getUrl());
        }

    }

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
