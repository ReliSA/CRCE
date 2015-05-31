package cz.zcu.kiv.crce.repository.maven.internal;

/**
 * Enum for resolving Versions from IndexingContext
 * @author Miroslav Brozek
 *
 */
public enum ArtifactResolutionStrategy {
    ALL("all"),
    NEWEST("newest"),
    HIGHEST_MAJOR("highest-major"),
    HIGHEST_MINOR("highest-minor"),
    HIGHEST_MICRO("highest-micro"),
    HIGHEST_QUALIFIER("highest-qualifier"),
    LOWEST_MINOR("lowest-minor"),
    LOWEST_MICRO("lowest-micro"),
    GAV("gav"),
    GROUP_ID("groupid"),
    GROUPID_ARTIFACTID("groupid-artifactid"),
    GROUPID_ARTIFACTID_FROM_VERSION("groupid-artifactid-minversion");
    
    private final String value;

    private ArtifactResolutionStrategy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ArtifactResolutionStrategy fromValue(String value) {
        for (ArtifactResolutionStrategy v : values()) {
            if (v.value.equals(value.trim())) {
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid enum value: " + value);
    }
}
