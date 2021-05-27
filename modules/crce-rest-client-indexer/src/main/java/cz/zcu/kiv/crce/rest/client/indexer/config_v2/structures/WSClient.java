package cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures;

import java.util.HashSet;
import java.util.Set;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.ArgConfig;
import cz.zcu.kiv.crce.rest.client.indexer.general.HttpMethod;

public class WSClient {
    String owner;
    String name;
    HttpMethod httpMethod;
    Set<Set<ArgConfig>> args = new HashSet<>();

    /**
     * @param owner Owner of this method
     * @param name Name of this method
     * @param httpMethod HTTP method type of this ws client
     * @param args Arguments which can be provided to this method
     */
    public WSClient(final String owner, final String name, final HttpMethod httpMethod,
            final Set<Set<ArgConfig>> args) {
        this.owner = owner;
        this.name = name;
        this.httpMethod = httpMethod;
        this.args = args;
    }

    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the httpMethod
     */
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    /**
     * @return the args
     */
    public Set<Set<ArgConfig>> getArgs() {
        return args;
    }

}
