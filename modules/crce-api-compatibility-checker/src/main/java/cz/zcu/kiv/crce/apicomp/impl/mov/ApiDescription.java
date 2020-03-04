package cz.zcu.kiv.crce.apicomp.impl.mov;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiDescription extends HashMap<String, Map<String, List<String>>> {

    public void addOperations(String hostname, String pathToEndpoint, List<String> operations) {
        if (!containsKey(hostname)) {
            put(hostname, new HashMap<>());
        }

        if (!get(hostname).containsKey(pathToEndpoint)) {
            get(hostname).put(pathToEndpoint, operations);
        } else {
            get(hostname).get(pathToEndpoint).addAll(operations);
        }
    }
}
