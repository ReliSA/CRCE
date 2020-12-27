package cz.zcu.kiv.crce.metadata.json.internal;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class ResourceSerializer extends JsonSerializer<Resource> {

    @Override
    public void serialize(Resource resource, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();

        jgen.writeStringField(Constants.RESOURCE__ID, resource.getId());
//        jgen.writeObjectField(Constants.RESOURCE__REPOSITORY, resource.getRepository());

        // capabilities
        List<Capability> capabilities = resource.getRootCapabilities();
        if (!capabilities.isEmpty()) {
            jgen.writeArrayFieldStart(Constants.RESOURCE__CAPABILITIES);
            for (Capability capability : capabilities) {
                jgen.writeObject(capability);
            }
            jgen.writeEndArray();
        }

        // requirements
        List<Requirement> requirements = resource.getRequirements();
        if (!requirements.isEmpty()) {
            jgen.writeArrayFieldStart(Constants.RESOURCE__REQUIREMENTS);
            for (Requirement capability : requirements) {
                jgen.writeObject(capability);
            }
            jgen.writeEndArray();
        }

        // properties
        List<Property> properties = resource.getProperties();
        if (!properties.isEmpty()) {
            jgen.writeArrayFieldStart(Constants.RESOURCE__PROPERTIES);
            for (Property property : properties) {
                jgen.writeObject(property);
            }
            jgen.writeEndArray();
        }

        jgen.writeEndObject();
    }

}
