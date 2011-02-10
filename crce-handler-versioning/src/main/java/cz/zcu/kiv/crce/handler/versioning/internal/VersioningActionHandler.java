package cz.zcu.kiv.crce.handler.versioning.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.ResourceBuffer;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class VersioningActionHandler extends AbstractActionHandler {

    @Override
    public void onUpload(Resource resource, String name, ResourceBuffer buffer) {
        System.out.println("############################################");
        System.out.println("*** versioning ***");
        System.out.println("resource: " + resource.getId());
        System.out.println("name: " + name);
        System.out.println("stored resources length: " + buffer.getStoredResources().length);
        System.out.println("############################################");
    }

}
