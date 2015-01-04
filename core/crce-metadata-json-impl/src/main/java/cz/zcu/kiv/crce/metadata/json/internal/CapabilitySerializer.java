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
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class CapabilitySerializer extends JsonSerializer<Capability> {

    @Override
    public void serialize(Capability capability, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();

        jgen.writeStringField(Constants.CAPABILITY__ID, capability.getId());
        jgen.writeStringField(Constants.CAPABILITY__NAMESPACE, capability.getNamespace());

        // attributes
        List<Attribute<?>> attributes = capability.getAttributes();
        if (!attributes.isEmpty()) {
            jgen.writeArrayFieldStart(Constants.CAPABILITY__ATTRIBUTES);
            for (Attribute<?> attribute : attributes) {
                jgen.writeObject(attribute);
            }
            jgen.writeEndArray();
        }

        // directives
        Map<String, String> directives = capability.getDirectives();
        if (!directives.isEmpty()) {
            jgen.writeObjectFieldStart(Constants.CAPABILITY__DIRECTIVES);
            for (Entry<String, String> directive : directives.entrySet()) {
                jgen.writeStringField(directive.getKey(), directive.getValue());
            }
            jgen.writeEndObject();
        }

        // requirements
        List<Requirement> requirements = capability.getRequirements();
        if (!requirements.isEmpty()) {
            jgen.writeArrayFieldStart(Constants.CAPABILITY__REQUIREMENTS);
            for (Requirement requirement : requirements) {
                jgen.writeObject(requirement);
            }
            jgen.writeEndArray();
        }

        // properties
        List<Property> properties = capability.getProperties();
        if (!properties.isEmpty()) {
            jgen.writeArrayFieldStart(Constants.CAPABILITY__PROPERTIES);
            for (Property property : properties) {
                jgen.writeObject(property);
            }
            jgen.writeEndArray();
        }

        // children
        List<Capability> children = capability.getChildren();
        if (!children.isEmpty()) {
            jgen.writeArrayFieldStart(Constants.CAPABILITY__CHILDREN);
            for (Capability child : children) {
                jgen.writeObject(child);
            }
            jgen.writeEndArray();
        }

        jgen.writeEndObject();
    }
}
