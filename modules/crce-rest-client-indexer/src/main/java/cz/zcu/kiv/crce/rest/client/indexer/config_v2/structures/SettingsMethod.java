package cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures;

import java.util.Set;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.ArgConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.MethodArgType;

public class SettingsMethod implements IWSClient {

    private MethodArgType innerType;
    private Set<Set<ArgConfig>> args;
    private Set<Set<ArgConfig>> varArgs;
    private ArgConfig returns;



    /**
     * @param innerType
     * @param args
     * @param varArgs
     * @param returns
     */
    public SettingsMethod(ArgConfig returns, MethodArgType innerType, Set<Set<ArgConfig>> args,
            Set<Set<ArgConfig>> varArgs) {
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
    public Set<Set<ArgConfig>> getArgs() {
        return args;
    }

    @Override
    public Set<Set<ArgConfig>> getVarArgs() {
        return varArgs;
    }

    @Override
    public ArgConfig getReturns() {
        return returns;
    }

}
