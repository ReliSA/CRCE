package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WSClientMethodConfig {
    @JsonProperty("names")
    private Set<String> names;

    @JsonProperty("args")
    private Set<Set<String>> argsReferences;

    public void setArgs(Set<Set<String>> argsReferences) {
        this.argsReferences = argsReferences;
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
}
