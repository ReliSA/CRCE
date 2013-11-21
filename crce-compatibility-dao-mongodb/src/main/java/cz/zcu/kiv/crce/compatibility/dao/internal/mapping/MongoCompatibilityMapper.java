package cz.zcu.kiv.crce.compatibility.dao.internal.mapping;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.osgi.framework.Version;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import cz.zcu.kiv.typescmp.Difference;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.compatibility.CompatibilityFactory;

/**
 * Serialization/Deserialization support class between Compatibility and MongoDB.
 *
 * Date: 17.11.13
 *
 * @author Jakub Danek
 */
public class MongoCompatibilityMapper {

    /*
        KEY SPACE
     */
    public static String C_RESOURCE_NAME = "resourceName";
    public static String C_RESOURCE_VERSION = "resourceVersion";
    public static String C_BASE_NAME = "baseName";
    public static String C_BASE_VERSION = "baseVersion";
    public static String C_BUNDLE_DIFF = "bundleDifference";

    public static String C_VERSION_MAJOR = "major";
    public static String C_VERSION_MINOR = "minor";
    public static String C_VERSION_MICRO = "micro";
    public static String C_VERSION_QUALIFIER = "qualifier";

    /**
     * Map given string to ObjectId or generate a new one if empty/null.
     * @param id
     * @return  ObjectId instance
     */
    private static ObjectId mapId(String id) {
        ObjectId oid;
        if(id == null || id.isEmpty()) {
            oid = ObjectId.get();
        } else {
            oid = ObjectId.massageToObjectId(id);
        }
        return oid;
    }

    /**
     * Maps Compatibility do DBObject
     */
    public static DBObject mapToDbObject(Compatibility compatibility) {
        DBObject obj = new BasicDBObject();

        obj.put("_id", mapId(compatibility.getId()));

        obj.put(C_RESOURCE_NAME, compatibility.getResourceName());
        obj.put(C_RESOURCE_VERSION, mapVersion(compatibility.getResourceVersion()));

        obj.put(C_BASE_NAME, compatibility.getBaseResourceName());
        obj.put(C_BASE_VERSION, mapVersion(compatibility.getBaseResourceVersion()));

        obj.put(C_BUNDLE_DIFF, compatibility.getDiffValue().name());

        return obj;
    }

    /**
     * Maps DBObject to compability
     * @param source source DBObject
     * @param factory factory class for creating new Compatibility instance
     * @return
     */
    public static Compatibility mapToCompatibility(DBObject source, CompatibilityFactory factory) {
        if(source == null) {
            return null;
        }

        String id = source.get("_id").toString();
        String baseName = (String) source.get(C_BASE_NAME);
        Version baseVersion = mapToVersion((DBObject) source.get(C_BASE_VERSION));
        String resourceName = (String) source.get(C_RESOURCE_NAME);
        Version resrouceVersion = mapToVersion((DBObject) source.get(C_RESOURCE_VERSION));
        Difference diffValue = Difference.valueOf((String) source.get(C_BUNDLE_DIFF));

        return factory.createCompatibility(id, resourceName, resrouceVersion, baseName, baseVersion, diffValue, null);
    }

    /**
     * Maps version object to DBObject
     * @param version
     * @return
     */
    public static DBObject mapVersion(Version version) {
        DBObject obj = new BasicDBObject(C_VERSION_MAJOR, version.getMajor());
        obj.put(C_VERSION_MINOR, version.getMinor());
        obj.put(C_VERSION_MICRO, version.getMicro());
        obj.put(C_VERSION_QUALIFIER, version.getQualifier());

        return obj;
    }

    /**
     * Maps DBObject to Version instance.
     * @param source
     * @return
     */
    public static Version mapToVersion(DBObject source) {
        int major = (int) source.get(C_VERSION_MAJOR);
        int minor = (int) source.get(C_VERSION_MINOR);
        int micro = (int) source.get(C_VERSION_MICRO);
        String qualifier = (String) source.get(C_VERSION_QUALIFIER);

        return new Version(major, minor, micro, qualifier);
    }

    /**
     * Parse list of Enums to a list of Strings using their name() value.
     * @param e list of enumerations
     * @return list of strings
     */
    public static List<String> parseEnum(List<? extends Enum> e) {
        if(e == null) {
            return new ArrayList<>();
        }
        List<String> names = new ArrayList<>(e.size());
        for(Enum d : e) {
            names.add(d.name());
        }
        return names;
    }
}
