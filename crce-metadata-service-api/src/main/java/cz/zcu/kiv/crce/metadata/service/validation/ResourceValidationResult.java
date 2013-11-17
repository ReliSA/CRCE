package cz.zcu.kiv.crce.metadata.service.validation;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface ResourceValidationResult extends ValidationResult {

    void setResource(@Nonnull Resource resource);

    @Nullable
    Resource getResource();

    @Nonnull
    List<CapabilityValidationResult> getCapabilityValidationResults();

    void addCapabilityValidationResult(@Nonnull CapabilityValidationResult result);

    @Nonnull
    List<RequirementValidationResult> getRequirementValidationResults();

    void addRequirementValidationResult(@Nonnull RequirementValidationResult result);

    @Nonnull
    List<PropertyValidationResult> getPropertyValidationResults();

    void addPropertyValidationResult(@Nonnull PropertyValidationResult result);
}
