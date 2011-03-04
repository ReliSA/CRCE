package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import java.net.URI;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class ResourceCreatorImpl implements ResourceCreator {

    @Override
    public Resource createResource() {
        return new ResourceImpl();
    }

    @Override
    public Capability createCapability(String name) {
        return new CapabilityImpl(name);
    }

    @Override
    public Requirement createRequirement(String name) {
        return new RequirementImpl(name);
    }

    @Override
    public WritableRepository createRepository(URI uri) {
        return new RepositoryImpl(uri);
    }

}
