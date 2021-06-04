package cz.zcu.kiv.crce.rest.client.indexer.processor.structures;

import cz.zcu.kiv.crce.rest.client.indexer.config_v2.ArgConfig;

public class MethodArg extends ArgConfig {
    private Object value;

    public void setValue(Object val) {
        this.value = val;
    }

    public Object getValue() {
        return this.value;
    }

    public void setDataFromArgConfig(ArgConfig argConfig) {
        this.setInterfaces(argConfig.getInterfaces());
        this.setType(argConfig.getType());
        this.setClasses(argConfig.getClasses());
    }
}
