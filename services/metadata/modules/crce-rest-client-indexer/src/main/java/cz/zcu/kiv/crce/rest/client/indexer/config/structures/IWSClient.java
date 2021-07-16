package cz.zcu.kiv.crce.rest.client.indexer.config.structures;

import java.util.LinkedHashSet;
import java.util.Set;
import cz.zcu.kiv.crce.rest.client.indexer.config.ArgConfig;

public interface IWSClient {
    WSClientType getType();

    Enum<?> getInnerType();

    Set<LinkedHashSet<ArgConfig>> getArgs();

    Set<LinkedHashSet<ArgConfig>> getVarArgs();

    ArgConfig getReturns();

}
