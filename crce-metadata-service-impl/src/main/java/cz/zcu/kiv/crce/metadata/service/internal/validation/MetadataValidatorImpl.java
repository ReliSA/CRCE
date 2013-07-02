package cz.zcu.kiv.crce.metadata.service.internal.validation;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.Start;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.service.validation.CapabilityValidationResult;
import cz.zcu.kiv.crce.metadata.service.validation.PropertyValidationResult;
import cz.zcu.kiv.crce.metadata.service.validation.ReasonType;
import cz.zcu.kiv.crce.metadata.service.validation.RequirementValidationResult;
import cz.zcu.kiv.crce.metadata.service.validation.ResourceValidationResult;
import cz.zcu.kiv.crce.metadata.service.validation.MetadataValidator;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@Component(provides = MetadataValidator.class)
public class MetadataValidatorImpl implements MetadataValidator, ManagedService {

    private static final Logger logger = LoggerFactory.getLogger(MetadataValidatorImpl.class);

    public static final String CFG__STRUCTURE_VALIDATION = "structure-validation";

    private boolean structureValidationEnabled = true;

    @Start
    public void activate() {
        logger.info("CRCE Validator started.");
    }

    @Override
    public ResourceValidationResult validate(Resource resource) {
        return validate(resource, true);
    }

    @Override
    public ResourceValidationResult validate(Resource resource, boolean includeChildren) {
        ResourceValidationResult result = new ResourceValidationResultImpl();
        result.setResource(resource);

        if (includeChildren) {
            // --- Capabilities validation ---
            // Capabilities not processed in capability tree (only for structure validation)
            Set<Capability> remaining = null;
            if (structureValidationEnabled) {
                remaining = new HashSet<>(resource.getCapabilities());
            }
            // Validate capability tree
            List<Capability> rootCapabilities = resource.getRootCapabilities();
            for (Capability capability : rootCapabilities) {
                CapabilityValidationResult capabilityResult = validate(capability, resource, remaining, includeChildren);
                if (!capabilityResult.isContextValid()) {
                    result.setContextValid(false);
                }
                result.addCapabilityValidationResult(capabilityResult);
            }
            // Validate structure of all capabilities
            if (structureValidationEnabled && remaining != null && !remaining.isEmpty()) {
                for (Capability capability : remaining) {
                    CapabilityValidationResult capabilityResult = new CapabilityValidationResultImpl();
                    capabilityResult.addReason(
                            new ReasonImpl(ReasonType.CAPABILITY_TREE, capability.getId(),
                            "Capability from list of all capabilities is not in hierarchy tree."));
                    capabilityResult.setContextValid(false);
                    capabilityResult.setEntityValid(false);
                    result.addCapabilityValidationResult(capabilityResult);
                }
                result.setContextValid(false);
            }

            // --- Requirements validation ---
            List<Requirement> requirements = resource.getRequirements();
            for (Requirement requirement : requirements) {
                RequirementValidationResult requirementResult = validate(requirement, resource, includeChildren);
                if (!requirementResult.isContextValid()) {
                    result.setContextValid(false);
                }
                result.addRequirementValidationResult(requirementResult);
            }
            // --- Properties validation ---
        }

        return result;
    }

    @Override
    public CapabilityValidationResult validate(Capability capability) {
        return validate(capability, true);
    }

    @Override
    public CapabilityValidationResult validate(Capability capability, boolean includeChildren) {
        return validate(capability, null, null, includeChildren);
    }

    @Nonnull
    private CapabilityValidationResult validate(Capability capability, Resource resource, Set<Capability> remaining, boolean includeChildren) {
        CapabilityValidationResult result = validateRecursive(capability, null, resource, remaining, includeChildren);
        return result;
    }

    /**
     *
     * @param capability Validated capability.
     * @param parent Parent capability.
     * @param resource Parent resource.
     * @param remaining Unprocessed capabilities.
     * @param includeChildren
     * @return
     */
    @Nonnull
    private CapabilityValidationResult validateRecursive(
            @Nonnull Capability capability, @CheckForNull Capability parent, @CheckForNull Resource resource,
            @CheckForNull Set<Capability> remaining, boolean includeChildren) {

        CapabilityValidationResult result = new CapabilityValidationResultImpl();
        result.setCapability(capability);

        if (structureValidationEnabled) {
            if (remaining != null && !remaining.remove(capability)) {
                    result.addReason(new ReasonImpl(ReasonType.CAPABILITY_TREE, capability.getId(),
                            "Capability from hierarchy tree is not on list of all capabilities."));
                    result.setContextValid(false);
                    result.setEntityValid(false);
            }
            if (parent != null && !parent.equals(capability.getParent())) {
                result.addReason(
                        new ReasonImpl(ReasonType.PARENT_REFERENCE, capability.getId(),
                        "Wrong reference to parent: " + capability.getParent()));

                result.setEntityValid(false);
                result.setContextValid(false);
            }
            if (resource != null && !resource.equals(capability.getResource())) {
                result.addReason(
                        new ReasonImpl(ReasonType.RESOURCE_REFERENCE, capability.getId(),
                        "Wrong reference to resource: " + capability.getResource()));

                result.setEntityValid(false);
                result.setContextValid(false);
            }
        }

        if (includeChildren) {
            for (Capability child : capability.getChildren()) {
                CapabilityValidationResult childResult = validateRecursive(child, capability, resource, remaining, includeChildren);
                if (!childResult.isContextValid()) {
                    result.setContextValid(false);
                }
            }
        }

        return result;
    }

    @Override
    public RequirementValidationResult validate(Requirement requirement) {
        RequirementValidationResult result = validate(requirement, null, true);
        return result;
    }

    @Override
    public RequirementValidationResult validate(Requirement requirement, boolean includeChildren) {
        return validate(requirement, null, includeChildren);
    }

    @Nonnull
    private RequirementValidationResult validate(@Nonnull Requirement requirement, @CheckForNull Resource resource, boolean includeChildren) {
        return validateRecursive(requirement, null, resource, includeChildren);
    }

    @Nonnull
    private RequirementValidationResult validateRecursive(@Nonnull Requirement requirement, @CheckForNull Requirement parent,
            @CheckForNull Resource resource, boolean includeChildren) {

        RequirementValidationResult result = new RequirementValidationResultImpl();
        result.setRequirement(requirement);

        if (structureValidationEnabled) {
            if (parent != null && !parent.equals(requirement.getParent())) {
                result.addReason(
                        new ReasonImpl(ReasonType.PARENT_REFERENCE, requirement.getId(),
                        "Wrong reference to parent: " + requirement.getParent()));

                result.setEntityValid(false);
                result.setContextValid(false);
            }
            if (resource != null && !resource.equals(requirement.getResource())) {
                result.addReason(
                        new ReasonImpl(ReasonType.RESOURCE_REFERENCE, requirement.getId(),
                        "Wrong reference to resource: " + requirement.getResource()));

                result.setEntityValid(false);
                result.setContextValid(false);
            }
        }

        if (includeChildren) {
            for (Requirement child : requirement.getChildren()) {
                RequirementValidationResult childResult = validateRecursive(child, requirement, resource, includeChildren);
                if (!childResult.isContextValid()) {
                    result.setContextValid(false);
                }
            }
        }

        return result;
    }

    @Override
    public PropertyValidationResult validate(Property property) {
        PropertyValidationResult result = validate(property, null, true);
        return result;
    }

    @Override
    public PropertyValidationResult validate(Property property, boolean includeChildren) {
        return validate(property, null, includeChildren);
    }

    @Nonnull
    private PropertyValidationResult validate(@Nonnull Property property, @CheckForNull Resource resource, boolean includeChildren) {
        return validateRecursive(property, null, resource, includeChildren);
    }

    @Nonnull
    private PropertyValidationResult validateRecursive(@Nonnull Property property, @CheckForNull Property parent,
            @CheckForNull Resource resource, boolean includeChildren) {

        PropertyValidationResult result = new PropertyValidationResultImpl();
        result.setProperty(property);

        if (structureValidationEnabled) {
            if (parent != null && !parent.equals(property.getParent())) {
                result.addReason(
                        new ReasonImpl(ReasonType.PARENT_REFERENCE, property.getId(),
                        "Wrong reference to parent: " + property.getParent()));

                result.setEntityValid(false);
                result.setContextValid(false);
            }
            if (resource != null && !resource.equals(property.getResource())) {
                result.addReason(
                        new ReasonImpl(ReasonType.RESOURCE_REFERENCE, property.getId(),
                        "Wrong reference to resource: " + property.getResource()));

                result.setEntityValid(false);
                result.setContextValid(false);
            }
        }

        if (includeChildren) {
            for (Property child : property.getChildren()) {
                PropertyValidationResult childResult = validateRecursive(child, property, resource, includeChildren);
                if (!childResult.isContextValid()) {
                    result.setContextValid(false);
                }
            }
        }

        return result;
    }

    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        String structureValidation = (String) properties.get(CFG__STRUCTURE_VALIDATION);
        if (structureValidation != null) {
            switch (structureValidation) {
                case "true":
                case "enabled":
                    structureValidationEnabled = true;
                    break;

                case "false":
                case "disabled":
                default:
                    structureValidationEnabled = false;
            }
        }
    }
}
