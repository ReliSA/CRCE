package cz.zcu.kiv.crce.repository.maven.internal;

/**
 * Enum for resolving Versions from IndexingContext
 * @author Miroslav Brozek
 *
 */
public enum ArtifactResolve {
	ALL("all"),
	NEWEST("newest"),
	HIGHEST_MAJOR("highest-major"),
	HIGHEST_MINOR("highest-minor"),
	HIGHEST_MICRO("highest-micro"),
	HIGHEST_QUALIFIER("highest-qualifier"),
	GAV("gav"),
	GROUP_ID("groupid"),
	GROUPID_ARTIFACTID("groupid-artifactid"),
	GROUPID_ARTIFACTID_FROM_VERSION("groupid-artifactid-minversion");
	
	
	
	
	private String value;

	private ArtifactResolve(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	public static ArtifactResolve fromValue(String value) {
        for (ArtifactResolve v : values()) {
            if (v.value.equals(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid enum value: " + value);
    }
}
