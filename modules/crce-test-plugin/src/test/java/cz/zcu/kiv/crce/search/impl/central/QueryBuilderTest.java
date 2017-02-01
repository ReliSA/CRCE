package cz.zcu.kiv.crce.search.impl.central;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Zdenek Vales
 */
public class QueryBuilderTest {

    @Test
    public void testBuildQuery() {
        String expected1 = "q=g:\"group-id\"+AND+a:\"artifact.some.id\"&core=gav&wt=json";
        String expected2 = "q=g:\"group-id\"+AND+a:\"artifact.some.id\"+AND+v:\"VERSION-1.0.1\"&core=gav&wt=json";
        String expected3 = "q=g:\"group-id\"+AND+a:\"artifact.some.id\"+AND+v:\"VERSION-1.0.2\"&core=gav&wt=json";

        QueryBuilder qb = new QueryBuilder()
                            .addParameter(QueryParam.GROUP_ID, "group-id")
                            .addParameter(QueryParam.ARTIFACT_ID, "artifact.some.id")
                            .addAdditionalParameter(AdditionalQueryParam.CORE, "gav")
                            .addAdditionalParameter(AdditionalQueryParam.SERVICE, "json");
        assertEquals("Wrong query 1!", expected1, qb.toString());

        qb.addParameter(QueryParam.VERSION, "VERSION-1.0.1");
        assertEquals("Wrong query 2!", expected2, qb.toString());

        qb.addParameter(QueryParam.VERSION, "VERSION-1.0.2");
        assertEquals("Wrong query 2!", expected3, qb.toString());
    }

    @Test
    public void testBuildStandardQuery() {
        String expected2 = "q=g:\"group-id\"+AND+a:\"artifact.some.id\"+AND+v:\"VERSION-1.0.1\"&core=gav&wt=json";
        String expected3 = "q=g:\"group-id\"+AND+a:\"artifact.some.id\"+AND+v:\"VERSION-1.0.2\"&core=gav&wt=json";

        QueryBuilder qb = QueryBuilder.createStandard("group-id", "artifact.some.id", "VERSION-1.0.1");
        assertEquals("Wrong query 2!", expected2, qb.toString());

        qb.addParameter(QueryParam.VERSION, "VERSION-1.0.2");
        assertEquals("Wrong query 2!", expected3, qb.toString());
    }
}
