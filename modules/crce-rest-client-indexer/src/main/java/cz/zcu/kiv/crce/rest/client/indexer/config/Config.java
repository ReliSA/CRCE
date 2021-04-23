package cz.zcu.kiv.crce.rest.client.indexer.config;

import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Config {
    @JsonProperty("name")
    private String name;

    @JsonProperty("apiCall")
    private Set<ApiCallConfig> methods;

    @JsonProperty
    private Set<EnumConfigItem> enums;

    @JsonProperty
    private Set<String> generics;

    @JsonProperty
    private Set<EDataContainerConfig> endpointDataContainers;

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
     * @return the methods
     */
    public Set<ApiCallConfig> getMethods() {
        return methods;
    }

    /**
     * @param methods the methods to set
     */
    public void setMethods(Set<ApiCallConfig> methods) {
        this.methods = methods;
    }

    /**
     * @return the enums
     */
    public Set<EnumConfigItem> getEnums() {
        return enums;
    }

    /**
     * @param enums the enums to set
     */
    public void setEnums(Set<EnumConfigItem> enums) {
        this.enums = enums;
    }

    /**
     * @param httpTypeContainer the typeHolders to set
     */
    public void setGenerics(Set<String> genericWrappers) {
        Set<String> genericWrappersParsed = new HashSet<>();
        for (String type : genericWrappers) {
            genericWrappersParsed.add(DefinitionValuesProcessor.processClassName(type));
        }
        this.generics = genericWrappersParsed;
    }

    /**
     * @return the endpointDataContainers
     */
    public Set<EDataContainerConfig> getEndpointDataContainers() {
        return endpointDataContainers;
    }

    /**
     * @param endpointDataContainer the endpointDataContainers to set
     */
    public void setEndpointDataContainers(Set<EDataContainerConfig> endpointDataContainer) {
        this.endpointDataContainers = endpointDataContainer;
    }

    /**
     * @return the httpTypeContainer
     */
    public Set<String> getGenerics() {
        return generics;
    }


}
