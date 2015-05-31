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
    private List<Dependency> directDependencies;
    private List<DependencyNode> hiearchyDependencies;
    
    public MavenArtifactWrapper(Artifact artifact, List<Dependency> directDependency, List<DependencyNode> hiearchyDependency) {
        super();
        this.artifact = artifact;
        this.directDependencies = directDependency;
        this.hiearchyDependencies = hiearchyDependency;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    public List<Dependency> getDirectDependencies() {
        return directDependencies;
    }

    public void setDirectDependencies(List<Dependency> directDependencies) {
        this.directDependencies = directDependencies;
    }

    public List<DependencyNode> getHiearchyDependencies() {
        return hiearchyDependencies;
    }

    public void setHiearchyDependencies(List<DependencyNode> hiearchyDependencies) {
        this.hiearchyDependencies = hiearchyDependencies;
    }    
}
