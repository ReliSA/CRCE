package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ToStringTools;

public class EndpointRequestBody implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1178419129599203262L;

    private String structure;
    private boolean isArray;


    /**
     * 
     * @param structure Stringified sutucture of body
     * @param isArray Is it array
     */
    public EndpointRequestBody(String structure, boolean isArray) {
        this.structure = structure;
        this.isArray = isArray;
    }

    public EndpointRequestBody() {}

    /**
     * 
     * @return Structure
     */
    public String getStructure() {
        return structure;
    }

    /**
     * @param structure Stringified sutucture of body
     */
    public void setStructure(String structure) {
        this.structure = structure;
    }

    /**
     * 
     * @return Is it array
     */
    public boolean isArray() {
        return isArray;
    }

    /**
     * This structure is array
     * @param array
     */
    public void setIsArray(boolean array) {
        isArray = array;
    }

    @Override
    public String toString() {
        return "{ \"structure\": " + ToStringTools.objToString(structure) + ", \"isArray\" : "
                + isArray + " }";

    }

    @Override
    public int hashCode() {
        return Objects.hash(structure, isArray);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EndpointRequestBody) {
            EndpointRequestBody eReqBody = (EndpointRequestBody) obj;
            boolean structureEq = structure.compareTo(eReqBody.getStructure()) == 0;
            boolean isArrayEq = isArray == eReqBody.isArray();
            return structureEq && isArrayEq;
        }
        return false;
    }
}
