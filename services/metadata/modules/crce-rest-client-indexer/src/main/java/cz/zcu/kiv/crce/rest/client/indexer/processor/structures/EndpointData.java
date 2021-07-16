package cz.zcu.kiv.crce.rest.client.indexer.processor.structures;

import java.util.Set;

import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointBody;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Header;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HttpMethod;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointParameter;

public class EndpointData extends Endpoint {

    /**
     *
     */
    private static final long serialVersionUID = 7312540085684152917L;

    /**
     * EndpointData is holder (some sepecified object see: config files) for data of Endpoint
     * @param baseUrl http://www.zcu.cz
     * @param path /some/path
     * @param httpMethod GET | POST ...
     * @param requestBodies Body of request (POST method)
     * @param expectedResponses Expected response from service
     * @param parameters Parameters for request
     * @param produces Producing JSON | XML etc. (header params)
     * @param consumes Consuming JSON | XML etx. (header params)
     */
    public EndpointData(String baseUrl, String path, HttpMethod httpMethod,
            Set<EndpointBody> requestBodies, Set<EndpointBody> expectedResponses,
            Set<EndpointParameter> parameters, Header produces, Header consumes) {
        this.baseUrl = baseUrl;
        this.path = path;
        this.httpMethods.add(httpMethod);
        this.requestBodies = requestBodies;
        this.expectedResponses = expectedResponses;
        this.parameters = parameters;
        this.produces.add(produces);
        this.consumes.add(consumes);
    }

    public EndpointData() {};
}
