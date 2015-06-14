package cz.zcu.kiv.crce.repository.maven.internal.metadata;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.type.Version;

/**
* Getting Maven artifact informations
* Parsing capabilities and requiremnts to Resource metadata
* @author Miroslav Brožek
*/
public class MavenArtifactMetadataIndexer {

    public static final String NAMESPACE__CRCE_MAVEN_ARTIFACT = "maven.artifact";
    public static final AttributeType<String> ATTRIBUTE__GROUP_ID = new SimpleAttributeType<>("groupId", String.class);
    public static final AttributeType<String> ATTRIBUTE__ARTIFACT_ID = new SimpleAttributeType<>("artifactId", String.class);
    public static final AttributeType<Version> ATTRIBUTE__VERSION = new SimpleAttributeType<>("version", Version.class);
    public static final AttributeType<String> ATTRIBUTE__PACKAGING = new SimpleAttributeType<>("packaging", String.class);
    public static final AttributeType<String> ATTRIBUTE__CLASSIFIER = new SimpleAttributeType<>("classifier", String.class);
    public static final AttributeType<String> ATTRIBUTE__EXTENSION = new SimpleAttributeType<>("extension", String.class);

    public static final AttributeType<String> ATTRIBUTE__NAME = new SimpleAttributeType<>("name", String.class);
    public static final String NAMESPACE__OSGI_BUNDLE = "osgi.wiring.bundle"; // XXX remove
    public static final AttributeType<String> ATTRIBUTE__SYMBOLIC_NAME = new SimpleAttributeType<>("symbolic-name", String.class);

    public static final String DEPENDENCY_SCOPE = "scope";
    public static final String DEPENDENCY_OPTIONAL = "optional";

    private static final Logger logger = LoggerFactory.getLogger(MavenArtifactMetadataIndexer.class);

    private final MetadataService metadataService;
    private final MetadataFactory metadataFactory;

    public MavenArtifactMetadataIndexer(MetadataService metadataService, MetadataFactory metadaFactory) {
        this.metadataService = metadataService;
        this.metadataFactory = metadaFactory;
    }

    public void indexMavenArtifactMetadata(Resource resource, MavenArtifactWrapper maw) {

        metadataService.addCategory(resource, "maven");

        try {
            addArtifactCapability(resource, maw.getArtifact());
            addArtifactRequirements(resource, maw);

        } catch (Exception e) {
            logger.error("Not possible create Capability or Requirements for {} due error:", maw.getArtifact(), e);
        }
    }

    private void addArtifactCapability(Resource resource, Artifact artifact) {
        Capability cap = metadataFactory.createCapability(NAMESPACE__CRCE_MAVEN_ARTIFACT);
        cap.setAttribute(ATTRIBUTE__GROUP_ID, artifact.getGroupId());
        cap.setAttribute(ATTRIBUTE__ARTIFACT_ID, artifact.getArtifactId());
        Version version = new MavenArtifactVersion(artifact.getBaseVersion()).convertVersion();
        cap.setAttribute(ATTRIBUTE__VERSION, version);

        // empty string make no sense to store
        if (!artifact.getClassifier().isEmpty()) {
            cap.setAttribute(ATTRIBUTE__CLASSIFIER, artifact.getClassifier());
        }
        cap.setAttribute(ATTRIBUTE__EXTENSION, artifact.getExtension());
        cap.setAttribute(ATTRIBUTE__PACKAGING, "bundle");

        // if indexing by POM file...this will fix GUI name
        // XXX get rid of this
        if (metadataService.getPresentationName(resource).startsWith("unknown-name")) {

            // need more info from POM file?
            File jar = new File(metadataService.getUri(resource));
            String pomPath = FilenameUtils.removeExtension(jar.toString()) + ".pom";
            File pom = new File(pomPath);
            if (pom.exists()) {
                MavenXpp3Reader reader = new MavenXpp3Reader();
                Model model;
                try {
                    model = reader.read(new FileReader(pom));

                    String presName = model.getName();

                    if (presName == null || presName.contains("${project.artifactId}") || presName.contains("$")) {
                        model.setName(artifact.getArtifactId());
                    }

                    metadataService.setPresentationName(resource, "POM - " + model.getName());
                    // XXX - check why OSGi stuff is in Maven indexer + fix (it must be independent)
                    Capability capOsgi = metadataFactory.createCapability(NAMESPACE__OSGI_BUNDLE);

                    String symbName;
                    Object symbolicNameProp = model.getProperties().get("bundle.symbolicName");

                    if (symbolicNameProp != null) {
                        String sm = symbolicNameProp.toString();
                        int index = sm.lastIndexOf('.');

                        if (index > 0) {
                            symbName = artifact.getGroupId() + sm.substring(index);
                        } else {
                            symbName = artifact.getGroupId() + sm;
                        }
                    } else {
                        symbName = artifact.getGroupId();
                    }
                    capOsgi.setAttribute(ATTRIBUTE__SYMBOLIC_NAME, symbName);
                    capOsgi.setAttribute(ATTRIBUTE__VERSION, version);
                    metadataService.addRootCapability(resource, capOsgi);

                } catch (IOException | XmlPullParserException e) {
                    logger.error("{} POM file has corrupted XML structure, can't be read properly", artifact);
                }
            }

        }

        metadataService.addRootCapability(resource, cap);
    }

    private void addArtifactRequirements(Resource resource, MavenArtifactWrapper maw) {
        if (maw.getDirectDependencies() != null) {
            for (Dependency dependency : maw.getDirectDependencies()) {
                resource.addRequirement(createDependencyRequirement(dependency));
            }
        }

        if (maw.getHiearchyDependencies() != null) {
            for (DependencyNode node : maw.getHiearchyDependencies()) {
                resource.addRequirement(indexDependencies(node, null));
            }
        }
    }
    
    private Requirement indexDependencies(DependencyNode node, Requirement parent) {
        Requirement requirement = createDependencyRequirement(node.getDependency());
        
        if (parent != null) {
            parent.addChild(requirement);
            requirement.setParent(parent);
        }
        
        for (DependencyNode child : node.getChildren()) {
            indexDependencies(child, requirement);
        }
        
        return requirement;
    }
    
    private Requirement createDependencyRequirement(Dependency dependency) {
        Requirement requirement = metadataFactory.createRequirement(NAMESPACE__CRCE_MAVEN_ARTIFACT);
        
        Artifact a = dependency.getArtifact();
        requirement.addAttribute(ATTRIBUTE__GROUP_ID, a.getGroupId());
        requirement.addAttribute(ATTRIBUTE__ARTIFACT_ID, a.getArtifactId());
        
        checkRangeVersion(requirement, new MavenArtifactVersion(a.getBaseVersion()));

        String scope = dependency.getScope();
        if (scope != null && !scope.isEmpty() && !scope.equals("compile")) {
            requirement.setDirective(DEPENDENCY_SCOPE, scope);
        }

        if (dependency.getOptional()) {
            requirement.setDirective(DEPENDENCY_OPTIONAL, dependency.getOptional().toString());
        }
        
        return requirement;
    }

    /**
     * Check if dependency Version is range of versions
     * @param requirement is dependency
     * @param version is version of dependency
     */
    private void checkRangeVersion(Requirement requirement, MavenArtifactVersion version) {
        if (version.isRangeVersion()) {
            if (!version.getvMin().isEmpty()) {
                requirement.addAttribute(ATTRIBUTE__VERSION,
                        new MavenArtifactVersion(version.getvMin()).convertVersion(), version.getvMinOperator());
            }
            if (!version.getvMax().isEmpty()) {
                requirement.addAttribute(ATTRIBUTE__VERSION,
                        new MavenArtifactVersion(version.getvMax()).convertVersion(), version.getvMaxOperator());
            }
        } else {
            requirement.addAttribute(ATTRIBUTE__VERSION, version.convertVersion());
        }
    }
}
