package cz.zcu.kiv.crce.search.impl.central;

/**
 * Additional query parameters. Sucha as number of returned rows and service specification.
 *
 * @author Zdenek Vales
 */
public enum AdditionalQueryParam {

    /**
     * Basically only working value is 'cov'
     */
    CORE("core"),

    /**
     * Number of returned rows. If the specified value is not a number, server will return error 500.
     * If this param is specified, but empty, only few artifacts are returned.
     */
    ROWS("rows"),

    /**
     * Starting index of the returned artifact array.
     * If the row parameter is specified, this can be used for pagination.
     */
    START("start"),

    /**
     * Service specification.
     * Use 'json' for JSON and 'xml' for XML format.
     */
    SERVICE("wt");

    public final String paramName;

    AdditionalQueryParam(String paramName) {
        this.paramName = paramName;
    }
}
