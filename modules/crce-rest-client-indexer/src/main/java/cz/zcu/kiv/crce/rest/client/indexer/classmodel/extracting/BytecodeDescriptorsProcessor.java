package cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.DataType;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Method;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.MethodSignature;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Variable;

/**
 * Created by ghessova on 24.04.2018.
 *
 * Class for extracting information from bytecode descriptors and signatures (for methods, classes,
 * fields).
 *
 * A descriptor is a string representing the type of a field or method. Descriptors are represented
 * in the class file format using modified UTF-8 strings.
 *
 * https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3.2
 */
public class BytecodeDescriptorsProcessor {

    private static String baseTypeRegex = "[BCDFIJSZ]";
    private static Pattern baseTypePattern = Pattern.compile(baseTypeRegex);

    private static String objectTypeRegex = "^L[^;<>]+(<.*>)*;";
    private static Pattern objectTypePattern = Pattern.compile(objectTypeRegex);

    private static Pattern voidPattern = Pattern.compile("V");

    /*
     * private static final Logger logger =
     * LoggerFactory.getLogger(BytecodeDescriptorsProcessor.class);
     */


    private static boolean isPrimitive(String dataType) {
        return dataType.matches(baseTypeRegex);
    }

    public static boolean isPrimitiveOrString(String dataType) {
        return "java/lang/String".equals(dataType) || isPrimitive(dataType);
    }

    /**
     *
     * Method processes method descriptor and sets parameter data types and return type to the given
     * method.
     * 
     * @param desc   MethodDescriptor: ( ParameterDescriptor* ) ReturnDescriptor
     * @param method method object the values are set fto
     */
    public static void processMethodDescriptor(String desc, Method method) {
        if (desc.contains("T;") || desc.contains(":"))
            return;
        try {
            MethodSignature methodSignature = processMethodDescriptor(desc);
            List<DataType> paramDataTypes = methodSignature.getParameterTypes();

            // set parameters and return type
            List<Variable> parameters = new ArrayList<>(paramDataTypes.size());
            for (DataType paramDataType : paramDataTypes) {
                parameters.add(new Variable(paramDataType));
            }
            method.setParameters(parameters);
            method.setReturnType(methodSignature.getReturnType());
        } catch (Exception e) {
            /*
             * logger.error("Error when processing method with desc: " + desc, e);
             */ }

    }


    /**
     * Processes method descriptor (or method signature) and retrieves information about method
     * return type and parameter types.
     * 
     * @param desc method descriptor or method signature
     * @return method signature - information about return type and parameter types
     */
    public static MethodSignature processMethodDescriptor(String desc) {

        String[] parts = desc.split("\\)");
        String parametersDescriptor = parts[0].substring(parts[0].indexOf("(") + 1);
        String returnDescriptor = parts[1]; // todo throws part
        // retrieve data types
        List<DataType> paramDataTypes = processParametersDescriptor(parametersDescriptor);
        DataType returnDataType = processReturnDescriptor(returnDescriptor);

        return new MethodSignature(returnDataType, paramDataTypes);
    }

    /**
     * Retrieves the name of generic class from class type signature.
     * 
     * @param classTypeSign class type signature
     * @return name of generic class
     */
    private static String getOuterGenericType(String classTypeSign) {
        String outerType = classTypeSign.replaceAll("<.*>", "");
        outerType = outerType.replaceFirst("L", "");
        outerType = outerType.substring(0, outerType.length() - 1);
        return outerType;
    }

    /**
     * Gets type parameter from class type signature.
     * 
     * @param classTypeSign
     * @return
     */
    private static String getInnerGenericType(String classTypeSign) {
        String inner = classTypeSign.substring(classTypeSign.indexOf("<") + 1,
                classTypeSign.lastIndexOf(">"));
        if (inner.startsWith("L")) {
            return getFullClassName(inner);
        } else {
            return inner;
        }

    }

    /**
     * Processes parameters descriptor and returns list of parameters data types.
     * 
     * @param desc parameters descriptor
     * @return list of parameters data types
     */
    private static List<DataType> processParametersDescriptor(String desc) {
        List<DataType> dataTypes = new ArrayList<>();
        DateTypeWrapper wrapper;
        for (int i = 0; i < desc.length(); i = wrapper.end) {
            try {
                wrapper = processFieldDescriptor(desc, i);
                dataTypes.add(wrapper.dataType);
            } catch (Exception e) {
                /*
                 * logger.error("Error when processing parameters descriptor", e);
                 */ break;
            }
        }
        return dataTypes;
    }

    /**
     * Processes return type descriptor and returns method return type.
     * 
     * @param desc method return type descriptor
     * @return method return type - data type or void
     */
    private static DataType processReturnDescriptor(String desc) {
        Matcher matcher = voidPattern.matcher(desc);
        if (matcher.find() && matcher.start() == 0) {
            return new DataType("void");
        }
        return processFieldDescriptor(desc);
    }


    /*
     * FieldDescriptor: FieldType FieldType: BaseType | ObjectType | ArrayType BaseType: B C D F I J
     * S Z ObjectType: L ClassName ; ArrayType: [ FieldType
     */
    public static DataType processFieldDescriptor(String desc) {
        return processFieldDescriptor(desc, 0).dataType;
    }


    private static void processSignature(String type) {
        Matcher matcher = baseTypePattern.matcher(type);
        String dateType;

        if (matcher.find() && matcher.start() == 0) {
            dateType = type.substring(0, matcher.end());
            // return new DateTypeWrapper(start + matcher.end(), new DataType(datetype));
        }
        matcher = objectTypePattern.matcher(type);
        if (matcher.find() && matcher.start() == 0) {
            type = type.substring(0, matcher.end());
            DataType outType;
            if (!type.contains("<")) {
                outType = new DataType(getFullClassName(type));
            } else {
                outType = new DataType(BytecodeDescriptorsProcessor.getOuterGenericType(type));
                DataType innerType =
                        new DataType(BytecodeDescriptorsProcessor.getInnerGenericType(type));
                outType.setInnerType(innerType);

            }
        }
    }


    /**
     * Processes parameters descriptor and extracts one a data type for field on current position.
     * 
     * @param desc  parameters descriptor
     * @param start current position in descriptor
     * @return data type wrapper containing the last position of field descriptor in parameters
     *         descriptor
     */
    private static DateTypeWrapper processFieldDescriptor(String desc, int start) {
        try {
            String part = desc.substring(start);
            Matcher matcher = baseTypePattern.matcher(part);
            String datetype;
            if (matcher.find() && matcher.start() == 0) {
                datetype = part.substring(0, matcher.end());
                return new DateTypeWrapper(start + matcher.end(), new DataType(datetype));
            }
            matcher = objectTypePattern.matcher(part);
            if (matcher.find() && matcher.start() == 0) {
                part = part.substring(0, matcher.end());
                DataType dateType;
                if (!part.contains("<")) {
                    dateType = new DataType(getFullClassName(part));
                } else {
                    dateType = new DataType(BytecodeDescriptorsProcessor.getOuterGenericType(part));
                    DataType innerType =
                            new DataType(BytecodeDescriptorsProcessor.getInnerGenericType(part));
                    dateType.setInnerType(innerType);

                }
                return new DateTypeWrapper(start + matcher.end(), dateType);
            }
            DataType outerType = new DataType("["); // array
            DateTypeWrapper outerWrapper = new DateTypeWrapper(0, outerType);
            DateTypeWrapper innerWrapper = processFieldDescriptor(desc, start + 1);
            outerType.setInnerType(innerWrapper.dataType);
            outerWrapper.end = innerWrapper.end;
            return outerWrapper;
        } catch (Exception e) {
            /*
             * logger.error("Error in field descriptor processing", e);
             */ return null;
        }

    }

    /**
     * Class representing data type wrapper.
     */
    private static class DateTypeWrapper {
        int end; // last index of current field descriptor in parameters descriptor string
        DataType dataType; // extracted data type from current field descriptor

        DateTypeWrapper(int end, DataType dataType) {
            this.end = end;
            this.dataType = dataType;
        }
    }

    /**
     * Returns true if data type is array or collection.
     * 
     * @param type data type as string
     * @return true if data type is array or collection
     */
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
     * Retrieves full class name from object descriptor.
     * 
     * @param desc object descriptor
     * @return full class name
     */
    public static String getFullClassName(String desc) {
        return desc.replaceFirst("L", "").replace(";", "");
    }

}

