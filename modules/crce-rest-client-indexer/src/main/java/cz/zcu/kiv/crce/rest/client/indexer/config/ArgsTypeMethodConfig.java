package cz.zcu.kiv.crce.rest.client.indexer.config;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ArgsTypeMethodConfig {
    @JsonProperty
    private ArrayList<ArgConfigType> arg;

    /**
     * @return the arg
     */
    public ArrayList<ArgConfigType> getArg() {
        return arg;
    }

    /**
     * @param arg the arg to set
     */
    public void setArg(ArrayList<ArgConfigType> arg) {
        this.arg = arg;
    }
}
