package cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures;

import java.util.LinkedHashSet;
import java.util.Set;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.ArgConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.MethodArgType;

public class SettingsMethod implements IWSClient {

    private MethodArgType innerType;
    private Set<LinkedHashSet<ArgConfig>> args;
    private Set<LinkedHashSet<ArgConfig>> varArgs;
    private ArgConfig returns;



    /**
     * @param innerType
     * @param args
     * @param varArgs
     * @param returns
     */
    public SettingsMethod(ArgConfig returns, MethodArgType innerType,
            Set<LinkedHashSet<ArgConfig>> args, Set<LinkedHashSet<ArgConfig>> varArgs) {
        this.innerType = innerType;
        this.args = args;
        this.varArgs = varArgs;
        this.returns = returns;
    }

    @Override
    public WSClientType getType() {
        return WSClientType.SETTINGS;
    }

    @Override
    public MethodArgType getInnerType() {
        return innerType;
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
