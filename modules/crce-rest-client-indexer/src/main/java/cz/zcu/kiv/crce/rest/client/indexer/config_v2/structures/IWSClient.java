package cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures;

import java.util.LinkedHashSet;
import java.util.Set;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.ArgConfig;

public interface IWSClient {
    WSClientType getType();

    Enum<?> getInnerType();

    Set<LinkedHashSet<ArgConfig>> getArgs();

    Set<LinkedHashSet<ArgConfig>> getVarArgs();

    ArgConfig getReturns();

}
