package cz.zcu.kiv.crce.metadata.dao.internal.type;

import java.net.URI;
import java.util.List;

import org.osgi.framework.Version;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public enum DbAttributeType {

    STRING(String.class, (short) 0),
    LONG(Long.class, (short) 1),
    DOUBLE(Double.class, (short) 2),
    LIST(List.class, (short) 3),
    VERSION(Version.class, (short) 4),
    URI(URI.class, (short) 5);

    private final Class<?> clazz;
    private final short dbValue;

    private DbAttributeType(Class<?> clazz, short dbValue) {
        this.clazz = clazz;
        this.dbValue = dbValue;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public short getDbValue() {
        return dbValue;
    }

    public static DbAttributeType fromClass(Class<?> clazz) {
        for (DbAttributeType value : values()) {
            if (value.clazz.equals(clazz)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid attribute type class: " + clazz);
    }

    public static DbAttributeType fromDbValue(short dbValue) {
        for (DbAttributeType value : values()) {
            if (value.dbValue == dbValue) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid attribute type DB value: " + dbValue);
    }

    public static short getDbValue(Class<?> clazz) {
        for (DbAttributeType value : values()) {
            if (value.clazz.equals(clazz)) {
                return value.dbValue;
            }
        }
        throw new IllegalArgumentException("Invalid attribute type class: " + clazz);
    }

    public static Class<?> getClassValue(short dbValue) {
        for (DbAttributeType value : values()) {
            if (value.dbValue == dbValue) {
                return value.clazz;
            }
        }
        throw new IllegalArgumentException("Invalid attribute type DB value: " + dbValue);
    }
}
