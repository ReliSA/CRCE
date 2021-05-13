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
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.EndpointRequestBody;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassMap;

public class BeanProcessor {

    private Collection<Endpoint> endpoints;
    private ClassMap classes;
    private Map<String, String> cache = new HashMap<>();
    private ObjectMapper mapperObj = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(BeanProcessor.class);


    public BeanProcessor(ClassMap classes, Collection<Endpoint> endpoints) {
        this.classes = classes;
        this.endpoints = endpoints;
    }

    /**
     * Converts Endpoint body to json
     * @param body Body
     * @return String
     */
    private String convertBodyToJson(EndpointRequestBody body) {
        final String className = body.getStructure();
        if (cache.containsKey(className)) {
            return cache.get(className);
        }
        if (classes.containsKey(className)) {
            Map<String, Object> fields = ClassTools.fieldsToMap(classes, classes.get(className));
            try {
                final String jsonifiedClass = mapperObj.writeValueAsString(fields);
                cache.put(className, jsonifiedClass);
                return jsonifiedClass;
            } catch (JsonProcessingException e) {
                logger.error("Unsuported MAP of objects");
                return body.getStructure();
            }
        }
        return body.getStructure();
    }

    /**
     * Process Beans inside Bodies
     * @return Endpoints
     */
    public Collection<Endpoint> process() {

        for (Endpoint endpoint : endpoints) {
            for (EndpointRequestBody body : endpoint.getRequestBodies()) {
                body.setStructure(convertBodyToJson(body));
            }
            for (EndpointRequestBody body : endpoint.getExpectedResponses()) {
                body.setStructure(convertBodyToJson(body));
            }
        }
        return endpoints;
    }
}
