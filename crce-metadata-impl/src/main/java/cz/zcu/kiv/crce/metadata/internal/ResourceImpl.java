package cz.zcu.kiv.crce.metadata.internal;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 * Implementation of <code>Resource</code> interface.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class ResourceImpl extends AbstractEntityBase implements Resource {

    private static final long serialVersionUID = 2594634894045505360L;

    private final String id;
    private Repository repository = null;
    /*
     * All maps:
     * Key: namespace, value: list of entities.
     */
    private final Map<String, List<Capability>> allCapabilities = new HashMap<>();
    private final Map<String, List<Capability>> rootCapabilities = new HashMap<>();
    private final Map<String, List<Requirement>> allRequirements = new HashMap<>();
    private final Map<String, List<Property>> allProperties = new HashMap<>();

    public ResourceImpl(@Nonnull String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Repository getRepository() {
        return repository;
    }

    @Override
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    public List<Capability> getCapabilities() {
        List<Capability> result = new ArrayList<>();
        for (List<Capability> capabilities : allCapabilities.values()) {
            result.addAll(capabilities);
        }
        return result;
    }

    @Override
    public List<Capability> getRootCapabilities() {
        List<Capability> result = new ArrayList<>();
        for (List<Capability> capabilities : rootCapabilities.values()) {
            result.addAll(capabilities);
        }
        return result;
    }

    @Override
    public List<Capability> getRootCapabilities(String namespace) {
        List<Capability> capabilities = rootCapabilities.get(namespace);
        if (capabilities == null) {
            capabilities = Collections.emptyList();
        }
        return capabilities;
    }

    @Override
    public List<Capability> getCapabilities(String namespace) {
        List<Capability> capabilities = allCapabilities.get(namespace);
        if (capabilities == null) {
            capabilities = Collections.emptyList();
        }
        return capabilities;
    }

    @Override
    public boolean hasCapability(Capability capability) {
        List<Capability> capabilities = allCapabilities.get(capability.getNamespace());
        if (capabilities != null) {
            return capabilities.contains(capability);
        }
        return false;
    }

    @Override
    public void addCapability(Capability capability) {
        List<Capability> capabilities = allCapabilities.get(capability.getNamespace());
        if (capabilities == null) {
            capabilities = new ArrayList<>();
            allCapabilities.put(capability.getNamespace(), capabilities);
        }
        capabilities.add(capability);
    }

    @Override
    public void addRootCapability(Capability capability) {
        List<Capability> capabilities = rootCapabilities.get(capability.getNamespace());
        if (capabilities == null) {
            capabilities = new ArrayList<>();
            rootCapabilities.put(capability.getNamespace(), capabilities);
        }
        capabilities.add(capability);
    }

    @Override
    public void removeCapability(Capability capability) {
        List<Capability> roots = allCapabilities.get(capability.getNamespace());
        if (roots != null) {
            roots.remove(capability);
        }
    }

    @Override
    public void removeRootCapability(Capability capability) {
        List<Capability> all = rootCapabilities.get(capability.getNamespace());
        if (all != null) {
            all.remove(capability);
        }
    }

    @Override
    public List<Requirement> getRequirements() {
        List<Requirement> result = new ArrayList<>();
        for (List<Requirement> requirements : allRequirements.values()) {
            result.addAll(requirements);
        }
        return result;
    }

    @Override
    public List<Requirement> getRequirements(String namespace) {
        List<Requirement> result = allRequirements.get(namespace);
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    @Override
    public boolean hasRequirement(Requirement requirement) {
        List<Requirement> requirements = allRequirements.get(requirement.getNamespace());
        if (requirements != null) {
            return requirements.contains(requirement);
        }
        return false;
    }

    @Override
    public void addRequirement(Requirement requirement) {
        List<Requirement> requirements = allRequirements.get(requirement.getNamespace());
        if (requirements == null) {
            requirements = new ArrayList<>();
            allRequirements.put(requirement.getNamespace(), requirements);
        }
        requirements.add(requirement);
    }

    @Override
    public void removeRequirement(Requirement requirement) {
        List<Requirement> requirements = allRequirements.get(requirement.getNamespace());
        if (requirements != null) {
            requirements.remove(requirement);
        }
    }

    @Override
    public List<Property> getProperties() {
        List<Property> result = new ArrayList<>();
        for (List<Property> properties : allProperties.values()) {
            result.addAll(properties);
        }
        return result;
    }

    @Override
    public List<Property> getProperties(String namespace) {
        List<Property> result = allProperties.get(namespace);
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    @Override
    public boolean hasProperty(Property property) {
        List<Property> requirements = allProperties.get(property.getNamespace());
        if (requirements != null) {
            return requirements.contains(property);
        }
        return false;
    }

    @Override
    public void addProperty(Property property) {
        List<Property> properties = allProperties.get(property.getNamespace());
        if (properties == null) {
            properties = new ArrayList<>();
            allProperties.put(property.getNamespace(), properties);
        }
        properties.add(property);
    }

    @Override
    public void removeProperty(Property property) {
        List<Property> properties = allProperties.get(property.getNamespace());
        if (properties != null) {
            properties.remove(property);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResourceImpl other = (ResourceImpl) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }
}
