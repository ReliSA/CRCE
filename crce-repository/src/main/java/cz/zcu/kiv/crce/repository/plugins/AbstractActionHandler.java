package cz.zcu.kiv.crce.repository.plugins;

import cz.zcu.kiv.crce.plugin.AbstractPlugin;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class AbstractActionHandler extends AbstractPlugin implements ActionHandler {

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
    public Resource onExecuteInBuffer(Resource resource, Buffer buffer) {
        return resource;
    }

    @Override
    public Resource[] onBufferCommit(Resource[] resources, Buffer buffer, Store store) {
        return resources;
    }
    
    @Override
    public Resource onExecuteInStore(Resource resource, Store store) {
        return resource;
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

}
