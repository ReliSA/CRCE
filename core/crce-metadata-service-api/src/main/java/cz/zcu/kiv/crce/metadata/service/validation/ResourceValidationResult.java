package cz.zcu.kiv.crce.metadata.service.validation;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface ResourceValidationResult extends ValidationResult {

    void setResource(Resource resource);

    @Nullable
    Resource getResource();

    @Nonnull
    List<CapabilityValidationResult> getCapabilityValidationResults();

    void addCapabilityValidationResult(CapabilityValidationResult result);

    @Nonnull
    List<RequirementValidationResult> getRequirementValidationResults();

    void addRequirementValidationResult(RequirementValidationResult result);

    @Nonnull
    List<PropertyValidationResult> getPropertyValidationResults();

    void addPropertyValidationResult(PropertyValidationResult result);
}
