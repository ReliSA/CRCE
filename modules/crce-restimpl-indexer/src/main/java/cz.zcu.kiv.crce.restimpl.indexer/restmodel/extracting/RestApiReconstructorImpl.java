package cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.extracting.BytecodeDescriptorsProcessor;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.ClassType;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.DataType;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Method;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.PathPartAttributes;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Variable;
import cz.zcu.kiv.crce.restimpl.indexer.definition.RestApiDefinition;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.Endpoint;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.EndpointRequestBody;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.EndpointResponse;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.RequestParameter;
import cz.zcu.kiv.crce.restimpl.indexer.util.WebXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ghessova on 29.03.2018.
 */
public class RestApiReconstructorImpl implements RestApiReconstructor {

    private static final Logger logger = LoggerFactory.getLogger(RestApiReconstructorImpl.class);


    // current working directory is crce\modules\runner
    private static final String DEFS_DIR = "../crce-restimpl-indexer/config/def/api";

    private Map<String, ClassType> classesMap;

    private Set<ClassType> resources;
    private Set<ClassType> filters;
    private Map<String, Set<EndpointResponse>> exceptionsMappings;
    private ClassModelProcessor classModelProcessor;
    private MethodBodyInterpreter interpreter;
    private WebXmlParser.Result webXmlResult;

    /**
     * Constructor - assigns classesMap and loads REST API definitions from YAML config files defined by DEFS_DIR field.
     * @param classesMap class model represented by map where keys are full qualified class names
     */
    public RestApiReconstructorImpl(Map<String, ClassType> classesMap, WebXmlParser.Result webXmlResult ) {
        this.classesMap = classesMap;
        Map<String, RestApiDefinition> definitions = loadDefinitions();
        this.classModelProcessor = new ClassModelProcessorImpl(definitions);
        this.webXmlResult = webXmlResult;
    }


    /**
     * Method loads Java REST API framework definitions from YAML config files and converts them into RestApiDefinition structures.
     * @return definitions map, where the key is framework name
     */
    private Map<String, RestApiDefinition> loadDefinitions() {
        Map<String, RestApiDefinition> definitions = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        File[] files = new File(DEFS_DIR).listFiles();
        assert files != null;
        for (File file : files) {
            try {
                RestApiDefinition restApiDefinition = mapper.readValue(file, RestApiDefinition.class);
                definitions.put(restApiDefinition.getFramework(), restApiDefinition);
            } catch (IOException e) {
                logger.error("Failed to read REST API definition from " + file.getAbsolutePath(), e);
                e.printStackTrace();
            }
        }
        return definitions;

    }

    /**
     * Extracts REST API endpoints from class model injected in constructor.
     * @return set of endpoints representing the REST API
     */
    public Collection<Endpoint> extractEndpoints() {
        detectResources(classesMap.values());
        this.interpreter = new MethodBodyInterpreter(classModelProcessor.getDefinition(), classesMap); // the correct definition has been picked by now
        Collection<Endpoint> endpoints = new ArrayList<>();
        for (ClassType resource : resources) {
            PathPartAttributes resourceAttributes = classModelProcessor.getPathAttributes(resource);
            for (Method method : resource.getMethods()) {
                if (classModelProcessor.isEndpoint(method)) {
                    processEndpoint(resourceAttributes, method, endpoints);

                }
            }
        }
        addPrefixesToEndpoints(endpoints);
        return endpoints;
    }

    @Override
    public String getFramework() {
        RestApiDefinition definition = classModelProcessor.getDefinition();
        if (definition == null) {
            return "undefined";
        }
        else {
            return definition.getFramework();
        }
    }

    /**
     * Detects REST API resource classes (controllers) among given classes.
     * @param classes collection of classes
     */
    private void detectResources(Collection<ClassType> classes) {
        resources = new HashSet<>();
        Set<ClassType> exceptionHandlers = new HashSet<>();
        filters = new HashSet<>();
        for (ClassType clazz : classes) {
            if (isRegisteredProvider(clazz)) {
                if (classModelProcessor.isResource(clazz)) {
                    resources.add(clazz);
                }
                if (classModelProcessor.isRequestFilter(clazz)) {
                    filters.add(clazz);
                }
                if (classModelProcessor.isExceptionHandler(clazz)) {
                    exceptionHandlers.add(clazz);
                }
            }

        }
        if (!resources.isEmpty()) {
            exceptionsMappings = classModelProcessor.prepareExceptionsMappings(exceptionHandlers, classesMap);
        }
    }

    private boolean isRegisteredProvider(ClassType clazz) {
        if (webXmlResult == null) {  // providers are not set in web.xml
            return true;
        }
        Set<String> providers = webXmlResult.getProviders();
        if (providers == null) {
            return true;    // providers are not set in web.xml
        }
        if (providers.contains("*")) {
            return true;
        }
        String className = clazz.getName();
        for (String provider : providers) {
            if (className.startsWith(provider)) {
                return true;
            }

        }
        return false;

    }


    /**
     * Converts a method from the class model representation into a REST API endpoint and adds it into set of endpoints.
     * @param resourceAttributes attributes of a resource class the method belongs to
     * @param method endpoint method
     * @param endpoints set of detected endpoints
     */
    private void processEndpoint(PathPartAttributes resourceAttributes, Method method, Collection<Endpoint> endpoints) {
        Endpoint endpoint = new Endpoint();

        endpoint.setName(resourceAttributes.getName() + "." + method.getName());

        // path information - method, path, produces, consumes
        PathPartAttributes endpointAttributes = classModelProcessor.getPathAttributes(method);

        // methods are all added together
        endpoint.setHttpMethods(getEndpointMethods(resourceAttributes.getMethods(), endpointAttributes.getMethods()));
        // paths are appended
        endpoint.setPaths(getEndpointPaths(resourceAttributes.getPaths(), endpointAttributes.getPaths()));
        // MIME types are overwritten
        endpoint.setConsumes(getMimeTypes(resourceAttributes.getConsumes(), endpointAttributes.getConsumes()));
        endpoint.setProduces(getMimeTypes(resourceAttributes.getProduces(), endpointAttributes.getProduces()));

        // parameters, headers, cookies, request body
        processMethodParameters(method, endpoint);

        // responses from return type
        boolean saveEndpoint = processReturnType(method, endpoint, endpoints);

        // responses from exception handlers
        Set<EndpointResponse> responses = classModelProcessor.getResponsesFromExceptions(method.getExceptions(), exceptionsMappings);
        if (endpoint.getResponses() == null) {
            endpoint.setResponses(new HashSet<EndpointResponse>());
        }
        endpoint.getResponses().addAll(responses);

        if (saveEndpoint) {
            setDefaultHttpMethods(endpoint, classModelProcessor.getDefinition());
            setDefaultMimeTypes(endpoint, classModelProcessor.getDefinition());
            endpoints.add(endpoint);
        }
    }

    private void setDefaultHttpMethods(Endpoint endpoint, RestApiDefinition definition) {
        if (endpoint.getHttpMethods().isEmpty()) {
            endpoint.getHttpMethods().addAll(definition.getDefaultHttpMethods());
        }
    }

    /**
     * Processes an endpoint method return type and returns true if the endpoint can be added to endpoint list.
     * Endpoint method return type can be either
     * a) special class like Response
     * b) resource class - subresource (jaxrs)
     * c) primitive Java type
     * d) other class - present or not present in this jar
     * @param method endpoint method
     * @param endpoint endpoint
     * @param endpoints set of detected endpoints
     * @return  true if the endpoint can be added to endpoint list, false when the method redirects to a subresource (jaxrs)
     * and should be further processed
     */
    private boolean processReturnType(Method method, Endpoint endpoint, Collection<Endpoint> endpoints) {
        EndpointResponse response = new EndpointResponse();

        DataType returnType = method.getReturnType();
        String returnTypeString = returnType.getBasicType();
        // is this a class defined in this archive ?
        ClassType clazz = classesMap.get(returnTypeString);

        if (clazz != null && classModelProcessor.isSubresource(clazz)) {
            PathPartAttributes resourceAttributes = classModelProcessor.getPathAttributes(clazz);
            resourceAttributes.getConsumes().addAll(endpoint.getConsumes());
            resourceAttributes.getProduces().addAll(endpoint.getProduces());
            resourceAttributes.setPaths(getEndpointPaths(endpoint.getPaths(), resourceAttributes.getPaths()));

            for (Method method1 : clazz.getMethods()) {
                if (classModelProcessor.isEndpoint(method1)) {
                    processEndpoint(resourceAttributes, method1, endpoints);
                }
            }
            return false;
        }
        else if (classModelProcessor.isGenericResponseClass(returnTypeString)) {
            //System.out.println(method.getName());
            // process body log
            Set<EndpointResponse> responses = interpreter.interpretBody(method.getBodyLog(), 0);
            endpoint.setResponses(responses);
            return true;
        }
        else {

            if (returnType.isStructured()) {
                if (BytecodeDescriptorsProcessor.isArrayOrCollection(returnTypeString)) {
                    response.setArray(true);
                    response.setStructure(returnType.getInnerType().getBasicType());
                }
                else { // something else, generic
                    response.setArray(false);
                    response.setStructure(returnTypeString);
                }
            }
            else { // primitive Java type or other class
                response.setStructure(returnTypeString);

            }
        }
        Set<EndpointResponse> responses = new HashSet<>();
        responses.add(response);
        endpoint.setResponses(responses);

        return  true;
    }

    /**
     * Creates endpoint URL by appending a method path to a class resource path.
     * @param resourcePath class resource path
     * @param methodPath method path
     * @return endpoint path
     */
    private String getEndpointPath(String resourcePath, String methodPath) {
        String url = resourcePath + "/" + (methodPath == null ? "" : methodPath);
        url = url.replaceAll("/+", "/");
        if (url.charAt(url.length() - 1) == '/') url = url.substring(0, url.length() - 1);
        return url;
    }

    /**
     * Adds all resource HTTP methods to method HTTP methods (for Spring, JAX-RS does not allow HTTP methods on resources)
     * @param resourceMethods class resource HTTP methods
     * @param methodMethods endpoint method HTTP methods
     * @return list of all methods allowed
     */
    private Set<String> getEndpointMethods(List<String> resourceMethods, List<String> methodMethods) {
        // remove duplicates
        Set<String> endpointMethods = new HashSet<>(methodMethods);
        endpointMethods.addAll(resourceMethods);
        return endpointMethods;
    }

    /**
     * Gets all endpoint paths by creating cartesian product of all method paths to a class resource paths.
     * @param resourcePaths class resource paths
     * @param methodPaths method path
     * @return list of endpoint paths
     */
    private List<String> getEndpointPaths(List<String> resourcePaths, List<String> methodPaths) {
        List<String> urls = new ArrayList<>();
        for (String resourcePath : resourcePaths) {
            for (String methodPath : methodPaths) {
                urls.add(getEndpointPath(resourcePath, methodPath));
            }
        }
        return urls;
    }

    /**
     * Gets all endpoint MIME types (produces/consumes) by replacing the resource ones by the method ones,
     * if there are any.
     * @param resourceTypes class resource produces/consumes
     * @param methodTypes method  produces/consumes
     * @return list of endpoint  produces/consumes
     */
    private Set<String> getMimeTypes(List<String> resourceTypes, List<String> methodTypes) {
        Set<String> mimeTypes = new HashSet<>();
        if (methodTypes.isEmpty()) {
            mimeTypes.addAll(resourceTypes);
        }
        else {
            mimeTypes.addAll(methodTypes);
        }
        return mimeTypes;
    }

    /**
     * Processes method parameters and converts them into endpoint request parameters, endpoint request body and others information.
     * Method parameter can be either:
     * a) endpoint request parameter - query parameter, path parameter, form parameter, matrix parameter, header, cookie
     * b) parameter bean
     * c) endpoint request body
     * d) additional information
     * @param method endpoint method
     * @param endpoint endpoint
     */
    private void processMethodParameters(Method method, Endpoint endpoint) {
        Set<RequestParameter> parameters = new HashSet<>();

        for (Variable variable : method.getParameters()) {
            if (classModelProcessor.isRequestParameter(variable)) {
                RequestParameter parameter = classModelProcessor.convertParameter(variable, endpoint.getConsumes());
                parameters.add(parameter);
            }
            else if (classModelProcessor.isRequestBody(variable)) {
                EndpointRequestBody body = new EndpointRequestBody();
                DataType bodyDataType = variable.getDataType();
                if (bodyDataType.isStructured()) {
                    if (BytecodeDescriptorsProcessor.isArrayOrCollection(bodyDataType.getBasicType())) {
                        body.setArray(true);
                        body.setStructure(bodyDataType.getInnerType().getBasicType());
                    }
                    else { // something else, generic
                        body.setArray(false);
                        body.setStructure(bodyDataType.getBasicType());
                    }
                }
                else { // primitive Java type or other class
                    body.setStructure(bodyDataType.getBasicType());

                }
                body.setOptional(!variable.getAnnotations().containsKey(classModelProcessor.getDefinition().getNotNullAnnotation()));
                if (endpoint.getBody() == null) {
                    endpoint.setBody(body);
                }
            }
            else if (canBeParameterBean(variable)) {
                Set<String> pathVariables = getPathVariablesFromUrl(endpoint.getPaths());
                Set<RequestParameter> beanParameters = classModelProcessor.getParamsFromBean(endpoint, variable, pathVariables, classesMap);
                parameters.addAll(beanParameters);
            }


           /* else {
                // log.. info or warning
            }*/
        }
        endpoint.setParameters(parameters);
    }





    private void setDefaultMimeTypes(Endpoint endpoint, RestApiDefinition definition) {
        EndpointRequestBody requestBody = endpoint.getBody();
        if (endpoint.getConsumes().isEmpty() && requestBody != null) {
            setDefaultMimeTypes(requestBody.getStructure(), requestBody.isArray(), endpoint.getConsumes(), definition);
        }
        if (endpoint.getProduces().isEmpty()) {
            Set<EndpointResponse> responses = endpoint.getResponses();
            for (EndpointResponse response : responses) {
                setDefaultMimeTypes(response.getStructure(), response.isArray(), endpoint.getProduces(), definition);
            }
        }
    }

    private void setDefaultMimeTypes(String dataType, boolean isArray, Set<String> mimeTypes, RestApiDefinition definition) {
        if (dataType == null || "".equals(dataType)) {
            return;
        }
        if (isArray || BytecodeDescriptorsProcessor.isPrimitiveOrString(dataType)) {
            mimeTypes.add(definition.getDefaultObjectMime());

        }
        else {
            mimeTypes.add(definition.getDefaultPrimitiveMime());

        }
    }

    /**
     * Determines whether a method parameter is a parameter bean.
     * @param parameter method parameter
     * @return true if parameter is a parameter bean
     */
    private boolean canBeParameterBean(Variable parameter) {
        String dataType = parameter.getDataType().getBasicType();
        ClassType clazz = classesMap.get(dataType);
        return classModelProcessor.canBeParameterBean(clazz, parameter);
    }

    private Set<String> getPathVariablesFromUrl(String url) {
        Set<String> variables = new HashSet<>();
        Pattern pattern = Pattern.compile("\\{.*}");
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            variables.add(url.substring(matcher.start() + 1, matcher.end() - 1));
        }
        return variables;
    }

    private Set<String> getPathVariablesFromUrl(Collection<String> urls) {
        Set<String> variables = new HashSet<>();
        for (String url : urls) {
            variables.addAll(getPathVariablesFromUrl(url));
        }
        return variables;
    }

    private void addPrefixesToEndpoints(Collection<Endpoint> endpoints) {
        if (webXmlResult == null) {
            return;
        }
        if (webXmlResult.getUrlPrefixes() == null) {
            return;
        }
        for (Endpoint endpoint : endpoints) {
            List<String> paths = getEndpointPaths(webXmlResult.getUrlPrefixes(), endpoint.getPaths());
            endpoint.setPaths(paths);

        }
    }


}
