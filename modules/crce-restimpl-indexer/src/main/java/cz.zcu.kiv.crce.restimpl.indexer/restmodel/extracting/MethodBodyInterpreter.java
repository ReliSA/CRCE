package cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting;

import cz.zcu.kiv.crce.restimpl.indexer.classmodel.extracting.BytecodeDescriptorsProcessor;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.ClassType;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.DataType;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Method;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.MethodSignature;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Operation;
import cz.zcu.kiv.crce.restimpl.indexer.definition.RestApiDefinition;

import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.EndpointParameter;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.EndpointResponse;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.ParameterCategory;
import org.objectweb.asm.Opcodes;



import java.util.*;

/**
 * Created by ghessova on 08.04.2018.
 */
public class MethodBodyInterpreter {

    private RestApiDefinition definition;

    private Deque<StoredElement> stack;
    private Map<Integer, StoredElement> localVariables;
    private EndpointResponse response;
    private Set<EndpointResponse> responses;
    private Map<String, ClassType> classesMap;

    private final int MAX_DEPTH = 3;

    MethodBodyInterpreter(RestApiDefinition definition, Map<String, ClassType> classesMap) {
        this.definition = definition;
        this.classesMap = classesMap;
    }

    /**
     * Searches method body instructions for HTTP responses.
     * @param bodyLog operations list (instructions)
     * @return set of responses
     */
    public Set<EndpointResponse> interpretBody(List<Operation> bodyLog, int depth) {
        reset();
        if (depth == MAX_DEPTH) {
            return responses;
        }
        for (Operation operation : bodyLog) {
            //System.out.println(operation);
            interpretOperation(operation, depth);
        }
        return responses;
    }

    private void interpretOperation(Operation operation, int depth) {
        switch (operation.getType()) {
            case INT_CONSTANT:
            case STRING_CONSTANT:
                stack.push(new StoredElement(null, operation.getValue()));
                break;
            case FIELD:
                if (isStatusField(operation)) {
                    Integer status = getStatus(operation);
                    if (status != null) {
                        stack.push(new StoredElement(null, status));
                    }
                }
                break;
            case STORE:
                try {
                    StoredElement element = stack.peek();
                    if (element != null) {
                        localVariables.put(operation.getIndex(), stack.pop());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case LOAD:
                try {
                    StoredElement element = localVariables.get(operation.getIndex());
                    if (element != null) {
                        Object object = element.getValue();
                        if (object != null && object instanceof EndpointResponse) {
                            response = (EndpointResponse) object;
                        }
                        stack.push(element);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CALL:  // if method return type is not void, push result to stack
                MethodSignature signature = BytecodeDescriptorsProcessor.processMethodDescriptor(operation.getDesc());
                List<StoredElement> parametersFromStack = new ArrayList<>();
                for (int i = 0; i < signature.getParameterTypes().size(); i++) {
                    if (!stack.isEmpty()) {
                        parametersFromStack.add(stack.pop());

                    }
                }
                if (isCookieInit(operation)) {
                    EndpointParameter cookie = new EndpointParameter();
                    StoredElement nameElement = parametersFromStack.get(1);
                    StoredElement valueElement = parametersFromStack.get(0);
                    if (nameElement != null) {
                        cookie.setCategory(ParameterCategory.COOKIE);
                        cookie.setName((String)nameElement.getValue());
                        cookie.setDataType(valueElement.dataType);
                    }
                    stack.push(new StoredElement(null, cookie));
                    break;
                }
                if (isResponseInit(operation)) {
                    response = new EndpointResponse();
                }

                if (isCookieSetting(operation)) {
                    StoredElement element = parametersFromStack.get(0);
                    if (element != null && element.value instanceof EndpointParameter) {
                        response.addParameter((EndpointParameter)element.value);
                    }
                }
                else if (isHeaderSetting(operation)) {
                    StoredElement valueElement = parametersFromStack.get(0);
                    StoredElement keyElement = parametersFromStack.get(1);
                    if (keyElement != null && valueElement != null) {
                        EndpointParameter parameter = new EndpointParameter();
                        parameter.setCategory(ParameterCategory.HEADER);
                        parameter.setName("" + keyElement.getValue());
                        parameter.setDataType(valueElement.dataType);
                        response.addParameter(parameter);
                    }
                }
                else  if (!isStatusSettingMethod(operation) && !isEntitySettingMethod(operation)) { // other method, only return type is known
                    String returnType = getReturnType(operation);
                    if (returnsResponse(returnType)) {
                        ClassType owner = classesMap.get(operation.getOwner());
                        if (owner != null) {
                            for (Method method : owner.getMethods()) {
                                if (method.getName().equals(operation.getName()) && method.getDesc().equals(operation.getDesc())) {
                                    MethodBodyInterpreter newInterpreter = new MethodBodyInterpreter(definition, classesMap);
                                    Set<EndpointResponse> innerResponses = newInterpreter.interpretBody(method.getBodyLog(), ++depth);
                                    if (!innerResponses.isEmpty()) {
                                        response = innerResponses.iterator().next();    // we get only first response, nothing complicated
                                    }
                                }
                            }
                        }
                    }
                    stack.push(new StoredElement(returnType, null));
                }
                else  {
                    if (isStatusSettingMethod(operation)) {
                        response.setStatus(determineStatus(operation, parametersFromStack));

                    }
                    if (isEntitySettingMethod(operation)) {
                        try {
                            StoredElement element = parametersFromStack.get(0);
                            String entityType = element.getDataType();
                            response.setStructure(entityType);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (isResponseBuildMethod(operation)) { // may be also entity setting or status setting method
                    stack.push(new StoredElement(null, response));
                }

                break;
            case RETURN:
                responses.add(response);
                response = new EndpointResponse();
                break;
        }
        //System.out.println(stack);

    }

    private boolean returnsResponse(String returnType) {
        return definition.getGenericResponseClasses().contains(returnType);
    }

    private boolean isCookieSetting(Operation operation) {
        MemberProcessor processor = definition.getCookieSettingMethod();
        return operation.getOwner().equals(processor.getOwner()) && operation.getName().equals(processor.getSimpleName());
    }

    private boolean isHeaderSetting(Operation operation) {
        MemberProcessor processor = definition.getHeaderSettingMethod();
        return operation.getOwner().equals(processor.getOwner()) && operation.getName().equals(processor.getSimpleName());
    }

    private boolean isCookieInit(Operation operation) {
        return "<init>".equals(operation.getName()) && definition.getCookieClass().equals(operation.getOwner());
    }

    private boolean isResponseInit(Operation operation) {
        return isStatusSettingMethod(operation) && operation.getOpcode() == Opcodes.INVOKESTATIC;   // todo
    }

    private String getReturnType(Operation operation) {
        if ("<init>".equals(operation.getName())) {
            return operation.getOwner();
        }
        else {
            return operation.getDataType();
        }
    }


    private Integer getStatus(Operation operation) {
        Map<String, Object> mapping = definition.getStatusFields().getMapping();
        return (Integer)mapping.get(operation.getName());
    }

    private boolean isStatusField(Operation operation) {
        return definition.getStatusFields().getOwner().equals(operation.getOwner());
    }

    private boolean isResponseBuildMethod(Operation operation) {
        String returnType = BytecodeDescriptorsProcessor.processMethodDescriptor(operation.getDesc()).getReturnType().getBasicType();
        return definition.getGenericResponseClasses().contains(returnType);
    }

    private boolean isStatusSettingMethod(Operation operation) {
        String methodName = operation.getName();
        MemberProcessor processor = definition.getStatusSettingMethods();
        return operation.getOwner().equals(processor.getOwner()) && (methodName.equals(processor.getSimpleName()) || processor.getMapping().keySet().contains(methodName));
    }

    private boolean isEntitySettingMethod(Operation operation) {
        String methodOwner = operation.getOwner();
        String methodName = operation.getName();
        String methodDesc = operation.getDesc();
        List<DataType> parameters = BytecodeDescriptorsProcessor.processMethodDescriptor(methodDesc).getParameterTypes();
        Set<MemberProcessor> processors = definition.getEntitySettingMethods();
        for (MemberProcessor processor : processors) {
            if (methodOwner.equals(processor.getOwner()) && methodName.equals(processor.getSimpleName()) && !parameters.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private int determineStatus(Operation operation, List<StoredElement> parametersFromStack) {
        String methodName = operation.getName();
        Map<String, Object> mapping = definition.getStatusSettingMethods().getMapping();
        Integer status = (Integer)mapping.get(methodName);
        if (status != null) return status;
        StoredElement element = parametersFromStack.get(0);
        if (element != null) {
            Object object = element.getValue();
            if (object instanceof String) {
                return Integer.parseInt((String)object);
            }
            else if (object instanceof Integer) {
                return (Integer)object;
            }
        }

        return -1;
    }

    private void reset() {
        stack = new ArrayDeque<>();
        response = new EndpointResponse();
        responses = new HashSet<>();
        localVariables = new HashMap<>();
    }

    class StoredElement {

        private String dataType;
        private Object value;       // value is not required


        StoredElement(String dataType, Object value) {
            if (dataType == null) {
                this.dataType = value.getClass().getName();
            }
            else {
                this.dataType = dataType;
            }
            this.value = value;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public Object getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "StoredElement{" +
                    "dataType='" + dataType + '\'' +
                    ", value=" + value +
                    '}';
        }
    }
}