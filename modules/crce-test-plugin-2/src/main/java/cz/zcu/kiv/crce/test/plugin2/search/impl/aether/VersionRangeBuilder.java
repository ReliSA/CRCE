package cz.zcu.kiv.crce.test.plugin2.search.impl.aether;

/**
 * Use this class to build version ranges when using Aether locator. User should not need to use this class directly.
 * Methods in this class should return string which then can be used in artifact coordinates (see maven conventions for version ranges).
 */
public class VersionRangeBuilder {

    /**
     * Creates a singe version coordinates.
     * @param version Version.
     * @return A string [version].
     */
    public static String singleVersion(String version) {
        return "["+version+"]";
    }

    /**
     * Creates a string specifying all versions.
     * @return A string which specifies all versions - [0,).
     */
    public static String allVersions() {
        return "[0,)";
    }

    /**
     * Creates a string specifying all version in range (both ends included).
     *
     * @param fromVersion Starting version.
     * @param toVersion End version.
     * @return A string which specifies version range, with both end included - [fromVersion,toVersion]
     */
    public static String versionRange(String fromVersion, String toVersion) {
        return "["+fromVersion+","+toVersion+"]";
    }
}
