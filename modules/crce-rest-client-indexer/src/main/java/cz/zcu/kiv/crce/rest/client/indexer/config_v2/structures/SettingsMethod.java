package cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures;

import java.util.Set;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.ArgConfig;

public class SettingsMethod implements IWSClient {

    private SettingsType innerType;
    private Set<Set<ArgConfig>> args;
    private Set<Set<ArgConfig>> varArgs;
    private String returns;



    /**
     * @param innerType
     * @param args
     * @param varArgs
     * @param returns
     */
    public SettingsMethod(String returns, SettingsType innerType, Set<Set<ArgConfig>> args,
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
    public SettingsType getInnerType() {
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
    public String getReturns() {
        return returns;
    }

}
