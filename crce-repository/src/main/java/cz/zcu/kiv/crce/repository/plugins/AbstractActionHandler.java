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
    public void onStore(Resource resource, Repository repository) {
        // do nothing
    }

    @Override
    public void onBufferDelete(Resource resource, ResourceBuffer buffer) {
        // do nothing
    }

    @Override
    public void onUpload(Resource resource, String name, ResourceBuffer buffer) {
        // do nothing
    }

    @Override
    public void onDelete(Resource resource, Repository repository) {
        // do nothing
    }

    @Override
    public void onBufferExecute(Resource resource, ResourceBuffer buffer) {
        // do nothing
    }

    @Override
    public void onExecute(Resource resource, Repository repository) {
        // do nothing
    }

    @Override
    public void onDownload(Resource resource, Repository repository) {
        // do nothing
    }

    @Override
    public void onBufferDownload(Resource resource, ResourceBuffer buffer) {
        // do nothing
    }

}
