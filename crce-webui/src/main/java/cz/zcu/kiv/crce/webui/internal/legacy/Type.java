package cz.zcu.kiv.crce.webui.internal.legacy;

import java.util.Arrays;
import java.util.List;
import org.osgi.framework.Version;

/**
 * This enumeration indicates the type of Properties.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public enum Type {

    STRING("String", String.class),
    VERSION("Version", Version.class),
    LONG("Long", Long.class),
    DOUBLE("Double", Double.class),
//    URL("url", String.class),
//    URI("uri", String.class),
//    SET("set", List.class);
    LIST("List", List.class);

    private String string;
    private Class<?> clazz;

    Type(String string, Class<?> clazz) {
        this.string = string;
        this.clazz = clazz;
    }

    @Override
    public String toString() {
        return string;
    }

    public Class<?> getTypeClass() {
        return clazz;
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

    public static Object propertyValueFromString(Type type, String value) {
        if (value == null) {
            return null;
        }

        switch (type) {
            case DOUBLE:
                return Double.valueOf(value);

            case LIST:
                return Arrays.asList(value.split(","));

            case LONG:
                return Long.valueOf(value);

            case STRING:
                return String.valueOf(type);

            case VERSION:
                return new Version(value);
                
            default:
                return null;
        }
    }
}
