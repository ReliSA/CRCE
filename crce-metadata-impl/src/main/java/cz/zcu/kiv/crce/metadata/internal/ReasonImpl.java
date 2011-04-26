package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Reason;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class ReasonImpl implements Reason {

    private Resource resource;
    private Requirement requirement;
    
    public ReasonImpl(Resource resource, Requirement requirement) {
        this.requirement = requirement;
        this.resource = resource;
    }
    
    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public Requirement getRequirement() {
        return requirement;
    }
    
}
