package cz.zcu.kiv.crce.test.plugin2.search.impl.central.rest;

import cz.zcu.kiv.crce.test.plugin2.search.impl.central.rest.json.CentralRepoJsonResponse;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
                .addAdditionalParameter(AdditionalQueryParam.CORE, "gav")
                .addAdditionalParameter(AdditionalQueryParam.SERVICE, "json");

        CentralRepoRestConsumer restConsumer = new CentralRepoRestConsumer();

        Response response = restConsumer.sendRequest(qb);
        assertEquals("Wrong response obtained for url: "+qb.toString()+" !", Response.Status.OK.getStatusCode(), response.getStatus());

        CentralRepoJsonResponse jsonResponse = response.readEntity(CentralRepoJsonResponse.class);
        assertTrue("No artifcat found!", jsonResponse.getResponse().getNumFound() > 0);
        assertTrue("No artifacts loaded!", jsonResponse.getResponse().getDocs().length > 0);
    }
}
