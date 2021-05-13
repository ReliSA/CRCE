package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

public class StringTools {
    static private final String APPEND_FC = "append";
    static private final String TO_STRING_FC = "toString";

    /**
     * Detects a toString method by its name
     * 
     * @param name name of the method
     * @return is or is not
     */
    public static boolean isToString(String name) {
        return name.equals(TO_STRING_FC);
    }

    /**
     * Detects an append method by its name
     * 
     * @param name name of the method
     * @return is or is not
     */
    public static boolean isAppend(String name) {
        return name.equals(APPEND_FC);
    }

    public enum OperationType {
        APPEND, TOSTRING
    }
}
