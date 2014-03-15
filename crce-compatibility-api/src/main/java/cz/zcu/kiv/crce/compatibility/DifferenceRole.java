package cz.zcu.kiv.crce.compatibility;

/**
 * Enumeration of possible roles of compared bundle parts.
 * <p/>
 * Date: 13.3.14
 *
 * @author Jakub Danek
 */
public enum DifferenceRole {

    CAPABILITY("capability"),
    REQUIREMENT("requirement");

    private final String value;

    private DifferenceRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
