package cz.zcu.kiv.crce.metadata.service.internal.validation;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.service.validation.CapabilityValidationResult;
import cz.zcu.kiv.crce.metadata.service.validation.Reason;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class CapabilityValidationResultImpl extends AbstractValidationResult implements CapabilityValidationResult {

    private final List<CapabilityValidationResult> childResults = new ArrayList<>();
    private Capability capability;

    @Override
    public void setCapability(Capability capability) {
        this.capability = capability;
    }

    @Override
    public Capability getCapability() {
        return capability;
    }

    @Override
    public List<CapabilityValidationResult> getChildResults() {
        return childResults;
    }

    @Override
    public void addChildResult(@Nonnull CapabilityValidationResult result) {
        childResults.add(result);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Capability context: ").append(isContextValid() ? "valid" : "invalid").append("\r\n");
        sb.append("Capability");
        if (capability != null) {
            sb.append(" - ").append(capability.getNamespace()).append(" - ").append(capability.getId());
        }
        sb.append(": ").append(isEntityValid() ? "valid" : "invalid").append("\r\n");
        for (Reason reason : getReasons()) {
            sb.append("  ").append(reason.getId()).append(" - ").append(reason.getType()).append(" - ").append(reason.getText()).append("\r\n");
        }
        for (CapabilityValidationResult capabilityValidationResult : childResults) {
            sb.append(capabilityValidationResult.toString());
        }
        return sb.toString();
    }
}
