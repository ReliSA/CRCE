package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

import java.util.Map;

public class ArrayTools {
    /**
     * Converts array of objects into array in json style
     * 
     * @param array Array of objects
     * @return Stringified array of objects
     */
    @SuppressWarnings("unchecked")
    public static String arrayToString(Object[] array) {
        final String delimeter = ", ";
        String responseStringified = "[ ";
        for (Object item : array) {
            String stringified;
            if (item instanceof Map) {
                stringified = MapTools.mapToString((Map<String, Object>) item);
            } else if (item instanceof Object[]) {
                stringified = arrayToString(array);
            } else {
                stringified = item.toString();
            }
            responseStringified += stringified + delimeter;
        }
        return responseStringified.substring(0, responseStringified.length() - delimeter.length())
                + " ]";
    }
}
