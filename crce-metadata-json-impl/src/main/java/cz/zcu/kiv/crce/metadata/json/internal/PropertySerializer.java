package cz.zcu.kiv.crce.metadata.json.internal;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Property;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class PropertySerializer extends JsonSerializer<Property<?>> {

    @Override
    public void serialize(Property<?> property, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();

        jgen.writeStringField(Constants.PROPERTY__ID, property.getId());
        jgen.writeStringField(Constants.PROPERTY__NAMESPACE, property.getNamespace());

        List<Attribute<?>> attributes = property.getAttributes();
        if (!attributes.isEmpty()) {
            jgen.writeArrayFieldStart(Constants.CAPABILITY__ATTRIBUTES);
            for (Attribute<?> attribute : attributes) {
                jgen.writeObject(attribute);
            }
            jgen.writeEndArray();
        }

        jgen.writeEndObject();
    }
}
