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
    
    /**
     * Returns <code>Type</code> for given string value.
     * @param value
     * @return 
     */
    public static Type getValue(String value) {
        if (value != null) {
            return valueOf(value.toUpperCase());
        }
        return STRING;
    }
}
