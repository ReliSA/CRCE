package cz.zcu.kiv.crce.compatibility;

/**
 * Date: 8.4.14
 *
 * @author Jakub Danek
 */
public enum Contract {

    SYNTAX("syntax"),
    SEMANTICS("semantics"),
    INTERACTION("interaction"),
    EXTRA_FUNCTIONAL("extra-functional");

    private final String value;

    private Contract(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
