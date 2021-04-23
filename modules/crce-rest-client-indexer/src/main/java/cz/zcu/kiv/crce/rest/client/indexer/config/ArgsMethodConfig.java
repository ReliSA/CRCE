package cz.zcu.kiv.crce.rest.client.indexer.config;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ArgsMethodConfig {
    @JsonProperty
    private Set<ArgsTypeMethodConfig> args;

    /**
     * @return the args
     */
    public Set<ArgsTypeMethodConfig> getArgs() {
        return args;
    }

    /**
     * @param args the args to set
     */
    public void setArgs(Set<ArgsTypeMethodConfig> args) {
        this.args = args;
    }

    enum ArgType {
        URL, URL_PARAM, RETRIEVE;
    }
}
