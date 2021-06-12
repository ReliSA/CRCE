package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.util.ArrayList;
import java.util.HashMap;
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
import cz.zcu.kiv.crce.rest.client.indexer.shared.HttpMethod;

public class VariableFactory {

    private static final Logger logger = LoggerFactory.getLogger(VariableFactory.class);

    /**
     * Converts varArray into String list
     * @param varArray
     * @return
     */
    private static List<String> varArrayToStringList(VarArray varArray) {
        List<String> output = new LinkedList<>();
        for (final Variable variable : varArray.getInnerArray()) {
            output.add((String) variable.getValue());
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
        Map<MethodArgType, MethodArg> output = new HashMap<>();
        if (method.getArgs() == null && method.getVarArgs() == null || values.isEmpty()) {
            return output;
        }
        for (final Set<ArgConfig> versionOfArgs : method.getArgs()) {
            if (versionOfArgs.size() == values.size()) {
                for (ArgConfig arg : versionOfArgs) {
                    final Variable var = values.pop();
                    MethodArg mArg = new MethodArg();
                    if (arg.getType() == MethodArgType.UNKNOWN) {
                        continue;
                    }
                    mArg.setVar(var);
                }
            }
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
                endpointData.addParameter(new EndpointParameter(null, argValue.getDescription(),
                        ClassTools.isArrayOrCollection(argValue.getDescription())));
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
                endpointData.addExpectedResponse(new EndpointBody(argValue.getDescription(),
                        ClassTools.isArrayOrCollection(argValue.getDescription())));
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
    private static void processHeaderArg(MethodArg methodArg, Endpoint endpointData) {
        List<String> values;
        if (methodArg.getVar().getType() == VariableType.ARRAY
                && methodArg.getVar().getValue() instanceof VarArray) {
            values = varArrayToStringList((VarArray) (methodArg.getVar().getValue()));
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
        if (argType.ordinal() <= MethodArgType.ACCEPT_RANGES.ordinal()) {
            Header header = new Header(argType.name(), argValue);
            HeaderType.valueOf(arg0)
            endpointData.addConsumes(header);

        } else if (argType.ordinal() >= MethodArgType.CONTENT_ENCODING.ordinal()
                && argType.ordinal() <= MethodArgType.CONTENT_TYPE.ordinal()) {
            Header header = new Header(argType.name(), argValue);
            endpointData.addProduces(header);
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
                endpointData.addHeader(new Header(key, data.getVar().getValue().toString()));
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
                continue;
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

        return endpointData;
    }

    public static void main(String[] args) {
        System.out.println("TEST");
    }
}
