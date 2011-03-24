package cz.zcu.kiv.crce.repository.plugins;

import cz.zcu.kiv.crce.plugin.AbstractPlugin;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;
import java.util.List;
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
    public List<Resource> beforeExecuteInBuffer(List<Resource> resources, Executable executable, Properties properties, Buffer buffer) {
        return resources;
    }

    @Override
    public List<Resource> afterExecuteInBuffer(List<Resource> resources, Executable executable, Properties properties, Buffer buffer) {
        return resources;
    }

    @Override
    public List<Resource> onBufferCommit(List<Resource> resources, Buffer buffer, Store store) {
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
    public List<Resource> beforeExecuteInStore(List<Resource> resources, Executable executable, Properties properties, Store store) {
        return resources;
    }

    @Override
    public List<Resource> afterExecuteInStore(List<Resource> resources, Executable executable, Properties properties, Store store) {
        return resources;
    }

}
