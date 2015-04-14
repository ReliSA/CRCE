package cz.zcu.kiv.crce.repository.maven.internal;

import org.eclipse.aether.artifact.Artifact;

/**
 * 
 * @author Miroslav Brozek
 */
public interface MetadataIndexerCallback {    
    
    void index(Artifact artifact, LocalMavenRepositoryIndexer caller);   
    
}
