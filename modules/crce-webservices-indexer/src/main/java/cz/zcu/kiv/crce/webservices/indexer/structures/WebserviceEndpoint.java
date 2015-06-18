package cz.zcu.kiv.crce.webservices.indexer.structures;

import java.util.List;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceEndpoint {
    
    private String name;
    private String url;
    private List<WebserviceEndpointParameter> parameters;
    private WebserviceEndpointResponse response;

    public WebserviceEndpoint(String name, String url, List<WebserviceEndpointParameter> parameters, WebserviceEndpointResponse response) {
        this.name = name;
        this.url = url;
        this.parameters = parameters;
        this.response = response;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the parameters
     */
    public List<WebserviceEndpointParameter> getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(List<WebserviceEndpointParameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * @return the response
     */
    public WebserviceEndpointResponse getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(WebserviceEndpointResponse response) {
        this.response = response;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
}
