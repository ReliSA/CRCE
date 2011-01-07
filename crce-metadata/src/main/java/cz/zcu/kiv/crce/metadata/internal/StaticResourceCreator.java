package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import java.net.URI;

/**
 *
 * @author kalwi
 */
public class StaticResourceCreator implements ResourceCreator {

    @Override
    public void save(Resource resource) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void move(Resource resource, URI uri) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Resource getResource(URI uri) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
