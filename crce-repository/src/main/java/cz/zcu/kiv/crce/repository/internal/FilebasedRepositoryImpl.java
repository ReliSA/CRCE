package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Repository;
import java.io.IOException;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class FilebasedRepositoryImpl implements Repository {

    @Override
    public void put(Resource resource) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Resource[] get(String filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Resource getNewestVersion(String symbolicName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isCompatible(Resource resource) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
