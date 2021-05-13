package cz.zcu.kiv.crce.rest.client.indexer.internal;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import cz.zcu.kiv.crce.rest.client.indexer.processor.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Inspired by ghessova on 23.03.2018.
 *
 * @author Gabriela Hessova
 *
 *         Indexer for REST model extraction from input archive.
 */
public class RestClientResourceIndexer extends AbstractResourceIndexer {

    // injected by dependency manager (in Activator)
    private volatile MetadataFactory metadataFactory;
    private volatile MetadataService metadataService;

    private static final Logger logger = LoggerFactory.getLogger(RestClientResourceIndexer.class);

    /**
     * Indexer's entry point.
     *
     * Indexing consists of three steps: 1. Class model representation is created from the archive.
     * 2. REST API model is created based on analysis of the class model. 3. REST model is converted
     * to metadata, which are added to the resource.
     *
     * @param input archive input stream
     * @param resource CRCE resource the metadata are set to
     * @return list of categories assigned by this indexer ('restimpl' or empty list)
     */
    @Override
    public List<String> index(InputStream input, Resource resource) {

        logger.info("Indexing resource " + resource.getId());
        Collection<Endpoint> endpoints = new LinkedList<>();
        try {
            endpoints = Processor.process(input).values();
        } catch (IOException e) {
            logger.error("Could not create class model.", e);
        }
        // CONVERTING REST API MODEL TO METADATA
        if (endpoints.isEmpty()) {
            logger.info("No endpoints found for resource " + resource.getId());
            return Collections.emptyList();
        } else {
            logger.debug("REST API model (Client) extracted");
            // save endpoints and metadata
            RestClientMetadataManager metadataManager =
                    new RestClientMetadataManager(metadataFactory);
            metadataManager.setMetadata(resource, endpoints);
            // label the resource with categories and other common attributes
            metadataService.addCategory(resource, RestClientMetadataConstants.MAIN_CATEGORY); // assign
                                                                                              // main
                                                                                              // category
                                                                                              // tag
            logger.debug("Rest client indexer finished");

            return Collections.singletonList(RestClientMetadataConstants.MAIN_CATEGORY);
        }
    }

    /**
     * 
     * @return Metadata factory
     */
    public MetadataFactory getMetadataFactory() {
        return metadataFactory;
    }

    /**
     * 
     * @param metadataFactory
     */
    public void setMetadataFactory(MetadataFactory metadataFactory) {
        this.metadataFactory = metadataFactory;
    }

    /**
     * 
     * @return Metadata service
     */
    public MetadataService getMetadataService() {
        return metadataService;
    }

    /**
     * 
     * @param metadataService
     */
    public void setMetadataService(MetadataService metadataService) {
        this.metadataService = metadataService;
    }
}
