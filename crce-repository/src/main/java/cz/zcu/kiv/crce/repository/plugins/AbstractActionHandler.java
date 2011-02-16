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
    public Resource onRepositoryStore(Resource resource, Repository repository) {
        return resource;
    }

    @Override
    public Resource onBufferDelete(Resource resource, ResourceBuffer buffer) {
        return resource;
    }

    @Override
    public Resource onBufferUpload(Resource resource, ResourceBuffer buffer, String name) {
        return resource;
    }

    @Override
    public Resource onRepositoryDelete(Resource resource, Repository repository) {
        return resource;
    }

    @Override
    public Resource onBufferExecute(Resource resource, ResourceBuffer buffer) {
        return resource;
    }

    @Override
    public Resource onRepositoryExecute(Resource resource, Repository repository) {
        return resource;
    }

    @Override
    public Resource onRepositoryDownload(Resource resource, Repository repository) {
        return resource;
    }

    @Override
    public Resource onBufferDownload(Resource resource, ResourceBuffer buffer) {
        return resource;
    }

    @Override
    public boolean isModifying() {
        return false;
    }

}
