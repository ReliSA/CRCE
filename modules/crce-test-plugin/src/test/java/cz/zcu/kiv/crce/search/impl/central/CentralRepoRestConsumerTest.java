package cz.zcu.kiv.crce.search.impl.central;

import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * @author Zdenek Vales
 */
public class CentralRepoRestConsumerTest {

    @Test
    public void testFindArtifacts() {
        String g = "org.hibernate";
        String a = "hibernate-core";
        QueryBuilder qb = new QueryBuilder()
                .addParameter(QueryParam.GROUP_ID, g)
                .addParameter(QueryParam.ARTIFACT_ID, a)
                .addAdditionalParameter(AdditionalQueryParam.CORE, "cov")
                .addAdditionalParameter(AdditionalQueryParam.SERVICE, "json");

        CentralRepoRestConsumer restConsumer = new CentralRepoRestConsumer();

        Response response = restConsumer.sendRequest(qb);
        assertEquals("Wrong response obtained!", Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
