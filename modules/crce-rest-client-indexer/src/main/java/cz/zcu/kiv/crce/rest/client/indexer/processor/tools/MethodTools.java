package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.ArgConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.MethodArgType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.VarArray;
import cz.zcu.kiv.crce.rest.client.indexer.processor.VarEndpointData;
import cz.zcu.kiv.crce.rest.client.indexer.processor.Variable;

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

    /**
     * Stringifies variable
     * 
     * @param var Variable
     * @return Stringified variable
     */
    private static String getStringValueVar(Variable var) {
        String val = var.getValue() != null ? var.getValue().toString() : null;
        if (val == null || val.length() == 0) {
            return var.getDescription();
        }
        return val;
    }

    //TODO: zpracovat interfaces, classes atd z nastavení args??
    public static Map<String, Object> parseArgs(Stack<Variable> values, Set<Set<ArgConfig>> args) {
        Map<String, Object> output = new HashMap<>();
        if (args == null || values.isEmpty()) {
            return output;
        }
        for (final Set<ArgConfig> versionOfArgs : args) {
            if (versionOfArgs.size() == values.size()) {
                for (ArgConfig arg : versionOfArgs) {
                    final Variable var = values.pop();
                    final Object val = var.getValue();
                    if (arg.getType() == MethodArgType.UNKNOWN) {
                        continue;
                    }
                    if (val instanceof VarArray) {
                        VarArray arrayCasted = (VarArray) val;
                        output.put(arg.getType().name(), arrayCasted.getInnerArray());
                    } else if (val instanceof VarEndpointData || val instanceof Endpoint) {
                        output.put(arg.getType().name(), val);
                    } else {
                        output.put(arg.getType().name(), getStringValueVar(var));
                    }

                }
            }
        }
        return output;
    }
}
