package cz.zcu.kiv.crce.mvn.plugin.search;

import cz.zcu.kiv.crce.mvn.plugin.search.impl.VersionFilter;

import java.util.Collection;

/**
 * Interface for locating artifacts in a maven repo.
 *
 * Created by Zdenek Vales on 30.1.2017.
 */
public interface MavenLocator {

    /**
     * Locates the artifact in the repo and returns it.
     *
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
     *
     * Locates artifacts in the repo which contain includedPackage.
     *
     * @param includedPackage Name of the package included in the artifact.
     * @param groupIdFilter GroupId filter which will be used.
     * @param highestGroupIdMatch If true, only artifacts with groupId same as the longest possible part of groupIdFilter will be returned.
     * @return
     */
    Collection<FoundArtifact> locate(String includedPackage, String groupIdFilter, boolean highestGroupIdMatch);

    /**
     * Locates artifacts in the repo which contain includedPackage. Uses includedPackage as a groupIdFilter.
     *
     * @param includedPackage Name of the package included in the artifact.
     * @param highestGroupIdMatch If true, only artifacts with groupId same as the longest possible part of includedPackage will be returned.
     * @return Collection of artifacts containing this package or empty collection if nothing is found.
     */
    Collection<FoundArtifact> locate(String includedPackage, boolean highestGroupIdMatch);

    /**
     * Locates the artifacts in the repo if they're containing includedPackage.
     * @param includedPackage Name of the package included in the artifact.
     * @return Collection of artifacts containing this package or empty collection if nothing is found.
     */
    Collection<FoundArtifact> locate(String includedPackage);

    /**
     * Filters the found artifacts by version.
     *
     * @param foundArtifacts Collection to be filtered.
     * @param versionFilter Version filter.
     * @return Filtered collection.
     */
    Collection<FoundArtifact> filter(Collection<FoundArtifact> foundArtifacts, VersionFilter versionFilter);

    /**
     * Filters the found artifacts by group id. Only those with the beginning of group id same
     * as the provided one will pass.
     *
     * @param foundArtifacts Collection to be filtered.
     * @param groupId Group id filter.
     * @return Filtered collection.
     */
    Collection<FoundArtifact> filter(Collection<FoundArtifact> foundArtifacts, String groupId);

}
