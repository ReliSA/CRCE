package cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting;

import cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures.ClassType;
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
    boolean isResource(ClassType clazz);

    /**
     * Returns true if the class is a request filter.
     * @param clazz class
     * @return true if the class is a request filter
     */
    boolean isRequestFilter(ClassType clazz);

    /**
     * Returns true if the class is a parameter bean.
     * @param clazz class
     * @return true if the class is a parameter bean
     */
    boolean canBeParameterBean(ClassType clazz, Variable variable);

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
    boolean isSubresource(ClassType clazz);

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

    Set<RequestParameter> getParamsFromBean(Endpoint endpoint, Variable paramBeanVariable, Set<String> pathTemplates, Map<String, ClassType> classesMap);

    boolean isExceptionHandler(ClassType clazz);

    Map<String, Set<EndpointResponse>> prepareExceptionsMappings(Set<ClassType> exceptionHandlers, Map<String, ClassType> classesMap);

    Set<EndpointResponse> getResponsesFromExceptions(String[] exceptions, Map<String, Set<EndpointResponse>> mappings);

}
