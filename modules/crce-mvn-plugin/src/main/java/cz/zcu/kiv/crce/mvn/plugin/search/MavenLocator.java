package cz.zcu.kiv.crce.mvn.plugin.search;

import java.util.Collection;

/**
 * Interface for locating artifacts in a maven repo.
 *
 * Created by Zdenek Vales on 30.1.2017.
 */
public interface MavenLocator {

    /**
     * Locates the artifact in the repo and returns it.
     * @param groupId Artifact group id.
     * @param artifactId Artifact id.
     * @param version Artifact version.
     * @return Found artifact or null.
     */
    FoundArtifact locate(String groupId, String artifactId, String version);

    /**
     * Locates the artifacts in the repo and returns them.
     * @param groupId Artifact group id.
     * @param artifactId Artifact id.
     * @return Collection of found artifacts (with different versions) or empty collection if nothing is found.
     */
    Collection<FoundArtifact> locate(String groupId, String artifactId);

    /**
     * Locates the artifacts in the repo with version in range [from,to] (inclusive).
     * @param groupId Artifact group id.
     * @param artifactId Artifact id.
     * @param fromVersion The oldest version to be located.
     * @param toVersion The newest version to be located.
     * @return Collection of found artifacts or empty collection if nothing is found.
     */
    Collection<FoundArtifact> locate(String groupId, String artifactId, String fromVersion, String toVersion);

    /**
     * Locates the artifacts in the repo if they're containing includedPackage.
     * @param includedPackage Name of the package included in the artifact.
     * @return Collection of artifacts containing this package or empty collection if nothing is found.
     */
    Collection<FoundArtifact> locate(String includedPackage);
}
