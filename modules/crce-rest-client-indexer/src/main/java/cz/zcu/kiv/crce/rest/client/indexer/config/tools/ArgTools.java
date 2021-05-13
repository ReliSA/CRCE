package cz.zcu.kiv.crce.rest.client.indexer.config.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.extracting.BytecodeDescriptorsProcessor;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation;
import cz.zcu.kiv.crce.rest.client.indexer.config.ApiCallMethodConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config.ArgConfigType;
import cz.zcu.kiv.crce.rest.client.indexer.config.ConfigTools;
import cz.zcu.kiv.crce.rest.client.indexer.config.EDataContainerConfigMap;
import cz.zcu.kiv.crce.rest.client.indexer.config.EDataContainerMethodConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config.Header;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointParameter;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.ParameterCategory;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointRequestBody;
import cz.zcu.kiv.crce.rest.client.indexer.processor.Helpers;
import cz.zcu.kiv.crce.rest.client.indexer.processor.VarArray;
import cz.zcu.kiv.crce.rest.client.indexer.processor.VarEndpointData;
import cz.zcu.kiv.crce.rest.client.indexer.processor.Variable;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint.HttpMethod;
import cz.zcu.kiv.crce.rest.client.indexer.processor.Variable.VariableType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.HeaderTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.MethodTools;

public class ArgTools {

    private static EDataContainerConfigMap eDataConfig = ConfigTools.getEDataContainerConfigMap();

    private static boolean isURI(Object uri) {
        return (uri instanceof String);
    }

    /**
     * Extracts arguments from args of method
     * 
     * @param values Constants put into arguments
     * @param args Definition of the methods arguments
     * @return Either merged constants arguments or null
     */
    private static String extract(Stack<Variable> values, Set<ArrayList<ArgConfigType>> args) {
        String merged = "";
        for (final ArrayList<ArgConfigType> oneVersion : args) {
            if (oneVersion.size() == values.size()) {
                for (int i = 0; i < oneVersion.size(); i++) {
                    final Object value = values.remove(0).getValue();
                    if (oneVersion.get(i) == ArgConfigType.PATH && isURI(value)) {
                        merged += value;
                        return merged;
                    }
                }
            }
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

    /**
     * Retrieves parameters from stack based on definition of method arguments
     * 
     * @param values Stack
     * @param args Definition of arguments definition
     * @return Endpoint parameters
     */
    public static Map<String, Object> getParams(Stack<Variable> values,
            Set<ArrayList<ArgConfigType>> args) {
        Map<String, Object> output = new HashMap<>();
        if (args == null || values.isEmpty()) {
            return output;
        }
        for (final ArrayList<ArgConfigType> versionOfArgs : args) {
            if (versionOfArgs.size() == values.size()) {
                for (ArgConfigType definition : versionOfArgs) {
                    final Variable var = values.pop();
                    final Object val = var.getValue();
                    if (definition == ArgConfigType.SKIP) {
                        continue;
                    }
                    if (output.containsKey(definition.name())) {
                        output.put(definition.name(),
                                output.get(definition.name()) + getStringValueVar(var));
                    } else if (val instanceof VarArray) {
                        VarArray arrayCasted = (VarArray) val;
                        output.put(definition.name(), arrayCasted.getInnerArray());
                    } else if (val instanceof VarEndpointData) {
                        output.put(definition.name(), val);
                    } else if (val instanceof Endpoint) {
                        output.put(definition.name(), val);
                    } else {
                        output.put(definition.name(), getStringValueVar(var));
                    }

                }
            }
        }
        return output;
    }

    /**
     * Adds parameter to endpoint
     * 
     * @param param Parameter of endpoint
     * @param endpoint Endpoint which will be updated
     */
    private static void addParamToEndpoint(Variable param, Endpoint endpoint) {
        EndpointParameter newParam = new EndpointParameter();
        newParam.setArray(BytecodeDescriptorsProcessor.isArrayOrCollection(param.getDescription()));
        newParam.setDataType(ClassTools.descriptionToClassName(param.getDescription()));
        endpoint.addParameter(newParam);
    }

    /**
     * Adds expected response to endpoint
     * 
     * @param response Class description of expected response
     * @param endpoint Endpoint which will be updated
     */
    private static void addExpectedResponseToEndpoint(String response, Endpoint endpoint) {
        EndpointRequestBody responseBody = new EndpointRequestBody();
        responseBody.setArray(BytecodeDescriptorsProcessor.isArrayOrCollection(response));
        response = ClassTools.descriptionToClassName(response);
        responseBody.setStructure(response);
        endpoint.addExpectedResponse(responseBody);
    }

    /**
     * Handles request body and adds it to Endpoint
     * 
     * @param rBody Request body string
     * @param endpoint Endpoint which will be updated
     */
    private static void addRequestBodyToEndpoint(String rBody, Endpoint endpoint) {
        if (rBody == null || rBody.length() == 0) {
            return;
        }
        EndpointRequestBody requestBody = new EndpointRequestBody();
        requestBody.setArray(BytecodeDescriptorsProcessor.isArrayOrCollection(rBody));
        rBody = ClassTools.descriptionToClassName(rBody);
        requestBody.setStructure(rBody);
        endpoint.addRequestBody(requestBody);
    }

    //TODO:CHANGES!!
    private static interface HandleEndpointAttrI {
        public void run(Variable param);
    }
    private static interface HandleEndpointAttrIS {
        public void run(String param);
    }
    private static interface HandleEndpointPairAttrGeneric<T> {
        public void run(String param1, T param2);
    }

    /**
     * Helper method for attributes (attributeKey: attributeValue) of endpoint
     * 
     * @param param1 Param key or array of param keys
     * @param param2 Param value or Param values
     * @param method Callback for processing those values
     */
    @SuppressWarnings("unchecked")
    private static <T> void handleAttrPair(Object param1, Object param2,
            HandleEndpointPairAttrGeneric<T> method) {
        if (param1 instanceof String[] && param2.getClass().isArray()) {
            String[] params1 = (String[]) param1;
            T[] params2 = (T[]) param2;
            if (params1.length != params2.length) {
                return;
            }
            for (int i = 0; i < params1.length; i++) {
                method.run(params1[i], params2[i]);
            }
        } else if (param1 instanceof String && !param2.getClass().isArray()) {
            method.run((String) param1, (T) param2);
        } else if (param1 instanceof String && param2.getClass().isArray()) {
            T[] params2 = (T[]) param2;
            for (int i = 0; i < params2.length; i++) {
                method.run((String) param1, params2[i]);
            }
        }
    }

    /**
     * Helper method for attributes of endpoint
     * 
     * @param param Param for endpoint either String or String[]
     * @param endpoint
     * @param method
     */
    private static void handleAttr(Object param, HandleEndpointAttrI method) {
        if (param instanceof Variable[]) {
            Variable[] params = (Variable[]) param;
            for (Variable val : params) {
                method.run(val);
            }
        } else {
            method.run((Variable) param);
        }

    }

    /**
     * Helper method for attributes of endpoint
     * 
     * @param param Param for endpoint either String or String[]
     * @param endpoint
     * @param method
     */
    private static void handleAttr(Object param, HandleEndpointAttrIS method) {
        if (param instanceof String[]) {
            String[] params = (String[]) param;
            for (String val : params) {
                method.run(val);
            }
        } else {
            method.run((String) param);
        }

    }

    /**
     * Retrieves arguments from method execution
     * 
     * @param values Stack
     * @param operation Operation which contains method description
     * @return Arguments extracted from stack
     */
    private static Stack<Variable> methodArgsFromValues(Stack<Variable> values,
            Operation operation) {
        String[] methodArgsDef = MethodTools.getArgsFromSignature(operation.getDescription());
        Stack<Variable> output = new Stack<>();
        if (methodArgsDef == null || methodArgsDef.length == 0 || values.isEmpty()) {
            return output;
        }

        for (int counter = 0; counter < methodArgsDef.length; counter++) {
            output.push(values.pop());
        }
        return output;
    }


    interface HandlePathParam {

        public void run(Map<String, Object> params, Variable var, Stack<Variable> args);
    }

    /**
     * Retrieves parameters from endpoint data container and calls callback function on that
     * 
     * @param values Stack
     * @param operation Operation
     * @param callback Callback for seting params into endpoint data holder
     * @return Created endpoint from endpoint data container
     */
    private static Variable getEndpointDataFromContainer(Stack<Variable> values,
            Operation operation, HandlePathParam callback) {
        Stack<Variable> args = methodArgsFromValues(values, operation);
        EDataContainerMethodConfig methodConfig = eDataConfig.get(operation.getOwner())
                .get(MethodTools.getMethodNameFromSignature(operation.getDescription()));
        Map<String, Object> params = ArgTools.getParams(args, methodConfig.getArgs());
        VarEndpointData varEndpointData = new VarEndpointData();
        Variable newEndpointData = new Variable(varEndpointData).setType(VariableType.ENDPOINTDATA)
                .setOwner(operation.getOwner());
        callback.run(params, newEndpointData, args);
        return newEndpointData;
    }

    /**
     * Gets endpoint from container (container holds information about endpoint) into endpoint
     * itself
     * 
     * @implNote Endpoint based on parameters
     * @param values Stack
     * @param operation Operation (like FIELD, STORE, CONSTANT etc.)
     * @return new endpoint based on endpoint data container
     */
    public static Variable getEndpointAttrFromContainer(Stack<Variable> values,
            Operation operation) {
        return getEndpointDataFromContainer(values, operation,
                (Map<String, Object> params, Variable var,
                        Stack<Variable> args) -> setParamsToEndpoint(params,
                                (Endpoint) var.getValue()));
    }

    /**
     * Gets endpoint from container (container holds information about endpoint) into endpoint
     * 
     * @implNote Endpoint based on path
     * @param values Stack
     * @param methodConfig Configuration of method
     * @param operation Operation (like FIELD, STORE, CONSTANT etc.)
     * @param category Endpoint parameter Category
     * @return Endpoint data container
     */
    public static Variable getPathParamFromContainer(Stack<Variable> values,
            EDataContainerMethodConfig methodConfig, Operation operation,
            ParameterCategory category) {

        return getEndpointDataFromContainer(values, operation, (Map<String, Object> params,
                Variable var, Stack<Variable> args) -> setPathParam(var, category, params));
    }

    /**
     * Sets header parameters to endpoint stored inside Stack
     * 
     * @param values Stack put into arguments
     * @param methodConfig Configuration for method
     * @param operation operation Operation (like FIELD, STORE, CONSTANT etc.)
     * @return Variable wrapping the endpoint
     */
    public static Variable setHeaderParamFromArgs(Stack<Variable> values,
            ApiCallMethodConfig methodConfig, Operation operation) {

        return setEndpointAttrFromArgs(values, methodConfig.getArgs(), operation,
                (Map<String, Object> params_, Variable var, Stack<Variable> args) -> {
                    setHeaderParams(methodConfig, var, params_);
                    var.setValue(setParamsToEndpoint(params_, (Endpoint) var.getValue()));
                });
    }

    /**
     * 
     * @param methodConfig Config of method
     * @param var Variable which holds endpoint
     * @param params Parameter for endpoint
     */
    private static void setHeaderParams(ApiCallMethodConfig methodConfig, Variable var,
            Map<String, Object> params) {
        String headerType = methodConfig.getValue();
        Object acceptValue = params.getOrDefault(ArgConfigType.HEADERVALUE.name(), null);
        if (acceptValue != null) {
            handleAttr(acceptValue, (Variable value) -> ((Endpoint) var.getValue())
                    .addHeader(new Header(headerType, value)));
            params.remove(ArgConfigType.ACCEPT.name());
        }
    }

    /**
     * Directly sets path parameters to endpoint stored in varible
     * 
     * @param var Variable which holds endpoint
     * @param category Category of parameter (like MATRIX, QUERY, PATH etc.)
     * @param params Parameters of endpoint
     */
    private static void setPathParam(Variable var, ParameterCategory category,
            Map<String, Object> params) {

        Object param = params.getOrDefault(ArgConfigType.PARAM.name(), null);
        Object paramKey = params.getOrDefault(ArgConfigType.PARAMKEY.name(), null);
        Object paramValue = params.getOrDefault(ArgConfigType.PARAMVALUE.name(), null);

        if (param != null) {
            handleAttr(param, (String p) -> ((Endpoint) var.getValue())
                    .addParameter(new EndpointParameter(null, p, false, category)));
            params.remove(ArgConfigType.PARAM.name());
        } else if (paramKey != null && paramValue != null) {
            handleAttrPair(paramKey, (Variable) paramValue,
                    (String pKey, Variable pValue) -> ((Endpoint) var.getValue())
                            .addParameter(new EndpointParameter(null,
                                    new Header(pKey, pValue).toString(), false, category)));
            params.remove(ArgConfigType.PARAMKEY.name());
            params.remove(ArgConfigType.PARAMVALUE.name());
        }
    }

    /**
     * Sets parameters of path
     * 
     * @param values Stack
     * @param argDefs Definition of method arguments (from configuration file)
     * @param operation operation Operation (like FIELD, STORE, CONSTANT etc.)
     * @param category Category of parameter (like MATRIX, QUERY, PATH etc.)
     * @return Variable wrapping the endpoint
     */
    public static Variable setPathParamFromArgs(Stack<Variable> values,
            Set<ArrayList<ArgConfigType>> argDefs, Operation operation,
            ParameterCategory category) {
        return setEndpointAttrFromArgs(values, argDefs, operation,
                (Map<String, Object> params_, Variable var, Stack<Variable> args) -> {
                    setPathParam(var, category, params_);
                    var.setValue(setParamsToEndpoint(params_, (Endpoint) var.getValue()));
                });
    }

    /**
     * Sets endpoint attributes to endpoint which is inside Stack
     * 
     * @param values Stack
     * @param argDefs Definition of method arguments (from configuration file)
     * @param operation operation Operation (like FIELD, STORE, CONSTANT etc.)
     * @return Variable wrapping the endpoint
     */
    public static Variable setEndpointAttrFromArgs(Stack<Variable> values,
            Set<ArrayList<ArgConfigType>> argDefs, Operation operation) {
        return setEndpointAttrFromArgs(values, argDefs, operation,
                (Map<String, Object> params_, Variable var, Stack<Variable> args) -> var
                        .setValue(setParamsToEndpoint(params_, (Endpoint) var.getValue())));
    }

    /**
     * Wrapper for handling attributes of endpoint with given function callback
     * 
     * @param values Stack
     * @param argDefs Definition of method arguments (from configuration file)
     * @param operation Operation (like FIELD, STORE, CONSTANT etc.)
     * @param callback Callback which will perform some actions on endpoint Params and endpoint
     *        itself
     * @return Variable wrapping the endpoint
     */
    public static Variable setEndpointAttrFromArgs(Stack<Variable> values,
            Set<ArrayList<ArgConfigType>> argDefs, Operation operation, HandlePathParam callback) {
        Stack<Variable> args = methodArgsFromValues(values, operation);

        Variable varEndpoint = Helpers.StackF.peekEndpoint(values);
        if (varEndpoint == null) {
            Endpoint endpoint = new Endpoint();
            varEndpoint = new Variable(endpoint).setType(VariableType.ENDPOINT);
            values.push(varEndpoint);
        }
        if (args.size() == 0) {
            return varEndpoint;
        }
        Map<String, Object> params = getParams(args, argDefs);
        callback.run(params, varEndpoint, args);
        return varEndpoint;
    }

    /**
     * Update endpoint by given parameters
     * 
     * @param params Parameters of endpoint
     * @param endpoint Endpoint which will be updated
     * @return Updated endpoint
     */
    private static Endpoint setParamsToEndpoint(Map<String, Object> params, Endpoint endpoint) {
        Object path = params.getOrDefault(ArgConfigType.PATH.name(), null);
        Object baseURL = params.getOrDefault(ArgConfigType.BASEURL.name(), null);
        Object httpMethod = params.getOrDefault(ArgConfigType.HTTPMETHOD.name(), null);
        Object expect = params.getOrDefault(ArgConfigType.EXPECT.name(), null);
        Object param = params.getOrDefault(ArgConfigType.PARAM.name(), null);
        Object send = params.getOrDefault(ArgConfigType.SEND.name(), null);
        Object contentType = params.getOrDefault(ArgConfigType.CONTENTTYPE.name(), null);
        Object headerType = params.getOrDefault(ArgConfigType.HEADERTYPE.name(), null);
        Object headerValue = params.getOrDefault(ArgConfigType.HEADERVALUE.name(), null);
        Object accept = params.getOrDefault(ArgConfigType.ACCEPT.name(), null);
        Object eData = params.getOrDefault(ArgConfigType.EDATA.name(), null);

        if (path != null) {
            if (path instanceof Endpoint || path instanceof VarEndpointData) {
                Endpoint pathData = (Endpoint) path;
                endpoint.setBaseUrl(pathData.getBaseUrl());
            } else if (path instanceof String) {
                endpoint.setPath((String) path);
            }
        }
        if (baseURL != null) {
            if (baseURL instanceof Endpoint || baseURL instanceof VarEndpointData) {
                Endpoint baseURLData = (Endpoint) baseURL;
                endpoint.setBaseUrl(baseURLData.getBaseUrl());
            } else if (baseURL instanceof String) {
                endpoint.setBaseUrl((String) baseURL);
            }
        }
        if (param != null) {
            if (param instanceof VarEndpointData) {
                VarEndpointData varEData = (VarEndpointData) param;

                endpoint.merge(varEData);
                varEData.merge(endpoint);
            } else {
                handleAttr(param, (Variable p) -> addParamToEndpoint(p, endpoint));
            }
        }
        if (expect != null) {
            handleAttr(expect, (String p) -> addExpectedResponseToEndpoint(p, endpoint));
        }
        if (send != null) {
            if (send instanceof VarEndpointData) {
                Endpoint varEData = (Endpoint) send;
                endpoint.merge(varEData);
                varEData.merge(endpoint);
            } else if (!(send instanceof Endpoint)) {
                handleAttr(send, (String p) -> addRequestBodyToEndpoint(p, endpoint));
            }
        }
        if (httpMethod != null) {
            handleAttr(httpMethod,
                    (String httpmethod) -> endpoint.addHttpMethod(HttpMethod.valueOf(httpmethod)));
        }
        if (headerType != null && headerValue != null) {
            if (headerValue instanceof String) {
                handleAttrPair(headerType, headerValue, (String headerName,
                        String headerVal) -> endpoint.addHeader(new Header(headerName, headerVal)));
            } else if (headerValue.getClass().isArray()) {
                handleAttrPair(headerType, headerValue,
                        (String headerName, Variable headerVal) -> endpoint
                                .addHeader(new Header(headerName, headerVal)));
            }
        }
        if (contentType != null) {
            handleAttr(contentType, (String cType) -> endpoint
                    .addProduces(new Header(HeaderTools.CONTENTTYPE, cType)));
        }
        if (accept != null) {
            handleAttr(accept, (String aType) -> endpoint
                    .addConsumes(new Header(HeaderTools.CONTENTTYPE, aType)));
        }
        if (eData != null) {
            if (eData instanceof VarEndpointData) {
                VarEndpointData varEData = (VarEndpointData) eData;
                endpoint.merge(varEData);
                varEData.merge(endpoint);
            }
        }
        return endpoint;
    }

    public static String getURI(Stack<Variable> values, Set<ArrayList<ArgConfigType>> args) {
        return extract(values, args);
    }

}
