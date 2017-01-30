package cz.zcu.kiv.crce.search;

import java.net.URL;

/**
 * Interface for artifact found in a maven repo.
 * Class implementing this interface is not an artifact at all. It's
 * actually just it's description and the artifact itself can be downloaded
 * from links the implementing class will provide.
 *
 * Created by Zdenek Vales on 30.1.2017.
 */
public interface FoundArtifact {

    /**
     * Returns the group id of the artifact.
     * @return String containing group id.
     */
    String getGroupId();

    /**
     * Returns the artifact id of the artifact.
     * @return String containing artifact id.
     */
    String getArtifactId();

    /**
     * Returns the version of artifact.
     * @return String containing version.
     */
    String getVersion();

    /**
     * Returns the download link for pom.xml of this artifact
     * Note that not every repo may specify this link.
     *
     * @return Link to the pom.xml or null.
     */
    URL getPomDownloadLink();

    /**
     * Returns the download link to jar containing the artifact.
     *
     * @return Link to the jar or null (that really shouldn't happen).
     */
    URL getJarDownloadLink();

}
