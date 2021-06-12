package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting.BytecodeDescriptorsProcessor;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.HeaderTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ToJSONTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.UrlTools;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HttpMethod;

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
    protected Set<EndpointBody> requestBodies;
    protected Set<EndpointBody> expectedResponses;
    protected Set<Header> produces;
    protected Set<Header> consumes;


    protected Set<Header> responseHeaders = new HashSet<>(); //https://datatracker.ietf.org/doc/html/rfc7231#section-7
    protected Set<Header> controlsHeaders = new HashSet<>(); //https://datatracker.ietf.org/doc/html/rfc7231#section-5.1
    protected Set<Header> conditionals = new HashSet<>(); //https://datatracker.ietf.org/doc/html/rfc7231#section-5.2
    protected Set<Header> contentNegotiation = new HashSet<>(); //https://datatracker.ietf.org/doc/html/rfc7231#section-5.3
    protected Set<Header> authenticationCredentials = new HashSet<>(); //https://datatracker.ietf.org/doc/html/rfc7231#section-5.4
    protected Set<Header> requestContext = new HashSet<>(); //https://datatracker.ietf.org/doc/html/rfc7231#section-5.5



    /**
     * @return the controlsHeaders
     */
    public Set<Header> getControlsHeaders() {
        return controlsHeaders;
    }

    /**
     * @param controlsHeaders the controlsHeaders to set
     */
    public void setControlsHeaders(Set<Header> controlsHeaders) {
        this.controlsHeaders = controlsHeaders;
    }

    /**
     * 
     * @param header new control header
     */
    public void addControlsHeaders(Header header) {
        this.controlsHeaders.add(header);
    }


    /**
     * @return the conditionals
     */
    public Set<Header> getConditionals() {
        return conditionals;
    }

    /**
     * 
     * @param header new conditional header
     */
    public void addConditionals(Header header) {
        this.conditionals.add(header);
    }

    /**
     * @param conditionals the conditionals to set
     */
    public void setConditionals(Set<Header> conditionals) {
        this.conditionals = conditionals;
    }

    /**
     * @return the contentNegotiation
     */
    public Set<Header> getContentNegotiation() {
        return contentNegotiation;
    }

    /**
     * 
     * @param header new content negotiation
     */
    public void addContentNegotiation(Header header) {
        this.contentNegotiation.add(header);
    }

    /**
     * @param contentNegotiation the contentNegotiation to set
     */
    public void setContentNegotiation(Set<Header> contentNegotiation) {
        this.contentNegotiation = contentNegotiation;
    }

    /**
     * @return the authenticationCredentials
     */
    public Set<Header> getAuthenticationCredentials() {
        return authenticationCredentials;
    }

    /**
     * 
     * @param header new authentication credentials
     */
    public void addAuthenticationCredentials(Header header) {
        this.authenticationCredentials.add(header);
    }

    /**
     * @param authenticationCredentials the authenticationCredentials to set
     */
    public void setAuthenticationCredentials(Set<Header> authenticationCredentials) {
        this.authenticationCredentials = authenticationCredentials;
    }

    /**
     * @return the requestContext
     */
    public Set<Header> getRequestContext() {
        return requestContext;
    }

    /**
     * @param requestContext the requestContext to set
     */
    public void setRequestContext(Set<Header> requestContext) {
        this.requestContext = requestContext;
    }

    /**
     * 
     * @param header new request context header
     */
    public void addRequestContext(Header header) {
        this.requestContext.add(header);
    }

    /**
     * @return the responseHeaders
     */
    public Set<Header> getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * @param responseHeaders the responseHeaders to set
     */
    public void setResponseHeaders(Set<Header> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public Endpoint(Endpoint endpoint) {
        this();
        this.setBaseUrl(endpoint.getBaseUrl());
        this.setPath(endpoint.getPath());
        this.httpMethods.addAll(endpoint.getHttpMethods());
        this.requestBodies = copyEndpointReqBodySet(endpoint.getRequestBodies());
        this.expectedResponses = copyEndpointReqBodySet(endpoint.getExpectedResponses());
        this.produces = copyHeaders(endpoint.getProduces());
        this.consumes = copyHeaders(endpoint.getConsumes());
        this.parameters = copyEndpointParameters(endpoint.getParameters());
    }

    private Set<EndpointParameter> copyEndpointParameters(
            Set<EndpointParameter> endpointParameters) {
        Set<EndpointParameter> copy = new HashSet<>();

        for (final EndpointParameter item : endpointParameters) {
            EndpointParameter copyEndpointParameter = new EndpointParameter();
            copyEndpointParameter.setArray(item.isArray());
            copyEndpointParameter.setCategory(item.getCategory());
            copyEndpointParameter.setDataType(item.getDataType());
            copyEndpointParameter.setDataTypeH(item.getDataTypeH());
            copyEndpointParameter.setName(item.getName());
            copy.add(copyEndpointParameter);
        }
        return copy;
    }

    private Set<EndpointBody> copyEndpointReqBodySet(Set<EndpointBody> setEbody) {
        Set<EndpointBody> copy = new HashSet<>();

        for (final EndpointBody body : setEbody) {
            copy.add(new EndpointBody(body.getStructure(), body.isArray()));
        }
        return copy;
    }

    private Set<Header> copyHeaders(Set<Header> headers) {
        Set<Header> copy = new HashSet<>();

        for (final Header body : headers) {
            copy.add(new Header(body.getType(), body.getValue()));
        }
        return copy;
    }

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
            Set<EndpointBody> requestBodies, Set<EndpointBody> expectedResponses,
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
    public Endpoint(String path, Set<HttpMethod> httpMethods, Set<EndpointBody> requestBodies,
            Set<EndpointBody> expectedResponses, Set<EndpointParameter> parameters,
            Set<Header> produces, Set<Header> consumes) {
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
    public Set<EndpointBody> getRequestBodies() {
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
            addRequestBody(new EndpointBody(param.getDataType(),
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
    public Endpoint addRequestBody(EndpointBody body) {
        this.requestBodies.add(body);
        return this;
    }

    /**
     * 
     * @param response Adds new response
     * @return this
     */
    public Endpoint addExpectedResponse(EndpointBody response) {
        this.expectedResponses.add(response);
        return this;
    }

    /**
     * 
     * @return Endpoint request bodies
     */
    public Set<EndpointBody> getExpectedResponses() {
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
        if (HeaderTools.isConsumingType(header.getType())) {
            this.addConsumes(header);
        } else if (HeaderTools.isProducingType(header.getType())) {
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
        this.requestBodies.addAll(copyEndpointReqBodySet(endpoint.getRequestBodies()));
        this.expectedResponses.addAll(copyEndpointReqBodySet(endpoint.getExpectedResponses()));
        this.produces.addAll(copyHeaders(endpoint.getProduces()));
        this.consumes.addAll(copyHeaders(endpoint.getConsumes()));
        this.parameters.addAll(copyEndpointParameters(endpoint.getParameters()));
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
        return "{ \"baseUrl\": " + ToJSONTools.convertString(baseUrl) + ", \"path\": "
                + ToJSONTools.convertString(path) + ", \"httpMethods\": "
                + ToJSONTools.convertSet(httpMethods) + ", \"requestBodies\": "
                + ToJSONTools.convertSet(requestBodies) + ", \"responses\": "
                + ToJSONTools.convertSet(expectedResponses) + ", \"parameters\": "
                + ToJSONTools.convertSet(parameters) + ", \"produces\": "
                + ToJSONTools.convertSet(produces) + ", \"consumes\": "
                + ToJSONTools.convertSet(consumes) + " }";
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
