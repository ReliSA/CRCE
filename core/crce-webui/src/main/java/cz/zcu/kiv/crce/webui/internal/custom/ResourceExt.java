package cz.zcu.kiv.crce.webui.internal.custom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

public class ResourceExt extends ResourceWrap {

    private static final Logger logger = LoggerFactory.getLogger(ResourceExt.class);

    private boolean satisfied;

    public ResourceExt(Resource r, MetadataService metadataService) {
        super(r, metadataService);
        this.satisfied = true;
    }

    public boolean getSatisfied() {
        return satisfied;
    }

    @Override
    public void addRequirement(cz.zcu.kiv.crce.webui.internal.legacy.Requirement requirement) {
        satisfied = false;
        logger.warn("Adding legacy requirements is not supported yet with new Metadata API: {}", requirement); // TODO fix functionality
//        resource.unsetRequirement(requirement);
//        RequirementExt rext = new RequirementExt(requirement);
//        resource.addRequirement(rext);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof cz.zcu.kiv.crce.webui.internal.legacy.Resource) {
            return this.getUri().equals(((cz.zcu.kiv.crce.webui.internal.legacy.Resource) obj).getUri());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return resource.hashCode();
    }
}
