package cz.zcu.kiv.crce.restimpl.indexer.restmodel.extracting;


import cz.zcu.kiv.crce.restimpl.indexer.restmodel.structures.Endpoint;

import java.util.Collection;


/**
 * Created by ghessova on 26.04.2018.
 */
public interface RestApiReconstructor {

    /**
     * Extracts REST API endpoints from given source.
     * @return set of endpoints representing the REST API
     */
    Collection<Endpoint> extractEndpoints();

    String getFramework();


}
