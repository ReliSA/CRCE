package cz.zcu.kiv.crce.repository.maven.internal.metadata;


/**
 * 
 * @author Miroslav Brozek
 */
public interface MetadataIndexerCallback {    
    
    void index(MavenArtifactWrapper maw);   
    
}
