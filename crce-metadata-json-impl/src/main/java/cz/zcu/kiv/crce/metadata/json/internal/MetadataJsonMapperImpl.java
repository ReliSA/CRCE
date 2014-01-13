package cz.zcu.kiv.crce.metadata.json.internal;

import java.io.IOException;
import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.Init;
import org.apache.felix.dm.annotation.api.ServiceDependency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.json.MetadataJsonMapper;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@Component(provides = MetadataJsonMapper.class)
public class MetadataJsonMapperImpl implements MetadataJsonMapper {

    @ServiceDependency private volatile ResourceFactory resourceFactory;
    ObjectMapper mapper;

    public MetadataJsonMapperImpl() {

    }

    public MetadataJsonMapperImpl(ResourceFactory resourceFactory) {
        this();
        this.resourceFactory = resourceFactory;
        init();
    }

    @Init
    @SuppressWarnings("unchecked")
    final void init() {
        mapper  = new ObjectMapper();

        SimpleModule module = new SimpleModule(); // "resource", new Version(1, 0, 0, null)

        module.addSerializer(Resource.class, new ResourceSerializer());
        module.addSerializer(Capability.class, new CapabilitySerializer());
        module.addSerializer(Requirement.class, new RequirementSerializer());
        module.addSerializer((Class<Attribute<?>>) (Class<?>) Attribute.class, new AttributeSerializer());
        module.addSerializer((Class<Property<?>>) (Class<?>) Property.class, new PropertySerializer());

        module.addDeserializer(Resource.class, new ResourceDeserializer(resourceFactory));
        mapper.registerModule(module);
    }

    @Override
    public String serialize(Resource resource) {
        try {
            //        SimpleModule module = new SimpleModule(null, null);

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resource);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Resource deserialize(String json) {
        try {
            return mapper.readValue(json, Resource.class);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
