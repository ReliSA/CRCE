package cz.zcu.kiv.crce.rest.client.indexer.config.structures;

import java.util.LinkedHashSet;
import java.util.Set;
import cz.zcu.kiv.crce.rest.client.indexer.config.ArgConfig;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HttpMethodExt;

public class RequestMethod implements IWSClient {

    private ArgConfig returns;
    private HttpMethodExt innerType;
    private Set<LinkedHashSet<ArgConfig>> args;
    private Set<LinkedHashSet<ArgConfig>> varArgs;



    /**
     * @param returns
     * @param innerType
     * @param args
     * @param varArgs
     */
    public RequestMethod(ArgConfig returns, HttpMethodExt innerType,
            Set<LinkedHashSet<ArgConfig>> args, Set<LinkedHashSet<ArgConfig>> varArgs) {
        this.returns = returns;
        this.innerType = innerType;
        this.args = args;
        this.varArgs = varArgs;
    }

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
    public Set<LinkedHashSet<ArgConfig>> getArgs() {
        return args;
    }

    @Override
    public Set<LinkedHashSet<ArgConfig>> getVarArgs() {
        return varArgs;
    }

    @Override
    public ArgConfig getReturns() {
        return returns;
    }

}
