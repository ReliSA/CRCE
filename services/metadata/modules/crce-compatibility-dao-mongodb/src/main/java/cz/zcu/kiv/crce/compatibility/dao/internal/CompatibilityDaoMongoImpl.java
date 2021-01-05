package cz.zcu.kiv.crce.compatibility.dao.internal;

import com.mongodb.*;
import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.compatibility.CompatibilityFactory;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.dao.CompatibilityDao;
import cz.zcu.kiv.crce.compatibility.dao.internal.mapping.MongoCompatibilityMapper;
import cz.zcu.kiv.crce.compatibility.impl.DefaultCompatibilityFactoryImpl;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.mongodb.BaseMongoDao;
import cz.zcu.kiv.crce.metadata.type.Version;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of CompatibilityDao for MongoDB.
 *
 *
 * Date: 17.11.13
 *
 * @author Jakub Danek
 */
public class CompatibilityDaoMongoImpl extends BaseMongoDao implements CompatibilityDao {

    private static final Logger logger = LoggerFactory.getLogger(CompatibilityDaoMongoImpl.class);
    /*
     *  Flags for operation used during search by version.
     *
     *  Either resources with higher or lower version are searched for.
     */
    private static final int V_HIGHER = 1;
    private static final int V_LOWER = 2;

    private CompatibilityFactory factory; /*injected by DependencyManager*/

    private DBCollection col;

    public CompatibilityDaoMongoImpl(MongoClient client) {
        super(client);
        this.col = getCollection("compatibility");

        // todo: service or just like this? probably should be service
        factory = new DefaultCompatibilityFactoryImpl();
    }

    @Override
    public Compatibility readCompability(String id) {
        logger.debug("Read compatibility with id: {}", id);
        BasicDBObject query = new BasicDBObject("_id", ObjectId.massageToObjectId(id));

        DBObject ret = col.findOne(query);

        return MongoCompatibilityMapper.mapToCompatibility(ret, factory);
    }

    @Override
    public Compatibility saveCompatibility(Compatibility compatibility) {
        DBObject cmp = MongoCompatibilityMapper.mapToDbObject(compatibility);

        //TODO catch MONGO exception for failure handling
        col.save(cmp);
        logger.debug("Saved compatibility: {}", cmp);
        return MongoCompatibilityMapper.mapToCompatibility(cmp, factory);
    }

    @Override
    public void deleteCompatibility(Compatibility compatibility) {
        if(logger.isDebugEnabled()) {
            logger.debug("Delete compatibility id: {}", compatibility.getId());
        }
        BasicDBObject query = new BasicDBObject("_id", ObjectId.massageToObjectId(compatibility.getId()));
        col.remove(query);
    }

    @Override
    public void deleteAllRelatedCompatibilities(String resourceName, Version resourceVersion) {
        logger.debug("Deleting all compatibility data related to {}-{}", resourceName, resourceVersion);
        DBObject qRes = QueryBuilder.start(MongoCompatibilityMapper.C_RESOURCE_NAME).is(resourceName)
                .and(MongoCompatibilityMapper.C_RESOURCE_VERSION).is(MongoCompatibilityMapper.mapVersion(resourceVersion))
                .get();
        DBObject qBase = QueryBuilder.start(MongoCompatibilityMapper.C_BASE_NAME).is(resourceName)
                .and(MongoCompatibilityMapper.C_BASE_VERSION).is(MongoCompatibilityMapper.mapVersion(resourceVersion))
                .get();
        DBObject q = QueryBuilder.start().or(qRes, qBase).get();

        col.remove(q);
    }

    @Override
    public List<Compatibility> listOwnedCompatibilities(String resourceName, Version resourceVersion) {
        DBObject query = BasicDBObjectBuilder.start(MongoCompatibilityMapper.C_RESOURCE_NAME, resourceName)
                                                    .add(MongoCompatibilityMapper.C_RESOURCE_VERSION, MongoCompatibilityMapper.mapVersion(resourceVersion)).get();

        DBCursor cursor = col.find(query);
        if(logger.isDebugEnabled()) {
            logger.debug("Compatibilities found for {}-{}: {}", resourceName, resourceVersion, cursor.size());
        }

        return parseCursor(cursor);
    }

    @Override
    public List<Compatibility> findHigher(String baseName, Version baseVersion, List<Difference> difference) {
        return findByVersion(V_HIGHER, baseName, baseVersion, difference);
    }

    @Override
    public List<Compatibility> findLower(String resourceName, Version resourceVersion, List<Difference> difference) {
        return findByVersion(V_LOWER, resourceName, resourceVersion, difference);
    }

    @Override
    public List<Compatibility> findCompatibility(Resource baseResource, Resource resource) {
        DBObject query = BasicDBObjectBuilder.start(MongoCompatibilityMapper.C_BASE_NAME, baseResource.getId())
                .add(MongoCompatibilityMapper.C_RESOURCE_NAME, resource.getId()).get();

        DBCursor cursor = col.find(query);
        if(logger.isDebugEnabled()) {
            logger.debug("Compatibilities found for {};{} pair: {}.", baseResource.getId(), resource.getId(), cursor.size());
        }

        return parseCursor(cursor);
    }

    /**
     * Shared method for Compatibility-data search by version.
     *
     *
     * @param flag flag designating whether search for higher or lower version is requested
     * @param name resource name
     * @param version current resource version which will be compared against
     * @param differences list of allowed difference values, at least one is required
     * @return list of found compatibility data items
     */
    private List<Compatibility> findByVersion(int flag, String name, Version version, List<Difference> differences) {
        final String nameKey, fixedVersionKey, searchVersionkey, op;
        switch (flag) {
            case V_HIGHER: //search for higher versions
                //method arguments represent the resource which has been compared-to
                //query will return list of Compatibility data, where the provided resource (via arguments) served
                //as BASE resource
                nameKey = MongoCompatibilityMapper.C_BASE_NAME;
                fixedVersionKey = MongoCompatibilityMapper.C_BASE_VERSION;
                searchVersionkey = MongoCompatibilityMapper.C_RESOURCE_VERSION;
                op = "$gt";
                break;
            case V_LOWER: //search for lower versions
                //method arguments represent the resource which was compared to all others
                //query will return list of Compatibility data, where the provided resource (via arguments) served
                //as NEW resource
                nameKey = MongoCompatibilityMapper.C_RESOURCE_NAME;
                fixedVersionKey = MongoCompatibilityMapper.C_RESOURCE_VERSION;
                searchVersionkey = MongoCompatibilityMapper.C_BASE_VERSION;
                op = "$lt";
                break;
            default: //unsupported
                throw new RuntimeException("Invalid flag argument!");
        }

        List<String> diffNames = MongoCompatibilityMapper.parseEnum(differences);

        DBObject q = QueryBuilder.start(nameKey).is(name)     //name
                .and(fixedVersionKey).is(MongoCompatibilityMapper.mapVersion(version))
                .and(createVersionComparisonQuery(version, searchVersionkey, op))
                .and(MongoCompatibilityMapper.C_BUNDLE_DIFF).in(diffNames).get();

        logger.debug("findByVersion params: name: {}; version: {}; differences: {}", name, version, differences);
        logger.debug("findByVersion query, flag ({}): {}", flag, q);
        DBCursor cursor = col.find(q);
        return parseCursor(cursor);
    }

    /**
     * Parse cursor into a list of Compatibility items
     * @param cursor cursor gained by a query execution
     * @return list of Compatibilities or an empty list if cursor empty
     */
    private List<Compatibility> parseCursor(DBCursor cursor) {
        List<Compatibility> retList = new ArrayList();


        DBObject tmp;
        while(cursor.hasNext()) {
            tmp = cursor.next();
            retList.add(MongoCompatibilityMapper.mapToCompatibility(tmp, factory));
        }

        return retList;
    }

    /**
     * Creates query for greater/less than comparioson of serialized Version structure.
     * <br/>
     * <br/>
     * NOTE: While this query works for EQUALITY comparison as well, it is not recommended due to performance overhead.
     * Use simple query by serialized Version object instead.
     *
     * @param version version for comparions
     * @param baseKey key of the version structure within document
     * @param op operation for comparison (either $gt or $lt)
     * @return query object
     */
    private DBObject createVersionComparisonQuery(Version version, String baseKey, String op) {
        String majorKey = baseKey + "." + MongoCompatibilityMapper.C_VERSION_MAJOR;
        String minorKey = baseKey + "." + MongoCompatibilityMapper.C_VERSION_MINOR;
        String microKey = baseKey + "." + MongoCompatibilityMapper.C_VERSION_MICRO;

        DBObject major = BasicDBObjectBuilder.start(majorKey, new BasicDBObject(op, version.getMajor()))
                            .get();
        logger.debug("Version comparison query - major: {}", major);
        DBObject minor = QueryBuilder.start().and(new BasicDBObject(majorKey, version.getMajor()),
                                    new BasicDBObject(minorKey, new BasicDBObject(op, version.getMinor())))
                                    .get();
        logger.debug("Version comparison query - minor: {}", minor);
        DBObject micro = QueryBuilder.start().and(new BasicDBObject(majorKey, version.getMajor()),
                new BasicDBObject(minorKey, version.getMinor()),
                new BasicDBObject(microKey, new BasicDBObject(op, version.getMicro())))
                                    .get();
        logger.debug("Version comparison query - micro: {}", micro);

        DBObject query = QueryBuilder.start().or(major, minor, micro).get();
        logger.debug("Version comparison query: {}", query);
        return query;
    }
}
