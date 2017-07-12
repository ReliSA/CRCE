package cz.zcu.kiv.crce.mvn.plugin.search.impl;


import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import org.eclipse.aether.artifact.Artifact;

/**
 * A simple implementation of FoundArtifact.
 *
 * Messenger design pattern.
 *
 * Created by Zdenek Vales on 1.2.2017.
 */
public class SimpleFoundArtifact implements FoundArtifact {

    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String jarDownload;
    private final String pomDownload;

    /**
     * Creates a new artifact from aether artifact.
     * @param artifact
     */
    public SimpleFoundArtifact(Artifact artifact) {
        this.groupId = artifact.getGroupId();
        this.artifactId = artifact.getArtifactId();
        this.version = artifact.getVersion();
        this.jarDownload = "";
        this.pomDownload = "";
    }

    public SimpleFoundArtifact(String groupId, String artifactId, String version, String jarDownload, String pomDownload) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.jarDownload = jarDownload;
        this.pomDownload = pomDownload;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getPomDownloadLink() {
        return pomDownload;
    }

    @Override
    public String getJarDownloadLink() {
        return jarDownload;
    }

    @Override
    public String toString() {
        return "SimpleFoundArtifact{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", jarDownload='" + jarDownload + '\'' +
                ", pomDownload='" + pomDownload + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleFoundArtifact that = (SimpleFoundArtifact) o;

        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) return false;
        if (artifactId != null ? !artifactId.equals(that.artifactId) : that.artifactId != null) return false;
        return version != null ? version.equals(that.version) : that.version == null;
    }

    @Override
    public int hashCode() {
        int result = groupId != null ? groupId.hashCode() : 0;
        result = 31 * result + (artifactId != null ? artifactId.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
