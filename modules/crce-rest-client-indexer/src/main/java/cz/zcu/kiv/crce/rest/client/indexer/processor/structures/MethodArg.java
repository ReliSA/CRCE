package cz.zcu.kiv.crce.rest.client.indexer.processor.structures;

import cz.zcu.kiv.crce.rest.client.indexer.config.ArgConfig;

public class MethodArg extends ArgConfig {
    private Variable var;
    private boolean isarray;



    /**
     * @return the var
     */
    public Variable getVar() {
        return var;
    }

    /**
     * @param var the var to set
     */
    public void setVar(Variable var) {
        this.var = var;
    }

    public boolean isArray() {
        return isarray;
    }

    public void setIsArray(boolean isarray) {
        this.isarray = isarray;
    }

    public void setDataFromArgConfig(ArgConfig argConfig) {
        this.setInterfaces(argConfig.getInterfaces());
        this.setType(argConfig.getType());
        this.setClasses(argConfig.getClasses());
    }
}
