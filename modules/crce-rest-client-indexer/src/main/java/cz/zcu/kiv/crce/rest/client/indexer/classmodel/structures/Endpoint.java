package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting.BytecodeDescriptorsProcessor;
import cz.zcu.kiv.crce.rest.client.indexer.config.Header;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.HeaderTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ToStringTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.UrlTools;

/**
 * @author Tomáš Ballák
 */
public class Endpoint implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5099598028525455821L;

    private static final Logger logger = LoggerFactory.getLogger(Endpoint.class);

    protected String path;
    protected String baseUrl;
    protected Set<HttpMethod> httpMethods;
    protected Set<EndpointParameter> parameters;
    protected Set<EndpointRequestBody> requestBodies;
    protected Set<EndpointRequestBody> expectedResponses;
    protected Set<Header> produces;
    protected Set<Header> consumes;

    /**
     * 
     * @param path Path
     * @param httpMethods Set of http methods
     */
    public Endpoint(String path, Set<HttpMethod> httpMethods) {
        this();
        this.setPath(path);
        this.httpMethods = httpMethods;
    }

    /**
     * 
     * @param baseUrl http://www.zcu.cz
     * @param path /some/path
     * @param httpMethod GET | POST ...
     * @param requestBodies Body of request (POST method)
     * @param expectedResponses Expected response from service
     * @param parameters Parameters for request
     * @param produces Producing JSON | XML etc. (header params)
     * @param consumes Consuming JSON | XML etx. (header params)
    */
    public Endpoint(String baseUrl, String path, Set<HttpMethod> httpMethods,
            Set<EndpointRequestBody> requestBodies, Set<EndpointRequestBody> expectedResponses,
            Set<EndpointParameter> parameters, Set<Header> produces, Set<Header> consumes) {
        this(path, httpMethods, requestBodies, expectedResponses, parameters, produces, consumes);
        this.setBaseUrl(baseUrl);
    }

    /**
     * 
     * @param path /some/path
     * @param httpMethod GET | POST ...
     * @param requestBodies Body of request (POST method)
     * @param expectedResponses Expected response from service
     * @param parameters Parameters for request
     * @param produces Producing JSON | XML etc. (header params)
     * @param consumes Consuming JSON | XML etx. (header params)
     */
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

    /**
     * 
     * @param path /some/path
     * @param httpMethod GET | POST ...
     */
    public Endpoint(String path, HttpMethod httpMethod) {
        this();
        this.setPath(path);
        httpMethods.add(httpMethod);
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

    /**
     * 
     * @return Get all http methods
     */
    public Set<HttpMethod> getHttpMethods() {
        return this.httpMethods;
    }

    /**
     * 
     * @return Get all request bodies
     */
    public Set<EndpointRequestBody> getRequestBodies() {
        return this.requestBodies;
    }

    /**
     * Adds new http method
     * @param httpMethod New http method
     * @return this
     */
    public Endpoint addHttpMethod(HttpMethod httpMethod) {
        this.httpMethods.add(httpMethod);
        return this;
    }

    /**
     * Adds parameter to endpoint
     * @param param Endpoint parameter
     * @return this
     */
    public Endpoint addParameter(EndpointParameter param) {
        if (param.getCategory() == ParameterCategory.BODY) {
            addRequestBody(new EndpointRequestBody(param.getDataType(),
                    BytecodeDescriptorsProcessor.isArrayOrCollection(param.getDataType())));
        } else {
            parameters.add(param);
        }
        return this;
    }

    /**
     * 
     * @return Parameters
     */
    public Set<EndpointParameter> getParameters() {
        return this.parameters;
    }

    /**
     * 
     * @param body Adds request body
     * @return this
     */
    public Endpoint addRequestBody(EndpointRequestBody body) {
        this.requestBodies.add(body);
        return this;
    }

    /**
     * 
     * @param response Adds new response
     * @return this
     */
    public Endpoint addExpectedResponse(EndpointRequestBody response) {
        this.expectedResponses.add(response);
        return this;
    }

    /**
     * 
     * @return Endpoint request bodies
     */
    public Set<EndpointRequestBody> getExpectedResponses() {
        return this.expectedResponses;
    }

    /**
     * Sets new baseurl and tries to retrieve path and params from it
     * @param baseUrl Base url
     * @return this
     */
    public Endpoint setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        if (baseUrl != null && baseUrl.length() > 0 && baseUrl.startsWith("http")) {
            URL url;
            try {
                url = new URL(baseUrl);
                if (this.path == null) {
                    this.path = url.getFile();
                }
                this.baseUrl = url.getProtocol() + "://" + url.getHost()
                        + (url.getPort() > 0 ? (":" + url.getPort()) : "");
                String query = UrlTools.getQuery(path);
                String queryMatrix = UrlTools.getMatrixQuery(path);
                if (query != null) {
                    parameters.add(
                            new EndpointParameter(null, query, false, ParameterCategory.QUERY));
                }
                if (queryMatrix != null) {
                    parameters.add(new EndpointParameter(null, queryMatrix, false,
                            ParameterCategory.MATRIX));
                }
            } catch (MalformedURLException e) {
                logger.error("Wrong type of Base URL=" + baseUrl);
            }

        }
        return this;
    }

    /**
     * Adds new header to endpoint
     * @param header New header
     */
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

    /**
     * 
     * @param endpoint Merges incoming endpoint into this
     */
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

    /**
     * Compares endpoints if they are equlas
     * @param endpoint Endpoit for comparison
     * @return same or not
     */
    public boolean sEquals(Endpoint endpoint) {
        return super.equals(endpoint);
    }

    /**
     * Compares endpoints if they are equlas
     * @param endpoint Endpoit for comparison
     * @return same or not
     */
    public boolean equals(Endpoint endpoint) {
        final boolean httpMethodEq = httpMethods.equals(endpoint.getHttpMethods());
        /*         final boolean reqBodiesEq = requestBodies.containsAll(endpoint.getRequestBodies()); */
        /*         final boolean expectedResponsesEq =
                expectedResponses.containsAll(endpoint.getExpectedResponses())
                        && endpoint.getExpectedResponses().containsAll(expectedResponses); */
        final boolean parametersEq = parameters.containsAll(endpoint.getParameters())
                && endpoint.getParameters().containsAll(parameters);
        final boolean consumesEq = consumes.containsAll(endpoint.getConsumes())
                && endpoint.getConsumes().containsAll(consumes);
        final boolean producesEq = produces.containsAll(endpoint.getProduces())
                && endpoint.getProduces().containsAll(produces);
        return httpMethodEq && parametersEq && consumesEq && producesEq;
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
     * @return Path
     */
    public String getPath() {
        return path;
    }

    /**
     * 
     * @return Base url
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * 
     * @return Full url
     */
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
            } else {
                URL url;
                try {
                    url = new URL(path);
                    this.path = url.getFile();
                    this.baseUrl = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
                } catch (MalformedURLException e) {
                    logger.error("Wrong type of Path =" + path);
                }

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
     * @return Produces (content-type: json...)
     */
    public Set<Header> getProduces() {
        return produces;
    }

    /**
     * Sets new produce types
     * @param produces Produces (content-type: json...)
     */
    public Endpoint addProduces(Header produces) {
        this.produces.add(produces);
        return this;
    }

    /**
     * Gets what client is able to consume
     * @return (content-type: json...)
     */
    public Set<Header> getConsumes() {
        return consumes;
    }

    /**
     * Sets headers which endpoint is able to consume
     * @param consumes Headers which will be consumed
     */
    public Endpoint setConsumes(Set<Header> consumes) {
        this.consumes = consumes;
        return this;
    }

    /**
     * Sets header which is client able to consume
     * @param consumes Consumint types
     */
    public Endpoint addConsumes(Header consumes) {
        this.consumes.add(consumes);
        return this;
    }

}
