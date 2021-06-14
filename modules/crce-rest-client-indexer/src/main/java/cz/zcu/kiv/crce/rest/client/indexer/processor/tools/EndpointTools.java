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


        if (endpoints.containsKey(endpoint.getUrl())) {
            //System.out.println("MERGE=" + endpoint);
            final Endpoint oldEndpoint = endpoints.get(endpoint.getUrl());
            oldEndpoint.merge(endpoint);
        } else {
            endpoints.put(endpoint.getUrl(), new Endpoint(endpoint));
            return;
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
