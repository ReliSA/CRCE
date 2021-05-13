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
    public static void merge(Map<String, Endpoint> endpoints, Endpoint endpoint) {
        if (endpoint.getUrl() == null) {
            return;
        }

        Endpoint updatedEndpoint = null;

        if (endpoints.containsKey(endpoint.getUrl())) {
            final Endpoint oldEndpoint = endpoints.remove(endpoint.getUrl());
            updatedEndpoint = oldEndpoint;

        } else if (endpoints.containsKey(endpoint.getPath())) {
            final Endpoint oldEndpoint = endpoints.remove(endpoint.getPath());
            updatedEndpoint = oldEndpoint;

        } else if (endpoints.containsKey(endpoint.getBaseUrl())) {
            final Endpoint oldEndpoint = endpoints.remove(endpoint.getBaseUrl());
            updatedEndpoint = oldEndpoint;

        } else {
            endpoints.put(endpoint.getUrl(), endpoint);
            return;
        }

        if (updatedEndpoint.sEquals(endpoint)) {
            // same instance, data are already changed
        } else {
            updatedEndpoint.merge(endpoint);
        }

        endpoints.put(updatedEndpoint.getUrl(), updatedEndpoint);
    }

    /**
     * Merges new endpoints map into existing one
     * 
     * @param endpoints    Current endpoints
     * @param newEndpoints New endpoints
     */
    public static void merge(Map<String, Endpoint> endpoints,
            Map<String, Endpoint> newEndpoints) {
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