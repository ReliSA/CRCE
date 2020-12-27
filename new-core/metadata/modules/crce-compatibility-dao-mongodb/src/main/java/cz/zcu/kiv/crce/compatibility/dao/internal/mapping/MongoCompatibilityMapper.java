package cz.zcu.kiv.crce.compatibility.dao.internal.mapping;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.compatibility.CompatibilityFactory;
import cz.zcu.kiv.crce.compatibility.Contract;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.DifferenceRole;
import cz.zcu.kiv.crce.metadata.type.Version;

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
    public static final String C_RESOURCE_NAME = "resourceName";
    public static final String C_RESOURCE_VERSION = "resourceVersion";
    public static final String C_BASE_NAME = "baseName";
    public static final String C_BASE_VERSION = "baseVersion";
    public static final String C_BUNDLE_DIFF = "bundleDifference";
    public static final String C_CONTRACT = "contract";

    public static final String C_VERSION_MAJOR = "major";
    public static final String C_VERSION_MINOR = "minor";
    public static final String C_VERSION_MICRO = "micro";
    public static final String C_VERSION_QUALIFIER = "qualifier";

    public static final String C_DETAILS = "details";
    public static final String C_DETAILS_CHILDREN = "children";
    public static final String C_DETAILS_LEVEL = "level";
    public static final String C_DETAILS_NAME = "name";
    public static final String C_DETAILS_NAMESPACE = "namespace";
    public static final String C_DETAILS_ROLE = "role";
    public static final String C_DETAILS_VALUE = "value";
    public static final String C_DETAILS_SYNTAX = "syntax";


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

        obj.put(C_CONTRACT, compatibility.getContract().name());

        List<DBObject> details = new ArrayList<>(compatibility.getDiffDetails().size());
        for (Diff detail : compatibility.getDiffDetails()) {
            details.add(mapDifferenceDetails(detail));
        }
        obj.put(C_DETAILS, details);

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
        Difference diffValue = getEnumFromValue(Difference.class, (String) source.get(C_BUNDLE_DIFF));
        Contract contract = getEnumFromValue(Contract.class, (String) source.get(C_CONTRACT));

        List<Diff> diffDetails = new ArrayList<>();
        List<DBObject> children = (List<DBObject>) source.get(C_DETAILS);
        if (children != null) {
            for (DBObject o : children) {
                diffDetails.add(mapToDiff(o, factory));
            }
        }

        return factory.createCompatibility(id, resourceName, resrouceVersion, baseName, baseVersion, diffValue, diffDetails, contract);
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
     * Map Diff into JSON format.
     * @param details to be mapped
     * @return DbObject instance
     */
    public static DBObject mapDifferenceDetails(Diff details) {
        DBObject obj = new BasicDBObject(C_DETAILS_NAME, details.getName());
        obj.put(C_DETAILS_LEVEL, details.getLevel().name());
        obj.put(C_DETAILS_VALUE, details.getValue().name());
        obj.put(C_DETAILS_NAMESPACE, details.getNamespace());
        obj.put(C_DETAILS_SYNTAX, details.getSyntax());

        if (details.getRole() != null) {
            obj.put(C_DETAILS_ROLE, details.getRole().name());
        }

        List<DBObject> children = new ArrayList<>(details.getChildren().size());
        for (Diff child : details.getChildren()) {
            children.add(mapDifferenceDetails(child));
        }
        obj.put(C_DETAILS_CHILDREN, children);

        return obj;
    }

    /**
     * Maps from JSON to Diff
     *
     * @param obj                  JSON formatted data
     * @param compatibilityFactory factory
     * @return diff instance with the data
     */
    public static Diff mapToDiff(DBObject obj, CompatibilityFactory compatibilityFactory) {
        Diff d = compatibilityFactory.createEmptyDiff();

        //string values
        String tmp = (String) obj.get(C_DETAILS_NAME);
        d.setName(tmp);
        tmp = (String) obj.get(C_DETAILS_NAMESPACE);
        d.setNamespace(tmp);
        tmp = (String) obj.get(C_DETAILS_SYNTAX);
        d.setSyntax(tmp);

        //enums
        Difference value = getEnumFromValue(Difference.class, (String) obj.get(C_DETAILS_VALUE));
        DifferenceRole role = getEnumFromValue(DifferenceRole.class, (String) obj.get(C_DETAILS_ROLE));
        DifferenceLevel level = getEnumFromValue(DifferenceLevel.class, (String) obj.get(C_DETAILS_LEVEL));

        d.setValue(value);
        d.setRole(role);
        d.setLevel(level);

        //children
        List<DBObject> children = (List<DBObject>) obj.get(C_DETAILS_CHILDREN);
        if (children != null) {
            for (DBObject o : children) {
                d.addChild(mapToDiff(o, compatibilityFactory));
            }
        }

        return d;
    }

    /**
     * Safe method to get enum value from strings stored in db.
     * <p/>
     * Returns null if the value is null or doesnt match any enum value.
     *
     * @param clazz enum class
     * @param value value to parse
     * @param <E>   enum type
     * @return enum value
     */
    public static <E extends Enum<E>> E getEnumFromValue(Class<E> clazz, String value) {
        try {
            return E.valueOf(clazz, value);
        } catch (Exception ex) {
            return null;
        }
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
