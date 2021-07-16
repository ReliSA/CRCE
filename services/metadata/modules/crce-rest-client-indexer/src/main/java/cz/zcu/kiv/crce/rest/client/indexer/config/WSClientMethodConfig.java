package cz.zcu.kiv.crce.rest.client.indexer.config;

import java.util.LinkedHashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WSClientMethodConfig {
    @JsonProperty("names")
    private Set<String> names;

    @JsonProperty("args")
    private Set<LinkedHashSet<String>> argsReferences;

    @JsonProperty("varArgs")
    private Set<LinkedHashSet<String>> varArgsReferences;

    @JsonProperty("returns")
    private String returns;

    /**
     * @return the returns
     */
    public String getReturns() {
        return returns;
    }

    /**
     * @param returns the returns to set
     */
    public void setReturns(String returns) {
        this.returns = returns;
    }

    public void setArgs(Set<LinkedHashSet<String>> argsReferences) {
        this.argsReferences = argsReferences;
    }

    /**
     * @return the varArgsReferences
     */
    public Set<LinkedHashSet<String>> getVarArgsReferences() {
        return varArgsReferences;
    }

    /**
     * @param varArgsReferences the varArgsReferences to set
     */
    public void setVarArgs(Set<LinkedHashSet<String>> varArgsReferences) {
        this.varArgsReferences = varArgsReferences;
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
    public Set<LinkedHashSet<String>> getArgsReferences() {
        return argsReferences;
    }
}
