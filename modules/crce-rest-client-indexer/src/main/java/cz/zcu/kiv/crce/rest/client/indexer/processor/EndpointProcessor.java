package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.RequestParamFieldType;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures.IWSClient;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures.RequestParam;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.tools.ConfigTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.EndpointData;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.Variable;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.Variable.VariableType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.EndpointDataMiningTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.EndpointTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.MethodTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.MethodTools.MethodType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.SafeStack;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.VariableTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.MethodWrapper;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HttpMethod;

class EndpointHandler extends MethodProcessor {

    private static final Logger logger = LoggerFactory.getLogger(EndpointHandler.class);

    private String classInProgress = "";
    private String currentMethod = "";
    private Set<String> callingChain = new HashSet<>();


    private Map<String, Endpoint> endpoints = new HashMap<>();

    private Map<String, Map<String, IWSClient>> wsClients = ConfigTools.getWSClients();
    private Map<String, Map<String, RequestParam>> requestParams = ConfigTools.getRequestParams();
    private Map<String, Map<String, IWSClient>> wsClientDataContainers =
            ConfigTools.getWSClientDataContainers();


    /*     private EnumConfigMap ecMAp = ConfigTools.getEnumDefinitions();
    private MethodConfigMap mcMap = ConfigTools.getMethodConfigs();
    private EDataContainerConfigMap eDataConfigMap = ConfigTools.getEDataContainerConfigMap();
     */
    private FieldProcessor fProcessor;

    public EndpointHandler(ClassMap classes) {
        super(classes);
        fProcessor = new FieldProcessor(classes);
    }

    /**
     * Checks method call is type of EndpointDataContainer (sets data to endpoint data holder)
     * @param operation Operation
     * @return Is it EndpointDataContainer type of method call
     */
    private boolean isWSClientDataContainer(Operation operation) {
        return wsClientDataContainers.containsKey(operation.getOwner()) && wsClientDataContainers
                .get(operation.getOwner()).containsKey(operation.getMethodName());
    }

    /**
     * Checks method call is type of Endpoint method (sets data directly to endpoint)
     * @param operation Operation
     * @return Is it Endpoint method type of method call
     */
    private boolean isWSClient(Operation operation) {
        return wsClients.containsKey(operation.getOwner())
                && wsClients.get(operation.getOwner()).containsKey(operation.getMethodName());
    }

    /**
     * Checks method call is type of Enum method (Enum of HTTP methods etc.)
     * @param operation Operation
     * @return Is it Enum method type of method call
     */
    private boolean isRequestParamField(Operation operation) {
        return requestParams.containsKey(operation.getOwner())
                && requestParams.get(operation.getOwner()).containsKey(operation.getFieldName());
    }

    /**
     * Checks method call is type of init (init for class)
     * @param operation Operation
     * @return Is it type of init (init for class
     */
    private boolean isInitMethod(Operation operation) {
        return MethodTools.getType(operation.getDescription()) == MethodType.INIT;
    }

    /**
     * Merges Endpoint data holders into one and pushes outcome into stack
     * @param values Stack
     * @param var Variable (Endpoint data holder)
     */
    private void mergeVarEndpointData(Stack<Variable> values, Variable var) {
        Variable lastVar = SafeStack.peek(values);
        if (!VariableTools.isEmpty(lastVar) && lastVar.getValue() instanceof EndpointData) {
            EndpointData lastVarEData = (EndpointData) lastVar.getValue();
            EndpointData varEData = (EndpointData) var.getValue();

            lastVarEData.merge(varEData);
        } else {
            values.push(var);
        }
    }

    /**
     * Base method for processing INVOKE method calls
     * @param values Stack
     * @param operation Operation
     */
    private void processINVOKE(Stack<Variable> values, Operation operation) {
        Variable newEndointData =
                EndpointDataMiningTools.getEndpointAttrFromContainer(values, operation);
        mergeVarEndpointData(values, newEndointData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MethodWrapper getMethodWrapper(Operation operation) {
        MethodWrapper mw = super.getMethodWrapper(operation);
        if (mw == null) {
            return null;
        }
        final String operationOwner = operation.getOwner();
        if (!mw.isProcessed() && !classInProgress.equals(operationOwner)) {
            //Method is from other class which was not processed yet
            final ClassWrapper cw = this.classes.getOrDefault(operationOwner, null);
            if (cw == null) {
                mw.setIsProcessed();
                logger.info("Missing class=" + operationOwner);
                return null;
            }
            logger.info("Not processed class=" + cw.getClassStruct().getName());
            process(mw);
        } else if (!mw.isProcessed()) {
            //Method is from this class but was not processed yet
            logger.info("Not processed method=" + operation.getMethodName() + " class="
                    + operationOwner);
            process(mw);
        }
        return mw;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processINVOKESTATIC(Stack<Variable> values, Operation operation) {
        if (isWSClientDataContainer(operation)) {
            processINVOKE(values, operation);
            return;
        }
        super.processINVOKESTATIC(values, operation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processINVOKESPECIAL(Stack<Variable> values, Operation operation) {
        if (isWSClientDataContainer(operation)) {
            processINVOKE(values, operation);
            return;
        } else if (isInitMethod(operation)) {
            removeMethodArgsFromStack(values, operation);
            ClassWrapper class_ = this.classes.getOrDefault(operation.getOwner(), null);
            if (ClassTools.isGenericClass(class_)) {
                Stack<Object> types =
                        ClassTools.processTypes(class_.getClassStruct().getSignature());
                types.pop(); // Throw away generic wrapper
                String type = (String) SafeStack.pop(types);
                values.push(new Variable().setDescription(type).setType(VariableType.OTHER));
                return;
            }
        } else {
            removeMethodArgsFromStack(values, operation);
            handleAccessingObject(values, operation);
        }
        super.processINVOKESPECIAL(values, operation);
    }

    /**
     * {@inheritDoc}
     */
    /*     @Override
    protected void processINVOKEVIRTUAL(Stack<Variable> values, Operation operation) {
        if (isWSClientDataContainer(operation)) {
            processINVOKE(values, operation);
            return;
        } else {
            super.processINVOKEVIRTUAL(values, operation);
        }
    } */

    /**
     * Processes HEADER argumetns from Stack (parsing based on config.yml)
     * @param values Stack
     * @param methodConfig Method configuration (source config.yml)
     * @param operation Operation
     */
    /* private void processHEADER(Stack<Variable> values, ApiCallMethodConfig methodConfig,
            Operation operation) {
        Variable varEndpoint =
                EndpointDataMiningTools.setHeaderParamFromArgs(values, methodConfig, operation);
        if (varEndpoint != null) {
            EndpointTools.merge(endpoints, (Endpoint) varEndpoint.getValue());
        }
    } */

    /**
     * Processes GENERIC methods without additianol semantic information given by method name
     * @param values Stack
     * @param methodConfig Method configuration (source config.yml)
     * @param operation Operation
     */
    /*     private void processGENERIC(Stack<Variable> values, ApiCallMethodConfig methodConfig,
            Operation operation) {
        Variable varEndpoint = EndpointDataMiningTools.setEndpointAttrFromArgs(values,
                methodConfig.getArgs(), operation);
        if (varEndpoint != null) {
            EndpointTools.merge(endpoints, (Endpoint) varEndpoint.getValue());
        }
    
    }
     */
    /**
     * Processes HTTP methods from Stack (parsing based on config.yml)
     * @param values Stack
     * @param methodConfig Method configuration (source config.yml)
     * @param type HTTP method retrieved from method name e.g. POST, GET, PUT, PATCH
     * @param operation Operation
     */
    /*     private void processHTTPMetod(Stack<Variable> values, ApiCallMethodConfig methodConfig,
            ApiCallMethodType type, Operation operation) {
        processGENERIC(values, methodConfig, operation);
        HttpMethod eType = HttpMethod.valueOf(methodConfig.getValue());
        Endpoint endpoint = (Endpoint) SafeStack.peekEndpoint(values).getValue();
        endpoint.addHttpMethod(eType);
        if (endpoint != null) {
            EndpointTools.merge(endpoints, endpoint);
        }
    } */

    /**
     * Processes PATH params
     * @param values Stack
     * @param methodConfig Method configuration (source config.yml)
     * @param operation Operation
     */
    /*     private void processPathParam(Stack<Variable> values, ApiCallMethodConfig methodConfig,
            Operation operation) {
        Variable varEndpoint =
                EndpointDataMiningTools.setPathParamFromArgs(values, methodConfig.getArgs(),
                        operation, ParameterCategory.valueOf(methodConfig.getValue()));
        if (varEndpoint != null) {
            EndpointTools.merge(endpoints, (Endpoint) varEndpoint.getValue());
        }
    } */

    /**
     * Process PATH params stored in Endpoint Data container (holder of endpoints attributes)
     * @param values Stack
     * @param methodConfig Method configuration (source config.yml)
     * @param operation Operation
     */
    /*    private void processPathParamEDataContainer(Stack<Variable> values,
            EDataContainerMethodConfig methodConfig, Operation operation) {
        ParameterCategory category = ParameterCategory.valueOf(methodConfig.getValue());
        Variable varEndpoint = EndpointDataMiningTools.getPathParamFromContainer(values,
                methodConfig, operation, category);
        mergeVarEndpointData(values, varEndpoint);
    }
    */
    /**
     * Process PATH params (without semantic info) stored in Endpoint Data container (holder of endpoints attributes)
     * @param values Stack
     * @param methodConfig Method configuration (source config.yml)
     * @param operation Operation
     */
    /*     private void processPathParamEDataContainerGENERIC(Stack<Variable> values,
            EDataContainerMethodConfig methodConfig, Operation operation) {
        Variable varEndpoint =
                EndpointDataMiningTools.getEndpointAttrFromContainer(values, operation);
        mergeVarEndpointData(values, varEndpoint);
    }
     */

    /**
     * Processes retrieving from framework Enums
     * @param values Stack
     * @param operation Operation
     */
    /*     private void processGETFIELDLibEnum(Stack<Variable> values, Operation operation) {
     if (isRequestParamMethod(operation)) {
         HashMap<String, EnumFieldOrMethodConfig> enumClass = ecMAp.get(operation.getOwner());
         if (enumClass.containsKey(operation.getFieldName())) {
             EnumFieldOrMethodConfig enumField = enumClass.get(operation.getFieldName());
             if (enumField.getContentType() != null) {
                 values.push(
                         new Variable(enumField.getContentType()).setType(VariableType.SIMPLE));
             } else if (enumField.getHttpMethod() != null) {
                 values.push(
                         new Variable(enumField.getHttpMethod()).setType(VariableType.SIMPLE));
    
             } else if (enumField.getHeaderType() != null) {
                 values.push(
                         new Variable(enumField.getHeaderType()).setType(VariableType.SIMPLE));
             }
         }
     }
    } */

    /**
     * Processes enums and classes which holds essential informations about endpoints (e.g. Content-Type, Http method etc.)
     * @param values Stack
     * @param operation Operation
     */
    private void processFIELDRequestParameters(Stack<Variable> values, Operation operation) {
        if (isRequestParamField(operation)) {
            RequestParam param =
                    requestParams.get(operation.getOwner()).get(operation.getFieldName());
            Variable newVar = new Variable();
            if (param.getType() == RequestParamFieldType.HTTP_METHOD) {
                newVar.setValue(HttpMethod.valueOf(param.getValue()));
            } else {
                newVar.setValue(param.getValue());
            }
            values.push(newVar);
        } else {
            super.processGETSTATICFIELD(values, operation);
        }
    }

    /**
     * [+Enum processing]
     * 
     * {@inheritDoc}
     */
    @Override
    protected void processGETSTATICFIELD(Stack<Variable> values, Operation operation) {
        processFIELDRequestParameters(values, operation);
    }

    /**
     * Processes possible endpoints by detecting method CALL like .uri(), .put() etc.
     * 
     * @param operation Operation to be handled
     * @param values String values
     */
    @Override
    protected void processCALL(Operation operation, Stack<Variable> values) {
        if (isWSClient(operation) || isWSClientDataContainer(operation)) {
            Stack<Variable> methodArgs =
                    EndpointDataMiningTools.methodArgsFromValues(values, operation);
            Endpoint endpointData = null;
            if (isWSClient(operation)) {
                //operation.get
                final IWSClient client =
                        wsClients.get(operation.getOwner()).get(operation.getMethodName());
                endpointData = VariableFactory.getEndpointData(methodArgs, client);
                if (SafeStack.peekEndpoint(values) != null) {
                    Endpoint endpointFromStack = (Endpoint) values.pop().getValue();
                    endpointData.merge(endpointFromStack);
                }
                EndpointTools.merge(endpoints, endpointData);
            } else if (isWSClientDataContainer(operation)) {
                final IWSClient client = wsClientDataContainers.get(operation.getOwner())
                        .get(operation.getMethodName());
                endpointData = VariableFactory.getEndpointData(methodArgs, client);
                if (SafeStack.peekEndpoint(values) != null) {
                    Endpoint endpointFromStack = (Endpoint) values.peek().getValue();
                    endpointData.merge(endpointFromStack);
                }
            }
            final String returnType =
                    MethodTools.getReturnTypeFromMethodDescription(operation.getDescription());
            if (wsClients.containsKey(returnType)
                    || wsClientDataContainers.containsKey(returnType)) {
                final Variable variable = new Variable();
                values.push(variable.setValue(endpointData).setType(VariableType.ENDPOINT_DATA));
            } else {
                //merge it into top of the stack endpoint (if it exsits)
                if (SafeStack.peekEndpoint(values) != null) {
                    Endpoint endpointFromStack = (Endpoint) values.peek().getValue();
                    endpointFromStack.merge(endpointData);
                }
            }
        } else {
            super.processCALL(operation, values);
        }

    }

    /**
     * [+Recursion detection]
     * {@inheritDoc}
     */
    @Override
    public void process(MethodWrapper mw) {
        if (mw.isProcessed()) {
            return;
        }

        classInProgress = mw.getOwner();
        final String currentClass = mw.getOwner();
        final String methodName = mw.getMethodStruct().getName();
        final String chainKey = currentClass + "." + methodName + mw.getMethodStruct().getDesc();
        this.currentMethod = chainKey;

        if (callingChain.contains(chainKey)) {
            logger.info("Recursion detected method=" + methodName + " owner=" + mw.getOwner());
            mw.setIsProcessed();
            return;
        }
        callingChain.add(chainKey);
        mw.setIsProcessed();
        super.process(mw);
        callingChain = new HashSet<>();
    }

    /**
     * Processes whole class (fields + methods)
     * @param class_ Input class
     */
    public void process(ClassWrapper class_) {

        classInProgress = class_.getClassStruct().getName();
        this.fProcessor.process(class_);
        for (MethodWrapper method : class_.getMethods()) {
            //calling chain
            process(method);
            if (!method.hasPrimitiveReturnType()) {
                class_.removeMethod(method.getMethodStruct().getName());
            }
        }

        callingChain = new HashSet<>();
    }

    /**
     * 
     * @return Endpoints
     */
    public Map<String, Endpoint> getEndpoints() {
        return this.endpoints;
    }
}


public class EndpointProcessor {
    private EndpointHandler endpointHandler;
    private Map<String, Endpoint> endpoints = null;


    public EndpointProcessor(ClassMap classes) {
        this.endpointHandler = new EndpointHandler(classes);
    }

    /**
     * Process class its methods and fields with endpoints handler
     * 
     * @param class_ Class to processing
     */
    public void process(ClassWrapper class_) {
        this.endpointHandler.process(class_);
        this.endpoints = endpointHandler.getEndpoints();
    }

    /**
     * 
     * @return Endpoints
     */
    public Map<String, Endpoint> getEndpoints() {
        return this.endpoints;
    }
}
