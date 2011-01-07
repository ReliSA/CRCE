package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import java.net.URI;

/**
 *
 * @author kalwi
 */
public class MetafileResourceCreator implements ResourceCreator {
    
    public static final String METAFILE_EXTENSION = ".meta";


    @Override
    public void save(Resource resource) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void copy(Resource resource, URI uri) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Resource getResource(URI uri) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
