package cz.zcu.kiv.crce.handler.metrics.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;

public class FileSizeHandler extends AbstractActionHandler implements ActionHandler {

	
    @Override
    public Resource onUploadToBuffer(Resource resource, Buffer buffer, String name) throws RefusedArtifactException {

    	// TODO add code here
    	
        return resource;
    }
	
    @Override
    public boolean isExclusive() {
        return true;
    }
}
