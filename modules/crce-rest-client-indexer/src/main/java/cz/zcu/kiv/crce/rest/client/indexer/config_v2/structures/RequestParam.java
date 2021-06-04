package cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures;

import cz.zcu.kiv.crce.rest.client.indexer.config_v2.RequestParamFieldType;

public class RequestParam {
    private RequestParamFieldType type;
    private String value;

    /**
     * @param type
     * @param value
     */
    public RequestParam(RequestParamFieldType type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * @return the type
     */
    public RequestParamFieldType getType() {
        return type;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
}
