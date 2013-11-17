package cz.zcu.kiv.crce.metadata.service.validation;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cz.zcu.kiv.crce.metadata.Requirement;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface RequirementValidationResult extends ValidationResult {

    void setRequirement(@Nonnull Requirement requirement);

    @Nullable
    Requirement getRequirement();

    @Nonnull
    List<RequirementValidationResult> getChildResults();

    void addChildResult(@Nonnull RequirementValidationResult result);
}
