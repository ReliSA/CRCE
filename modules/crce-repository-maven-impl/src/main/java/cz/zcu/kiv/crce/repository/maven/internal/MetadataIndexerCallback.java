package cz.zcu.kiv.crce.repository.maven.internal;

import java.io.File;

/**
 *
 * @author Miroslav Brozek
 */
public interface MetadataIndexerCallback {

    void index(File file);
    
    void setIndexer(Object indexer);
    
}
