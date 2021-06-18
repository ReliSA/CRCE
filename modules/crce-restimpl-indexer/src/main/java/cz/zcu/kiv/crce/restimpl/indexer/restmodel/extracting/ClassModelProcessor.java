package cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting;

import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Method;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.PathPart;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.PathPartAttributes;
import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.Variable;
import cz.zcu.kiv.crce.restimpl.indexer.definition.RestApiDefinition;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.Endpoint;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.EndpointResponse;
import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.RequestParameter;

import java.util.Map;
import java.util.Set;

/**
 * Created by ghessova on 27.03.2018.
 */
public interface ClassModelProcessor {

    /**
     * Returns true if the class is a resource of endpoints.
     * @param clazz class
     * @return true if the class is a resource of endpoints
     */
    boolean isResource(ClassStruct clazz);

    /**
     * Returns true if the class is a request filter.
     * @param clazz class
     * @return true if the class is a request filter
     */
    boolean isRequestFilter(ClassStruct clazz);

    /**
     * Returns true if the class is a parameter bean.
     * @param clazz class
     * @return true if the class is a parameter bean
     */
    boolean canBeParameterBean(ClassStruct clazz, Variable variable);

    /**
     * Returns true if the method is a REST endpoint.
     * @param method method
     * @return true if the method is a REST endpoint
     */
    boolean isEndpoint(Method method);

    /**
     * Extracts path attributes from a path part.
     * @param pathPart path part - class or method
     * @return path attributes (name, paths, produces, consumes)
     */
    PathPartAttributes getPathAttributes(PathPart pathPart);

    /**
     * Returns true if the variable represents an endpoint request parameter (query/path/form/matrix/header/cookie).
     * @param variable method parameter or parameter bean field
     * @return true if the variable represents an endpoint request parameter
     */
    boolean isRequestParameter(Variable variable);

    /**
     * Converts variable into an endpoint request parameter.
     * @param variable method parameter or parameter bean field
     * @return endpoint request parameter
     */
    // EndpointParameter convertParameter(Variable variable);

    RequestParameter convertParameter(Variable variable, Set<String> consumes);

    /**
     * Returns true if the variable represents a HTTP request body.
     * @param variable method parameter
     * @return true if the variable represents a HTTP request body
     */
    boolean isRequestBody(Variable variable);

    /**
     * Returns true if the class represents a subresource (jaxrs).
     * @param clazz class
     * @return true if the class represents a subresource
     */
    boolean isSubresource(ClassStruct clazz);

    /**
     * Returns true if the class is a generic response class.
     * @param className class name
     * @return true if the class is a generic response class
     */
    boolean isGenericResponseClass(String className);

    /**
     * Gets the REST API definition applied in the current archive being processed.
     * @return REST API definition
     */
    RestApiDefinition getDefinition();

    /**
     * Extracts request parameters from JavaBean.
     * @param endpoint processed endpoint
     * @param paramBeanVariable method parameter representing parameter bean
     * @param pathVariables set of path variables extracted from URL
     * @param classesMap map of all classes in archive
     * @return set of request parameters
     */
    Set<RequestParameter> getParamsFromBean(Endpoint endpoint, Variable paramBeanVariable, Set<String> pathVariables, Map<String, ClassStruct> classesMap);

    /**
     * Returns true if the class is an exception handler.
     * @param clazz class
     * @return true if the class is an exception handler
     */
    boolean isExceptionHandler(ClassStruct clazz);

    /**
     * Prepares exception to responses mapping .
     * @param exceptionHandlers classes representing exception handlers
     * @param classesMap map of all classes in archive
     * @return map where key is exception name and value set of possible responses the exception is mapped to
     */
    Map<String, Set<EndpointResponse>> prepareExceptionsMappings(Set<ClassStruct> exceptionHandlers, Map<String, ClassStruct> classesMap);

    /**
     * Extracts responses from method declared exceptions
     * @param exceptions array of exceptions declared in method signatures
     * @param mappings exception to responses mapping
     * @return set of responses corresponding to the given exceptions
     */
    Set<EndpointResponse> getResponsesFromExceptions(String[] exceptions, Map<String, Set<EndpointResponse>> mappings);

}
