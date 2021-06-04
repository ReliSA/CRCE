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
    private String convertBodyToJson(EndpointBody body) {
        final String className = body.getStructure();
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
     * Process Beans inside Bodies
     * @return Endpoints
     */
    public Collection<Endpoint> process() {

        for (Endpoint endpoint : endpoints) {
            for (EndpointBody body : endpoint.getRequestBodies()) {
                body.setStructure(convertBodyToJson(body));
            }
            for (EndpointBody body : endpoint.getExpectedResponses()) {
                body.setStructure(convertBodyToJson(body));
            }
        }
        return endpoints;
    }
}
