package cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ghessova on 10.03.2018.
 *
 - name - id
 - url
 - method (HTTP method)
 - produces - MIME type - XML, JSON, YAML, plain text, all ...
 - consumes - MIME type - XML, JSON, YAML, plain text, all ...
 */
public class Endpoint implements Serializable {

    private String name;
    private List<String> paths;
    private Set<String> httpMethods; // Spring allows endpoint to have more methods specified
    private Set<String> consumes;
    private Set<String> produces;
    private Set<String> dependentOn = new HashSet<>();

    /* -------------------------------------*/
    private EndpointRequestBody body;
    private Set<EndpointResponse> responses;
    private Set<RequestParameter> parameters;


    /**
     * @return the dependentOn
     */
    public Set<String> getDependency() {
        return dependentOn;
    }

    public void addDependency(String dependentOn) {
        this.dependentOn.add(dependentOn);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public Set<String> getHttpMethods() {
        return httpMethods;
    }

    public void setHttpMethods(Set<String> httpMethods) {
        this.httpMethods = httpMethods;
    }

    public Set<String> getConsumes() {
        return consumes;
    }

    public void setConsumes(Set<String> consumes) {
        this.consumes = consumes;
    }

    public Set<String> getProduces() {
        return produces;
    }

    public void setProduces(Set<String> produces) {
        this.produces = produces;
    }

    public Set<EndpointResponse> getResponses() {
        return responses;
    }

    public void setResponses(Set<EndpointResponse> responses) {
        this.responses = responses;
    }

    public Set<RequestParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Set<RequestParameter> parameters) {
        this.parameters = parameters;
    }

    public EndpointRequestBody getBody() {
        return body;
    }

    public void setBody(EndpointRequestBody body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Endpoint{" + "name='" + name + '\'' + ", paths=" + paths + ", httpMethods='"
                + httpMethods + '\'' + ", consumes=" + consumes + ", produces=" + produces
                + ", responses=" + responses + ", parameters=" + parameters + '}';
    }
}
