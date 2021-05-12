package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodTools {
    public enum MethodType {
        OTHER, INIT
    }

    private static final String initString = "<init>";
    private static final Pattern argPattern = Pattern.compile("\\((.*?)\\)");
    private static final Pattern methodNamePattern = Pattern.compile("\\.<?(\\w*)>?-?\\(");

    /**
     * Checkes if
     * 
     * @param signature Signature of the method
     * @return
     */
    public static MethodType getType(String signature) {
        if (signature == null) {
            return null;
        }
        if (signature.contains(initString)) {
            return MethodType.INIT;
        }
        return MethodType.OTHER;
    }

    /**
     * Retrieves arguments from signature of method
     * 
     * @param signature Signature of method
     * @return Parsed arguments
     */
    public static String[] getArgsFromSignature(String signature) {
        Matcher matcher = argPattern.matcher(signature);
        if (matcher.find()) {
            String[] args = matcher.group(1).split(";");
            if (args == null || args.length == 0 || args[0].equals("")) {
                return null;
            }
            // cleanup
            int counter = 0;
            for (String arg : args) {
                //System.out.println("ARG=" + arg);
                args[counter++] = ClassTools.descriptionToClassPath(arg);
            }
            return args;
        }
        return null;
    }

    /**
     * Retrieves method name from the signature
     * 
     * @param signature Signature of method
     * @return Name of the method
     */
    public static String getMethodNameFromSignature(String signature) {
        Matcher matcher = methodNamePattern.matcher(signature);
        if (matcher.find()) {
            final String found = matcher.group(1);
            return found;
        }
        return null;
    }
}
