package cz.zcu.kiv.crce.rest.client.indexer.tools;

public class SBTool {
    public static void clear(StringBuilder sb) {
        sb.setLength(0);
    }

    /**
     * Sets value into string builder
     * 
     * @param sb  StringBuilder
     * @param val new value
     */
    public static void set(StringBuilder sb, String val) {
        sb.setLength(0); // clear the string
        sb.append(val);
    }

    /**
     * Sets value into string builder
     * 
     * @param sb  StringBuilder
     * @param val new value
     */
    public static void set(StringBuilder sb, StringBuilder val) {
        sb.setLength(0); // clear the string
        sb.append(val.toString());
    }

    /**
     * Sets value into string builder
     * 
     * @param sb  StringBuilder
     * @param val new value
     */
    public static void set(StringBuilder sb, Object val) {
        sb.setLength(0); // clear the string
        sb.append(val.toString());
    }

    /**
     * Merges two StrinBuilders into newone
     * 
     * @param first  First StringBuilder
     * @param second Second StringBuilder
     */
    public static StringBuilder merge(StringBuilder first, StringBuilder second) {
        StringBuilder merged = new StringBuilder();
        merged.append(first.toString());
        merged.append(second.toString());
        return merged;
    }
}
