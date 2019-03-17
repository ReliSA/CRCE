package cz.zcu.kiv.crce.webui.internal.custom;

import cz.zcu.kiv.crce.metadata.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class RequirementImpl implements cz.zcu.kiv.crce.webui.internal.legacy.Requirement {
    private static final Logger logger = LoggerFactory.getLogger(RequirementImpl.class);

    private final Requirement requirement;

    public RequirementImpl(@Nonnull Requirement requirement) {
        this.requirement = requirement;
    }

    @Override
    public String getName() {
        return requirement.getNamespace();
    }

    @Override
    public String getFilter() {
        return requirement.getDirective("filter");
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
    public boolean isExtend() {
        return Boolean.valueOf(requirement.getDirective("extend"));
    }

    @Override
    public String getComment() {
        return requirement.getDirective("comment");
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public boolean isSatisfied(cz.zcu.kiv.crce.webui.internal.legacy.Capability capability) {
        logger.warn("Method isSatisfied is not supported by new Metadata API, returning false for Capability: {}, Requirement: {}", capability, requirement);
        return false;
    }

    @Override
    public cz.zcu.kiv.crce.webui.internal.legacy.Requirement setFilter(String filter) {
        requirement.setDirective("filter", filter);
        return this;
    }

    @Override
    public cz.zcu.kiv.crce.webui.internal.legacy.Requirement setMultiple(boolean multiple) {
        requirement.setDirective("multiple", String.valueOf(multiple));
        return this;
    }

    @Override
    public cz.zcu.kiv.crce.webui.internal.legacy.Requirement setOptional(boolean optional) {
        requirement.setDirective("optional", String.valueOf(optional));
        return this;
    }

    @Override
    public cz.zcu.kiv.crce.webui.internal.legacy.Requirement setExtend(boolean extend) {
        requirement.setDirective("extend", String.valueOf(extend));
        return this;
    }

    @Override
    public cz.zcu.kiv.crce.webui.internal.legacy.Requirement setComment(String comment) {
        requirement.setDirective("comment", String.valueOf(comment));
        return this;
    }
}
