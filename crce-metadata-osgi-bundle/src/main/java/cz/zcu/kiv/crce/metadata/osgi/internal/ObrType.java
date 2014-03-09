package cz.zcu.kiv.crce.metadata.osgi.internal;

import java.util.Arrays;
import java.util.List;

import cz.zcu.kiv.crce.metadata.type.Version;

/**
 * Legacy OBR property types.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public enum ObrType {

    STRING("string", String.class),
    VERSION("version", Version.class),
    LONG("long", Long.class),
    DOUBLE("double", Double.class),
    URL("url", String.class),
    URI("uri", String.class),
    SET("set", List.class);
//    LIST("List", List.class);

    private final String string;
    private final Class<?> clazz;

    ObrType(String string, Class<?> clazz) {
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
    public static ObrType getValue(String value) {
        if (value != null) {
            return valueOf(value.toUpperCase());
        }
        return STRING;
    }

    public static Object propertyValueFromString(ObrType type, String value) {
        if (value == null) {
            return null;
        }

        switch (type) {
            case DOUBLE:
                return Double.valueOf(value);

            case SET:
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
