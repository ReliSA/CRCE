package cz.zcu.kiv.crce.repository.maven.internal;

import org.eclipse.aether.resolution.ArtifactResult;



/**
 *
 * @author Miroslav Brozek
 */
public interface MetadataIndexerCallback {    
    
    void index(ArtifactResult result, LocalMavenRepositoryIndexer caller);   
    
}
