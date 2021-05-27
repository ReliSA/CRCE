package cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures;

import cz.zcu.kiv.crce.rest.client.indexer.config_v2.MethodArgType;

public class EnumItem {
    private MethodArgType type;
    private String value;

    /**
     * @param type
     * @param value
     */
    public EnumItem(MethodArgType type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * @return the type
     */
    public MethodArgType getType() {
        return type;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
}
