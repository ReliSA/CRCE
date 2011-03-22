package cz.zcu.kiv.crce.repository.plugins;

import cz.zcu.kiv.crce.plugin.AbstractPlugin;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;
import java.util.Collection;
import java.util.Properties;

/**
 * Abstract implementation of <code>ActionHandler</code> which do nothing and
 * it's methods return unmodified argument.
 * 
 * <p>This class is recommended to be used as superclass for all implementations
 * of <code>ActionHandler</code> so then it's not necessary to implement all
 * methods.
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public abstract class AbstractActionHandler extends AbstractPlugin implements ActionHandler {

    @Override
    public Resource onPutToStore(Resource resource, Store repository) throws RevokedArtifactException {
        return resource;
    }

    @Override
    public Resource onDeleteFromBuffer(Resource resource, Buffer buffer) {
        return resource;
    }

    @Override
    public Resource onUploadToBuffer(Resource resource, Buffer buffer, String name) throws RevokedArtifactException {
        return resource;
    }

    @Override
    public Resource onDeleteFromStore(Resource resource, Store repository) {
        return resource;
    }

    @Override
    public Collection<Resource> beforeExecuteInBuffer(Collection<Resource> resources, Executable executable, Properties properties, Buffer buffer) {
        return resources;
    }

    @Override
    public Collection<Resource> afterExecuteInBuffer(Collection<Resource> resources, Executable executable, Properties properties, Buffer buffer) {
        return resources;
    }

    @Override
    public Collection<Resource> onBufferCommit(Collection<Resource> resources, Buffer buffer, Store store) {
        return resources;
    }
    
    @Override
    public Resource onDownloadFromStore(Resource resource, Store repository) {
        return resource;
    }

    @Override
    public Resource onDownloadFromBuffer(Resource resource, Buffer buffer) {
        return resource;
    }

    @Override
    public boolean isModifying() {
        return false;
    }

    @Override
    public Collection<Resource> beforeExecuteInStore(Collection<Resource> resources, Executable executable, Properties properties, Store store) {
        return resources;
    }

    @Override
    public Collection<Resource> afterExecuteInStore(Collection<Resource> resources, Executable executable, Properties properties, Store store) {
        return resources;
    }

}
