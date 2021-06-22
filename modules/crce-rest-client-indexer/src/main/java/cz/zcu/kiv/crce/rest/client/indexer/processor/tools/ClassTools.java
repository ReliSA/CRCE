package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting.BytecodeDescriptorsProcessor;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Field;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassWrapper;

public class ClassTools {
    private static final String descrToClassPathRegexp = "^((\\((\\w|\\/|;)*\\)\\[?)?[A-Z])";
    private static final String descrToClassPathRegexpEnd = "((\\$|\\.).*)";
    private static final String descrClassNameRegexpEnd = "((\\.).*)";
    private static final String genericClasss = "(<.*>)";
    private static final String baseTypeRegex = "[BCDFIJSZ]";
    private static final Pattern baseTypePattern = Pattern.compile(baseTypeRegex);
    private static final String objectTypeRegex = "^L[^;<>]+(<.*>)*;";
    private static final Pattern objectTypePattern = Pattern.compile(objectTypeRegex);
    private static final Pattern descrToClassPathPattern = Pattern.compile(descrToClassPathRegexp);
    private static final Pattern genericClassPatern = Pattern.compile(genericClasss);
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
        Matcher matcher = baseTypePattern.matcher(className);
        return primitiveClassNames.contains(className) || matcher.matches();
    }

    /**
     * Is className Enum
     * @param className ClassName
     * @return
     */
    public static boolean isEnum(String className) {
        return className.contains("java/lang/Enum");
    }

    /**
    * Is className Map
    * @param className ClassName
    * @return
    */
    public static boolean isMap(String className) {
        return className.contains("java/util/Map");
    }

    /**
    * Returns true if data type is array or collection.
    * 
    * @param type data type as string
    * @return true if data type is array or collection
    * @source G. Hessova
    */
    @SuppressWarnings("rawtypes")
    public static boolean isArrayOrCollection(String type) {
        if ("[".equals(type)) { // array
            return true;
        }
        type = type.replaceAll("/", "\\.");
        if ("java.util.Collection".equals(type))
            return true;
        try {
            Class c = Class.forName(type);
            Class[] interfaces = c.getInterfaces();
            for (Class anInterface : interfaces) {
                if ("java.util.Collection".equals(anInterface.getName())) {
                    return true;
                }
            }
        } catch (ClassNotFoundException e) {
            return false;
        }
        return false;
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
            matcherStart.find();
            matcherEnd.find();
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
        String dateType = "";

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

    private static Object signatureToType(String signature, ClassMap classes,
            Set<String> recDetection) {
        List<Object> types = ClassTools.getTypes(signature);
        return signatureToType(types, classes, recDetection);
    }

    private static Object signatureToType(List<Object> signature, ClassMap classes,
            Set<String> recDetection) {
        if (signature.size() == 0) {
            return null;
        }
        Object type = signature.remove(0);

        if (type instanceof String) {
            String typeS = (String) type;

            if (BytecodeDescriptorsProcessor.isArrayOrCollection(typeS)) {
                return List.of(signatureToType(signature, classes, recDetection));
            } else if (isMap(typeS)) {
                return typeS;
            } else if (isPrimitive(typeS)) {
                return typeS;
            } else if (!recDetection.contains(typeS)) {
                ClassWrapper classWrapper = classes.get(typeS);
                return fieldsToTypes(classes, classWrapper, recDetection);
            } else {
                return typeS;
            }
        }

        return null;
    }

    public static Map<String, Object> fieldsToTypes(ClassMap classes, ClassWrapper class_) {
        Set<String> recDetection = new HashSet<>();
        return fieldsToTypes(classes, class_, recDetection);
    }

    /**
     * Converts fields of class into map structure like "field_name: field_type"
     * 
     * @param class_ Input class
     * @return Map of fields
     */
    public static Map<String, Object> fieldsToTypes(ClassMap classes, ClassWrapper class_,
            Set<String> recDetection) {
        if (class_ == null) {
            return null;
        }
        recDetection.add(class_.getClassStruct().getName());
        Map<String, Object> map = new HashMap<>();

        final Map<String, Field> fields = class_.getFieldsContainer();

        for (Field field : fields.values()) {
            final String fieldName = field.getName();
            final String fieldType = field.getDataType().getBasicType();

            if (BytecodeDescriptorsProcessor.isArrayOrCollection(fieldType)) {
                if (field.getDataType().getInnerType() == null) {
                    if (field.getSignature() != null) {
                        map.put(fieldName,
                                signatureToType(field.getSignature(), classes, recDetection));
                        continue;
                    }
                }
                ClassWrapper classWrapper =
                        classes.get(field.getDataType().getInnerType().getBasicType());
                if (recDetection.contains(class_.getClassStruct().getName())) {
                    map.put(fieldName, List.of(class_.getClassStruct().getName()));
                    continue;
                }
                map.put(fieldName, List.of(fieldsToTypes(classes, classWrapper)));
            } else if (ClassTools.isPrimitive(fieldType)) {
                map.put(fieldName, field.getDataType().getBasicType());
            } else if (classes.containsKey(fieldType)) {
                ClassWrapper classWrapper = classes.get(fieldType);
                if (isEnum(classWrapper.getClassStruct().getParent())) {

                    map.put(fieldName,
                            classWrapper.getFieldNames().stream()
                                    .filter((String val) -> !val.equals("$VALUES"))
                                    .collect(Collectors.toList()));
                    continue;
                }
                map.put(fieldName, fieldsToTypes(classes, classWrapper));
            } else {
                logger.error("Not serializable type/class= '" + fieldType + "' of this field= '"
                        + fieldName + "'");
                map.put(fieldName, fieldType);
            }
        }
        return Map.of("type", class_.getClassStruct().getName(), "data", map);
    }

    public static boolean isGenericClass(ClassWrapper cWrapper) {
        if (cWrapper == null) {
            return false;
        }
        ClassStruct class_ = cWrapper.getClassStruct();
        if (class_ != null && class_.getSignature() != null) {
            String signature = class_.getSignature();
            boolean patternMatch = genericClassPatern.matcher(class_.getSignature()).find();
            return signature != null && patternMatch;
        }
        return false;
    }
}
