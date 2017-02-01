package cz.zcu.kiv.crce.test.plugin.search.impl.central;

import java.util.HashMap;
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
//todo: delete unused methods
public class QueryBuilder {

    public static final String AND_CONCAT = "+AND+";

    /**
     * Name of the query parameter in the url.
     */
    public static final String QUERY_PARAM_NAME = "q";

    /**
     * Prepares a quer builder with specified artifact parameters and standard additional parameters.
     * @param groupId Group id.
     * @param artifactId Artifact id.
     * @param version Version.
     * @return Standard query builder.
     */
    public static QueryBuilder createStandard(String groupId, String artifactId, String version) {
        return new QueryBuilder()
                .addParameter(QueryParam.GROUP_ID, groupId)
                .addParameter(QueryParam.ARTIFACT_ID, artifactId)
                .addParameter(QueryParam.VERSION, version)
                .addStandardAdditionalParameters();
    }

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
     * Adds core=gav and wt=json parameters.
     * @return This query builder.
     */
    public QueryBuilder addStandardAdditionalParameters() {
        return addAdditionalParameter(AdditionalQueryParam.CORE, "gav").addAdditionalParameter(AdditionalQueryParam.SERVICE,"json");
    }

    /**
     * Returns the query as a string.
     * @return Query with format param:"param-value"+AND+param:"param-value"...
     */
    public String queryToString() {
        if(this.query == null || this.query.isEmpty()) {
            return "";
        }
        // build the query
        StringBuilder sb = new StringBuilder();
        for (String paramName : query.keySet()) {
            sb.append(paramName);
            sb.append(":\"");
            sb.append(query.get(paramName));
            sb.append("\"");
            sb.append(AND_CONCAT);
        }

        // remove the last AND_CONCAT
        sb.delete(sb.length()-AND_CONCAT.length(),sb.length());
        return sb.toString();
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
        // build the query
        StringBuilder sb = new StringBuilder();
        String q = queryToString();
        if(!q.isEmpty()) {
            sb.append(QUERY_PARAM_NAME);
            sb.append("=");
            sb.append(q);
        }

        // append additional parameters
        if(additionalParams == null) {
            return sb.toString();
        }
        for(String paramName : additionalParams.keySet()) {
            sb.append("&");
            sb.append(paramName);
            sb.append("=");
            sb.append(additionalParams.get(paramName));
        }

        return sb.toString();
    }

    /**
     * Returns query and additional parameters as a map of parameters
     * suitable for http client.
     * @return
     */
    public Map<String, Object> asUrlParameters() {
        Map<String, Object> tmp = new HashMap<>();

        if(query != null) {
//            tmp.put(QUERY_PARAM_NAME, queryToString());
            for(String paramName : query.keySet()) {
                tmp.put(paramName, query.get(paramName));
            }
        }

        if(additionalParams != null) {
            for(String paramName : additionalParams.keySet()) {
                tmp.put(paramName, additionalParams.get(paramName));
            }
        }

        return tmp;
    }

    /**
     * Converts the query to template. The format is
     * paramName:{paramName}+AND+paramName:{paramName}...
     *
     * To be filled with values from asUrlParameters() method.
     *
     * @return A template for query.
     */
    public String getQueryTemplate() {
        StringBuilder sb = new StringBuilder();

        if(query == null || query.isEmpty()) {
            return sb.toString();
        }

        for (String paramName : query.keySet()) {
            sb.append(paramName);

            //todo: figure out how to properly use quotes
            sb.append(":{");
            sb.append(paramName);
            sb.append("}");
            sb.append(AND_CONCAT);
        }
        // remove the last AND_CONCAT
        sb.delete(sb.length()-AND_CONCAT.length(),sb.length());

        return sb.toString();
    }

    /**
     * Creates a template for the whole url. This will be filled with values from
     * asUrlParameters() method. The format is:
     *
     * rootUrl?q=qParamName:{qParamName}+AND+...&additionalParamName={additionalParamName}&additionalParamName={additionalParamName}....
     *
     * @param rootUrl Root url.
     * @return Query template
     */
    public String getUrlTemplate(String rootUrl) {
        StringBuilder sb = new StringBuilder(rootUrl);
        sb.append("?");
        if(query != null && !query.isEmpty()) {
            sb.append(QUERY_PARAM_NAME).append("=").append(getQueryTemplate());
        }

        if(additionalParams != null && !additionalParams.isEmpty()) {
            for(String paramName : additionalParams.keySet()) {
                sb.append("&").append(paramName).append("={").append(paramName).append("}");
            }
        }

        return sb.toString();
    }
}
