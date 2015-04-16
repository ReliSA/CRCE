package cz.zcu.kiv.crce.repository.maven.internal.metadata;

import org.eclipse.aether.artifact.Artifact;

import cz.zcu.kiv.crce.repository.maven.internal.LocalMavenRepositoryIndexer;

/**
 * 
 * @author Miroslav Brozek
 */
public interface MetadataIndexerCallback {    
    
    void index(Artifact artifact, LocalMavenRepositoryIndexer caller);   
    
}
