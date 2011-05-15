package cz.zcu.kiv.crce.metadata.wrapper.felix;

import cz.zcu.kiv.crce.metadata.Requirement;
import org.apache.felix.bundlerepository.Capability;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class RequirementWrapper implements org.apache.felix.bundlerepository.Requirement {

    Requirement requirement;
    
    RequirementWrapper(Requirement requirement) {
        this.requirement = requirement;
    }

    @Override
    public String getName() {
        return requirement.getName();
    }

    @Override
    public String getFilter() {
        return requirement.getFilter();
    }

    @Override
    public boolean isMultiple() {
        return requirement.isMultiple();
    }

    @Override
    public boolean isOptional() {
        return requirement.isOptional();
    }

    @Override
    public boolean isExtend() {
        return requirement.isExtend();
    }

    @Override
    public String getComment() {
        return requirement.getComment();
    }

    @Override
    public boolean isSatisfied(Capability capability) {
        return requirement.isSatisfied(Wrapper.unwrap(capability));
    }

}
