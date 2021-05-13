package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Inspired by ghessova on 10.03.2018.
 *
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


    /**
     * 
     * @return Structure
     */
    public String getStructure() {
        return structure;
    }

    /**
     * Sets structure of response
     * @param structure Structure
     */
    public void setStructure(String structure) {
        this.structure = structure;
    }

    /**
     * @return Is it array
     */
    public boolean isArray() {
        return isArray;
    }

    /**
     * Sets wheter its array
     * @param array Is it array
     */
    public void setIsArray(boolean array) {
        isArray = array;
    }

    /**
     * Adds parameter to this response
     * @param parameter EndpointParameter
     */
    public void addParameter(EndpointParameter parameter) {
        parameters.add(parameter);
    }

    /**
     * 
     * @return List of Endpoint parameters
     */
    public List<EndpointParameter> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "EndpointResponse{" + " status=" + status + ", structure='" + structure + '\''
                + ", isArray=" + isArray + ", parameters=" + parameters + '}';
    }
}
