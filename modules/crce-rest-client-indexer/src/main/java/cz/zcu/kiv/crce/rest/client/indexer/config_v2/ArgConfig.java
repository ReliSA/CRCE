package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ArgConfig {
    @JsonProperty("type")
    private MethodArgType type;
    @JsonProperty("classes")
    private Set<String> classes;
    @JsonProperty("interfaces")
    private Set<String> interfaces;

    /**
     * @return the type
     */
    public MethodArgType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(MethodArgType type) {
        this.type = type;
    }

    /**
     * @return the classes
     */
    public Set<String> getClasses() {
        return classes;
    }

    /**
     * @param classes the classes to set
     */
    public void setClasses(Set<String> classes) {
        this.classes = classes;
    }

    /**
     * @return the interfaces
     */
    public Set<String> getInterfaces() {
        return interfaces;
    }

    /**
     * @param interfaces the interfaces to set
     */
    public void setInterfaces(Set<String> interfaces) {
        this.interfaces = interfaces;
    }


}
