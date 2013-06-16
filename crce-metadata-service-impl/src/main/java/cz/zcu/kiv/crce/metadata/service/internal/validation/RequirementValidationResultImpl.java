package cz.zcu.kiv.crce.metadata.service.internal.validation;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.service.validation.Reason;
import cz.zcu.kiv.crce.metadata.service.validation.RequirementValidationResult;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
class RequirementValidationResultImpl extends AbstractValidationResult implements RequirementValidationResult {

    private final List<RequirementValidationResult> childResults = new ArrayList<>();
    private Requirement requirement;

    @Override
    public Requirement getRequirement() {
        return requirement;
    }

    @Override
    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

    @Override
    public List<RequirementValidationResult> getChildResults() {
        return childResults;
    }

    @Override
    public void addChildResult(@Nonnull RequirementValidationResult result) {
        childResults.add(result);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Requirement context: ").append(isContextValid() ? "valid" : "invalid").append("\r\n");
        sb.append("Requirement");
        if (requirement != null) {
            sb.append(" - ").append(requirement.getNamespace()).append(" - ").append(requirement.getId());
        }
        sb.append(": ").append(isEntityValid() ? "valid" : "invalid").append("\r\n");
        for (Reason reason : getReasons()) {
            sb.append("  ").append(reason.getId()).append(" - ").append(reason.getType()).append(" - ").append(reason.getText()).append("\r\n");
        }
        for (RequirementValidationResult requirementValidationResult : childResults) {
            sb.append(requirementValidationResult.toString());
        }
        return sb.toString();
    }
}
