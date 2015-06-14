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
    
    private final Artifact artifact;
    private final List<Dependency> directDependencies;
    private final List<DependencyNode> hiearchyDependencies;
    
    public MavenArtifactWrapper(Artifact artifact, List<Dependency> directDependencies, List<DependencyNode> hiearchyDependencies) {
        this.artifact = artifact;
        this.directDependencies = directDependencies;
        this.hiearchyDependencies = hiearchyDependencies;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public List<Dependency> getDirectDependencies() {
        return directDependencies;
    }

    public List<DependencyNode> getHiearchyDependencies() {
        return hiearchyDependencies;
    }
}
