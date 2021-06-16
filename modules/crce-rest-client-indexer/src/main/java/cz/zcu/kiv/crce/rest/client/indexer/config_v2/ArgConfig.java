package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.tools.ClassTools;

public class ArgConfig {
    @JsonProperty("type")
    private MethodArgType type;
    @JsonProperty("classes")
    private Set<String> classes = new HashSet<>();
    @JsonProperty("interfaces")
    private Set<String> interfaces = new HashSet<>();

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
        for (final String className : classes) {
            final String processed = ClassTools.processClassName(className);
            this.classes.add(processed);
        }
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
        for (final String className : interfaces) {
            final String processed = ClassTools.processClassName(className);
            this.interfaces.add(processed);
        }
    }


}
