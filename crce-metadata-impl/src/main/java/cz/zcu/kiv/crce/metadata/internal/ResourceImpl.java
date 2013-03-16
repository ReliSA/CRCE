package cz.zcu.kiv.crce.metadata.internal;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Implementation of <code>Resource</code> interface.
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class ResourceImpl extends AbstractEntityBase implements Resource {

    private String id;
    private Repository repository = null;
    /*
     * All maps:
     * Key: namespace, value: list of entities.
     */
    private Map<String, List<Capability>> allCapabilities = new HashMap<>();
    private Map<String, List<Capability>> rootCapabilities = new HashMap<>();
    private Map<String, List<Requirement>> allRequirements = new HashMap<>();
    private Map<String, List<Property>> allProperties = new HashMap<>();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
    
    @Override
    public Repository getRepository() {
        return repository;
    }

    @Override
    public void setRepository(WritableRepository repository) {
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
        // add to map of roots
        Capability root = getRootCapability(capability);
        putCapabilityToMap(root, rootCapabilities);
        
        // add to map of all
        putCapabilityToMap(root, allCapabilities);
        
        addChildrenToMap(root.getChildren(), allCapabilities);
    }

    @Override
    public void removeCapability(Capability capability) {
        List<Capability> roots = rootCapabilities.get(capability.getNamespace());
        if (roots != null) {
            roots.remove(capability);
        }
        
        List<Capability> all = allCapabilities.get(capability.getNamespace());
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
        Requirement root = getRootRequirement(requirement);
        
        List<Requirement> requirements = allRequirements.get(root.getNamespace());
        if (requirements == null) {
            requirements = new ArrayList<>();
            allRequirements.put(root.getNamespace(), requirements);
        }
        requirements.add(root);
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
        Property root = getRootProperty(property);
        
        List<Property> properties = allProperties.get(root.getNamespace());
        if (properties == null) {
            properties = new ArrayList<>();
            allProperties.put(root.getNamespace(), properties);
        }
        properties.add(root);
    }

    @Override
    public void removeProperty(Property property) {
        List<Property> properties = allProperties.get(property.getNamespace());
        if (properties != null) {
            properties.remove(property);
        }
    }
    
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Won't be null")
    @SuppressWarnings({"null", "ConstantConditions"})
    private Capability getRootCapability(@Nonnull Capability capability) {
        Capability root = capability;
        while (root.getParent() != null) {
            root = root.getParent();
        }
        return root;
    }

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Won't be null")
    @SuppressWarnings({"null", "ConstantConditions"})
    private Requirement getRootRequirement(@Nonnull Requirement requirement) {
        Requirement root = requirement;
        while (root.getParent() != null) {
            root = root.getParent();
        }
        return root;
    }

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Won't be null")
    @SuppressWarnings({"null", "ConstantConditions"})
    private Property getRootProperty(@Nonnull Property requirement) {
        Property root = requirement;
        while (root.getParent() != null) {
            root = root.getParent();
        }
        return root;
    }

    private void putCapabilityToMap(@Nonnull Capability capability, @Nonnull Map<String, List<Capability>> map) {
        List<Capability> capabilities = map.get(capability.getNamespace());
        if (capabilities == null) {
            capabilities = new ArrayList<>();
            map.put(capability.getNamespace(), capabilities);
        }
        if (!capabilities.contains(capability)) {
            capabilities.add(capability);
        }
    }
    
    private void addChildrenToMap(@Nonnull List<Capability> list, @Nonnull Map<String, List<Capability>> map) {
        for (Capability capability : list) {
            putCapabilityToMap(capability, map);
            addChildrenToMap(capability.getChildren(), map);
        }
    }
}
