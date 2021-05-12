package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;

public class ToStringTools {
    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * Converts endpoints into list of json objects
     * 
     * @param collection
     * @return
     */
    public static String endpointsToJSON(Collection<Endpoint> collection) {
        if (collection.size() == 0) {
            return null;
        }
        String notIndentedJSON = "[";
        for (final Endpoint endpoint : collection) {
            notIndentedJSON += endpoint.toString() + ",";
        }
        notIndentedJSON = notIndentedJSON.substring(0, notIndentedJSON.length() - 1) + "]";
        try {
            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(mapper.readValue(notIndentedJSON, Object.class));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Converts Set into String
     * 
     * @param <T> Custom type
     * @param set Input set
     * @return Converted set
     */
    public static <T> String setToString(Set<T> set) {

        String stringified = "[";
        for (T item : set) {
            if (item instanceof String || item instanceof Enum) {
                stringified += objToString(item) + ",";
            } else {
                stringified += item.toString() + ",";
            }
        }
        if (stringified.length() > 1) {
            stringified = stringified.substring(0, stringified.length() - 1);
        }
        stringified += "]";
        return stringified;
    }

    /**
     * Converts object into json string
     * 
     * @param obj Any object
     * @return Object converted to JSON string
     */
    public static String objToString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String && ((String) obj).startsWith("{")) {
            return (String) obj;
        }
        return "\"" + obj + "\"";
    }

    /**
     * Converts string into JSON like string
     * 
     * @param str String to conversion
     * @return Converted string
     */
    public static String stringToString(String str) {
        if (str == null) {
            return null;
        }
        return "\"" + str + "\"";
    }
}
