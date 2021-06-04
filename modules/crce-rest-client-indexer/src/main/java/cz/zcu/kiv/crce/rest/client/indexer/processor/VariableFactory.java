package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointBody;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Header;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation.OperationType;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.ArgConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.MethodArgType;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures.WSClient;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.tools.ConfigTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.EndpointData;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.MethodArg;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.VarArray;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.Variable;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.Variable.VariableType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.HeaderTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.MethodTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.MethodTools.MethodType;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HttpMethod;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.SafeStack;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.VariableTools;

public class VariableFactory {

    private static final Logger logger = LoggerFactory.getLogger(VariableFactory.class);

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
    private static Map<MethodArgType, MethodArg> getArgsFromStack(Stack<Variable> values,
            Set<Set<ArgConfig>> args) {
        Map<MethodArgType, MethodArg> output = new HashMap<>();
        if (args == null || values.isEmpty()) {
            return output;
        }
        for (final Set<ArgConfig> versionOfArgs : args) {
            if (versionOfArgs.size() == values.size()) {
                for (ArgConfig arg : versionOfArgs) {
                    final Variable var = values.pop();
                    final Object val = var.getValue();
                    MethodArg mArg = new MethodArg();
                    if (arg.getType() == MethodArgType.UNKNOWN) {
                        continue;
                    }
                    if (val instanceof VarArray) {
                        VarArray arrayCasted = (VarArray) val;
                        mArg.setValue(arrayCasted);
                        output.put(arg.getType(), mArg);
                    } else if (val instanceof EndpointData || val instanceof Endpoint) {
                        mArg.setValue(val);
                        output.put(arg.getType(), mArg);
                    } else {
                        mArg.setValue(getStringValueVar(var));
                        output.put(arg.getType(), mArg);
                    }

                }
            }
        }
        return output;
    }

    /**
    * Checking variable for its existence and type with logging errors
    * @param var Variable for checking
    * @param type expected VariableType
    * @return
    */
    private static boolean checkVariable(Variable var, VariableType type) {
        if (VariableTools.isEmpty(var)) {
            logger.error("No Variable on top of the Stack");
            return false;
        }
        if (var.getType() != type) {
            logger.error("Expected " + type.name() + " but got " + var.getType().name());
            return false;
        }
        return true;
    }

    public static Endpoint getEndpointData(Stack<Variable> values, Set<Set<ArgConfig>> wsClientArgsConfigs) {
        Map<MethodArgType, MethodArg> argValues = getArgsFromStack(values, wsClientArgsConfigs);
        Endpoint endpointData = new Endpoint();
        Stack<Header> headers = new Stack<>();
        for (final MethodArg arg : argValues.values()) {
            Object argValue = arg.getValue();
            //arg contains endpoint data or endpoint itself (does not matter what arg type it is) => merge it
            if (argValue instanceof Endpoint || argValue instanceof EndpointData) {
                Endpoint valCast = (Endpoint) argValue;
                endpointData.merge(valCast);
                continue;
            }
            switch (arg.getType()) {
                case ENDPOINT_DATA:
                    //SKIP -> wrapped in EndpointData
                    break;
                case HEADER:
                    //SKIP -> wrapped in EndpointData
                    break;
                case HTTP_METHOD: {
                    final HttpMethod httpMethod = (HttpMethod) argValue;
                    endpointData.addHttpMethod(httpMethod);
                }
                    break;
                case REQUEST_BODY: {
                    final String objectDescription = (String) argValue;
                    endpointData.addRequestBody(new EndpointBody(objectDescription,
                            ClassTools.isArrayOrCollection(objectDescription)));
                }
                    break;
                case REQUEST_CALLBACK:
                    //what to do with REQUEST CALLBACK???
                    break;
                case HEADER_TYPE: {
                    final String headerType = (String) argValue;
                    final Header lastHeader = SafeStack.peek(headers);
                    // no header on stack or last header does not have headerType set
                    if (lastHeader == null || lastHeader.getType() != null) {
                        Header newHeader = new Header(headerType, (String) null);
                        headers.push(newHeader);
                    } else {
                        lastHeader.setType(headerType);
                    }
                }
                    break;
                case HEADER_VALUE: {
                    final String headerValue = (String) argValue;
                    final Header lastHeader = SafeStack.peek(headers);
                    if (lastHeader == null || lastHeader.getValue() != null) {
                        Header newHeader = new Header(null, headerValue);
                        headers.push(newHeader);
                    } else {
                        lastHeader.setValue(headerValue);
                    }
                }
                    break;
                case URI_VARIABLE:
                    break;
                case RESPONSE:
                    final String objectDescription = (String) argValue;
                    endpointData.addExpectedResponse(new EndpointBody(objectDescription,
                            ClassTools.isArrayOrCollection(objectDescription)));
                    break;
                case UNKNOWN:
                    break;
                case BASE_URL:
                case URL: {
                    final String url = (String) argValue;
                    endpointData.setBaseUrl(url);
                }
                    break;
                case PATH: {
                    final String path = (String) argValue;
                    endpointData.setPath(path);
                }
                    break;
                default:
                    break;
            }
        }
        //add processed headers to endpointData
        for (final Header header : headers) {
            if (HeaderTools.isConsumingType(header)) {
                endpointData.addConsumes(header);
            } else {
                endpointData.addProduces(header);
            }
        }
        //LOAD DATA FROM ARGS
        //return lastVal.setValue(value);
        return null;
    }

    public static Variable getData(Stack<Variable> values, Set<Set<ArgConfig>> args) {
        Map<MethodArgType, MethodArg> argValues = getArgsFromStack(values, args);
        Endpoint endpointData = new Endpoint();
        MethodArg mArg = new MethodArg();
        Map<String, String> data = null;
        for (final MethodArgType key : argValues.keySet()) {
            switch (key) {
                case KEY:
                    //TODO: set data into MAP
                    break;
                case VALUE:
                    //mArg.setValue();
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    /*     public static Endpoint getEndpointData(Stack<Variable> values, Set<Set<ArgConfig>> args) {
        Map<MethodArgType, Object> argValues = getArgsFromStack(values, args);
        Variable lastVal = SafeStack.pop(values);
        if (checkVariable(lastVal, VariableType.ENDPOINT_DATA)) {
            return null;
        }
        //LOAD DATA FROM ARGS
        //return lastVal.setValue(value);
        return null;
    } */

    private static final Map<String, Map<String, WSClient>> wsClientData =
            ConfigTools.wsClientData();

    private static WSClient getWSClientMethod(String methodOwner, String methodName) {
        return wsClientData.get(methodOwner).get(methodName);
    }

    private static boolean isWSClientData(Operation operation) {
        final String owner = operation.getOwner();
        final String methodName = operation.getMethodName();

        if (methodName == null || owner == null) {
            return false;
        }

        return wsClientData.containsKey(owner) && wsClientData.get(owner).containsKey(methodName);
    }



    private static Variable getWSClientData(Stack<Variable> stack, Operation operation) {
        final String methodName = operation.getMethodName();
        final String methodOwner = operation.getOwner();
        final String[] args = MethodTools.getArgsFromSignature(operation.getDescription());
        Variable variable = new Variable();

        variable.setType(VariableType.WS_CLIENT_DATA);
        WSClient wsClientMethod = getWSClientMethod(methodOwner, methodName);

        return null;

    }

    public static Variable getVariable(Stack<Variable> stack, Operation operation) {

        if (operation.getType() == OperationType.CALL
                && MethodTools.getType(operation.getDescription()) == MethodType.INIT) {

        }

        return null;

    }

    public static void main(String[] args) {
        System.out.println("TEST");
    }
}
