package cz.zcu.kiv.crce.search.impl.central;

import cz.zcu.kiv.crce.search.impl.central.json.CentralRepoJsonResponse;
import cz.zcu.kiv.crce.search.impl.central.json.JsonArtifactDescriptor;
import cz.zcu.kiv.crce.search.impl.central.json.JsonResponseBody;
import cz.zcu.kiv.crce.search.impl.central.json.JsonResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This class will fetch actual calls to rest api.
 *
 * @author Zdenek Vales
 */
public class CentralRepoRestConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CentralRepoRestConsumer.class);

    /**
     * Base url for query and additional parameters to be appended.
     */
    public static final String REPO_URL = "http://search.maven.org/solrsearch/select";

    public CentralRepoRestConsumer() {
        System.setProperty("javax.xml.bind.context.factory","org.eclipse.persistence.jaxb.JAXBContextFactory");
    }

    /**
     * Calls a REST service and returns the response.
     * @param queryBuilder Query to be appended to the url.
     * @return Response
     */
    public Response sendRequest(QueryBuilder queryBuilder) {

        Client client = ClientBuilder.newClient()
                .register(CentralRepoJsonResponse.class)
                .register(JsonResponseHeader.class)
                .register(JsonResponseBody.class)
                .register(JsonArtifactDescriptor.class);

        //todo: fix this
        logger.debug("Sending request to: "+REPO_URL+"?"+queryBuilder.toString());
        WebTarget resource = client.target(queryBuilder.getUrlTemplate(REPO_URL)).resolveTemplatesFromEncoded(queryBuilder.asUrlParameters());
        return resource.request(MediaType.APPLICATION_JSON_TYPE).get();
    }
}
