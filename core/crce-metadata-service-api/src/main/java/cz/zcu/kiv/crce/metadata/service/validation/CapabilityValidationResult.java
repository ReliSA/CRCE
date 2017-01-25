package cz.zcu.kiv.crce.metadata.service.validation;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.metadata.Capability;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface CapabilityValidationResult extends ValidationResult {

    void setCapability(Capability capability);

    @Nullable
    Capability getCapability();

    @Nonnull
    List<CapabilityValidationResult> getChildResults();

    void addChildResult(CapabilityValidationResult result);
}
