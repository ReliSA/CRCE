package cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest;

import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.json.CentralRepoJsonResponse;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.json.JsonArtifactDescriptor;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.json.JsonResponseBody;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.central.rest.json.JsonResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
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
     * Calls a REST API of the mvn repo and if the response is OK and json is successfully parsed,
     * returns the parsed object.
     * @param queryBuilder Query specifiaction.
     * @return Parsed json or null.
     */
    public CentralRepoJsonResponse getJson(QueryBuilder queryBuilder) throws ServerErrorException {
        // get response
        Response response = sendRequest(queryBuilder);
        if(response == null ) {
            logger.error("Response is null!");
//            System.out.println("Response is null!");
            return null;
        } else if(response.getStatus() != Response.Status.OK.getStatusCode()) {
            logger.error("Response not OK! Status code: "+response.getStatus());
//            System.out.println("Response not OK! Status code: "+response.getStatus()+"\nQuery: "+queryBuilder.toString());
            throw new ServerErrorException(response.getStatus());
        }

        // get parsed json
        CentralRepoJsonResponse jsonResponse = null;
        try {
            jsonResponse = response.readEntity(CentralRepoJsonResponse.class);
        } catch (ProcessingException ex) {
            logger.error("Error while processing the data from response! "+ex.getMessage());
//            System.out.println("Error while processing the data from response! "+ex.getMessage());
            return null;
        } catch (IllegalStateException ex) {
            logger.error("Error while trying to read the data from response! "+ex.getMessage());
//            System.out.println("Error while trying to read the data from response! "+ex.getMessage());
            return null;
        } catch (Exception ex) {
            logger.error("Unexpected exception: "+ex.getMessage());
//            System.out.println("Unexpected exception: "+ex.getMessage());
            return null;
        }

        return jsonResponse;
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

        logger.debug("Sending request to: "+REPO_URL+"?"+queryBuilder.toString());
        WebTarget resource = client.target(queryBuilder.getUrlTemplate(REPO_URL)).resolveTemplatesFromEncoded(queryBuilder.asUrlParameters());
        return resource.request(MediaType.APPLICATION_JSON_TYPE).get();
    }
}
