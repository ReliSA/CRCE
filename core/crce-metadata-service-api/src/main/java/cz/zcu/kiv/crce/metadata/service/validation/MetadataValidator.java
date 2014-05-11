package cz.zcu.kiv.crce.metadata.service.validation;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.PropertyProvider;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 * Service for validation of metadata entities.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface MetadataValidator {

    /**
     * Validates the given resource including child entities (capabilities, requirements, properties).
     *
     * @param resource
     * @return Validation result.
     */
    @Nonnull
    ResourceValidationResult validate(Resource resource);

    /**
     * Validates the given resource and optionally validates its child entities (capabilities, requirements, properties).
     *
     * @param resource
     * @param includeChildren
     * @return
     */
    @Nonnull
    ResourceValidationResult validate(Resource resource, boolean includeChildren);

    /**
     * Validates the given capability including child capabilities.
     * @param capability
     * @return
     */
    @Nonnull
    CapabilityValidationResult validate(Capability capability);

    /**
     * Validates the given capability and optionally validates its child capabilities.
     * @param capability
     * @param includeChildren
     * @return
     */
    @Nonnull
    CapabilityValidationResult validate(Capability capability, boolean includeChildren);

    /**
     * Validates the given requirement including child requirements.
     * @param requirement
     * @return
     */
    @Nonnull
    RequirementValidationResult validate(Requirement requirement);

    /**
     * Validates the given requirement and optionally validates its child requirements.
     * @param requirement
     * @param includeChildren
     * @return
     */
    @Nonnull
    RequirementValidationResult validate(Requirement requirement, boolean includeChildren);

    /**
     * Validates the given property including child properties.
     * @param <T>
     * @param property
     * @return
     */
    @Nonnull
    <T extends PropertyProvider<T>> PropertyValidationResult<T> validate(Property<T> property);
}
