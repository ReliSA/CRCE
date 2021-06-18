package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

import java.util.Map;
import java.util.Stack;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.Variable;

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

    /**
     * Retrieves arguments from method execution
     * 
     * @param values Stack
     * @param operation Operation which contains method description
     * @return Arguments extracted from stack
     */
    public static Stack<Variable> methodArgsFromValues(Stack<Variable> values,
            Operation operation) {
        String[] methodArgsDef = MethodTools.getArgsFromSignature(operation.getDescription());
        Stack<Variable> output = new Stack<>();
        if (methodArgsDef == null || methodArgsDef.length == 0 || values.isEmpty()) {
            return output;
        }

        for (int counter = 0; counter < methodArgsDef.length; counter++) {
            output.push(values.pop());
        }
        return output;
    }

}
