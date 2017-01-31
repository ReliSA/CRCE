package cz.zcu.kiv.crce.search.impl.central;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple builder for creating queries for searching the central maven repo.
 * The queries are really simple: g:"groupId" AND a:"artifactId" ....
 *
 * Also adds additional parameters after the query - the query itself is just an url parameter.
 *
 * @author Zdenek Vales
 */
public class QueryBuilder {

    public static final String AND_CONCAT = "+AND+";

    /**
     * A map which will hold the query.
     * Key represents the param name, the value is param value.
     */
    private Map<String, String> query;

    /**
     * A map wth additional params. Those will be added after the query.
     */
    private Map<String, String> additionalParams;

    public QueryBuilder() {
        this.query = new LinkedHashMap<>();
        this.additionalParams = new LinkedHashMap<>();
    }

    /**
     * Adds a parameter to the query. If the same parameter is already in the query,
     * it will be overwritten. The parameters in the generated query will be insertion-ordered.
     * @param parameter Parameter.
     * @param value Actual value.
     * @return This query builder.
     */
    public QueryBuilder addParameter(QueryParam parameter, String value) {
        query.put(parameter.paramName, value);
        return this;
    }

    /**
     * Adds an additional parameter. If the same parameter is already in the query,
     * it will be overwritten. The parameters in the generated query will be insertion-ordered.
     * @param parameter Parameter.
     * @param value Actual value.
     * @return This query builder.
     */
    public QueryBuilder addAdditionalParameter(AdditionalQueryParam parameter, String value) {
        additionalParams.put(parameter.paramName, value);
        return this;
    }

    /**
     * Returns the query which can be append to the url.
     * paramName:"paramValue"+AND+paramName:"paramValue"...
     *
     * Additional parameters will be appended to the query so that the whole
     * string will be formatted as:
     * q=[query]&[additionalParam1]&[additionalParam2]...
     */
    @Override
    public String toString() {
        if(this.query == null || this.query.isEmpty()) {
            return "";
        }

        // build the query
        StringBuilder sb = new StringBuilder();
        sb.append("q=");
        for (String paramName : query.keySet()) {
            sb.append(paramName);
            sb.append(":\"");
            sb.append(query.get(paramName));
            sb.append("\"");
            sb.append(AND_CONCAT);
        }

        // remove the last AND_CONCAT
        sb.delete(sb.length()-AND_CONCAT.length(),sb.length());

        // append additional parameters
        for(String paramName : additionalParams.keySet()) {
            sb.append("&");
            sb.append(paramName);
            sb.append("=");
            sb.append(additionalParams.get(paramName));
        }

        return sb.toString();
    }
}
