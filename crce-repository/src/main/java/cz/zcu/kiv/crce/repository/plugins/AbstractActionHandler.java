package cz.zcu.kiv.crce.repository.plugins;

import cz.zcu.kiv.crce.plugin.AbstractPlugin;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Repository;
import cz.zcu.kiv.crce.repository.ResourceBuffer;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class AbstractActionHandler extends AbstractPlugin implements ActionHandler {

    @Override
    public Resource onStore(Resource resource, Repository repository) {
        return resource;
    }

    @Override
    public Resource onBufferDelete(Resource resource, ResourceBuffer buffer) {
        return resource;
    }

    @Override
    public Resource onUpload(Resource resource, ResourceBuffer buffer, String name) {
        return resource;
    }

    @Override
    public Resource onDelete(Resource resource, Repository repository) {
        return resource;
    }

    @Override
    public Resource onBufferExecute(Resource resource, ResourceBuffer buffer) {
        return resource;
    }

    @Override
    public Resource onExecute(Resource resource, Repository repository) {
        return resource;
    }

    @Override
    public Resource onDownload(Resource resource, Repository repository) {
        return resource;
    }

    @Override
    public Resource onBufferDownload(Resource resource, ResourceBuffer buffer) {
        return resource;
    }

}
