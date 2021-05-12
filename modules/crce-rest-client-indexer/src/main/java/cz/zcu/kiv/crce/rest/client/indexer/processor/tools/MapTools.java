package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapTools {
    /**
     * Converts map of objects into Stream
     * 
     * @source https://www.javaer101.com/en/article/1277074.html
     * @param entry
     * @return Stream of map object
     */
    public static Stream<Map.Entry<String, Object>> flatten(Map.Entry<String, Object> entry) {
        if (entry.getValue() instanceof Map<?, ?>) {
            Map<String, Object> nested = (Map<String, Object>) entry.getValue();
            return nested.entrySet().stream()
                    .map(e -> new AbstractMap.SimpleEntry<>(entry.getKey() + "." + e.getKey(),
                            e.getValue()))
                    .flatMap(MapTools::flatten);
        }
        return Stream.of(entry);
    }

    /**
     * Converts map to list of entries
     * 
     * @param map Input Map
     * @return Map entries
     */
    public static List<Map.Entry<String, Object>> mapToList(Map<String, Object> map) {
        return map.entrySet().stream().flatMap(MapTools::flatten).collect(Collectors.toList());
    }

    /**
     * Converts map to string
     * 
     * @param map Map which will be converted
     * @return Map converted into string
     */
    public static String mapToString(Map<String, Object> map) {
        final String delimeter = ", ";

        String responseStringified = "{ ";
        for (Map.Entry<String, Object> item : MapTools.mapToList(map)) {
            responseStringified += item.getKey() + "=" + item.getValue() + delimeter;
        }
        return responseStringified.substring(0, responseStringified.length() - delimeter.length())
                + " }";
    }
}
