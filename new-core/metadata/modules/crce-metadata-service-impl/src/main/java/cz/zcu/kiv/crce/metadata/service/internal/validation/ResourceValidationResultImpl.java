package cz.zcu.kiv.crce.metadata.service.internal.validation;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.service.validation.CapabilityValidationResult;
import cz.zcu.kiv.crce.metadata.service.validation.PropertyValidationResult;
import cz.zcu.kiv.crce.metadata.service.validation.Reason;
import cz.zcu.kiv.crce.metadata.service.validation.RequirementValidationResult;
import cz.zcu.kiv.crce.metadata.service.validation.ResourceValidationResult;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class ResourceValidationResultImpl extends AbstractValidationResult implements ResourceValidationResult {

    private final List<CapabilityValidationResult> capabilityValidationResults = new ArrayList<>();
    private final List<RequirementValidationResult> requirementValidationResults = new ArrayList<>();
    private final List<PropertyValidationResult> propertyValidationResults = new ArrayList<>();
    private Resource resource;

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public List<CapabilityValidationResult> getCapabilityValidationResults() {
        return capabilityValidationResults;
    }

    @Override
    public void addCapabilityValidationResult(CapabilityValidationResult result) {
        capabilityValidationResults.add(result);
    }

    @Override
    public List<RequirementValidationResult> getRequirementValidationResults() {
        return requirementValidationResults;
    }

    @Override
    public void addRequirementValidationResult(RequirementValidationResult result) {
        requirementValidationResults.add(result);
    }

    @Override
    public List<PropertyValidationResult> getPropertyValidationResults() {
        return propertyValidationResults;
    }

    @Override
    public void addPropertyValidationResult(PropertyValidationResult result) {
        propertyValidationResults.add(result);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Resource context: ").append(isContextValid() ? "valid" : "invalid").append("\r\n");
        sb.append("Resource");
        if (resource != null) {
            sb.append(" ").append(resource.getId());
        }
        sb.append(": ").append(isEntityValid() ? "valid" : "invalid").append("\r\n");
        for (Reason reason : getReasons()) {
            sb.append("  ").append(reason.getId()).append(" - ").append(reason.getType()).append(" - ").append(reason.getText()).append("\r\n");
        }
        for (CapabilityValidationResult capabilityValidationResult : capabilityValidationResults) {
            sb.append(capabilityValidationResult.toString());
        }
        for (RequirementValidationResult requirementValidationResult : requirementValidationResults) {
            sb.append(requirementValidationResult.toString());
        }
        for (PropertyValidationResult propertyValidationResult : propertyValidationResults) {
            sb.append(propertyValidationResult.toString());
        }
        return sb.toString();
    }
}
