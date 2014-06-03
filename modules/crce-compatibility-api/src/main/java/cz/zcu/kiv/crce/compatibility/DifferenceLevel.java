package cz.zcu.kiv.crce.compatibility;


/**
 * Enumeration for specification of level which a difference value is related to.
 * <p/>
 * Package, class, operation (method or constructor) or field are meant by the level.
 * <p/>
 * Date: 13.3.14
 *
 * @author Jakub Danek
 */
public enum DifferenceLevel {

    PACKAGE("package"),
    TYPE("type"),
    OPERATION("operation"),    //both methods and constructors
    FIELD("field"),
    UNKNOWN("unknown");

    private final String value;

    private DifferenceLevel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
