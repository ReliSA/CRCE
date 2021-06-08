package cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures;

import java.util.Set;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.ArgConfig;

public interface IWSClient {
    WSClientType getType();

    Enum<?> getInnerType();

    Set<Set<ArgConfig>> getArgs();

    Set<Set<ArgConfig>> getVarArgs();

    String getReturns();
}
