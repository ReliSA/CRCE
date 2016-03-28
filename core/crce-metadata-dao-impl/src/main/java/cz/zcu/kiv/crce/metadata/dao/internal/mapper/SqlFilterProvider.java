package cz.zcu.kiv.crce.metadata.dao.internal.mapper;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.dao.filter.CapabilityFilter;
import cz.zcu.kiv.crce.metadata.dao.filter.ResourceFilter;
import cz.zcu.kiv.crce.metadata.dao.internal.helper.SimpleStringBuilder;
import cz.zcu.kiv.crce.metadata.dao.internal.type.DbAttributeType;

/**
 *
 * Example for ORed CapabilityFilters
 *
 SELECT DISTINCT r.resource_id, r.repository_id, r.id, r.uri FROM resource r
 JOIN capability c1 ON c1.RESOURCE_ID = r.RESOURCE_ID
 JOIN CAPABILITY_ATTRIBUTE ca11 ON ca11.CAPABILITY_ID = c1.CAPABILITY_ID
 JOIN CAPABILITY sc11 ON c1.CAPABILITY_ID = sc11.PARENT_CAPABILITY_ID
 JOIN CAPABILITY_ATTRIBUTE ca111 ON ca111.CAPABILITY_ID = sc11.CAPABILITY_ID
 JOIN CAPABILITY_ATTRIBUTE ca112 ON ca112.CAPABILITY_ID = sc11.CAPABILITY_ID
 WHERE (c1.NAMESPACE = 'crce.api.java.package' AND ca11.name = 'name' AND ca11.STRING_VALUE = 'cz.zcu.kiv.osgi.demo.parking.carpark.flow'
 AND (sc11.NAMESPACE = 'crce.api.java.class' AND ((ca111.name = 'name' AND ca111.STRING_VALUE = 'IVehicleFlow') AND (ca112.name = 'interface' AND ca112.BOOLEAN_VALUE = true))))
 UNION
 SELECT DISTINCT r.resource_id, r.repository_id, r.id, r.uri FROM resource r
 JOIN capability c2 ON c2.RESOURCE_ID = r.RESOURCE_ID
 JOIN CAPABILITY_ATTRIBUTE ca21 ON ca21.CAPABILITY_ID = c2.CAPABILITY_ID
 WHERE (c2.NAMESPACE = 'crce.api.java.package' AND ca21.name = 'name' AND ca21.STRING_VALUE = 'org.danekja.java.util.function.serializable')


 Example for ANDed CapabilityFilters

 SELECT DISTINCT r.resource_id, r.repository_id, r.id, r.uri FROM resource r
 JOIN capability c1 ON c1.RESOURCE_ID = r.RESOURCE_ID
 JOIN CAPABILITY_ATTRIBUTE ca11 ON ca11.CAPABILITY_ID = c1.CAPABILITY_ID
 JOIN CAPABILITY sc11 ON c1.CAPABILITY_ID = sc11.PARENT_CAPABILITY_ID
 JOIN CAPABILITY_ATTRIBUTE ca111 ON ca111.CAPABILITY_ID = sc11.CAPABILITY_ID
 JOIN CAPABILITY_ATTRIBUTE ca112 ON ca112.CAPABILITY_ID = sc11.CAPABILITY_ID
 JOIN capability c2 ON c2.RESOURCE_ID = r.RESOURCE_ID
 JOIN CAPABILITY_ATTRIBUTE ca21 ON ca21.CAPABILITY_ID = c2.CAPABILITY_ID
 WHERE (c1.NAMESPACE = 'crce.api.java.package' AND ca11.name = 'name' AND ca11.STRING_VALUE = 'cz.zcu.kiv.osgi.demo.parking.carpark.flow'
 AND (sc11.NAMESPACE = 'crce.api.java.class' AND ((ca111.name = 'name' AND ca111.STRING_VALUE = 'IVehicleFlow') AND (ca112.name = 'interface' AND ca112.BOOLEAN_VALUE = true)))
 )
 AND (c2.NAMESPACE = 'crce.api.java.package' AND ca21.name = 'name' AND ca21.STRING_VALUE = 'cz.zcu.kiv.osgi.demo.parking.carpark.flow')

 *
 * Date: 10.3.16
 *
 * @author Jakub Danek
 */
public class SqlFilterProvider {

    public static final String PARAM_REPOSITORY_ID = "repositoryId";
    public static final String PARAM_FILTER = "filter";

    private static final String PREFIX_CAPABILITY = "c";
    private static final String PREFIX_ATTRIBUTE = "a";
    private static final Logger logger = LoggerFactory.getLogger(SqlFilterProvider.class);

    /**
     *
     * @param params expected params are:
     *               <ul>
     *                  <li>Long repositoryId</li>
     *                  <li>ResourceDAOFilter filter</li>
     *               </ul>
     * @return   generated SQL string for the search
     */
    public String generateSQL(Map<String, Object> params) {
        logger.debug("generateSQL({})", params);

        @SuppressWarnings("unchecked")
        ResourceFilter filter = (ResourceFilter) params.get(PARAM_FILTER);

        switch (filter.getOperator()) {
            case OR:
                return generateOrSQL(filter.getCapabilityFilters());
            case AND:
            default:
                return generateAndSQL(filter.getCapabilityFilters());
        }
    }

    /**
     * Generates SQL query in which individual Capability filters are joined using OR
     * @param filters list of capability constraints
     * @return SQL query
     */
    private String generateOrSQL(List<CapabilityFilter> filters) {
        StringBuilder output = new StringBuilder();
        SQL sql;

        for (int i = 0; i < filters.size(); i++) {
            sql = startQuery();
            processRootCapabilityJoin(sql, filters.get(i), i);
            sql.WHERE("r.repository_id = #{repositoryId}");
            processRootCapabilityWhere(sql, filters.get(i), i);

            if(output.length() > 0) {
                output.append(" UNION ");
            }
            output.append(sql);
        }

        return output.toString();
    }

    /**
     * Generates SQL query in which individual Capability filters are joined using AND
     * @param filters list of capability constraints
     * @return SQL query
     */
    private String generateAndSQL(List<CapabilityFilter> filters) {
        SQL sql = startQuery();

        for (int i = 0; i < filters.size(); i++) {
            processRootCapabilityJoin(sql, filters.get(i), i);
        }

        sql.WHERE("r.repository_id = #{repositoryId}");


        for (int i = 0; i < filters.size(); i++) {
            processRootCapabilityWhere(sql, filters.get(i), i);
        }

        return sql.toString();
    }

    private SQL startQuery() {
        SQL sql = new SQL();
        return sql.SELECT("DISTINCT r.resource_id, r.repository_id, r.id, r.uri").FROM("resource r");
    }

    /**
     * Generate JOIN clause related to a root capability filter
     * @param sql existing sql
     * @param filter root capability filter
     * @param index index of the filter in the filters list
     */
    private void processRootCapabilityJoin(SQL sql, CapabilityFilter filter, int index) {
        String capabilityAlias = createRootCapabilityAlias(index);
        sql.JOIN("capability " + capabilityAlias + " ON " + capabilityAlias + ".resource_id = r.resource_id");
        processAttributesJoin(sql, filter.getAttributes(), capabilityAlias);

        processSubCapabilitiesJoin(sql, filter, capabilityAlias);
    }

    /**
     * Generate WHERE clause related to a root capability filter
     * @param sql existing sql
     * @param filter root capability filter
     * @param index index of the filter in the filters list
     */
    private void processRootCapabilityWhere(SQL sql, CapabilityFilter filter, int index) {
        SimpleStringBuilder builder = new SimpleStringBuilder();
        List<Integer> filterPath = new LinkedList<>();
        filterPath.add(index);
        processCapabilityWhere(builder, createRootCapabilityAlias(index), filter, filterPath);
        sql.WHERE(builder.toString());
    }

    /**
     * Generates portion of the query for subcapability JOIN
     *
     * @param sql already existing portion of the SQL, presumed to have at least SELECT and FROM clauses.
     * @param filter filter for the particular subcapability
     * @param parentCapabilityAlias alias of the subcapability's parent in the #sql query
     */
    private void processSubCapabilitiesJoin(SQL sql, CapabilityFilter filter, String parentCapabilityAlias) {
        String subAlias;
        CapabilityFilter subFilter;
        for (int k = 0; k < filter.getSubFilters().size(); k++) {
            subFilter = filter.getSubFilters().get(k);

            subAlias = parentCapabilityAlias + k;
            sql.INNER_JOIN("capability " + subAlias + " ON " + parentCapabilityAlias + ".capability_id = " + subAlias + ".parent_capability_id");

            processAttributesJoin(sql, subFilter.getAttributes(), subAlias);

            processSubCapabilitiesJoin(sql, subFilter, subAlias);
        }
    }

    /**
     * Generates portion of the query for subcapability WHERE
     *
     * @param sql already existing portion of the SQL, with SELECT and FROM clauses finished
     * @param capabilityAlias alias of the capability for which the condition is to be made
     * @param filter filter describing the condition
     * @param filterPath indexes into the capability lists up to the root starting from this capability filter. It is used
     *                   to give MyBatis information where to look for parameter values
     */
    private void processCapabilityWhere(SimpleStringBuilder sql, String capabilityAlias, CapabilityFilter filter, List<Integer> filterPath) {
        sql.append("(", capabilityAlias, ".namespace = ");
        buildParameterValuePath(sql, filterPath, null, "namespace");
        processAttributesWhere(sql, filter.getAttributes(), filterPath, capabilityAlias);

        for (int i = 0; i < filter.getSubFilters().size(); i++) {
            List<Integer> path = new LinkedList<>(filterPath);
            path.add(i);

            sql.append(" AND ");

            processCapabilityWhere(sql, capabilityAlias + i, filter.getSubFilters().get(i), path);
        }
        sql.append(")");
    }

    /**
     * Generates portion of the query for attribute JOIN
     *
     * @param sql already existing portion of the SQL, presumed to have at least SELECT and FROM clauses.
     * @param attributes list of attribute constraints for the capability with #capabilityAlias
     * @param capabilityAlias alias of the capability to which these attribute constraints belong
     */
    private void processAttributesJoin(SQL sql, List<Attribute<?>> attributes, String capabilityAlias) {
        String attributeAlias;
        for (int j = 0; j < attributes.size(); j++) {
            attributeAlias = createAttributeAlias(capabilityAlias, j);
            sql.INNER_JOIN("capability_attribute " + attributeAlias + " ON " + attributeAlias + ".capability_id = " + capabilityAlias + ".capability_id");
        }
    }

    /**
     * Generates portion of the query for attribute WHEREs
     *
     * @param sql already existing portion of the SQL, with SELECT and FROM clauses finished
     * @param capabilityAlias alias of the capability to which these attribute constraints belong
     * @param attributes list of attribute constraints for the capability with #capabilityAlias
     * @param capabilityFilterIndexes indexes into the capability lists up to the root starting from this capability filter. It is used
     *                   to give MyBatis information where to look for parameter values
     */
    private void processAttributesWhere(SimpleStringBuilder sql, List<Attribute<?>> attributes, List<Integer> capabilityFilterIndexes, String capabilityAlias) {
        String attributeAlias;
        Attribute<?> at;
        for (int j = 0; j < attributes.size(); j++) {
            sql.append(" AND ");
            attributeAlias = createAttributeAlias(capabilityAlias, j);
            at = attributes.get(j);

            sql.append(false, attributeAlias, ".name = ");
            buildParameterValuePath(sql, capabilityFilterIndexes, j, "name");

            sql.append(" AND ");
            evaluateAttribute(sql, at, capabilityFilterIndexes, j, attributeAlias);
        }
    }

    /**
     * Helper method for generating capability attribute alias
     *
     * @param capabilityAlias alias of the capability the attribute belongs to
     * @param index index of the attribute in the filter
     * @return alias for the attribute
     */
    private String createAttributeAlias(String capabilityAlias, int index) {
        return capabilityAlias + PREFIX_ATTRIBUTE + index;
    }

    /**
     * Helper method for generating root capability alias
     *
     * @param index index of the root capability
     * @return alias for the capability
     */
    private String createRootCapabilityAlias(int index) {
        return PREFIX_CAPABILITY + index;
    }

    private void evaluateAttribute(SimpleStringBuilder sb, Attribute<?> attribute, List<Integer> capabilityFilterIndexes, int attributeIndex, String alias) {
        switch (DbAttributeType.fromClass(attribute.getType())) {
            case DOUBLE:
                sb.append(false, alias, ".double_value");
                evaluateOperator(sb, attribute.getOperator(), capabilityFilterIndexes, attributeIndex);
                break;

            case LONG:
                sb.append(false, alias, ".long_value");
                evaluateOperator(sb, attribute.getOperator(), capabilityFilterIndexes, attributeIndex);
                break;

            case STRING:
                sb.append(false, alias, ".string_value");
                evaluateOperator(sb, attribute.getOperator(), capabilityFilterIndexes, attributeIndex);
                break;

            case URI:
                sb.append(false, alias, ".string_value");
                evaluateOperator(sb, attribute.getOperator(), capabilityFilterIndexes, attributeIndex);
                break;

            case VERSION:
                sb.append(false, "(");
                evaluateVersion(sb, alias, attribute.getOperator(), capabilityFilterIndexes, attributeIndex);
                sb.append(false, ")");
                break;

            case BOOLEAN:
                sb.append(false, alias, ".boolean_value");
                evaluateOperator(sb, attribute.getOperator(), capabilityFilterIndexes, attributeIndex);
                break;

            case LIST:
                throw new UnsupportedOperationException();
        }

    }

    private void evaluateOperator(SimpleStringBuilder sb, cz.zcu.kiv.crce.metadata.Operator operator, List<Integer> subFilterPath, int attributeIndex) {
        switch (operator) {
            case EQUAL:
                sb.append(false, " = ");
                buildParameterValuePath(sb, subFilterPath, attributeIndex, "value");
                break;

            case NOT_EQUAL:
                sb.append(false, " <> ");
                buildParameterValuePath(sb, subFilterPath, attributeIndex, "value");
                break;

            case PRESENT:
                sb.append(false, " is not null");
                break;

            case LESS:
                sb.append(false, " < ");
                buildParameterValuePath(sb, subFilterPath, attributeIndex, "value");
                break;

            case LESS_EQUAL:
                sb.append(false, " <= ");
                buildParameterValuePath(sb, subFilterPath, attributeIndex, "value");
                break;

            case GREATER:
                sb.append(false, " > ");
                buildParameterValuePath(sb, subFilterPath, attributeIndex, "value");
                break;

            case GREATER_EQUAL:
                sb.append(false, " >= ");
                buildParameterValuePath(sb, subFilterPath, attributeIndex, "value");
                break;

            default:
                throw new UnsupportedOperationException("Operator " + operator + " is not supported for ... type.");

        }
    }

    private void evaluateVersion(SimpleStringBuilder sb, String alias, cz.zcu.kiv.crce.metadata.Operator operator, List<Integer> subFilterPath, int attributeIndex) {
        switch (operator) {
            case EQUAL:
                sb.append(false, alias, ".version_major_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.major");
                sb.append(false, " AND ", alias, ".version_minor_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.minor");
                sb.append(false, " AND ", alias, ".version_micro_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.micro");
                sb.append(false, " AND ", alias, ".string_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.qualifier");
                break;

            case NOT_EQUAL:
                sb.append(false, alias, ".version_major_value <> "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.major");
                sb.append(false, " OR ", alias, ".version_minor_value <> "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.minor");
                sb.append(false, " OR ", alias, ".version_micro_value <> "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.micro");
                sb.append(false, " OR ", alias, ".string_value <> "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.qualifier");
                break;

            case PRESENT:
                sb.append(false, alias, ".version_major_value is not null");
                sb.append(false, " AND ", alias, ".version_minor_value is not null");
                sb.append(false, " AND ", alias, ".version_micro_value is not null");
                sb.append(false, " AND ", alias, ".string_value is not null");
                break;

            case LESS:
                sb.append(false, alias, ".version_major_value < "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.major");
                sb.append(false, " OR ", alias, ".version_major_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.major"); sb.append(false, " AND (");
                sb.append(false, alias, ".version_minor_value < "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.minor");
                sb.append(false, " OR ", alias, ".version_minor_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.minor"); sb.append(false, " AND (");
                sb.append(false, alias, ".version_micro_value < "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.micro");
                sb.append(false, " OR ", alias, ".version_micro_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.micro"); sb.append(false, " AND (");
                sb.append(false, alias, ".string_value < "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.qualifier");
                sb.append(false, ")))");
                break;

            case LESS_EQUAL:
                sb.append(false, alias, ".version_major_value < "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.major");
                sb.append(false, " OR ", alias, ".version_major_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.major"); sb.append(false, " AND (");
                sb.append(false, alias, ".version_minor_value < "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.minor");
                sb.append(false, " OR ", alias, ".version_minor_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.minor"); sb.append(false, " AND (");
                sb.append(false, alias, ".version_micro_value < "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.micro");
                sb.append(false, " OR ", alias, ".version_micro_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.micro"); sb.append(false, " AND (");
                sb.append(false, alias, ".string_value <= "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.qualifier");
                sb.append(false, ")))");
                break;

            case GREATER:
                sb.append(false, alias, ".version_major_value > "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.major");
                sb.append(false, " OR ", alias, ".version_major_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.major"); sb.append(false, " AND (");
                sb.append(false, alias, ".version_minor_value > "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.minor");
                sb.append(false, " OR ", alias, ".version_minor_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.minor"); sb.append(false, " AND (");
                sb.append(false, alias, ".version_micro_value > "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.micro");
                sb.append(false, " OR ", alias, ".version_micro_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.micro"); sb.append(false, " AND (");
                sb.append(false, alias, ".string_value > "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.qualifier");
                sb.append(false, ")))");
                break;

            case GREATER_EQUAL:
                sb.append(false, alias, ".version_major_value > "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.major");
                sb.append(false, " OR ", alias, ".version_major_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.major"); sb.append(false, " AND (");
                sb.append(false, alias, ".version_minor_value > "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.minor");
                sb.append(false, " OR ", alias, ".version_minor_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.minor"); sb.append(false, " AND (");
                sb.append(false, alias, ".version_micro_value > "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.micro");
                sb.append(false, " OR ", alias, ".version_micro_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.micro"); sb.append(false, " AND (");
                sb.append(false, alias, ".string_value >= "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.qualifier");
                sb.append(false, ")))");
                break;

            case APPROX:
                sb.append(false, alias, ".version_major_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.major");
                sb.append(false, " AND ", alias, ".version_minor_value = "); buildParameterValuePath(sb, subFilterPath, attributeIndex, "value.minor");
                break;

            default:
                throw new UnsupportedOperationException("Operator " + operator + " is not supported for Version type.");

        }
    }

    /**
     * Builds value portion of the query using MyBatis mechanism for specifying values in queries based on parameter objects.
     *
     * The value path expects #ResourceDAOFilter with name filter to be present among the parameters.
     *
     * @param builder string builder used to create the portion of the query
     * @param capabilityFilterIndexes indexes into the capability filter tree (one per level) specifying the path from root to the capability owning the attribute
     * @param attributeIndex index of the attribute within the capability specified by the #capabilityFilterIndexes
     * @param valuePath name of the value column for that particular attribute type
     */
    private void buildParameterValuePath(SimpleStringBuilder builder, List<Integer> capabilityFilterIndexes, Integer attributeIndex, String valuePath) {
        Iterator<Integer> it = capabilityFilterIndexes.iterator();

        builder.append(false, "#{filter.capabilityFilters[", it.next().toString(), "]");
        while(it.hasNext()) {
            builder.append(false, ".subFilters[", it.next().toString(), "]");
        }
        if(attributeIndex != null) {
            builder.append(false, ".attributes[", attributeIndex.toString(), "]");
        }
        builder.append(false, ".", valuePath, "}");
    }

}
