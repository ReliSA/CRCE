package cz.zcu.kiv.crce.metadata.json.internal;

import java.io.IOException;
import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.Init;
import org.apache.felix.dm.annotation.api.ServiceDependency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Entity;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.json.MetadataJsonMapper;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@Component(provides = {MetadataJsonMapper.class, ManagedService.class}, properties = {
    @org.apache.felix.dm.annotation.api.Property(name = "service.pid", value = "cz.zcu.kiv.crce.metadata.json")
})
public class MetadataJsonMapperImpl implements MetadataJsonMapper, ManagedService {

    private static final Logger logger = LoggerFactory.getLogger(MetadataJsonMapperImpl.class);

    public static final String CFG__JSON_PRETTY_PRINT = "json.pretty-print";
    public static final String CFG__JSON_EXPAND_ATTRIBUTE_VALUES = "json.expand-attribute-values";

    @ServiceDependency private volatile MetadataFactory metadataFactory;
    private ObjectMapper mapper;

    private boolean prettyPrint = false;
    private boolean expandAttributes = false;

    @Init
    @SuppressWarnings("unchecked")
    final void init() {
        mapper  = new ObjectMapper();

        SimpleModule module = new SimpleModule(); // "resource", new Version(1, 0, 0, null)

        module.addSerializer(Resource.class, new ResourceSerializer());
        module.addSerializer(Capability.class, new CapabilitySerializer());
        module.addSerializer(Requirement.class, new RequirementSerializer());
        module.addSerializer((Class<Attribute<?>>) (Class<?>) Attribute.class, new AttributeSerializer(this));
        module.addSerializer((Class<Property>) (Class<?>) Property.class, new PropertySerializer());

        final MetadataDeserializer deserializer = new MetadataDeserializer(metadataFactory);

        module.addDeserializer(Resource.class, new JsonDeserializer<Resource>() {
            @Override
            public Resource deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                return deserializer.deserializeResource(jp, ctxt);
            }
        });

        module.addDeserializer(Capability.class, new JsonDeserializer<Capability>() {
            @Override
            public Capability deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                return deserializer.deserializeCapability(jp, ctxt);
            }
        });

        module.addDeserializer(Requirement.class, new JsonDeserializer<Requirement>() {
            @Override
            public Requirement deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                return deserializer.deserializeRequirement(jp, ctxt);
            }
        });

        module.addDeserializer(Property.class, new JsonDeserializer<Property>() {
            @Override
            public Property deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                return deserializer.deserializeProperty(jp, ctxt);
            }
        });

        mapper.registerModule(module);
    }

    @Override
    public String serialize(Entity entity) {
        return serialize(entity, prettyPrint);
    }

    @Override
    public String serialize(Entity entity, boolean prettyPrint) {
        try {
            ObjectWriter writer = prettyPrint ? mapper.writerWithDefaultPrettyPrinter() : mapper.writer();
            return writer.writeValueAsString(entity);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Resource deserialize(String json) {
        return deserialize(json, Resource.class);
    }

    @Override
    public <T extends Entity> T deserialize(String json, Class<T> entity) {
        try {
            return mapper.readValue(json, entity);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        if (properties != null) {
            Object value = properties.get(CFG__JSON_PRETTY_PRINT);
            prettyPrint = value != null && value instanceof String && Boolean.valueOf(((String) value).trim()); // default false

            value = properties.get(CFG__JSON_EXPAND_ATTRIBUTE_VALUES);
            expandAttributes = value != null && value instanceof String && Boolean.valueOf(((String) value).trim()); // default false

            logger.info("MetadataJsonMapper configured: pretty-print={}", prettyPrint);
        }
    }

    boolean expandAttributes() {
        return expandAttributes;
    }
}
