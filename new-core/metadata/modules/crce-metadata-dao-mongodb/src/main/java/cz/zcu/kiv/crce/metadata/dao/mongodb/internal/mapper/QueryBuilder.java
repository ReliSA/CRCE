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
            tmp.add(processCapabilityFilter(path, filter));
        }

        return joinQueries(tmp, operator);
    }

    private static DBQuery.Query processCapabilityFilter(String prefix, CapabilityFilter filter) {
        DBQuery.Query namespace = DBQuery.is(joinPath(prefix, DbCapability.NAMESPACE), filter.getNamespace());
        DBQuery.Query attributes = processAttributeFilters(joinPath(prefix, DbCapability.ATTRIBUTES), filter.getOperator(), filter.getAttributes());
        DBQuery.Query subFilters = processCapabilityFilters(joinPath(prefix, DbCapability.CHILDREN), Operator.AND, filter.getSubFilters());

        return DBQuery.and(namespace, attributes, subFilters);
    }

    private static DBQuery.Query processAttributeFilters(String path, Operator operator, List<Attribute<?>> attributes) {
        List<DBQuery.Query> tmp = new LinkedList<>();
        for (Attribute<?> attribute : attributes) {
            tmp.add(processAttributeFilter(path, attribute));
        }

        return joinQueries(tmp, operator);
    }

    private static DBQuery.Query joinQueries(List<DBQuery.Query> queries, Operator operator) {
        if(queries.size() > 1) {
            if (operator == Operator.AND) {
                return DBQuery.and(queries.toArray(new DBQuery.Query[0]));
            } else {
                return DBQuery.or(queries.toArray(new DBQuery.Query[0]));
            }
        } else if (queries.size() == 1){
            return queries.get(0);
        } else {
            return DBQuery.empty();
        }
    }

    private static DBQuery.Query processAttributeFilter(String prefix, Attribute<?> attribute) {
        DBQuery.Query name = DBQuery.is(joinPath(prefix, DbAttribute.NAME), attribute.getName());
        DBQuery.Query type = DBQuery.is(joinPath(prefix, DbAttribute.TYPE), attribute.getType().getName());
        DBQuery.Query value = processAttributeValue(prefix, attribute.getOperator(), attribute.getValue());

        return DBQuery.and(name, type, value);
    }

    private static DBQuery.Query processAttributeValue(String prefix, cz.zcu.kiv.crce.metadata.Operator operator, Object value) {
        String path = joinPath(prefix, DbAttribute.VALUE);
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

    private static String joinPath(String prefix, String addon) {
        if(StringUtils.isBlank(prefix)) {
            return addon;
        }
        return prefix + "." + addon;
    }

}
