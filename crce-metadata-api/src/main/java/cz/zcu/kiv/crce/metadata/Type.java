package cz.zcu.kiv.crce.metadata;

/**
 *
 * @author kalwi
 */
public enum Type {

    STRING("string"),
    VERSION("version"),
    LONG("long"),
    DOUBLE("double"),
    URL("url"),
    URI("uri"),
    SET("set");
    
    private String string;

    Type(String str) {
        string = str;
    }

    @Override
    public String toString() {
        return string;
    }
    
    public static Type getValue(String value) {
        if (value != null) {
            return valueOf(value.toUpperCase());
        }
        return STRING;
    }
}
