package cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by ghessova on 09.04.2018.
 */
public class MemberProcessor {

    @JsonProperty
    private String owner;
    @JsonProperty
    private String simpleName;
    @JsonProperty
    private Map<String, Object> mapping;
    @JsonProperty
    private String returnType;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public Map<String, Object> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, Object> mapping) {
        this.mapping = mapping;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
}
