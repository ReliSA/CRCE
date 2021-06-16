package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting.BytecodeDescriptorsProcessor;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointBody;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointParameter;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Header;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.HeaderGroup;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.ParameterCategory;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.ArgConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.MethodArgType;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures.IWSClient;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures.WSClientType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.EndpointData;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.MethodArg;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.VarArray;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.Variable;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.Variable.VariableType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.HeaderTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.SafeStack;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.StringTools;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HttpMethod;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HttpMethodExt;

public class VariableFactory {

    private static final Logger logger = LoggerFactory.getLogger(VariableFactory.class);


    private static String getStringValueFromArgValue(Variable varArg) {
        String argVal = (String) varArg.getValue();

        if (argVal == null || argVal.length() == 0) {
            return varArg.getDescription();
        }
        return argVal;
    }

    /**
     * Converts varArray into String list
     * @param varArray
     * @return
     */
    private static List<String> varArrayToStringList(VarArray varArray) {
        List<String> output = new LinkedList<>();
        if (varArray.getInnerArray() == null || varArray.getInnerArray().length == 0) {
            return output;
        }
        for (final Variable variable : varArray.getInnerArray()) {
            if (variable != null) {
                output.add((String) variable.getValue());
            }
        }
        return output;
    }

    /**
     * Process helping stack and retrieves arguments bases on method configuration
     * @param values Helping stack
     * @param method Configuration of method
     * @return Extracted args from stack
     */
    private static Map<MethodArgType, MethodArg> getArgsFromStack(Stack<Variable> values,
            IWSClient method) {
        Map<MethodArgType, MethodArg> output = new LinkedHashMap<>();
        if (method.getArgs() == null && method.getVarArgs() == null || values.isEmpty()) {
            return output;
        }
        Set<ArgConfig> lastPossibleVersion = null;
        Set<ArgConfig> lastPossibleVersionVar = null;
        Set<ArgConfig> exactVersion = null;
        Set<ArgConfig> exactVersionVar = null;
        boolean varArgs = false;
        for (final Set<ArgConfig> versionOfArgs : method.getArgs()) {
            exactVersion = versionOfArgs;
            if (versionOfArgs.size() == values.size()) {
                lastPossibleVersion = versionOfArgs;
                Iterator<ArgConfig> argIter = versionOfArgs.iterator();
                for (int i = versionOfArgs.size() - 1; i >= 0; i--) {
                    Variable var = values.get(i);
                    final String desc = var.getOwner() != null ? var.getOwner() : var.getOwner();
                    ArgConfig current = argIter.next();
                    if (!current.getClasses().contains("java/lang/Object")
                            && !current.getClasses().contains(desc)) {
                        exactVersion = null;
                        break;
                    }
                }
            } else {
                exactVersion = null;
            }
            if (exactVersionVar != null) {
                break;
            }
        }
        if (exactVersion == null) {
            for (final Set<ArgConfig> versionOfArgs : method.getVarArgs()) {
                exactVersionVar = versionOfArgs;
                if (versionOfArgs.size() <= values.size()) {
                    lastPossibleVersionVar = versionOfArgs;
                    Iterator<ArgConfig> argIter = versionOfArgs.iterator();
                    for (int i = versionOfArgs.size() - 1; i >= 0; i--) {
                        Variable var = values.get(i);
                        final String desc = !StringTools.isEmpty(var.getOwner()) ? var.getOwner()
                                : var.getDescription();
                        ArgConfig current = argIter.next();
                        if (current.getClasses() == null
                                || (!current.getClasses().contains("java/lang/Object")
                                        && !current.getClasses().contains(desc))) {
                            exactVersionVar = null;
                            break;
                        }
                    }
                } else {
                    exactVersionVar = null;
                }
                if (exactVersionVar != null) {
                    break;
                }
            }
        }

        Set<ArgConfig> versionOfArgs = null;

        if (exactVersion != null) {
            versionOfArgs = exactVersion;
        }

        if (exactVersionVar != null) {
            varArgs = true;
            versionOfArgs = exactVersionVar;
        }

        if (versionOfArgs == null) {
            if (lastPossibleVersion != null) {
                versionOfArgs = lastPossibleVersion;
            } else if (lastPossibleVersionVar != null) {
                versionOfArgs = lastPossibleVersionVar;
                varArgs = true;
            }
        }

        if (versionOfArgs == null) {
            return output;
        }
        ArgConfig lastArg = null;
        for (ArgConfig arg : versionOfArgs) {
            final Variable var = values.pop();
            MethodArg mArg = new MethodArg();
            if (arg.getType() == MethodArgType.UNKNOWN) {
                continue;
            }
            mArg.setVar(var);
            mArg.setDataFromArgConfig(arg);
            lastArg = mArg;
            output.put(arg.getType(), mArg);
        }
        if (varArgs && lastArg != null && values.size() > 0) {
            Variable newVariable = new Variable();
            newVariable.setType(VariableType.LIST);
            List<Variable> args = new LinkedList<>();
            while (values.size() > 0) {
                Variable last = SafeStack.peek(values);
                final String desc =
                        last.getOwner() != null ? last.getOwner() : last.getDescription();
                if (lastArg.getClasses().contains(desc)) {
                    args.add(new Variable().setValue(values.pop()));
                }
            }
            newVariable.setValue(args);
            MethodArg methodArg = new MethodArg();
            methodArg.setDataFromArgConfig(lastArg);
            methodArg.setVar(newVariable);
            output.put(lastArg.getType(), methodArg);
        }
        return output;

    }

    /**
     * Processes settings methods for request 
     * @param argValue Argument provided to method
     * @param headers 
     * @param endpointData
     */
    private static void processSettingsArg(MethodArg argValue, Stack<Header> headers,
            Endpoint endpointData) {
        if (argValue.getVar().getValue() instanceof VarArray) {
            VarArray varArray = (VarArray) argValue.getVar().getValue();
            MethodArg methodArg = new MethodArg();
            for (final Variable variable : varArray.getInnerArray()) {
                methodArg.setVar(variable);
                processSettingsArg(variable, argValue.getType(), headers, endpointData);
            }
        } else {
            processSettingsArg(argValue.getVar(), argValue.getType(), headers, endpointData);
        }
    }

    /**
     * Processes method argument 
     * @param argValue Method argument
     * @param argType Method argument type
     * @param headers Headers for filling up
     * @param endpointData Object which holds information about endpoint
     */
    private static void processSettingsArg(Variable argValue, MethodArgType argType,
            Stack<Header> headers, Endpoint endpointData) {
        switch (argType) {
            case ENDPOINT_DATA:
                //SKIP -> wrapped in EndpointData
                break;
            case HEADER:
                //SKIP -> wrapped in EndpointData
                break;
            case HTTP_METHOD: {
                final HttpMethod httpMethod = (HttpMethod) argValue.getValue();
                endpointData.addHttpMethod(httpMethod);
            }
                break;
            case REQUEST_BODY: {
                if (argValue.getType() == VariableType.ENDPOINT_DATA) {
                    Endpoint endpointFromArg = (Endpoint) argValue.getValue();
                    if (endpointFromArg.getObjects().size() > 0) {
                        for (String obj : endpointFromArg.getObjects()) {
                            endpointData.addParameter(new EndpointParameter("", obj,
                                    ClassTools.isArrayOrCollection(obj), ParameterCategory.BODY));
                        }
                        break;
                    }
                } else {
                    final String request =
                            ClassTools.descriptionToClassName(getStringValueFromArgValue(argValue));
                    endpointData.addParameter(new EndpointParameter(null, request,
                            ClassTools.isArrayOrCollection(request), ParameterCategory.BODY));
                }
            }
                break;
            case OBJECT: {
                final String object =
                        ClassTools.descriptionToClassName(getStringValueFromArgValue(argValue));
                endpointData.addObject(object);

            }
                break;
            case REQUEST_CALLBACK:
                //what to do with REQUEST CALLBACK???
                break;
            case URI_VARIABLE: {
                processURIVariable(argValue, endpointData);
            }
                break;
            case RESPONSE: {
                if (argValue.getType() == VariableType.ENDPOINT_DATA) {
                    Endpoint endpointFromArg = (Endpoint) argValue.getValue();
                    if (endpointFromArg.getObjects().size() > 0) {
                        for (String obj : endpointFromArg.getObjects()) {
                            endpointData.addExpectedResponse(
                                    new EndpointBody(obj, ClassTools.isArrayOrCollection(obj)));
                        }
                        break;
                    }
                } else {
                    final String object =
                            ClassTools.descriptionToClassName(getStringValueFromArgValue(argValue));
                    endpointData.addExpectedResponse(
                            new EndpointBody(object, ClassTools.isArrayOrCollection(object)));
                }
            }
                break;
            case UNKNOWN:
                break;
            case BASE_URL:
            case URL: {
                final String url = (String) argValue.getValue();
                endpointData.setBaseUrl(url);
            }
                break;
            case PATH: {
                final String path = (String) argValue.getValue();
                endpointData.setPath(path);
            }
                break;
            default:
                break;
        }

    }

    /**
     * Processes arguments of request
     * @param methodArg Method argument
     * @param endpointData Object which holds information about endpoint
     */
    @SuppressWarnings("unchecked")
    private static void processHeaderArg(MethodArg methodArg, Endpoint endpointData) {
        List<String> values;
        if (methodArg.getVar().getType() == VariableType.ARRAY
                && methodArg.getVar().getValue() instanceof VarArray) {
            values = varArrayToStringList((VarArray) (methodArg.getVar().getValue()));
        } else if (methodArg.getVar().getType() == VariableType.LIST) {
            values = new LinkedList<>();
            for (Variable var : (List<Variable>) methodArg.getVar().getValue()) {
                values.add((String) var.getValue());
            }
        } else {
            values = List.of((String) methodArg.getVar().getValue());
        }
        for (final String value : values) {
            processHeaderArg(value, methodArg.getType(), endpointData);
        }
    }

    /**
     * Processes arguments of request
     * @param methodArg Method argument
     * @param argType Type of the argument
     * @param endpointData Object which holds information about endpoint
     */
    private static void processHeaderArg(String argValue, MethodArgType argType,
            Endpoint endpointData) {

        Header newHeader = new Header(argType.getMethodArgType(), argValue);
        if (HeaderTools.isControlType(argType)) {
            newHeader.setHeaderGroup(HeaderGroup.CONTROL);
            endpointData.addControlsHeaders(newHeader);
        } else if (HeaderTools.isConditionalType(argType)) {
            newHeader.setHeaderGroup(HeaderGroup.CONDITIONAL);
            endpointData.addConditionals(newHeader);
        } else if (HeaderTools.isContentNegotiation(argType)) {
            newHeader.setHeaderGroup(HeaderGroup.CONTENT_NEGOTIATION);
            endpointData.addContentNegotiation(newHeader);
        } else if (HeaderTools.isAuthenticationCredentials(argType)) {
            newHeader.setHeaderGroup(HeaderGroup.AUTHENTICATION_CREDENTIALS);
            endpointData.addAuthenticationCredentials(newHeader);
        } else if (HeaderTools.isRequestContext(argType)) {
            newHeader.setHeaderGroup(HeaderGroup.REQUEST_CONTEXT);
            endpointData.addRequestContext(newHeader);
        } else if (HeaderTools.isRepresentation(argType)) {
            newHeader.setHeaderGroup(HeaderGroup.REPRESENTATION);
            endpointData.addRepresentationHeader(newHeader);
        }

        if (newHeader.getHeaderGroup() == null) {
            endpointData.addResponseHeader(newHeader);
        }
    }

    /**
     * Processes generic arguments
     * @param key Key value of the argument
     * @param dataHolderRecord One record
     * @param methodArgType Type of the method argument
     * @param endpointData Object which holds information about endpoint
     */
    private static void processGenericArgs(String key, List<MethodArg> dataHolderRecord,
            MethodArgType methodArgType, Endpoint endpointData) {
        for (final MethodArg item : dataHolderRecord) {
            processGenericArgs(key, item, methodArgType, endpointData);
        }
    }

    /**
     * Processes URI variables
     * @param data Variable which holds information about URI
     * @param endpointData Object which holds information about endpoint
     */
    @SuppressWarnings("unchecked")
    private static void processURIVariable(Variable data, Endpoint endpointData) {
        if (data.getType() == VariableType.MAP && data.getValue() instanceof Map) {
            //java/lang/Map structure given as URI variable
            Map<String, Variable> map = (Map<String, Variable>) data.getValue();
            for (final String key : map.keySet()) {
                final Variable variable = map.get(key);
                if (variable.getType() == VariableType.ARRAY
                        && variable.getValue() instanceof VarArray) {
                    VarArray varArray = (VarArray) variable.getValue();
                    for (Variable varArrayItem : varArray.getInnerArray()) {
                        endpointData.addParameter(new EndpointParameter(key,
                                varArrayItem.getDescription(), BytecodeDescriptorsProcessor
                                        .isArrayOrCollection(varArrayItem.getDescription()),
                                ParameterCategory.QUERY));
                    }
                } else {
                    endpointData.addParameter(new EndpointParameter(
                            key, variable.getDescription(), BytecodeDescriptorsProcessor
                                    .isArrayOrCollection(variable.getDescription()),
                            ParameterCategory.QUERY));
                }
            }
        } else {
            endpointData.addParameter(new EndpointParameter(null, data.getDescription(),
                    BytecodeDescriptorsProcessor.isArrayOrCollection(data.getDescription()),
                    ParameterCategory.QUERY));
        }
    }

    /**
     * Processes argumens and 
     * @param key Key under which data are stored 
     * @param data Argument of method holding important information
     * @param methodArgType Type of the method argument
     * @param endpointData Object which holds information about endpoint
     */
    private static void processGenericArgs(String key, MethodArg data, MethodArgType methodArgType,
            Endpoint endpointData) {
        switch (methodArgType) {
            case HEADER: {
                MethodArgType headerType = MethodArgType.ofValue(key);
                processHeaderArg(data.getVar().getValue().toString(), headerType, endpointData);
            }
                break;
            case URI_VARIABLE: {
                processURIVariable(data.getVar(), endpointData);
            }
                break;
            case MATRIX: {
                endpointData
                        .addParameter(new EndpointParameter(null, (String) data.getVar().getValue(),
                                data.isArray(), ParameterCategory.MATRIX));
            }
                break;
            case SET_COOKIE:
            case COOKIE: {
                endpointData.addParameter(new EndpointParameter(
                        key, data.getVar().getDescription(), BytecodeDescriptorsProcessor
                                .isArrayOrCollection(data.getVar().getDescription()),
                        ParameterCategory.COOKIE));
            }
            default:;
        }
    }

    /**
     * Processes KEY and VALUE type of arguments (generic arguments - context given by method type)
     * @param argValue  Argument of method
     * @param dataHolder Storage of all KEY and VALUE information for given method
     */
    private static void processKeyOrValue(MethodArg argValue,
            LinkedHashMap<String, List<MethodArg>> dataHolder) {
        if (argValue.getType() == MethodArgType.KEY) {
            dataHolder.put((String) argValue.getVar().getValue(), new LinkedList<>());
        } else if (argValue.getType() == MethodArgType.VALUE) {
            ArrayList<String> keys = new ArrayList<>(dataHolder.keySet());
            final String lastKey = keys.get(keys.size() - 1);
            dataHolder.get(lastKey).add(argValue);
        }
    }

    /**
     * Retrieves data about endpoint from helping Stack and method configuration
     * @param values Stack
     * @param method Method configuration
     * @return Object which contains informations about endpoint
     */
    public static Endpoint getEndpointData(Stack<Variable> values, IWSClient method) {
        Map<MethodArgType, MethodArg> argValues = getArgsFromStack(values, method);
        Endpoint endpointData = new Endpoint();
        Stack<Header> headers = new Stack<>();
        LinkedHashMap<String, List<MethodArg>> dataHolder = new LinkedHashMap<>();

        for (final MethodArg arg : argValues.values()) {
            Object argValue = arg.getVar().getValue();
            //arg contains endpoint data or endpoint itself (does not matter what arg type it is) => merge it
            if (argValue instanceof Endpoint || argValue instanceof EndpointData) {
                Endpoint valCast = (Endpoint) argValue;
                endpointData.merge(valCast);
            }
            if (arg.getType() == MethodArgType.KEY || arg.getType() == MethodArgType.VALUE
                    && method.getType() == WSClientType.SETTINGS) {
                processKeyOrValue(arg, dataHolder);
            } else if (arg.getType().ordinal() < MethodArgType.EMPTY.ordinal()) {
                processSettingsArg(arg, headers, endpointData);
            } else if (arg.getType().ordinal() > MethodArgType.EMPTY.ordinal()) {
                processHeaderArg(arg, endpointData);
            }
        }
        //add processed headers to endpointData
        for (final Header header : headers) {
            switch (header.getHeaderGroup()) {
                case CONTROL:
                    endpointData.addControlsHeaders(header);
                    break;
                case AUTHENTICATION_CREDENTIALS:
                    endpointData.addAuthenticationCredentials(header);
                    break;
                case CONDITIONAL:
                    endpointData.addConditionals(header);
                    break;
                case CONTENT_NEGOTIATION:
                    endpointData.addContentNegotiation(header);
                    break;
                case REQUEST_CONTEXT:
                    endpointData.addRequestContext(header);
                    break;
                default:;
            }
        }

        for (final String key : dataHolder.keySet()) {
            List<MethodArg> mapValue = dataHolder.get(key);
            processGenericArgs(key, mapValue, (MethodArgType) method.getInnerType(), endpointData);
        }
        if (method.getInnerType() instanceof HttpMethodExt) {
            if (method.getInnerType().ordinal() < HttpMethodExt.GENERIC.ordinal()) {
                endpointData.addHttpMethod(HttpMethod.values()[method.getInnerType().ordinal()]);
            }
        }
        return endpointData;
    }

    public static void main(String[] args) {
        System.out.println("TEST");
    }
}
