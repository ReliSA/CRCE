package cz.zcu.kiv.crce.repository.maven.internal.metadata;

import java.util.List;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;


/**
 * Wrapper to hold necesary info about Artifact 
 * gathered by Aether 
 * 
 * @author Miroslav Brozek
 *
 */
public class MavenArtifactWrapper {
    
    private Artifact artifact;
    
    private List<Dependency> directDependency;
    
    private List<DependencyNode> hiearchyDependency;
    
    
    public MavenArtifactWrapper(Artifact artifact, List<Dependency> directDependency, List<DependencyNode> hiearchyDependency) {
        super();
        this.artifact = artifact;
        this.directDependency = directDependency;
        this.hiearchyDependency = hiearchyDependency;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    public List<Dependency> getDirectDependency() {
        return directDependency;
    }

    public void setDirectDependency(List<Dependency> directDependency) {
        this.directDependency = directDependency;
    }

    public List<DependencyNode> getHiearchyDependency() {
        return hiearchyDependency;
    }

    public void setHiearchyDependency(List<DependencyNode> hiearchyDependency) {
        this.hiearchyDependency = hiearchyDependency;
    }    
    

}
