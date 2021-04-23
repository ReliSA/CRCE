package cz.zcu.kiv.crce.rest.client.indexer.config;

import java.util.ArrayList;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiCallMethodConfig {
    @JsonProperty("name")
    private String name;

    @JsonProperty("value")
    private String value;

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty("args")
    private Set<ArrayList<ArgConfigType>> args;

    @JsonProperty("type")
    protected ApiCallMethodType type;

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
     * @return the args
     */
    public Set<ArrayList<ArgConfigType>> getArgs() {
        return args;
    }

    /**
     * @param args the args to set
     */
    public void setArgs(Set<ArrayList<ArgConfigType>> args) {
        this.args = args;
    }

    /**
     * @return the type
     */
    public ApiCallMethodType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ApiCallMethodType type) {
        this.type = type;
    }

    /*
     * private String argsToString() { String argsString = "";
     * 
     * this.args. }
     */

    @Override
    public String toString() {
        return "MethodDefinition{" + "name='" + name + '\'' + ", types='" + this.type.toString()
                + '\'' + ", args=" + this.args.toString() + '}';
    }
}
