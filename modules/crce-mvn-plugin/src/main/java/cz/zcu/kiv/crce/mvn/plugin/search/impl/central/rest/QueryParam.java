package cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest;

/**
 * Parameter names for query builder.
 *
 * @author Zdenek Vales
 */
public enum QueryParam {

    CLASS_NAME("fc"),
    ARTIFACT_ID("a"),
    GROUP_ID("g"),
    VERSION("v");


    public final String paramName;

    QueryParam(String paramName) {
        this.paramName = paramName;
    }
}
