package cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting;

import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.ClassType;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Field;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Method;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.PathPart;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.PathPartAttributes;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Variable;
import cz.zcu.kiv.crce.restimpl.indexer.definition.RestApiDefinition;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.Endpoint;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.EndpointParameter;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.EndpointResponse;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.ParameterCategory;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.RequestParameter;
import cz.zcu.kiv.crce.restimpl.indexer.util.Util;
import org.objectweb.asm.Opcodes;


import java.util.*;


/**
 * Created by ghessova on 28.03.2018.
 */
public class ClassModelProcessorImpl implements ClassModelProcessor {

    private Map<String, RestApiDefinition> definitions;

    private RestApiDefinition definition;
    private Set<String> paramAnnotationNames;


    public ClassModelProcessorImpl(Map<String, RestApiDefinition> definitions) {
        this.definitions = definitions;
    }

    private void setDefinition(RestApiDefinition definition) {
        this.definition = definition;
        paramAnnotationNames = getAnnotationNames(definition.getEndpointParametersProcessors());

    }



    @Override
    public boolean isResource(ClassType clazz) {
        for (String annotationName : clazz.getAnnotations().keySet()) {
            if (definition == null) {
                for (RestApiDefinition restApiDefinition : definitions.values()) {
                    if (restApiDefinition.getResourceAnnotations().contains(annotationName)) {
                        setDefinition(restApiDefinition);
                        return true;
                    }
                }
            }
            else {
                if (definition.getResourceAnnotations().contains(annotationName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isRequestFilter(ClassType clazz) {
        String[] interfaces = clazz.getInterfaces();
        if (interfaces == null) {
            return false;
        }
        for (String anInterface : interfaces) {
            if ("javax/ws/rs/container/ContainerRequestFilter".equals(anInterface)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canBeParameterBean(ClassType clazz, Variable variable) {
        if (clazz == null) {
            return false;
        }
        if (definition.getParameterBeanAnnotations() != null && !definition.getParameterBeanAnnotations().isEmpty()) {
            for (String annotation : definition.getParameterBeanAnnotations()) {
                if (variable.getAnnotations().containsKey(annotation)) {
                    return true;
                }
            }
            return false;
        }
       /* for (Variable field : clazz.getFields()) {
            if (isRequestParameter(field)) {
                return true;
            }
        }*/
        return true;
    }


    @Override
    public boolean isEndpoint(Method method) {
        if (method.getAccess() != Opcodes.ACC_PUBLIC) {
            return false;
        }
        for (String annotationName : method.getAnnotations().keySet()) {
            if (definition.getEndpointAnnotations().contains(annotationName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public PathPartAttributes getPathAttributes(PathPart pathPart) {
        PathPartAttributes attributes = new PathPartAttributes();
        attributes.setName(pathPart.getName());
        attributes.setMethods(Util.getResultsFromAnnotations(definition.getHttpMethodProcessors(), pathPart.getAnnotations()));
        attributes.setPaths(Util.getResultsFromAnnotations(definition.getUrlProcessors(), pathPart.getAnnotations()));
        attributes.setConsumes(Util.getResultsFromAnnotations(definition.getConsumesProcessors(), pathPart.getAnnotations()));
        attributes.setProduces(Util.getResultsFromAnnotations(definition.getProducesProcessors(), pathPart.getAnnotations()));
        if (attributes.getPaths().isEmpty()) {
            attributes.getPaths().add("");
        }

        return attributes;
    }

    @Override
    public boolean isRequestParameter(Variable variable) {
        for (String annotationName : variable.getAnnotations().keySet()) {
            if (paramAnnotationNames.contains(annotationName)) {
                return true;
            }
        }
        return false;
    }



 /*   public EndpointParameter convertParameter(Variable variable) {
        BasicParameterConverter converter = new BasicParameterConverter(definition);
        EndpointParameter parameter = converter.processParameter(variable);
        if (parameter.getCategory() == ParameterCategory.PATH) {
            parameter.setOptional(false);
        }
        return parameter;
    }*/


    /**
     * For Spring returns true if the parameter has RequestBody annotation,
     * for JAX-RS returns true if the parameter does not have any annotations.
     * @param variable method parameter
     * @return true if parameter represents request body
     */
    @Override
    public boolean isRequestBody(Variable variable) {
        if (definition.getBodyProcessor().isWithoutAnnotations()) {
            for (String annotation : definition.getBodyProcessor().getExcludingAnnotations()) {
                if (variable.getAnnotations().containsKey(annotation)) {
                    return false;
                }
            }
            return true;
        }
        else {
            for (String annotation : definition.getBodyProcessor().getAnnotations()) {
                if (variable.getAnnotations().containsKey(annotation)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean isSubresource(ClassType clazz) {
        if (!definition.supportSubresources()) return false; // framework does not allow subresources (spring)
        for (Method method : clazz.getMethods()) {
            if (isEndpoint(method)) return true;
        }
        return false;
    }



    @Override
    public boolean isGenericResponseClass(String className) {
        return definition.getGenericResponseClasses().contains(className);
    }

    @Override
    public RestApiDefinition getDefinition() {
        return definition;
    }

    private Set<String> getAnnotationNames(Set<AnnotationProcessor> processors) {
        Set<String> annotations = new HashSet<>();
        for (AnnotationProcessor processor : processors) {
            annotations.add(processor.getAnnotationName());
        }
        return annotations;
    }

    public RequestParameter convertParameter(Variable variable, Set<String> consumes) {
        // get normal information from annotations
        BasicParameterConverter converter = new BasicParameterConverter(definition);
        RequestParameter parameter = converter.processParameter(variable, true);
        editCategoryAccordingToConsumes(consumes, parameter);
        if (parameter.getCategory().equals(ParameterCategory.PATH) || variable.getAnnotations().containsKey(definition.getNotNullAnnotation())) {
            parameter.setOptional(false); // path parameter is always required
        }
        return parameter;
    }

   /* public EndpointParameter convertFieldToParameter(Field field, Set<String> consumes) {
        // get normal information from annotations
        BasicParameterConverter converter = new BasicParameterConverter(definition);
        EndpointParameter parameter = converter.processParameter(field, fa);
        editCategoryAccordingToConsumes(consumes, parameter);
        return parameter;
    }*/

    private void editCategoryAccordingToConsumes(Set<String> consumes, EndpointParameter parameter) {
        if (parameter.getCategory() == null) { // not determined yet (request parameter in spring)
            if (consumes != null && consumes.contains("application/x-www-form-urlencoded")) { // todo
                parameter.setCategory(ParameterCategory.FORM); // form parameters are part of a body
            }
            else {
                parameter.setCategory(ParameterCategory.QUERY);
            }
        }
    }


    public Set<RequestParameter> getParamsFromBean(Endpoint endpoint, Variable paramBeanVariable, Set<String> pathTemplates, Map<String, ClassType> classesMap) {
        Set<RequestParameter> parameters = new HashSet<>();
        // retrieve all adepts for endpoint parameters
        Set<Field> fields = getAllFields(paramBeanVariable, definition.isFieldParamSetterRequired(), classesMap);
        if (definition.isFieldParamAnnotations()) {
            for (Field field : fields) {   // normal way where parameters must be annotated
                if (isRequestParameter(field)) {
                    parameters.add(convertParameter(field, endpoint.getConsumes()));
                }
            }
        }
        else {
            for (Field field : fields) { // spring way..
                BasicParameterConverter converter = new BasicParameterConverter(definition);
                RequestParameter parameter = new RequestParameter();
                parameter.setName(field.getName());
                parameter.setDataType(converter.getParameterDataType(field));
                parameter.setCategory(pathTemplates.contains(parameter.getName()) ? ParameterCategory.PATH : null);
                editCategoryAccordingToConsumes(endpoint.getConsumes(), parameter);
                parameter.setOptional(converter.isOptional(field));
                if (parameter.getCategory() == ParameterCategory.PATH) {
                    parameter.setOptional(false);
                }
                parameters.add(parameter);
            }
        }
        return parameters;

    }

    private Set<Field> getAllFields(Variable variable, boolean setterRequired, Map<String, ClassType> classesMap) {
        Set<Field> fields = new HashSet<>();
        Set<Field> classFields;
        for (ClassType clazz = classesMap.get(variable.getDataType().getBasicType());
             clazz != null;
             clazz = classesMap.get(clazz.getParent())) {
            classFields = clazz.getFields();
            for (Field classField : classFields) {
                boolean hasSetter = hasSetter(classField, clazz.getMethods());
                if (setterRequired && hasSetter || !setterRequired && (hasSetter || classField.getAccess() == Opcodes.ACC_PUBLIC)) {
                    fields.add(classField);
                }
            }
        }
        return fields;
    }



    private boolean hasSetter(Field field,  Collection<Method> methods) {
        String fieldName = field.getName();
        fieldName = fieldName.replaceFirst("" + fieldName.charAt(0), String.valueOf(fieldName.charAt(0)).toUpperCase());
        String setterName = "set" + fieldName;
        for (Method method : methods) {
            if (setterName.equals(method.getName()) && method.getAccess() == Opcodes.ACC_PUBLIC) {
                List<Variable> parameters = method.getParameters();
                if (parameters.size() == 1 && parameters.get(0).getDataType().equals(field.getDataType())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isExceptionHandler(ClassType clazz) {
        if (definition == null) {
            for (RestApiDefinition restApiDefinition : definitions.values()) {
                if (isExceptionHandler(clazz, restApiDefinition)) {
                    definition = restApiDefinition;
                    return true;
                }
            }
            return false;
        }
        else {
            return isExceptionHandler(clazz, definition);
        }
    }

    private boolean isExceptionHandler(ClassType clazz, RestApiDefinition definition) {
        ExceptionHandler handlerDef = definition.getExceptionHandler();
        if (handlerDef == null) {
            return false;
        }
        boolean match = false;
        for (String anInterface : clazz.getInterfaces()) {
            if (handlerDef.getInterfaces().contains(anInterface)) {
                match = true;
            }
        }
        if (match) {
            for (String annotation : clazz.getAnnotations().keySet()) {
                if (handlerDef.getAnnotations().contains(annotation)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Map<String, Set<EndpointResponse>> prepareExceptionsMappings(Set<ClassType> exceptionHandlers, Map<String, ClassType> classesMap) {
        Map<String, Set<EndpointResponse>> mappings = new HashMap<>();
        ExceptionHandler exceptionHandlerDef = definition.getExceptionHandler();
        if (exceptionHandlerDef == null) {
            return mappings;
        }
        Set<EndpointResponse> responses;
        String mappingMethod = exceptionHandlerDef.getMethod();
        MethodBodyInterpreter interpreter = new MethodBodyInterpreter(definition, classesMap);
        for (ClassType handler : exceptionHandlers) {
            for (Method method : handler.getMethods()) {
                if (mappingMethod.equals(method.getName()) && definition.getGenericResponseClasses().contains(method.getReturnType().getBasicType()) && method.getParameters().size() == 1) {
                    responses = interpreter.interpretBody(method.getBodyLog(), 0);
                    mappings.put(method.getParameters().iterator().next().getDataType().getBasicType(), responses);
                }
            }
        }
        return mappings;
    }

    public Set<EndpointResponse> getResponsesFromExceptions(String[] exceptions, Map<String, Set<EndpointResponse>> mappings) {
        Set<EndpointResponse> responses = new HashSet<>();
        if (exceptions == null) {
            return responses;
        }
        for (String exception : exceptions) {
            if (mappings.containsKey(exception)) {
                responses.addAll(mappings.get(exception));
            }
        }
        return responses;
    }
}
