package cz.zcu.kiv.crce.repository.maven.internal;

/**
 * Enum for resolving Versions from IndexingContext
 * @author Miroslav Brozek
 *
 */
public enum VersionResolve {
	ALL("all"),
	NEWEST("newest"),
	HIGHEST_MAJOR("highest-major"),
	HIGHEST_MINOR("highest-minor"),
	HIGHEST_MICRO("highest-micro"),
	HIGHEST_CLASSIFIER("highest-classifier");
	
	private String value;

	private VersionResolve(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	public static VersionResolve fromValue(String value) {
        for (VersionResolve v : values()) {
            if (v.value.equals(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid enum value: " + value);
    }
}
