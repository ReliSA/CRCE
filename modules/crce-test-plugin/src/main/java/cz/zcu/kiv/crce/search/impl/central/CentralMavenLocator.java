package cz.zcu.kiv.crce.search.impl.central;

import cz.zcu.kiv.crce.search.FoundArtifact;
import cz.zcu.kiv.crce.search.MavenLocator;

import java.util.Collection;

/**
 * This locator uses the central maven repo to search for artifacts - http://search.maven.org/.
 * This repo provides rest api (described here http://search.maven.org/#api) which will be used to
 * locate the artifacts.
 *
 * @author Zdenek Vales
 */
public class CentralMavenLocator implements MavenLocator {

    @Override
    public FoundArtifact locate(String groupId, String artifactId, String version) {
        return null;
    }

    @Override
    public Collection<FoundArtifact> locate(String groupId, String artifactId) {
        return null;
    }
}
