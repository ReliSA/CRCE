package cz.zcu.kiv.crce.handler.metrics.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;
import cz.zcu.kiv.crce.metadata.service.MetadataService;


public class MetricsIndexer extends AbstractResourceIndexer {

	private volatile MetadataService metadataService;
	
	@Override
	public List<String> index(InputStream input, Resource resource) {

		int size = 0;
				
		try {			
			size = input.available();
			input.close(); // TODO close input stream by its creator.
		} catch (IOException e) {
            
			//logger.error("Could not index resource.", e);
            return Collections.emptyList();
		} 
		
		Capability identity = metadataService.getSingletonCapability(resource, "crce.content");
		identity.setAttribute("size", Long.class, (long)size);
		
		return Collections.emptyList();
	}
	
    @Override
    public List<String> getRequiredCategories() {
        return Collections.singletonList("osgi");
    }
}
