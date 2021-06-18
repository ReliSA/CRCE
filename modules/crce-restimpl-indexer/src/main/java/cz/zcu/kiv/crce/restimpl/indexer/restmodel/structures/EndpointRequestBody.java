package cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures;

import java.io.Serializable;

/**
 * Created by ghessova on 10.03.2018.
 */
public class EndpointRequestBody implements Serializable {

    private String structure;
    private boolean isOptional = true;
    private boolean isArray;

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public void setOptional(boolean optional) {
        isOptional = optional;
    }

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean array) {
        isArray = array;
    }
}
