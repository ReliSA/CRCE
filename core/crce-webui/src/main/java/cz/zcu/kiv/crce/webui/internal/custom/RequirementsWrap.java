package cz.zcu.kiv.crce.webui.internal.custom;

import cz.zcu.kiv.crce.metadata.Requirement;

public class RequirementsWrap extends RequirementAdapter {

    protected Requirement requirement;

    public RequirementsWrap(Requirement r) {
        this.requirement = r;
    }

    @Override
    public String getName() {
        return requirement.getNamespace();
    }

    @Override
    public String getFilter() {
        return requirement.getDirective("filter"); // TODO filter is not supported yet
    }

    @Override
    public boolean isExtend() {
        return Boolean.valueOf(requirement.getDirective("extend"));
    }

    @Override
    public boolean isMultiple() {
        return Boolean.valueOf(requirement.getDirective("multiple"));
    }

    @Override
    public boolean isOptional() {
        return Boolean.valueOf(requirement.getDirective("optional"));
    }

    @Override
    public boolean isWritable() {
        return true;
    }
}
