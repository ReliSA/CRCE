package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.zcu.kiv.crce.rest.client.indexer.config.MethodArgType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.HeaderTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.StringTools;
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
    protected Set<HttpMethod> httpMethods = new HashSet<>();
    protected Set<EndpointParameter> parameters = new HashSet<>();
    protected Set<EndpointParameter> genericParameters = new HashSet<>();
    protected Set<EndpointBody> requestBodies = new HashSet<>();
    protected Set<String> objects = new HashSet<>(); //either response or request object
    protected Set<EndpointBody> expectedResponses = new HashSet<>();
    protected Set<Header> produces = new HashSet<>();
    protected Set<Header> consumes = new HashSet<>();


    protected Set<Header> responseHeaders = new HashSet<>(); //https://datatracker.ietf.org/doc/html/rfc7231#section-7
    protected Set<Header> controlsHeaders = new HashSet<>(); //https://datatracker.ietf.org/doc/html/rfc7231#section-5.1
    protected Set<Header> conditionals = new HashSet<>(); //https://datatracker.ietf.org/doc/html/rfc7231#section-5.2
    protected Set<Header> contentNegotiation = new HashSet<>(); //https://datatracker.ietf.org/doc/html/rfc7231#section-5.3
    protected Set<Header> authenticationCredentials = new HashSet<>(); //https://datatracker.ietf.org/doc/html/rfc7231#section-5.4
    protected Set<Header> requestContext = new HashSet<>(); //https://datatracker.ietf.org/doc/html/rfc7231#section-5.5
    protected Set<Header> representation = new HashSet<>();

    protected Set<EndpointParameter> bodyParameteres = new HashSet<>();
    protected Set<EndpointParameter> cookies = new HashSet<>();
    protected Set<EndpointParameter> uriParams = new HashSet<>();


    protected Set<String> dependency = new LinkedHashSet<>();

    /**
     * @param dependency the dependency to set
     */
    public void addDependency(Set<String> dependency) {
        this.dependency.addAll(dependency);
    }

    public void addDependency(String dependency) {
        this.dependency.add(dependency);
    }

    /**
     * @return the dependency
     */
    public Set<String> getDependency() {
        return dependency;
    }

    /**
     * @return the uriParams
     */
    public Set<EndpointParameter> getUriParams() {
        return uriParams;
    }

    /**
     * @return the cookies
     */
    public Set<EndpointParameter> getCookies() {
        return cookies;
    }

    /**
     * @return the bodyParameteres
     */
    public Set<EndpointParameter> getBodyParameteres() {
        return bodyParameteres;
    }

    /**
     * @return the objects
     */
    public Set<String> getObjects() {
        return objects;
    }

    /**
     * @param objects the objects to set
     */
    public void setObjects(Set<String> objects) {
        this.objects = objects;
    }

    /**
     * 
     * @param object Classname of object
     */
    public void addObject(String object) {
        this.objects.add(object);
    }

    /**
     * @param representation the representation to set
     */
    public void setRepresentation(Set<Header> representation) {
        this.representation = representation;
    }

    /**
     * @return the representation
     */
    public Set<Header> getRepresentation() {
        return representation;
    }

    public void addRepresentationHeader(Header header) {
        this.representation.add(header);
    }


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

    public void addResponseHeader(Header responseHeader) {
        this.responseHeaders.add(responseHeader);
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

        this.responseHeaders = copyHeaders(endpoint.getResponseHeaders());
        this.controlsHeaders = copyHeaders(endpoint.getControlsHeaders());
        this.conditionals = copyHeaders(endpoint.getConditionals());
        this.contentNegotiation = copyHeaders(endpoint.getContentNegotiation());
        this.authenticationCredentials = copyHeaders(endpoint.getAuthenticationCredentials());
        this.requestContext = copyHeaders(endpoint.getRequestContext());
        this.representation = copyHeaders(endpoint.getRepresentation());
        this.dependency.addAll(endpoint.getDependency());

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
            copy.add(new EndpointBody(body.getType(), body.isArray()));
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

    public Endpoint(String baseURL, String path, Set<HttpMethod> httpMethods,
            Set<EndpointParameter> endpointParameters, Set<Header> headers) {
        this.setBaseUrl(baseURL);
        this.setPath(path);
        this.httpMethods = httpMethods;
        this.parameters = endpointParameters;

        Set<Header> mutableSet = new HashSet<>(headers);

        for (Header header : mutableSet) {
            MethodArgType headerType = MethodArgType.ofValue(header.getType());
            if (HeaderTools.isControlType(headerType)) {
                header.setHeaderGroup(HeaderGroup.CONTROL);
                this.addControlsHeaders(header);
            } else if (HeaderTools.isConditionalType(headerType)) {
                header.setHeaderGroup(HeaderGroup.CONDITIONAL);
                this.addConditionals(header);
            } else if (HeaderTools.isContentNegotiation(headerType)) {
                header.setHeaderGroup(HeaderGroup.CONTENT_NEGOTIATION);
                this.addContentNegotiation(header);
            } else if (HeaderTools.isAuthenticationCredentials(headerType)) {
                header.setHeaderGroup(HeaderGroup.AUTHENTICATION_CREDENTIALS);
                this.addAuthenticationCredentials(header);
            } else if (HeaderTools.isRequestContext(headerType)) {
                header.setHeaderGroup(HeaderGroup.REQUEST_CONTEXT);
                this.addRequestContext(header);
            } else if (HeaderTools.isRepresentation(headerType)) {
                header.setHeaderGroup(HeaderGroup.REPRESENTATION);
                this.addRepresentationHeader(header);
            }

            if (header.getHeaderGroup() == null) {
                this.addResponseHeader(header);
            }
        }
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
        parameters.add(param);
        if (param.getCategory() == ParameterCategory.BODY) {
            this.bodyParameteres.add(param);
        } else if (param.getCategory() == ParameterCategory.COOKIE) {
            this.cookies.add(param);
        } else if (param.getCategory() == ParameterCategory.MATRIX
                || param.getCategory() == ParameterCategory.QUERY) {
            this.uriParams.add(param);
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
        return this;
    }

    /**
     * 
     * @param endpoint Merges incoming endpoint into this
     */
    public void merge(Endpoint endpoint) {
        String newPath = !StringTools.isEmpty(endpoint.getPath()) ? endpoint.getPath() : this.path;
        String newBaseUrl =
                !StringTools.isEmpty(endpoint.getBaseUrl()) ? endpoint.getBaseUrl() : this.baseUrl;
        this.setPath(newPath);
        this.setBaseUrl(newBaseUrl);
        this.httpMethods.addAll(endpoint.getHttpMethods());
        this.requestBodies.addAll(copyEndpointReqBodySet(endpoint.getRequestBodies()));
        this.expectedResponses.addAll(copyEndpointReqBodySet(endpoint.getExpectedResponses()));
        this.produces.addAll(copyHeaders(endpoint.getProduces()));
        this.consumes.addAll(copyHeaders(endpoint.getConsumes()));
        this.responseHeaders.addAll(copyHeaders(endpoint.getResponseHeaders()));
        this.controlsHeaders.addAll(copyHeaders(endpoint.getControlsHeaders()));
        this.conditionals.addAll(copyHeaders(endpoint.getConditionals()));
        this.contentNegotiation.addAll(copyHeaders(endpoint.getContentNegotiation()));
        this.authenticationCredentials.addAll(copyHeaders(endpoint.getAuthenticationCredentials()));
        this.requestContext.addAll(copyHeaders(endpoint.getRequestContext()));
        for (EndpointParameter parameter : copyEndpointParameters(endpoint.getParameters())) {
            addParameter(parameter);
        }
        this.representation.addAll(copyHeaders(endpoint.getRepresentation()));
        //this.objects.addAll(endpoint.getObjects());
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
        final boolean parametersEq = parameters.containsAll(endpoint.getParameters())
                && endpoint.getParameters().containsAll(parameters);
        final boolean consumesEq = consumes.containsAll(endpoint.getConsumes())
                && endpoint.getConsumes().containsAll(consumes);
        final boolean producesEq = produces.containsAll(endpoint.getProduces())
                && endpoint.getProduces().containsAll(produces);
        final boolean responseHeadersEq =
                responseHeaders.containsAll(endpoint.getResponseHeaders());
        final boolean controlsHeadersEq =
                controlsHeaders.containsAll(endpoint.getControlsHeaders());
        final boolean conditionalsEq = conditionals.containsAll(endpoint.getConditionals());
        final boolean contentNegotiationEq =
                contentNegotiation.containsAll(endpoint.getContentNegotiation());
        final boolean authenticationCredentialsEq =
                authenticationCredentials.containsAll(endpoint.getAuthenticationCredentials());
        final boolean requestContextEq = requestContext.containsAll(endpoint.getRequestContext());
        final boolean representationEq = representation.containsAll(endpoint.getRepresentation());
        final boolean expectedResponsesEq =
                expectedResponses.containsAll(endpoint.getExpectedResponses())
                        && endpoint.getExpectedResponses().containsAll(expectedResponses);
        return httpMethodEq && parametersEq && consumesEq && producesEq && responseHeadersEq
                && controlsHeadersEq && conditionalsEq && contentNegotiationEq
                && authenticationCredentialsEq && requestContextEq && representationEq
                && expectedResponsesEq;
    }


    @Override
    public String toString() {
        return "{ \"baseUrl\": " + ToJSONTools.convertString(baseUrl) + ", \"path\": "
                + ToJSONTools.convertString(path) + ", \"httpMethods\": "
                + ToJSONTools.convertSet(httpMethods) + ", \"requestBodies\": "
                + ToJSONTools.convertSet(requestBodies) + ", \"responses\": "
                + ToJSONTools.convertSet(expectedResponses) + ", \"parameters\": "
                + ToJSONTools.convertSet(parameters) + ", \"headers\": {" + " \"control\": "
                + ToJSONTools.convertSet(controlsHeaders) + ", \"conditionals\":"
                + ToJSONTools.convertSet(conditionals) + ", \"contentNegotiation\": "
                + ToJSONTools.convertSet(contentNegotiation) + ", \"authentication\": "
                + ToJSONTools.convertSet(authenticationCredentials) + ", \"requestContenxt\": "
                + ToJSONTools.convertSet(requestContext) + ", \"representation\": "
                + ToJSONTools.convertSet(representation) + ", \"response\": "
                + ToJSONTools.convertSet(responseHeaders) + ", \"calledFrom\": "
                + ToJSONTools.convertSet(dependency) + " } }";
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
