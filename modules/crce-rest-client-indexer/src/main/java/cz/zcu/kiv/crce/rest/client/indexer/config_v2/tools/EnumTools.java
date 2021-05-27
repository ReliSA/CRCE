package cz.zcu.kiv.crce.rest.client.indexer.config_v2.tools;

import cz.zcu.kiv.crce.rest.client.indexer.config_v2.MethodType;
import cz.zcu.kiv.crce.rest.client.indexer.general.HttpMethod;

public class EnumTools {
    private static final HttpMethod[] httpVals = HttpMethod.values();

    public static HttpMethod methodTypeToHttpMethod(MethodType methodType) {

        if (methodType.ordinal() >= httpVals.length) {
            return null;
        }
        return httpVals[methodType.ordinal()];
    }
}
