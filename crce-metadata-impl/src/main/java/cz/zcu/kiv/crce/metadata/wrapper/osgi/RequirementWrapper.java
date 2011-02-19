package cz.zcu.kiv.crce.metadata.wrapper.osgi;

import cz.zcu.kiv.crce.metadata.Requirement;
import org.osgi.service.obr.Capability;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class RequirementWrapper implements org.osgi.service.obr.Requirement {

    private final Requirement requirement;
    
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
