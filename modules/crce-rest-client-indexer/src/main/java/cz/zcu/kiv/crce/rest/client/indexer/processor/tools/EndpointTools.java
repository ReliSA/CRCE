package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

import java.util.Map;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;

public class EndpointTools {

    /**
     * Merges new endpoints into existing map of endpoints
     * 
     * @param endpoints Map of endpoints
     * @param endpoint  New endpoint
     */
    public static Endpoint merge(Map<String, Endpoint> endpoints, Endpoint endpoint) {
        if (endpoint.getUrl() == null) {
            return endpoint;
        }


        if (endpoints.containsKey(endpoint.getUrl())) {
            final Endpoint oldEndpoint = endpoints.get(endpoint.getUrl());
            oldEndpoint.merge(endpoint);
            return oldEndpoint;
        } else {
            Endpoint newEndpoint = new Endpoint(endpoint);
            endpoints.put(endpoint.getUrl(), newEndpoint);
            return newEndpoint;
        }


    }

    /**
     * Merges new endpoints map into existing one
     * 
     * @param endpoints    Current endpoints
     * @param newEndpoints New endpoints
     */
    public static void merge(Map<String, Endpoint> endpoints, Map<String, Endpoint> newEndpoints) {
        for (final String key : newEndpoints.keySet()) {
            final Endpoint endpoint = newEndpoints.get(key);
            if (endpoints.containsKey(key)) {
                merge(endpoints, endpoint);
            } else {
                endpoints.put(key, endpoint);
            }
        }
    }


}
