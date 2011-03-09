package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.Executable;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class FilebasedStoreImpl implements Store {

    @Override
    public Resource put(Resource resource) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean remove(Resource resource) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Repository getRepository() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void execute(List<Resource> resources, List<Executable> plugins) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
