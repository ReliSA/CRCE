package cz.zcu.kiv.crce.repository.plugins;

import cz.zcu.kiv.crce.plugin.AbstractPlugin;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.Buffer;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class AbstractActionHandler extends AbstractPlugin implements ActionHandler {

    @Override
    public Resource onStorePut(Resource resource, Store repository) {
        return resource;
    }

    @Override
    public Resource onBufferDelete(Resource resource, Buffer buffer) {
        return resource;
    }

    @Override
    public Resource onBufferUpload(Resource resource, Buffer buffer, String name) {
        return resource;
    }

    @Override
    public Resource onStoreDelete(Resource resource, Store repository) {
        return resource;
    }

    @Override
    public Resource onBufferExecute(Resource resource, Buffer buffer) {
        return resource;
    }

    @Override
    public Resource[] onBufferCommit(Resource[] resources, Buffer buffer, Store store) {
        return resources;
    }
    
    @Override
    public Resource onStoreExecute(Resource resource, Store store) {
        return resource;
    }

    @Override
    public Resource onStoreDownload(Resource resource, Store repository) {
        return resource;
    }

    @Override
    public Resource onBufferDownload(Resource resource, Buffer buffer) {
        return resource;
    }

    @Override
    public boolean isModifying() {
        return false;
    }

}
