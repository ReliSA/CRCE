package cz.zcu.kiv.crce.webservices.indexer.structures;

import java.util.List;

/**
 * This is a data class for inner representation of webservice endpoint functionality.
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceEndpoint {
    
    private String name;
    private String url;
    private List<WebserviceEndpointParameter> parameters;
    private WebserviceEndpointResponse response;

    /**
     * Constructor.
     *
     * @param name Name of webservice endpoint.
     * @param url URL from which webservice endpoint can be accessed.
     * @param parameters List of parameters passed to this webservice endpoint.
     * @param response Response returned by this webservice endpoint.
     */
    public WebserviceEndpoint(String name, String url, List<WebserviceEndpointParameter> parameters, WebserviceEndpointResponse response) {
        this.name = name;
        this.url = url;
        this.parameters = parameters;
        this.response = response;
    }
    
    /**
     * @return Name of webservice endpoint.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Name of webservice endpoint.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return List of parameters passed to this webservice endpoint.
     */
    public List<WebserviceEndpointParameter> getParameters() {
        return parameters;
    }

    /**
     * @param parameters List of parameters passed to this webservice endpoint.
     */
    public void setParameters(List<WebserviceEndpointParameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * @return Response returned by this webservice endpoint.
     */
    public WebserviceEndpointResponse getResponse() {
        return response;
    }

    /**
     * @param response Response returned by this webservice endpoint.
     */
    public void setResponse(WebserviceEndpointResponse response) {
        this.response = response;
    }

    /**
     * @return URL from which webservice endpoint can be accessed.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url URL from which webservice endpoint can be accessed.
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
}
