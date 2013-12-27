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

                    sb.append("  AND ", alias);

                    evaluateAttribute(sb, attributes.get(i), i);

                    INNER_JOIN(sb.toString());
                }

                WHERE("c.namespace = #{namespace}");
            }
        }.toString();

        logger.trace("getResourceIdsAnd() query:\n{}", sql);

        return sql;
    }

    private void evaluateAttribute(SimpleStringBuilder sb, Attribute<?> attribute, int index) {
        switch (DbAttributeType.fromClass(attribute.getAttributeType().getType())) {
            case DOUBLE:
                sb.append(false, ".double_value");
                evaluateOperator(sb, attribute.getOperator(), index);
                break;

            case LONG:
                sb.append(false, ".long_value");
                evaluateOperator(sb, attribute.getOperator(), index);
                break;

            case STRING:
                sb.append(false, ".string_value");
                evaluateOperator(sb, attribute.getOperator(), index);
                break;

            case URI:
                sb.append(false, ".string_value");
                evaluateOperator(sb, attribute.getOperator(), index);
                break;

            case VERSION:
                throw new UnsupportedOperationException();

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

    public String getResourcesOr(Map<String, Object> parameter) {
        throw new UnsupportedOperationException();
    }

}
