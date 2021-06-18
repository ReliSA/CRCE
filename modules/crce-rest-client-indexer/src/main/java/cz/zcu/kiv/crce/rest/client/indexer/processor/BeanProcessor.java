package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointBody;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointParameter;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassMap;

public class BeanProcessor {



    private static final Logger logger = LoggerFactory.getLogger(BeanProcessor.class);

    /**
     * Converts Endpoint body to json
     * @param body Body
     * @return String
     */
    private static String convertBodyToJson(EndpointBody body, Map<String, String> cache,
            ClassMap classes, ObjectMapper mapperObj) {
        final String className = body.getType();
        if (cache.containsKey(className)) {
            return cache.get(className);
        }
        if (classes.containsKey(className)) {
            Map<String, Object> fields = ClassTools.fieldsToTypes(classes, classes.get(className));
            try {
                String stringified = "java/lang/Object";
                if (fields != null) {
                    stringified = mapperObj.writeValueAsString(fields);
                }
                cache.put(className, stringified);
                return stringified;
            } catch (JsonProcessingException e) {
                logger.error("Unsuported MAP of objects");
                return body.getStructure();
            }
        }
        return body.getStructure();
    }

    /**
     * Converts Endpoint body to json
     * @param body Body
     * @return String
     */
    private static String convertEndpointParameterToJson(EndpointParameter endpointParameter,
            Map<String, String> cache, ClassMap classes, ObjectMapper mapperObj) {
        final String className = endpointParameter.getDataType();
        if (cache.containsKey(className)) {
            return cache.get(className);
        }
        if (classes.containsKey(className)) {
            Map<String, Object> fields = ClassTools.fieldsToTypes(classes, classes.get(className));
            try {
                String stringified = "java/lang/Object";
                if (fields != null) {
                    stringified = mapperObj.writeValueAsString(fields);
                }
                cache.put(className, stringified);
                return stringified;
            } catch (JsonProcessingException e) {
                logger.error("Unsuported MAP of objects");
                return endpointParameter.getDataType();
            }
        }
        return endpointParameter.getDataType();
    }

    /**
     * Process Beans inside Bodies
     * @return Endpoints
     */
    public static void process(Collection<Endpoint> endpoints, ClassMap classes) {
        Map<String, String> cache = new HashMap<>();
        ObjectMapper mapperObj = new ObjectMapper();
        for (Endpoint endpoint : endpoints) {
            for (EndpointBody body : endpoint.getRequestBodies()) {
                body.setStructure(convertBodyToJson(body, cache, classes, mapperObj));
            }
            for (EndpointBody body : endpoint.getExpectedResponses()) {
                body.setStructure(convertBodyToJson(body, cache, classes, mapperObj));
            }
            for (EndpointParameter endpointParameter : endpoint.getParameters()) {
                endpointParameter.setStructure(convertEndpointParameterToJson(endpointParameter,
                        cache, classes, mapperObj));
            }
        }
    }
}
