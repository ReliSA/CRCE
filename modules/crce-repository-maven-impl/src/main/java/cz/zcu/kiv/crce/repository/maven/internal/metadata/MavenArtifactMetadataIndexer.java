package cz.zcu.kiv.crce.repository.maven.internal.metadata;

import java.util.Iterator;
import java.util.List;

import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.util.graph.manager.DependencyManagerUtils;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;
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
import cz.zcu.kiv.crce.repository.maven.internal.LocalMavenRepositoryIndexer;
import cz.zcu.kiv.crce.repository.maven.internal.aether.RepositoryFactory;

/**
* Getting Maven artifact informations
* Parsing capabilities and requiremnts to Resource metadata
* @author Miroslav Brožek
*/
public class MavenArtifactMetadataIndexer {
	
	private volatile MetadataService metadataService;
	private volatile MetadataFactory metadataFactory;
	
	private static final Logger logger = LoggerFactory.getLogger(MavenArtifactMetadataIndexer.class);
	
	public static final String NAMESPACE__CRCE_MAVEN_ARTIFACT = "crce.maven.artifact";
	public static final AttributeType<String> ATTRIBUTE__GROUP_ID = new SimpleAttributeType<>("groupId", String.class);
	public static final AttributeType<String> ATTRIBUTE__ARTIFACT_ID = new SimpleAttributeType<>("artifactId", String.class);
	public static final AttributeType<Version> ATTRIBUTE__VERSION = new SimpleAttributeType<>("version", Version.class);
	public static final AttributeType<String> ATTRIBUTE__PACKAGING = new SimpleAttributeType<>("packaging", String.class);
	public static final AttributeType<String> ATTRIBUTE__CLASSIFIER = new SimpleAttributeType<>("classifier", String.class);
	public static final AttributeType<String> ATTRIBUTE__EXTENSION = new SimpleAttributeType<>("extension", String.class);

	public MavenArtifactMetadataIndexer(MetadataService metadataService, MetadataFactory metadaFactory) {
		this.metadataService = metadataService;
		this.metadataFactory = metadaFactory;
	}

	public void setMavenArtifactMetadata(LocalMavenRepositoryIndexer caller, Artifact artifact, Resource resource) {		

		metadataService.addCategory(resource, "artifact");		
		
		addArtifactCapability(artifact, resource);	
		
		try {
			
			addArtifactRequirements(artifact, resource);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void addArtifactCapability(Artifact artifact, Resource resource) {
		Capability cap = metadataFactory.createCapability(NAMESPACE__CRCE_MAVEN_ARTIFACT);
		cap.setAttribute(ATTRIBUTE__GROUP_ID, artifact.getGroupId());
		cap.setAttribute(ATTRIBUTE__ARTIFACT_ID, artifact.getArtifactId());
		
		String v = artifact.getBaseVersion();
		v = v.replaceAll("-", "."); //prevent failing in Version parser		
		
		cap.setAttribute(ATTRIBUTE__VERSION, new Version(v));
		cap.setAttribute(ATTRIBUTE__PACKAGING, "bundle");
		
		String classifier = artifact.getClassifier();
		
		//empty string make no sense to store
		if(!(classifier.equals(""))){
			cap.setAttribute(ATTRIBUTE__CLASSIFIER, classifier);
		}
		
		cap.setAttribute(ATTRIBUTE__EXTENSION, artifact.getExtension());
		
		metadataService.addRootCapability(resource, cap);
	}
	

	private void addArtifactRequirements(Artifact artifact, Resource resource) throws ArtifactDescriptorException, DependencyCollectionException {
		RepositorySystem system = RepositoryFactory.newRepositorySystem();
		DefaultRepositorySystemSession session = RepositoryFactory.newRepositorySystemSession( system );
		
		session.setConfigProperty( ConflictResolver.CONFIG_PROP_VERBOSE, true );
        session.setConfigProperty( DependencyManagerUtils.CONFIG_PROP_VERBOSE, true );

        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
        
        //debug
       // artifact = new DefaultArtifact( "org.apache.maven:maven-aether-provider:3.1.0" );
        descriptorRequest.setArtifact( artifact );
        descriptorRequest.setRepositories( RepositoryFactory.newRepositories( system, session ) );
        ArtifactDescriptorResult descriptorResult = system.readArtifactDescriptor( session, descriptorRequest );

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRootArtifact( descriptorResult.getArtifact() );
        collectRequest.setDependencies( descriptorResult.getDependencies() );
        collectRequest.setManagedDependencies( descriptorResult.getManagedDependencies() );
        collectRequest.setRepositories( descriptorRequest.getRepositories() );

        CollectResult collectResult = system.collectDependencies( session, collectRequest );   
        
        //solving inherited depencies
        createReqHierarchy(collectResult.getRoot().getChildren(), resource);
        logger.debug(artifact.toString() + " - DEPENDENCIES RESOLVED");
	}

	private void createReqHierarchy(List<DependencyNode> list, Resource resource) {
		for (DependencyNode dn : list) {
			Artifact a = dn.getArtifact();
			Requirement requirement = metadataFactory.createRequirement("maven.artifact");
			requirement.addAttribute(ATTRIBUTE__GROUP_ID, a.getGroupId());
			requirement.addAttribute(ATTRIBUTE__ARTIFACT_ID, a.getArtifactId());			
			String v = a.getBaseVersion();
			v = v.replaceAll("-", "."); //prevent failing in Version parser		
			requirement.addAttribute(ATTRIBUTE__VERSION, new Version(v));
			
			//any existing children dependcies?
			Iterator<DependencyNode> it = dn.getChildren().iterator(); 					
			while(it.hasNext()){
				solveChildren(it.next(), requirement);
			}
			
			resource.addRequirement(requirement);
		}
		
	}

	private void solveChildren(DependencyNode dn, Requirement requirement) {
		Artifact a = dn.getArtifact();
		Requirement child = metadataFactory.createRequirement("maven.artifact");
		child.addAttribute(ATTRIBUTE__GROUP_ID, a.getGroupId());
		child.addAttribute(ATTRIBUTE__ARTIFACT_ID, a.getArtifactId());			
		String v = a.getBaseVersion();
		v = v.replaceAll("-", "."); //prevent failing in Version parser		
		child.addAttribute(ATTRIBUTE__VERSION, new Version(v));
		
		requirement.addChild(child);
		child.setParent(requirement);
		
		Iterator<DependencyNode> it = dn.getChildren().iterator(); 					
		while(it.hasNext()){
			solveChildren(it.next(), child);
		}
		
	}	

}
