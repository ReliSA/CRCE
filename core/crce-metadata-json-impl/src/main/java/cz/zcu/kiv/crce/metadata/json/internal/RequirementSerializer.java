package cz.zcu.kiv.crce.metadata.json.internal;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Requirement;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class RequirementSerializer extends JsonSerializer<Requirement> {

    @Override
    public void serialize(Requirement requirement, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();

        jgen.writeStringField(Constants.REQUIREMENT__ID, requirement.getId());
        jgen.writeStringField(Constants.REQUIREMENT__NAMESPACE, requirement.getNamespace());
        
        // attributes
        List<Attribute<?>> attributes = requirement.getAttributes();
        if (!attributes.isEmpty()) {
            jgen.writeArrayFieldStart(Constants.REQUIREMENT__ATTRIBUTES);
            for (Attribute<?> attribute : attributes) {
                jgen.writeObject(attribute);
            }
            jgen.writeEndArray();
        }

        // directives
        Map<String, String> directives = requirement.getDirectives();
        if (!directives.isEmpty()) {
            jgen.writeObjectFieldStart(Constants.REQUIREMENT__DIRECTIVES);
            for (Entry<String, String> directive : directives.entrySet()) {
                jgen.writeStringField(directive.getKey(), directive.getValue());
            }
            jgen.writeEndObject();
        }

        // children
        List<Requirement> children = requirement.getChildren();
        if (!children.isEmpty()) {
            jgen.writeArrayFieldStart(Constants.REQUIREMENT__CHILDREN);
            for (Requirement child : requirement.getChildren()) {
                jgen.writeObject(child);
            }
            jgen.writeEndArray();
        }

        jgen.writeEndObject();
    }
}
