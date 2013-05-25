package cz.zcu.kiv.crce.metadata.service.internal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;
import org.apache.felix.dm.annotation.api.Start;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@Component(provides = MetadataService.class)
public class MetadataServiceImpl implements MetadataService {

    private static final Logger logger = LoggerFactory.getLogger(MetadataServiceImpl.class);

    public static final String NAMESPACE__CRCE_IDENTITY = "crce.identity";
    public static final AttributeType<String> ATTRIBUTE__NAME = new SimpleAttributeType<>("name", String.class);


    @ServiceDependency
    private ResourceFactory resourceFactory;

    @Start
    public void activate() {
        logger.info("CRCE MetadataService started.");
    }

    @Override
    public String getPresentationName(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource is null.");
        }

        String name = getSingleCapability(resource, NAMESPACE__CRCE_IDENTITY).getAttributeValue(ATTRIBUTE__NAME);

        if (name == null || name.isEmpty()) {
            logger.warn("Resource with id {} has no name specified.", resource.getId());
            name =  "unknown-name:" + resource.getId();
        }
        return name;
    }

    @Override
    public String getPresentationName(Capability capability) {
        if (capability == null) {
            throw new IllegalArgumentException("Capability is null.");
        }

        String name = capability.getAttributeValue(ATTRIBUTE__NAME);

        if (name == null || name.isEmpty()) {
            logger.warn("Capability with id {} has no name specified.", capability.getId());
            name = "unknown-name:" + capability.getId();
        }
        return name;
    }

    @Override
    public String getPresentationName(Requirement requirement) {
        if (requirement == null) {
            throw new IllegalArgumentException("Requirement is null.");
        }
        List<Attribute<String>> attributes = requirement.getAttributes(ATTRIBUTE__NAME);

        String name;
        if (attributes.isEmpty()) {
            logger.warn("Requirement with id {} has no name specified.", requirement.getId());
            name = "unknown-name:" + requirement.getId();
        } else if (attributes.size() == 1) {
            name = attributes.get(0).getAttributeType().getName();
        } else {
            Set<String> set = new HashSet<>();
            for (Attribute<String> attribute : attributes) {
                set.add(attribute.getAttributeType().getName());
            }
            if (set.size() == 1) {
                name = set.iterator().next();
            } else {
                Iterator<String> it = set.iterator();
                StringBuilder sb = new StringBuilder("(");
                sb.append(it.next());
                while (it.hasNext()) {
                    sb.append(",").append(it.next());
                }
                sb.append(")");
                name = sb.toString();
            }
        }

        return name;
    }

    @Override
    public String getPresentationName(Property property) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getPresentationName(Attribute<?> attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Attribute is null.");
        }
        return attribute.getAttributeType().getName();
    }

    private Capability getSingleCapability(@Nonnull Resource resource, @Nonnull String namespace) {
        List<Capability> capabilities = resource.getCapabilities(namespace);

        assert capabilities.size() < 2;

        Capability capability;
        if (capabilities.isEmpty()) {
            capability = resourceFactory.createCapability(namespace);
            resource.addCapability(capability);
        } else {
            capability = capabilities.get(0);
        }
        return capability;

    }
}
