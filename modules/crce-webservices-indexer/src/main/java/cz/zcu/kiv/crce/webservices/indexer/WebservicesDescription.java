package cz.zcu.kiv.crce.webservices.indexer;

import cz.zcu.kiv.crce.metadata.Resource;
import java.util.List;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public interface WebservicesDescription {

    List<Resource> createWebserviceRepresentations(String url);

}
