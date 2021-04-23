package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghessova on 10.03.2018.
 *
 * - name - responseid - stejný význam jako name u webservice.endpoint - status - type - MIME type -
 * structure - grammar - XSD, JSON-schema... - isOptional - isArray
 */
public class EndpointResponse implements Serializable {

    /**
    *
    */
    private static final long serialVersionUID = -3335505195408802604L;
    private int status; // http status
    private String structure;
    private boolean isArray;
    private List<EndpointParameter> parameters = new ArrayList<>();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }

    public void addParameter(EndpointParameter parameter) {
        parameters.add(parameter);
    }

    public List<EndpointParameter> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "EndpointResponse{" + " status=" + status + ", structure='" + structure + '\''
                + ", isArray=" + isArray + ", parameters=" + parameters + '}';
    }
}
