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
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 * Implementation of <code>Resource</code> interface.
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class ResourceImpl extends AbstractEntityBase implements Resource {

    private Map<String, List<Capability>> allCapabilities = new HashMap<>();
    private Map<String, List<Capability>> rootCapabilities = new HashMap<>();
    private Map<String, List<Requirement>> allRequirements = new HashMap<>();
    
    @Override
    public Repository getRepository() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRepository(WritableRepository repository) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    @SuppressWarnings({"null", "ConstantConditions"})
    private Capability getRootCapability(@Nonnull Capability capability) {
        Capability root = capability;
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
