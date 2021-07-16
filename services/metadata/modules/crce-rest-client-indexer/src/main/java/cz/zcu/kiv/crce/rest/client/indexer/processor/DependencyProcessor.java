package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;

public class DependencyProcessor {

    public static void process(Map<String, Set<String>> callingChains,
            Collection<Endpoint> endpoints) {
        for (Endpoint endpoint : endpoints) {
            HashSet<String> visitedMethodDescription = new HashSet<>();
            List<String> todoMethodDescription = new LinkedList<>();

            for (final String methodDescription : endpoint.getDependency()) {
                todoMethodDescription.add(methodDescription);
            }

            while (!todoMethodDescription.isEmpty()) {
                final String currentMD = todoMethodDescription.remove(0);
                if (visitedMethodDescription.contains(currentMD)) {
                    continue;
                }
                visitedMethodDescription.add(currentMD);
                loadDependency(currentMD, callingChains, todoMethodDescription);
            }
            endpoint.addDependency(visitedMethodDescription);
        }
    }

    private static void loadDependency(String methodDescription,
            Map<String, Set<String>> callingChains, List<String> todoMethodDescription) {
        if (callingChains.containsKey(methodDescription)) {
            todoMethodDescription.addAll(callingChains.get(methodDescription));
        }
    }
}
