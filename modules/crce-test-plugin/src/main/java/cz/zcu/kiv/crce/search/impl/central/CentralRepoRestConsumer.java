package cz.zcu.kiv.crce.search.impl.central;

import cz.zcu.kiv.crce.search.impl.central.json.CentralRepoJsonResponse;
import cz.zcu.kiv.crce.search.impl.central.json.JsonArtifactDescriptor;
import cz.zcu.kiv.crce.search.impl.central.json.JsonResponseBody;
import cz.zcu.kiv.crce.search.impl.central.json.JsonResponseHeader;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;

/**
 * This class will fetch actual calls to rest api.
 *
 * @author Zdenek Vales
 */
public class CentralRepoRestConsumer {

    /**
     * Base url for query and additional parameters to be appended.
     */
    public static final String REPO_URL = "http://http://search.maven.org/solrsearch/select";

    public static final String ADDITIONAL_PARAMS = "&core=gav&rows=200&wt=json";

    /**
     * Creates a request to REST service.
     * @param queryBuilder
     * @return
     */
    public Builder getRequest(QueryBuilder queryBuilder) {



        Client client = ClientBuilder.newClient()
                .register(CentralRepoJsonResponse.class)
                .register(JsonResponseHeader.class)
                .register(JsonResponseBody.class)
                .register(JsonArtifactDescriptor.class);

//        WebTarget resource = client.target();
        return null;
    }
}
