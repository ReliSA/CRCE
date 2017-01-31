package cz.zcu.kiv.crce.search.impl.central;

/**
 * Parameter names for query builder.
 *
 * @author Zdenek Vales
 */
public enum QueryParam {

    ARTIFACT_ID("a"),
    GROUP_ID("g"),
    VERSION("v");

    public final String paramName;

    QueryParam(String paramName) {
        this.paramName = paramName;
    }
}
