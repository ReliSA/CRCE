package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.util.Set;

import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointRequestBody;
import cz.zcu.kiv.crce.rest.client.indexer.config.Header;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointParameter;

public class VarEndpointData extends Endpoint {

    /**
     *
     */
    private static final long serialVersionUID = 7312540085684152917L;

    public VarEndpointData(String baseUrl, String path, HttpMethod httpMethod,
                           Set<EndpointRequestBody> requestBodies, Set<EndpointRequestBody> expectedResponses,
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

    public VarEndpointData() {
    };
}
