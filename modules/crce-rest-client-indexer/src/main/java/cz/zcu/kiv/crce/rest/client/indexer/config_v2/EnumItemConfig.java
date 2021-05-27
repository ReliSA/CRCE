package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnumItemConfig {
    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private MethodArgType type;
    @JsonProperty("value")
    private String value;

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


}
