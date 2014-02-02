package cz.zcu.kiv.crce.metadata.json.internal;

import java.io.IOException;
import java.util.List;

import org.osgi.framework.Version;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Operator;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@SuppressWarnings("unchecked")
public class AttributeSerializer extends JsonSerializer<Attribute<?>> {

    private final MetadataJsonMapperImpl metadataJsonMapperImpl;

    public AttributeSerializer(MetadataJsonMapperImpl metadataJsonMapperImpl) {
        this.metadataJsonMapperImpl = metadataJsonMapperImpl;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void serialize(Attribute<?> attribute, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();

        jgen.writeStringField(Constants.ATTRIBUTE__NAME, attribute.getAttributeType().getName());
        jgen.writeStringField(Constants.ATTRIBUTE__TYPE, attribute.getAttributeType().getType().getSimpleName());
        if (!Operator.EQUAL.equals(attribute.getOperator())) {
            jgen.writeStringField(Constants.ATTRIBUTE__OPERATOR, attribute.getOperator().getValue());
        }

        if (metadataJsonMapperImpl.expandAttributes()) {
            serializeAsExpanded(attribute, jgen);
        } else {
            serializeAsString(attribute, jgen);
        }

        jgen.writeEndObject();
    }

    private void serializeAsExpanded(Attribute<?> attribute, JsonGenerator jgen) throws IOException, JsonProcessingException {
        Class<?> type = attribute.getAttributeType().getType();
        if (String.class.equals(type)) {
            jgen.writeStringField(Constants.ATTRIBUTE__VALUE, (String) attribute.getValue());
        } else if (Long.class.equals(type)) {
            jgen.writeNumberField(Constants.ATTRIBUTE__VALUE, (Long) attribute.getValue());
        } else if (Double.class.equals(type)) {
            jgen.writeNumberField(Constants.ATTRIBUTE__VALUE, (Double) attribute.getValue());
        } else if (Version.class.equals(type)) {
            jgen.writeObjectFieldStart(Constants.ATTRIBUTE__VALUE);

            Version version = (Version) attribute.getValue();
            jgen.writeNumberField(Constants.ATTRIBUTE__VERSION_MAJOR, version.getMajor());
            jgen.writeNumberField(Constants.ATTRIBUTE__VERSION_MINOR, version.getMinor());
            jgen.writeNumberField(Constants.ATTRIBUTE__VERSION_MICRO, version.getMicro());
            if (version.getQualifier() != null && !version.getQualifier().trim().isEmpty()) {
                jgen.writeStringField(Constants.ATTRIBUTE__VERSION_QUALIFIER, version.getQualifier());
            }

            jgen.writeEndObject();
        } else if (List.class.equals(type)) {
            jgen.writeArrayFieldStart(Constants.ATTRIBUTE__VALUE);

            for (String str : (List<String>) attribute.getValue()) {
                jgen.writeString(str);
            }
            jgen.writeEndArray();
        }
    }

    private void serializeAsString(Attribute<?> attribute, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeStringField(Constants.ATTRIBUTE__VALUE, attribute.getStringValue());
    }
}
