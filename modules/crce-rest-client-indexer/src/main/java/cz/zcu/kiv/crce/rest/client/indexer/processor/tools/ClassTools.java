package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Field;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.MethodWrapper;

public class ClassTools {
    private static final String descrToClassPathRegexp = "^((\\((\\w|\\/|;)*\\)\\[?)?[A-Z])";
    private static final String descrToClassPathRegexpEnd = "((\\$|\\.).*)";
    private static final String descrClassNameRegexpEnd = "((\\.).*)";
    private static final String methodSetGetPrefixRegExp = "^(get)";
    private static final String baseTypeRegex = "[BCDFIJSZ]";
    private static final Pattern baseTypePattern = Pattern.compile(baseTypeRegex);
    private static final String objectTypeRegex = "^L[^;<>]+(<.*>)*;";
    private static final Pattern objectTypePattern = Pattern.compile(objectTypeRegex);
    private static final Pattern descrToClassPathPattern = Pattern.compile(descrToClassPathRegexp);
    private static final Pattern descrToClassPathEndPattern =
            Pattern.compile(descrToClassPathRegexpEnd);

    private static Set<String> primitiveClassNames = Set.of("java/lang/String", "java/lang/Integer",
            "java/lang/Float", "java/lang/Double", "java/lang/Long", "java/lang/Short",
            "java/lang/Character", "java/lang/Byte", "java/lang/Boolean");

    private static final Logger logger = LoggerFactory.getLogger(ClassTools.class);

    /**
     * Checks if classname is one of the primitive classes like java/lang/String, java/lang/Integer
     * etc.
     * 
     * @param className Classname
     * @return Is class primitive
     */
    public static boolean isPrimitive(String className) {
        return primitiveClassNames.contains(className);
    }

    /**
     * Converts methods like setX, getX into X
     * 
     * @param methodName Method name
     * @return X
     */
    private static String methodNameIntoFieldName(String methodName) {
        String newMethodName = methodName.replaceFirst(methodSetGetPrefixRegExp, "");
        return newMethodName.replaceFirst(newMethodName.charAt(0) + "",
                Character.toLowerCase(newMethodName.charAt(0)) + "");
    }

    /**
     * Converting description to owner like style (java/lang/String etc.)
     * 
     * @param description
     * @return Class name
     */
    public static String descriptionToClassPath(String description) {
        /*         if (isPrimitive(description)) {
            return description;
        } */
        try {
            Matcher matcherStart = descrToClassPathPattern.matcher(description);
            Matcher matcherEnd = descrToClassPathEndPattern.matcher(matcherStart.replaceFirst(""));

            String output = matcherEnd.replaceAll("").replace(";", "");
            return output;
        } catch (Exception ex) {
            ex.printStackTrace();
            //ex.printStackTrace();
            return "";

        }

    }

    /**
     * Converts descrtiption into class name
     * 
     * @param description
     * @return ClassName
     */
    public static String descriptionToClassName(String description) {
        String output = description.replaceFirst(descrToClassPathRegexp, "")
                .replaceAll(descrClassNameRegexpEnd, "").replaceAll(";", "").replace("$", ".");
        return output;
    }

    /**
     * Processes nested types in generic wrapper
     * 
     * @param signature
     * @return All nested types
     */
    private static List<Object> getTypes(String signature) {
        Stack<Object> typesStack = new Stack<>();
        processTypes(signature, typesStack);
        List<Object> types = new LinkedList<>(typesStack);
        Collections.reverse(types);
        return types;
    }

    /**
     * Processes signature and gains types from that
     * 
     * @param signature
     */
    public static Stack<Object> processTypes(String signature) {
        Stack<Object> types = new Stack<>();
        processTypes(signature, types);
        return types;
    }

    /**
     * Processes signature and gains types from that
     * 
     * @param signature
     * @param types
     */
    public static void processTypes(String signature, Stack<Object> types) {
        Matcher matcher = baseTypePattern.matcher(signature);
        String dateType;

        if (matcher.find() && matcher.start() == 0) {
            dateType = signature.substring(0, matcher.end());
            types.push(dateType);
            return;
        }

        matcher = objectTypePattern.matcher(signature);

        if (matcher.find() && matcher.start() == 0) {
            signature = signature.substring(0, matcher.end());
            if (!signature.contains("<")) {
                types.push(signature.replaceFirst("L", "").replace(";", ""));
            } else {
                String innerType = signature.replaceFirst(".*<(.*?)>.*", "$1");
                if (innerType != null) {
                    String[] params = innerType.split(";");
                    if (params.length > 1) {
                        types.push(params);
                    } else {
                        types.push(innerType.replaceFirst("L", "").replace(";", ""));
                    }
                    String remove = "<" + innerType + ">";
                    String outerType = signature.replace(remove, "");
                    processTypes(outerType, types);
                }
            }
        }
    }

    /**
     * Converts fields of class into map structure like "field_name: field_type"
     * 
     * @param class_ Input class
     * @return Map of fields
     */
    public static Map<String, Object> fieldsToMap(ClassMap classes, ClassWrapper class_) {
        Map<String, Object> map = new HashMap<>();
        final List<MethodWrapper> methods = class_.getMethods();
        final Map<String, Field> fields = class_.getFieldsContainer();

        for (final MethodWrapper method : methods) {
            final String expFieldName = methodNameIntoFieldName(method.getMethodStruct().getName());
            if (fields.containsKey(expFieldName)) {
                // checks the field has getter and setter function
                final Field field = fields.get(expFieldName);
                final String fieldName = field.getName();
                final String fieldType = field.getDataType().getBasicType();

                if (ClassTools.isPrimitive(fieldType)) {
                    map.put(fieldName, field.getDataType().getBasicType());
                } else if (classes.containsKey(fieldType)) {
                    ClassWrapper classWrapper = classes.get(fieldType);
                    map.put(fieldName, fieldsToMap(classes, classWrapper));
                } else {
                    logger.error(
                            "Could not find type/class=" + fieldType + "of this field=" + field);

                }

            }
        }
        return map;
    }
}
