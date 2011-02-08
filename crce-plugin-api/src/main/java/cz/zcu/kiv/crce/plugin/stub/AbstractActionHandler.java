package cz.zcu.kiv.crce.plugin.stub;

import cz.zcu.kiv.crce.plugin.AbstractPlugin;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.ActionHandler;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public abstract class AbstractActionHandler extends AbstractPlugin implements ActionHandler {

    @Override
    public void onCommited(Resource resource) {
        // do nothing
    }

    @Override
    public void onRevoked(Resource resource) {
        // do nothing
    }

    @Override
    public void onUploaded(Resource resource, String name) {
        // do nothing
    }

}
