package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MethodConfig {
    @JsonProperty("names")
    private Set<String> names;

    @JsonProperty("args")
    private Set<Set<String>> argsReferences;

    private Set<ArgConfig> args = new HashSet<>();

    public void addArg(ArgConfig argConfig) {
        args.add(argConfig);
    }

    public Set<ArgConfig> getArgs() {
        return this.args;
    }

    /**
     * @return the names
     */
    public Set<String> getNames() {
        return names;
    }

    /**
     * @param names the names to set
     */
    public void setNames(Set<String> names) {
        this.names = names;
    }

    /**
     * @return the argsReferences
     */
    public Set<Set<String>> getArgsReferences() {
        return argsReferences;
    }

    /**
     * @param argsReferences the argsReferences to set
     */
    public void setArgsReferences(Set<Set<String>> argsReferences) {
        this.argsReferences = argsReferences;
    }


}
