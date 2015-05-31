package cz.zcu.kiv.crce.repository.maven.internal.metadata;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

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
import cz.zcu.kiv.crce.repository.maven.internal.MavenStoreConfiguration;

/**
* Getting Maven artifact informations
* Parsing capabilities and requiremnts to Resource metadata
* @author Miroslav Brožek
*/
public class MavenArtifactMetadataIndexer {
    
    private volatile MetadataService metadataService;
    private volatile MetadataFactory metadataFactory;
    
    private static final Logger logger = LoggerFactory.getLogger(MavenArtifactMetadataIndexer.class);
    
    public static final String NAMESPACE__CRCE_MAVEN_ARTIFACT = "maven.artifact";
    public static final AttributeType<String> ATTRIBUTE__GROUP_ID = new SimpleAttributeType<>("groupId", String.class);
    public static final AttributeType<String> ATTRIBUTE__ARTIFACT_ID = new SimpleAttributeType<>("artifactId", String.class);
    public static final AttributeType<Version> ATTRIBUTE__VERSION = new SimpleAttributeType<>("version", Version.class);
    public static final AttributeType<String> ATTRIBUTE__PACKAGING = new SimpleAttributeType<>("packaging", String.class);
    public static final AttributeType<String> ATTRIBUTE__CLASSIFIER = new SimpleAttributeType<>("classifier", String.class);
    public static final AttributeType<String> ATTRIBUTE__EXTENSION = new SimpleAttributeType<>("extension", String.class);
    
    public static final AttributeType<String> ATTRIBUTE__NAME = new SimpleAttributeType<>("name", String.class);
    public static final String NAMESPACE__OSGI_BUNDLE = "osgi.wiring.bundle";
    public static final AttributeType<String> ATTRIBUTE__SYMBOLIC_NAME = new SimpleAttributeType<>("symbolic-name", String.class);
    
    public static final String DEPENDENCY_SCOPE = "scope";
    public static final String DEPENDENCY_OPTIONAL = "optional";
    
    
    public MavenArtifactMetadataIndexer(MetadataService metadataService, MetadataFactory metadaFactory) {
        this.metadataService = metadataService;
        this.metadataFactory = metadaFactory;
    }

    public void createMavenArtifactMetadata(Resource resource, MavenArtifactWrapper maw) {

        metadataService.addCategory(resource, "maven");    

        try{
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
        Version v = new MavenArtifactVersion(artifact.getBaseVersion()).convertVersion();
        cap.setAttribute(ATTRIBUTE__VERSION, v);

        // empty string make no sense to store
        if (!artifact.getClassifier().isEmpty()) {
            cap.setAttribute(ATTRIBUTE__CLASSIFIER, artifact.getClassifier());
        }
        cap.setAttribute(ATTRIBUTE__EXTENSION, artifact.getExtension());
        cap.setAttribute(ATTRIBUTE__PACKAGING, "bundle");

        //if indexing by POM file...this will fix GUI name
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
                    capOsgi.setAttribute(ATTRIBUTE__VERSION, v);
                    metadataService.addRootCapability(resource, capOsgi);

                } catch (IOException | XmlPullParserException e) {
                    logger.error("{} POM file has corrupted XML structure, can't be read properly", artifact);
                }
            }

        }

        metadataService.addRootCapability(resource, cap);
    }
    
    private void addArtifactRequirements(Resource resource, MavenArtifactWrapper maw) {
           
         //Create Only Direct Dependency
        if (!MavenStoreConfiguration.isDependencyHierarchy()) {
            createDirectDependency(resource, maw);            
        }
        
        //Create Dependency Hierarchy
        else{
            createDependencyHierarchy(resource, maw);
        }
    }

    private void createDirectDependency(Resource resource, MavenArtifactWrapper maw) {
        
        for (Dependency d : maw.getDirectDependencies()) {
            Requirement requirement = metadataFactory.createRequirement(NAMESPACE__CRCE_MAVEN_ARTIFACT);
            createDependencyRequirement(d, requirement);
            resource.addRequirement(requirement);
        }
    }
    

    /**
     * Adding Dependency info to Resource requirements
     * Very very time consuming
     * @param list of Node Dependencies
     * @param resource is Artifact Node
     */
    private void createDependencyHierarchy(Resource resource, MavenArtifactWrapper maw) {        
        
        for (DependencyNode dn : maw.getHiearchyDependencies()) {
            Requirement requirement = metadataFactory.createRequirement(NAMESPACE__CRCE_MAVEN_ARTIFACT);
            createDependencyRequirement(dn.getDependency(), requirement);
            
            //any existing children dependcies?
            Iterator<DependencyNode> it = dn.getChildren().iterator(); 
    
            while(it.hasNext()) {
                solveChildren(it.next(), requirement);                
            }            
            resource.addRequirement(requirement);
        }        
    }
    
    private void createDependencyRequirement(Dependency d, Requirement requirement) {
        Artifact a = d.getArtifact();
        requirement.addAttribute(ATTRIBUTE__GROUP_ID, a.getGroupId());
        requirement.addAttribute(ATTRIBUTE__ARTIFACT_ID, a.getArtifactId());    
        checkRangeVersion(requirement, new MavenArtifactVersion(a.getBaseVersion()));
                    
        String scope = d.getScope();
        if (scope != null && !scope.isEmpty() && !scope.equals("compile")) {
            requirement.setDirective(DEPENDENCY_SCOPE, scope);                
        }
        
        if ( d.getOptional()) {
            requirement.setDirective(DEPENDENCY_OPTIONAL, d.getOptional().toString());
        }
    }

    /**
     * Check if dependency Version is range of versions
     * @param r is dependency
     * @param v is version of dependency
     */
    private void checkRangeVersion(Requirement r, MavenArtifactVersion v) {
        if (v.isRangeVersion()) {
            if (!v.getvMin().isEmpty()) {
                r.addAttribute(ATTRIBUTE__VERSION, new MavenArtifactVersion(v.getvMin()).convertVersion(), v.getvMinOperator());    
            }
            if (!v.getvMax().isEmpty()) {            
                r.addAttribute(ATTRIBUTE__VERSION, new MavenArtifactVersion(v.getvMax()).convertVersion(), v.getvMaxOperator());    
            }
        }
        else{
            r.addAttribute(ATTRIBUTE__VERSION, v.convertVersion());
        }
    }
    

    private void solveChildren(DependencyNode dn, Requirement requirement) {
        Requirement child = metadataFactory.createRequirement(NAMESPACE__CRCE_MAVEN_ARTIFACT);
        createDependencyRequirement(dn.getDependency(), child);
        
        requirement.addChild(child);
        child.setParent(requirement);
        
        Iterator<DependencyNode> it = dn.getChildren().iterator();                     
        while(it.hasNext()) {
            solveChildren(it.next(), child);
        }    
    }
}
