package cz.zcu.kiv.crce.apicomp.exception;

/**
 * Each type of metadata sets used to compare APIs is identified by the so-called identity
 * capability. If such capability is not found in provided metadata set, this exception is
 * thrown.
 */
public class IdentityCapabilityNotFoundException extends Exception {

    /**
     * Namespace of the expected capability.
     */
    public final String expectedCapability;

    public IdentityCapabilityNotFoundException(String expectedCapability) {
        super("Expected capability with namespace '"+expectedCapability+"' not found.");
        this.expectedCapability = expectedCapability;
    }
}
