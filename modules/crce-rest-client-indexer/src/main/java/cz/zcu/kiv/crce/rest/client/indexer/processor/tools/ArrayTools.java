package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

import java.util.Map;

public class ArrayTools {
    @SuppressWarnings("unchecked")
    public static String arrayToString(Object[] response) {
        final String delimeter = ", ";
        String responseStringified = "[ ";
        for (Object item : response) {
            String stringified;
            if (item instanceof Map) {
                stringified = MapTools.mapToString((Map<String, Object>) item);
            } else if (item instanceof Object[]) {
                stringified = arrayToString(response);
            } else {
                stringified = item.toString();
            }
            responseStringified += stringified + delimeter;
        }
        return responseStringified.substring(0, responseStringified.length() - delimeter.length())
                + " ]";
    }
}
