package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation;
import cz.zcu.kiv.crce.rest.client.indexer.config.ApiCallMethodConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config.ApiCallMethodType;
import cz.zcu.kiv.crce.rest.client.indexer.config.ConfigTools;
import cz.zcu.kiv.crce.rest.client.indexer.config.EDataContainerConfigMap;
import cz.zcu.kiv.crce.rest.client.indexer.config.EDataContainerMethodConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config.EnumConfigMap;
import cz.zcu.kiv.crce.rest.client.indexer.config.EnumFieldOrMethodConfig;
import cz.zcu.kiv.crce.rest.client.indexer.config.MethodConfigMap;
import cz.zcu.kiv.crce.rest.client.indexer.config.tools.ArgTools;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.ParameterCategory;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint.HttpMethod;
import cz.zcu.kiv.crce.rest.client.indexer.processor.Variable.VariableType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.MethodTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.VariableTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.MethodTools.MethodType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.MethodWrapper;

class EndpointHandler extends MethodProcessor {

    private static final Logger logger = LoggerFactory.getLogger(EndpointHandler.class);

    private String lastProcessedClass = "";
    private String lastMethodName = "";
    private String classInProgress = "";
    private Set<String> callingChain = new HashSet<>();


    private Map<String, Endpoint> endpoints = new HashMap<>();
    private Set<String> typeHolders = ConfigTools.getGenerics();
    private EnumConfigMap ed = ConfigTools.getEnumDefinitions();
    private MethodConfigMap md = ConfigTools.getMethodDefinitions();
    private EDataContainerConfigMap eDataConfig = ConfigTools.getEDataContainerConfigMap();

    private FieldProcessor fProcessor;

    public EndpointHandler(ClassMap classes) {
        super(classes);
        fProcessor = new FieldProcessor(classes);
    }

    @Override
    protected MethodWrapper getMethodWrapper(Operation operation) {
        MethodWrapper mw = super.getMethodWrapper(operation);
        if (mw == null) {
            return null;
        }
        final String operationOwner = operation.getOwner();
        if (!mw.isProcessed() && !classInProgress.equals(operationOwner)) {
            final ClassWrapper cw = this.classes.getOrDefault(operationOwner, null);
            if (cw == null) {
                mw.setIsProcessed();
                logger.info("Missing class=" + operationOwner);
                return null;
            }
            logger.info("Not processed class=" + cw.getClassStruct().getName());
            process(mw);
        } else if (!mw.isProcessed()) {
            logger.info("Not processed method=" + operation.getMethodName() + " class="
                    + operationOwner);
            process(mw);
        }
        return mw;
    }

    @Override
    protected void processINVOKESTATIC(Stack<Variable> values, Operation operation) {
        if (eDataConfig.containsKey(operation.getOwner())) {
            Variable newEndointData = ArgTools.getEndpointAttrFromContainer(values, operation);
            values.push(newEndointData);
            return;
        }
        super.processINVOKESTATIC(values, operation);
    }

    @Override
    protected void processINVOKESPECIAL(Stack<Variable> values, Operation operation) {
        if (eDataConfig.containsKey(operation.getOwner())) {
            // TODO: new
            Variable newEndointData = ArgTools.getEndpointAttrFromContainer(values, operation);
            mergeVarEndpointData(values, newEndointData);
            return;
        } else if (MethodTools.getType(operation.getDescription()) == MethodType.INIT) {
            removeMethodArgsFromStack(values, operation);
            ClassWrapper class_ = this.classes.getOrDefault(operation.getOwner(), null);
            if (class_ != null && typeHolders.contains(class_.getClassStruct().getParent())
                    && class_.getClassStruct().getSignature() != null) {
                Stack<Object> types = new Stack<>();
                ClassTools.processTypes(class_.getClassStruct().getSignature(), types);
                types.pop(); // remove wraping type
                String type = (String) Helpers.StackF.pop(types);
                // TODO: in future transform to json recursive way
                values.push(new Variable().setDescription(type).setType(VariableType.OTHER));
                return;
            }
        } else {
            removeMethodArgsFromStack(values, operation);
            handleAccessingObject(values, operation);
        }
        super.processINVOKESPECIAL(values, operation);
    }

    @Override
    protected void processINVOKEINTERFACE(Stack<Variable> values, Operation operation) {
        super.processINVOKEINTERFACE(values, operation);
    }

    private static void mergeVarEndpointData(Stack<Variable> values, Variable var) {
        Variable lastVar = Helpers.StackF.peek(values);
        if (!VariableTools.isEmpty(lastVar) && lastVar.getValue() instanceof VarEndpointData) {
            VarEndpointData lastVarEData = (VarEndpointData) lastVar.getValue();
            VarEndpointData varEData = (VarEndpointData) var.getValue();

            lastVarEData.merge(varEData);
        } else {
            values.push(var);
        }
    }

    @Override
    protected void processINVOKEVIRTUAL(Stack<Variable> values, Operation operation) {
        if (eDataConfig.containsKey(operation.getOwner())) {
            // TODO: new
            Variable newEndointData = ArgTools.getEndpointAttrFromContainer(values, operation);
            mergeVarEndpointData(values, newEndointData);
            return;
        } else {
            super.processINVOKEVIRTUAL(values, operation);
        }
    }

    private void processHEADER(Stack<Variable> values, ApiCallMethodConfig methodConfig,
            Operation operation) {
        Variable varEndpoint = ArgTools.setHeaderParamFromArgs(values, methodConfig, operation);
        if (varEndpoint != null) {
            Helpers.EndpointF.merge(endpoints, (Endpoint) varEndpoint.getValue());
        }
    }

    private void processGENERIC(Stack<Variable> values, ApiCallMethodConfig methodDefinition,
            Operation operation) {
        Variable varEndpoint =
                ArgTools.setEndpointAttrFromArgs(values, methodDefinition.getArgs(), operation);
        if (varEndpoint != null) {
            Helpers.EndpointF.merge(endpoints, (Endpoint) varEndpoint.getValue());
        }

    }


    private void processHTTPMetod(Stack<Variable> values, ApiCallMethodConfig methodDefinition,
            ApiCallMethodType type, Operation operation) {
        processGENERIC(values, methodDefinition, operation);
        HttpMethod eType = HttpMethod.valueOf(methodDefinition.getValue());
        Endpoint endpoint = (Endpoint) Helpers.StackF.peekEndpoint(values).getValue();
        endpoint.addHttpMethod(eType);
        if (endpoint != null) {
            Helpers.EndpointF.merge(endpoints, endpoint);
        }
    }

    private void processPathParam(Stack<Variable> values, ApiCallMethodConfig methodDefinition,
            Operation operation) {
        Variable varEndpoint = ArgTools.setPathParamFromArgs(values, methodDefinition.getArgs(),
                operation, ParameterCategory.valueOf(methodDefinition.getValue()));
        if (varEndpoint != null) {
            Helpers.EndpointF.merge(endpoints, (Endpoint) varEndpoint.getValue());
        }
    }

    private void processPathParamEDataContainer(Stack<Variable> values,
            EDataContainerMethodConfig methodDefinition, Operation operation) {
        ParameterCategory category = ParameterCategory.valueOf(methodDefinition.getValue());
        Variable varEndpoint =
                ArgTools.getPathParamFromContainer(values, methodDefinition, operation, category);
        mergeVarEndpointData(values, varEndpoint);
    }

    private void processPathParamEDataContainerGENERIC(Stack<Variable> values,
            EDataContainerMethodConfig methodDefinition, Operation operation) {
        Variable varEndpoint = ArgTools.getEndpointAttrFromContainer(values, operation);
        mergeVarEndpointData(values, varEndpoint);
    }



    private void processGETFIELDLibEnum(Stack<Variable> values, Operation operation) {
        if (ed.containsKey(operation.getOwner())) {
            HashMap<String, EnumFieldOrMethodConfig> enumClass = ed.get(operation.getOwner());
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
    }

    @Override
    protected void processGETSTATICFIELD(Stack<Variable> values, Operation operation) {
        super.processGETSTATICFIELD(values, operation);
        processGETFIELDLibEnum(values, operation);

    }

    /**
     * Processes possible endpoints by detecting method CALL like .uri(), .put() etc.
     * 
     * @param operation Operation to be handled
     * @param values String values
     */
    @Override
    protected void processCALL(Operation operation, Stack<Variable> values) {


        if (md.containsKey(operation.getOwner())) {
            logger.info("Endpoint method=" + operation.getMethodName());
            HashMap<String, ApiCallMethodConfig> methodDefinitionMap = md.get(operation.getOwner());
            if (!methodDefinitionMap.containsKey(operation.getMethodName())) {
                removeMethodArgsFromStack(values, operation);
                return;
            }
            ApiCallMethodConfig methodDefinition =
                    methodDefinitionMap.get(operation.getMethodName());
            ApiCallMethodType type = methodDefinition.getType();
            switch (type) {
                case INIT:
                    values.push(new Variable().setType(VariableType.ENDPOINT));
                    break;
                case BASEURL:
                case EXECUTE:
                case SEND:
                case EXPECT:
                case PATH:
                case EXCHANGE:
                case GENERIC:
                case CONTENTTYPE:
                    processGENERIC(values, methodDefinition, operation);
                    break;
                case HTTPMETHOD:
                    processHTTPMetod(values, methodDefinition, type, operation);
                    break;
                case PARAM:
                    processPathParam(values, methodDefinition, operation);
                    break;
                case HEADER:
                case ACCEPT:
                    processHEADER(values, methodDefinition, operation);
                    break;
                default:
                    removeMethodArgsFromStack(values, operation);

            }
        } else if (eDataConfig.containsKey(operation.getOwner())) {
            HashMap<String, EDataContainerMethodConfig> methodDefinitionMap =
                    eDataConfig.get(operation.getOwner());
            final String mName = MethodTools.getMethodNameFromSignature(operation.getDescription());
            if (!methodDefinitionMap.containsKey(mName)) {
                return;
            }
            EDataContainerMethodConfig methodDefinition = methodDefinitionMap.get(mName);

            switch (methodDefinition.getType()) {
                case PARAM:
                    processPathParamEDataContainer(values, methodDefinition, operation);
                    break;
                case GENERIC:
                    processPathParamEDataContainerGENERIC(values, methodDefinition, operation);
                default:;
            }
        } else {
            super.processCALL(operation, values);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(MethodWrapper mw) {
        if (mw.isProcessed()) {
            return;
        }
        final String currentClass = mw.getOwner();
        final String methodName = mw.getMethodStruct().getName();
        final String chainKey = currentClass + "-" + methodName;

        if (callingChain.contains(chainKey)) {
            //System.out.println("Recursion=" + chainKey);
            //System.out.println("CHAIN=" + callingChain);
            logger.info("Recursion detected method=" + methodName + " class=" + mw.getOwner());
            //System.out.println("MW.isvisited=" + mw.isProcessed());
            mw.setIsProcessed();
            return;
        }
        callingChain.add(chainKey);
        super.process(mw);
        callingChain = new HashSet<>();
    }

    public void process(ClassWrapper class_) {

        if (class_.getClassStruct().getName().contains("org/springframework")) {
            return;
        }

        classInProgress = class_.getClassStruct().getName();
        this.fProcessor.process(class_);
        for (MethodWrapper method : class_.getMethods()) {
            //calling chain
            process(method);
        }
        callingChain = new HashSet<>();
        lastProcessedClass = classInProgress;
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
