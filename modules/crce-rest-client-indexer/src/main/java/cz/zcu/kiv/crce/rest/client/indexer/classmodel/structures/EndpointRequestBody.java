package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.io.Serializable;
import java.util.Objects;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ToStringTools;

public class EndpointRequestBody implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1178419129599203262L;

    private String structure;
    private boolean isArray;


    public EndpointRequestBody(String structure, boolean isArray) {
        this.structure = structure;
        this.isArray = isArray;
    }

    public EndpointRequestBody() {}

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

    @Override
    public String toString() {
        return "{ \"structure\": " + ToStringTools.stringToString(structure) + ", \"isArray\" : "
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
            boolean structureEq = structure.equals(eReqBody.getStructure());
            boolean isArrayEq = isArray == eReqBody.isArray();
            return structureEq && isArrayEq;
        }
        return false;
    }
}
