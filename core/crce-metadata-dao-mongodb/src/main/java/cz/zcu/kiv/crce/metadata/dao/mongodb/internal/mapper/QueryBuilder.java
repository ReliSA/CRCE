package cz.zcu.kiv.crce.metadata.dao.mongodb.internal.mapper;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mongojack.DBQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.dao.filter.CapabilityFilter;
import cz.zcu.kiv.crce.metadata.dao.filter.Operator;
import cz.zcu.kiv.crce.metadata.dao.filter.ResourceFilter;
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db.DbAttribute;
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db.DbCapability;
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db.DbResource;

/**
 * This class provides convenience methods for building queries in MongoDB Mongojack engine.
 *
 * Date: 10.3.16
 *
 * @author Jakub Danek
 */
public class QueryBuilder {

    private static final Logger logger = LoggerFactory.getLogger(QueryBuilder.class);

    public static final DBObject RESOURCE_ID = new BasicDBObject(DbResource.RESOURCE_ID, 1);

    /**
     *
     * @param category category which the resource mustnt have
     * @return query for all resources that dont have the given category
     */
    public static DBQuery.Query queryByMissingCategory(String category) {
        DBQuery.Query cats = DBQuery.is(joinPath(joinPath(DbResource.IDENTITY, DbCapability.ATTRIBUTES), DbAttribute.NAME), "categories");
        DBQuery.Query catsVal =  DBQuery.notIn(joinPath(joinPath(DbResource.IDENTITY, DbCapability.ATTRIBUTES), DbAttribute.VALUE), category);

        return DBQuery.and(cats, catsVal);
    }

    /**
     *
     * @param pk  resource uuid
     * @return query for resource with the given PK (resource id)
     */
    public static DBQuery.Query queryByPK(String pk) {
        return DBQuery.is("_id", pk);
    }

    /**
     *
     * @param pks  resource uuids
     * @return query for resource with the given PK (resource id)
     */
    public static DBQuery.Query queryByPK(List<String> pks) {
        return DBQuery.in("_id", pks);
    }

    /**
     *
     * @param uri resource URI
     * @return query for resource that has the given URI
     */
    public static DBQuery.Query queryResourceByURI(String uri) {
        return DBQuery.is(DbResource.URI, uri);
    }

    /**
     *
     * @param repositoryId uid of repository
     * @return query for resources that belong to the given repository
     */
    public static DBQuery.Query queryResourceByRepositoryId(String repositoryId) {
        return DBQuery.is(DbResource.REPOSITORY_UUID, repositoryId);
    }

    public static DBQuery.Query queryByResourceId(String resourceId) {
        return DBQuery.is(DbResource.RESOURCE_ID, resourceId);
    }


    /**
     *
     * @param filter  search filter instance
     * @return   generated query for the search
     */
    public static DBQuery.Query processCapabilityFilters(ResourceFilter filter) {
        logger.debug("processCapabilityFilters({})", filter);

        return processCapabilityFilters("", filter.getOperator(), filter.getCapabilityFilters());
    }

    /**
     * Generates query in which individual Capability filters are joined based on the given operator
     * @param filters list of capability constraints
     * @return SQL query
     */
    private static DBQuery.Query processCapabilityFilters(String path, Operator operator, List<CapabilityFilter> filters) {
        List<DBQuery.Query> tmp = new LinkedList<>();

        for (CapabilityFilter filter : filters) {
            if (path.equals("")) {
                tmp.add(processCapabilityFilter(filter));
            } else {
                tmp.add(DBQuery.elemMatch(path, processCapabilityFilter(filter)));
            }
        }

        return joinQueries(tmp, operator);
    }

    private static DBQuery.Query processCapabilityFilter(CapabilityFilter filter) {
        DBQuery.Query namespace = DBQuery.is(DbCapability.NAMESPACE, filter.getNamespace());
        DBQuery.Query attributes = processAttributeFilters(DbCapability.ATTRIBUTES, filter.getOperator(), filter.getAttributes());
        DBQuery.Query subFilters = processCapabilityFilters(DbCapability.CHILDREN, Operator.AND, filter.getSubFilters());

        return DBQuery.and(namespace, attributes, subFilters);
    }

    private static DBQuery.Query processAttributeFilters(String path, Operator operator, List<Attribute<?>> attributes) {
        List<DBQuery.Query> tmp = new LinkedList<>();
        for (Attribute<?> attribute : attributes) {
            tmp.add(DBQuery.elemMatch(path, processAttributeFilter(attribute)));
        }

        return joinQueries(tmp, operator);
    }

    private static DBQuery.Query processAttributeFilter(Attribute<?> attribute) {
        DBQuery.Query name = DBQuery.is(DbAttribute.NAME, attribute.getName());
        DBQuery.Query type = DBQuery.is(DbAttribute.TYPE, attribute.getType().getName());
        DBQuery.Query value = processAttributeValue(DbAttribute.VALUE, attribute.getOperator(), attribute.getValue());

        return DBQuery.and(name, type, value);
    }

    private static DBQuery.Query processAttributeValue(String path, cz.zcu.kiv.crce.metadata.Operator operator, Object value) {
        switch (operator) {
            case EQUAL:
                return DBQuery.is(path, value);

            case NOT_EQUAL:
                return DBQuery.notEquals(path, value);

            case PRESENT:
                return DBQuery.exists(path);

            case LESS:
                return DBQuery.lessThan(path, value);

            case LESS_EQUAL:
                return DBQuery.lessThanEquals(path, value);

            case GREATER:
                return DBQuery.greaterThan(path, value);

            case GREATER_EQUAL:
                return DBQuery.greaterThanEquals(path, value);

            default:
                throw new UnsupportedOperationException("Operator " + operator + " is not supported for ... type.");

        }
    }

    private static DBQuery.Query joinQueries(List<DBQuery.Query> queries, Operator operator) {
        switch (queries.size()) {
            case 0:
                return DBQuery.empty();

            case 1:
                return queries.get(0);

            default:
                switch (operator) {
                    case AND:
                        return DBQuery.and(queries.toArray(new DBQuery.Query[0]));
                    default:
                    case OR:
                        return DBQuery.or(queries.toArray(new DBQuery.Query[0]));
                }
        }
    }

    private static String joinPath(String prefix, String addon) {
        if(StringUtils.isBlank(prefix)) {
            return addon;
        }
        return prefix + "." + addon;
    }

}
