package cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures;

import java.util.Set;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.ArgConfig;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HttpMethodExt;

public class RequestMethod implements IWSClient {

    private String returns;
    private HttpMethodExt innerType;
    private Set<Set<ArgConfig>> args;
    private Set<Set<ArgConfig>> varArgs;

    /**
     * @return the innerType
     */
    public HttpMethodExt getInnerType() {
        return innerType;
    }

    @Override
    public WSClientType getType() {
        return WSClientType.REQUEST;
    }

    @Override
    public Set<Set<ArgConfig>> getArgs() {
        return args;
    }

    @Override
    public Set<Set<ArgConfig>> getVarArgs() {
        return varArgs;
    }

    @Override
    public String getReturns() {
        return returns;
    }

}
