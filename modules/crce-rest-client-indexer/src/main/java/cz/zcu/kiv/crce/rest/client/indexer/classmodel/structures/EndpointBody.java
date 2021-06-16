package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.io.Serializable;
import java.util.Objects;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ToJSONTools;

public class EndpointBody implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1178419129599203262L;

    private String type;
    private String structure;
    private boolean isArray;


    /**
     * 
     * @param structure Stringified sutucture of body
     * @param isArray Is it array
     */
    public EndpointBody(String type, boolean isArray) {
        this.type = type;
        this.isArray = isArray;
    }

    public EndpointBody() {}

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }


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
        return "{ \"type\": " + ToJSONTools.convertString(type) + " ,\"structure\": "
                + ToJSONTools.convertObject(structure) + ", \"isArray\" : " + isArray + " }";

    }

    @Override
    public int hashCode() {
        return Objects.hash(type, isArray);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EndpointBody) {
            EndpointBody eReqBody = (EndpointBody) obj;
            boolean typeEq = type.compareTo(eReqBody.getType()) == 0;
            boolean isArrayEq = isArray == eReqBody.isArray();
            return typeEq && isArrayEq;
        }
        return false;
    }
}
