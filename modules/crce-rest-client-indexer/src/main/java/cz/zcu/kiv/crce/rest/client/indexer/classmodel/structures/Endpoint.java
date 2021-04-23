package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting.BytecodeDescriptorsProcessor;
import cz.zcu.kiv.crce.rest.client.indexer.config.Header;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.HeaderTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ToStringTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.UrlTools;

public class Endpoint implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5099598028525455821L;


    protected String path;
    protected String baseUrl;
    protected Set<HttpMethod> httpMethods;
    protected Set<EndpointParameter> parameters;
    protected Set<EndpointRequestBody> requestBodies;
    protected Set<EndpointRequestBody> expectedResponses;
    protected Set<Header> produces;
    protected Set<Header> consumes;


    public Endpoint(String path, Set<HttpMethod> httpMethods) {
        this();
        this.setPath(path);
        this.httpMethods = httpMethods;
    }

    public Endpoint(String baseUrl, String path, Set<HttpMethod> httpMethods,
                    Set<EndpointRequestBody> requestBodies, Set<EndpointRequestBody> expectedResponses,
                    Set<EndpointParameter> parameters, Set<Header> produces, Set<Header> consumes) {
        this(path, httpMethods, requestBodies, expectedResponses, parameters, produces, consumes);
        this.baseUrl = baseUrl;
    }

    public Endpoint(String path, Set<HttpMethod> httpMethods,
                    Set<EndpointRequestBody> requestBodies, Set<EndpointRequestBody> expectedResponses,
                    Set<EndpointParameter> parameters, Set<Header> produces, Set<Header> consumes) {
        this.httpMethods = httpMethods;
        this.requestBodies = requestBodies;
        this.expectedResponses = expectedResponses;
        this.parameters = parameters;
        this.produces = produces;
        this.consumes = consumes;
        this.setPath(path);
    }

    public Endpoint(String path, HttpMethod type) {
        this();
        this.setPath(path);
        httpMethods.add(type);
    }

    public Endpoint() {
        this.httpMethods = new HashSet<>();
        this.requestBodies = new HashSet<>();
        this.expectedResponses = new HashSet<>();
        this.parameters = new HashSet<>();
        this.produces = new HashSet<>();
        this.consumes = new HashSet<>();
    }

    public enum HttpMethod {
        POST, GET, PUT, PATCH, DELETE, HEAD, OPTIONS, TRACE;
    }

    public Set<HttpMethod> getHttpMethods() {
        return this.httpMethods;
    }

    public Set<EndpointRequestBody> getRequestBodies() {
        return this.requestBodies;
    }

    public Endpoint addHttpMethod(HttpMethod type) {
        this.httpMethods.add(type);
        return this;
    }

    public Endpoint addParameter(EndpointParameter param) {
        if (param.getCategory() == ParameterCategory.BODY) {
            addRequestBody(new EndpointRequestBody(param.getDataType(),
                    BytecodeDescriptorsProcessor.isArrayOrCollection(param.getDataType())));
        } else {
            parameters.add(param);
        }
        return this;
    }

    public Set<EndpointParameter> getParameters() {
        return this.parameters;
    }

    public Endpoint addRequestBody(EndpointRequestBody body) {
        this.requestBodies.add(body);
        return this;
    }

    public Endpoint addExpectedResponse(EndpointRequestBody response) {
        this.expectedResponses.add(response);
        return this;
    }

    public Set<EndpointRequestBody> getExpectedResponses() {
        return this.expectedResponses;
    }

    /**
     * 
     * @param baseUrl
     */
    public Endpoint setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public void addHeader(Header header) {
        if (HeaderTools.isConsumingType(header.getName())) {
            this.addConsumes(header);
        } else if (HeaderTools.isProducingType(header.getName())) {
            this.addProduces(header);
        } else {
            // some other header attrs
            this.addParameter(new EndpointParameter(null, header, false, ParameterCategory.HEADER));
        }
    }

    public void merge(Endpoint endpoint) {
        String newPath = endpoint.getPath() != null ? endpoint.getPath() : this.path;
        String newBaseUrl = endpoint.getBaseUrl() != null ? endpoint.getBaseUrl() : this.baseUrl;
        this.setPath(newPath);
        this.setBaseUrl(newBaseUrl);
        this.httpMethods.addAll(endpoint.getHttpMethods());
        this.requestBodies.addAll(endpoint.getRequestBodies());
        this.expectedResponses.addAll(endpoint.getExpectedResponses());
        this.produces.addAll(endpoint.getProduces());
        this.consumes.addAll(endpoint.getConsumes());
        this.parameters.addAll(endpoint.getParameters());
    }

    public boolean sEquals(Endpoint endpoint) {
        return super.equals(endpoint);
    }


    public boolean equals(Endpoint endpoint) {
        final boolean httpMethodEq = httpMethods.equals(endpoint.getHttpMethods());
        final boolean reqBodiesEq = requestBodies.containsAll(endpoint.getRequestBodies());
        final boolean expectedResponsesEq =
                expectedResponses.containsAll(endpoint.getExpectedResponses())
                        && endpoint.getExpectedResponses().containsAll(expectedResponses);
        final boolean parametersEq = parameters.containsAll(endpoint.getParameters())
                && endpoint.getParameters().containsAll(parameters);
        final boolean consumesEq = consumes.containsAll(endpoint.getConsumes())
                && endpoint.getConsumes().containsAll(consumes);
        final boolean producesEq = produces.containsAll(endpoint.getProduces())
                && endpoint.getProduces().containsAll(produces);
        return httpMethodEq && reqBodiesEq && expectedResponsesEq && parametersEq && consumesEq
                && producesEq;
    }


    @Override
    public String toString() {
        return "{ \"baseUrl\": " + ToStringTools.stringToString(baseUrl) + ", \"path\": "
                + ToStringTools.stringToString(path) + ", \"httpMethods\": "
                + ToStringTools.setToString(httpMethods) + ", \"requestBodies\": "
                + ToStringTools.setToString(requestBodies) + ", \"responses\": "
                + ToStringTools.setToString(expectedResponses) + ", \"parameters\": "
                + ToStringTools.setToString(parameters) + ", \"produces\": "
                + ToStringTools.setToString(produces) + ", \"consumes\": "
                + ToStringTools.setToString(consumes) + " }";
    }



    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getUrl() {
        if (path == null && baseUrl == null) {
            return null;
        }

        if (path == null) {
            return baseUrl;
        }

        if (baseUrl == null) {
            return path;
        }

        return baseUrl + path;
    }

    /**
     * @param path the path to set
     */
    public Endpoint setPath(String path) {

        if (path != null) {
            if (!path.startsWith("http")) {
                this.path = path.replace("//", "/");
            }
            String query = UrlTools.getQuery(path);
            String queryMatrix = UrlTools.getMatrixQuery(path);
            if (query != null) {
                parameters.add(new EndpointParameter(null, query, false, ParameterCategory.QUERY));
            }
            if (queryMatrix != null) {
                parameters.add(
                        new EndpointParameter(null, queryMatrix, false, ParameterCategory.MATRIX));
            }
        }

        return this;
    }

    /**
     * @return the produces
     */
    public Set<Header> getProduces() {
        return produces;
    }

    /**
     * @param produces the produces to set
     */
    public Endpoint addProduces(Header produces) {
        this.produces.add(produces);
        return this;
    }

    /**
     * @return the consumes
     */
    public Set<Header> getConsumes() {
        return consumes;
    }

    /**
     * @param consumes the consumes to set
     */
    public Endpoint setConsumes(Set<Header> consumes) {
        this.consumes = consumes;
        return this;
    }

    /**
     * @param consumes the consumes to set
     */
    public Endpoint addConsumes(Header consumes) {
        this.consumes.add(consumes);
        return this;
    }

}
