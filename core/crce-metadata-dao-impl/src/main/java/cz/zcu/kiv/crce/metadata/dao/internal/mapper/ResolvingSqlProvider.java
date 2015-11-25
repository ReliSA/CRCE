package cz.zcu.kiv.crce.metadata.dao.internal.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.dao.internal.helper.SimpleStringBuilder;
import cz.zcu.kiv.crce.metadata.dao.internal.type.DbAttributeType;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class ResolvingSqlProvider {

    private static final Logger logger = LoggerFactory.getLogger(ResolvingSqlProvider.class);

    public String getResourcesAnd(final Map<String, Object> parameter) {
        logger.debug("getResourcesAnd({})", parameter);

        String sql = new SQL() {
            {
                @SuppressWarnings("unchecked")
                List<Attribute<?>> attributes = (List<Attribute<?>>) parameter.get("attributes");

                SELECT("DISTINCT r.resource_id, r.repository_id, r.id, r.uri");
                FROM("resource r");
                INNER_JOIN("capability c ON c.resource_id = r.resource_id");
                INNER_JOIN("capability_attribute ca ON ca.capability_id = c.capability_id");

                for (int i = 0; i < attributes.size(); i++) {
                    String alias = "ca" + i;

                    SimpleStringBuilder sb = new SimpleStringBuilder();

                    sb.append("capability_attribute ", alias);
                    sb.append("  ON ", alias, ".capability_id = ca.capability_id");
                    sb.append("  AND ", alias, ".name = #{attributes[", String.valueOf(i), "].attributeType.name}");

                    sb.append("  AND ");

                    evaluateAttribute(sb, attributes.get(i), i, alias);

                    INNER_JOIN(sb.toString());
                }

                WHERE("c.namespace = #{namespace}");
                WHERE("r.repository_id = #{repositoryId}");
            }
        }.toString();

        logger.trace("getResourceIdsAnd() query:\n{}", sql);

        return sql;
    }

    public String getResourcesOr(final Map<String, Object> parameter) {
        logger.debug("getResourcesOr({})", parameter);

        String sql = new SQL() {
            {
                @SuppressWarnings("unchecked")
                List<Attribute<?>> attributes = (List<Attribute<?>>) parameter.get("attributes");

                SELECT("DISTINCT r.resource_id, r.repository_id, r.id, r.uri");
                FROM("resource r");
                INNER_JOIN("capability c ON c.resource_id = r.resource_id");
                INNER_JOIN("capability_attribute ca ON ca.capability_id = c.capability_id");
                WHERE("c.namespace = #{namespace}");
                WHERE("r.repository_id = #{repositoryId}");

                SimpleStringBuilder sb = new SimpleStringBuilder();
                sb.append("(");

                String alias = "ca";
                for (int i = 0; i < attributes.size(); i++) {
                    if (i > 0) {
                        sb.append("OR");
                    }
                    sb.append(alias, ".name = #{attributes[", String.valueOf(i), "].attributeType.name}");
                    sb.append(false, "  AND ");
                    evaluateAttribute(sb, attributes.get(i), i, alias);
                }

                sb.append(")");

                WHERE(sb.toString());
            }
        }.toString();

        logger.trace("getResourcesOr() query:\n{}", sql);

        return sql;
    }

    private void evaluateAttribute(SimpleStringBuilder sb, Attribute<?> attribute, int index, String alias) {
        switch (DbAttributeType.fromClass(attribute.getAttributeType().getType())) {
            case DOUBLE:
                sb.append(false, alias, ".double_value");
                evaluateOperator(sb, attribute.getOperator(), index);
                break;

            case LONG:
                sb.append(false, alias, ".long_value");
                evaluateOperator(sb, attribute.getOperator(), index);
                break;

            case STRING:
                sb.append(false, alias, ".string_value");
                evaluateOperator(sb, attribute.getOperator(), index);
                break;

            case URI:
                sb.append(false, alias, ".string_value");
                evaluateOperator(sb, attribute.getOperator(), index);
                break;

            case VERSION:
                sb.append(false, "(");
                evaluateVersion(sb, alias, attribute.getOperator(), index);
                sb.append(false, ")");
                break;

            case BOOLEAN:
                sb.append(false, alias, ".boolean_value");
                evaluateOperator(sb, attribute.getOperator(), index);
                break;

            case LIST:
                throw new UnsupportedOperationException();
        }

    }

    private void evaluateOperator(SimpleStringBuilder sb, Operator operator, int index) {
        switch (operator) {
            case EQUAL:
                sb.append(false, " = ", "#{attributes[", String.valueOf(index), "].value}");
                break;

            case NOT_EQUAL:
                sb.append(false, " <> ", "#{attributes[", String.valueOf(index), "].value}");
                break;

            case PRESENT:
                sb.append(false, " is not null");
                break;

            case LESS:
                sb.append(false, " < ", "#{attributes[", String.valueOf(index), "].value}");
                break;

            case LESS_EQUAL:
                sb.append(false, " <= ", "#{attributes[", String.valueOf(index), "].value}");
                break;

            case GREATER:
                sb.append(false, " > ", "#{attributes[", String.valueOf(index), "].value}");
                break;

            case GREATER_EQUAL:
                sb.append(false, " >= ", "#{attributes[", String.valueOf(index), "].value}");
                break;

            default:
                throw new UnsupportedOperationException("Operator " + operator + " is not supported for ... type.");

        }
    }

    private void evaluateVersion(SimpleStringBuilder sb, String alias, Operator operator, int index) {
        switch (operator) {
            case EQUAL:
                sb.append(false, alias, ".version_major_value = #{attributes[", String.valueOf(index), "].value.major}");
                sb.append(false, " AND ", alias, ".version_minor_value = #{attributes[", String.valueOf(index), "].value.minor}");
                sb.append(false, " AND ", alias, ".version_micro_value = #{attributes[", String.valueOf(index), "].value.micro}");
                sb.append(false, " AND ", alias, ".string_value = #{attributes[", String.valueOf(index), "].value.qualifier}");
                break;

            case NOT_EQUAL:
                sb.append(false, alias, ".version_major_value <> #{attributes[", String.valueOf(index), "].value.major}");
                sb.append(false, " OR ", alias, ".version_minor_value <> #{attributes[", String.valueOf(index), "].value.minor}");
                sb.append(false, " OR ", alias, ".version_micro_value <> #{attributes[", String.valueOf(index), "].value.micro}");
                sb.append(false, " OR ", alias, ".string_value <> #{attributes[", String.valueOf(index), "].value.qualifier}");
                break;

            case PRESENT:
                sb.append(false, alias, ".version_major_value is not null");
                sb.append(false, " AND ", alias, ".version_minor_value is not null");
                sb.append(false, " AND ", alias, ".version_micro_value is not null");
                sb.append(false, " AND ", alias, ".string_value is not null");
                break;

            case LESS:
                sb.append(false, alias, ".version_major_value < #{attributes[", String.valueOf(index), "].value.major}");
                sb.append(false, " OR ", alias, ".version_major_value = #{attributes[", String.valueOf(index), "].value.major} AND (");
                sb.append(false, alias, ".version_minor_value < #{attributes[", String.valueOf(index), "].value.minor}");
                sb.append(false, " OR ", alias, ".version_minor_value = #{attributes[", String.valueOf(index), "].value.minor} AND (");
                sb.append(false, alias, ".version_micro_value < #{attributes[", String.valueOf(index), "].value.micro}");
                sb.append(false, " OR ", alias, ".version_micro_value = #{attributes[", String.valueOf(index), "].value.micro} AND (");
                sb.append(false, alias, ".string_value < #{attributes[", String.valueOf(index), "].value.qualifier}");
                sb.append(false, ")))");
                break;

            case LESS_EQUAL:
                sb.append(false, alias, ".version_major_value < #{attributes[", String.valueOf(index), "].value.major}");
                sb.append(false, " OR ", alias, ".version_major_value = #{attributes[", String.valueOf(index), "].value.major} AND (");
                sb.append(false, alias, ".version_minor_value < #{attributes[", String.valueOf(index), "].value.minor}");
                sb.append(false, " OR ", alias, ".version_minor_value = #{attributes[", String.valueOf(index), "].value.minor} AND (");
                sb.append(false, alias, ".version_micro_value < #{attributes[", String.valueOf(index), "].value.micro}");
                sb.append(false, " OR ", alias, ".version_micro_value = #{attributes[", String.valueOf(index), "].value.micro} AND (");
                sb.append(false, alias, ".string_value <= #{attributes[", String.valueOf(index), "].value.qualifier}");
                sb.append(false, ")))");
                break;

            case GREATER:
                sb.append(false, alias, ".version_major_value > #{attributes[", String.valueOf(index), "].value.major}");
                sb.append(false, " OR ", alias, ".version_major_value = #{attributes[", String.valueOf(index), "].value.major} AND (");
                sb.append(false, alias, ".version_minor_value > #{attributes[", String.valueOf(index), "].value.minor}");
                sb.append(false, " OR ", alias, ".version_minor_value = #{attributes[", String.valueOf(index), "].value.minor} AND (");
                sb.append(false, alias, ".version_micro_value > #{attributes[", String.valueOf(index), "].value.micro}");
                sb.append(false, " OR ", alias, ".version_micro_value = #{attributes[", String.valueOf(index), "].value.micro} AND (");
                sb.append(false, alias, ".string_value > #{attributes[", String.valueOf(index), "].value.qualifier}");
                sb.append(false, ")))");
                break;

            case GREATER_EQUAL:
                sb.append(false, alias, ".version_major_value > #{attributes[", String.valueOf(index), "].value.major}");
                sb.append(false, " OR ", alias, ".version_major_value = #{attributes[", String.valueOf(index), "].value.major} AND (");
                sb.append(false, alias, ".version_minor_value > #{attributes[", String.valueOf(index), "].value.minor}");
                sb.append(false, " OR ", alias, ".version_minor_value = #{attributes[", String.valueOf(index), "].value.minor} AND (");
                sb.append(false, alias, ".version_micro_value > #{attributes[", String.valueOf(index), "].value.micro}");
                sb.append(false, " OR ", alias, ".version_micro_value = #{attributes[", String.valueOf(index), "].value.micro} AND (");
                sb.append(false, alias, ".string_value >= #{attributes[", String.valueOf(index), "].value.qualifier}");
                sb.append(false, ")))");
                break;

            case APPROX:
                sb.append(false, alias, ".version_major_value = #{attributes[", String.valueOf(index), "].value.major}");
                sb.append(false, " AND ", alias, ".version_minor_value = #{attributes[", String.valueOf(index), "].value.minor}");
                break;

            default:
                throw new UnsupportedOperationException("Operator " + operator + " is not supported for Version type.");

        }

    }
}
