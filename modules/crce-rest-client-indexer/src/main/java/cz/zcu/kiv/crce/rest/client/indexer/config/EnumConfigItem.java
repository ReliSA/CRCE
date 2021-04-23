package cz.zcu.kiv.crce.rest.client.indexer.config;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EnumConfigItem {
    @JsonProperty("class")
    private String className;

    @JsonProperty
    private Set<EnumFieldOrMethodConfig> methods;

    @JsonProperty
    private Set<EnumFieldOrMethodConfig> fields;

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        String processed = DefinitionValuesProcessor.processClassName(className);
        this.className = processed;
    }

    /**
     * @return the methods
     */
    public Set<EnumFieldOrMethodConfig> getMethods() {
        return methods;
    }

    /**
     * @param methods the methods to set
     */
    public void setMethods(Set<EnumFieldOrMethodConfig> methods) {
        this.methods = methods;
    }

    /**
     * @return the fields
     */
    public Set<EnumFieldOrMethodConfig> getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(Set<EnumFieldOrMethodConfig> fields) {
        this.fields = fields;
    }


}
