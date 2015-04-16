package cz.zcu.kiv.crce.repository.maven.internal.metadata;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
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
	
	public static final String DEPENDENCY_SCOPE = "scope";
	public static final String DEPENDENCY_OPTIONAL = "optional";

	public MavenArtifactMetadataIndexer(MetadataService metadataService, MetadataFactory metadaFactory) {
		this.metadataService = metadataService;
		this.metadataFactory = metadaFactory;
	}

	public void setMavenArtifactMetadata(LocalMavenRepositoryIndexer caller, Artifact artifact, Resource resource) {

		metadataService.addCategory(resource, "maven");

		try {
			addArtifactCapability(artifact, resource);
			addArtifactRequirements(artifact, resource);

		} catch (Exception e) {
			logger.error("Not possible create maven artifact Capability or Requirements due error:", e);
			e.printStackTrace();
		}
	}	
	
	private void addArtifactCapability(Artifact a, Resource resource) throws Exception {
		Capability cap = metadataFactory.createCapability(NAMESPACE__CRCE_MAVEN_ARTIFACT);
		cap.setAttribute(ATTRIBUTE__GROUP_ID, a.getGroupId());
		cap.setAttribute(ATTRIBUTE__ARTIFACT_ID, a.getArtifactId());		
		cap.setAttribute(ATTRIBUTE__VERSION, convertVersion(new MavenArtifactVersion(a.getBaseVersion())));	
				
		//empty string make no sense to store				
		if(!(a.getClassifier().equals(""))){
			cap.setAttribute(ATTRIBUTE__CLASSIFIER, a.getClassifier());
		}		
		cap.setAttribute(ATTRIBUTE__EXTENSION, a.getExtension());
		
		//need more info from POM file?
		File jar = new File (metadataService.getUri(resource));			
		String pomPath = FilenameUtils.removeExtension(jar.toString()) +".pom";
		File pom = new File(pomPath);
		if (pom.exists()){
			MavenXpp3Reader reader = new MavenXpp3Reader();
			Model model = reader.read(new FileReader(pom));	
			cap.setAttribute(ATTRIBUTE__PACKAGING, model.getPackaging());
			//model.getParent() //do we need parentPOM?			
		}
		
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
        
        //solving children dependencies
        createReqHierarchy(collectResult.getRoot().getChildren(), resource);
        logger.debug(artifact.toString() + " - Dependencies search finished");
	}

	/**
	 * Adding Dependency info to Resource requirements
	 * @param list of Node Dependencies
	 * @param resource is Artifact Node
	 */
	private void createReqHierarchy(List<DependencyNode> list, Resource resource) {
		for (DependencyNode dn : list) {
			Requirement requirement = metadataFactory.createRequirement(NAMESPACE__CRCE_MAVEN_ARTIFACT);
			createDependencyRequirement(dn, requirement);
			
			//any existing children dependcies?
			Iterator<DependencyNode> it = dn.getChildren().iterator(); 					
			while(it.hasNext()){
				solveChildren(it.next(), requirement);
			}
			
			resource.addRequirement(requirement);
		}		
	}
	
	
	private void createDependencyRequirement(DependencyNode dn, Requirement requirement) {
		Artifact a = dn.getArtifact();
		requirement.addAttribute(ATTRIBUTE__GROUP_ID, a.getGroupId());
		requirement.addAttribute(ATTRIBUTE__ARTIFACT_ID, a.getArtifactId());	
		requirement.addAttribute(ATTRIBUTE__VERSION, convertVersion(new MavenArtifactVersion(a.getBaseVersion())));
					
		String scope = dn.getDependency().getScope();
		if(scope != null && !scope.isEmpty() && !scope.equals("compile")){
			requirement.setDirective(DEPENDENCY_SCOPE, scope);				
		}
		
		if( dn.getDependency().getOptional() ){
			requirement.setDirective(DEPENDENCY_OPTIONAL, dn.getDependency().getOptional().toString());
		}
	}

	private void solveChildren(DependencyNode dn, Requirement requirement) {
		Requirement child = metadataFactory.createRequirement(NAMESPACE__CRCE_MAVEN_ARTIFACT);
		createDependencyRequirement(dn,child);
		
		requirement.addChild(child);
		child.setParent(requirement);
		
		Iterator<DependencyNode> it = dn.getChildren().iterator(); 					
		while(it.hasNext()){
			solveChildren(it.next(), child);
		}		
	}	
	
	
	/**
	 * Prevent failing validation because of 
	 * short version format or strange qualifier
	 * @param v handled version from Artifact
	 * @return new format of Version.class
	 */
	private Version convertVersion(MavenArtifactVersion v) {
		return new Version(v.getMajorVersion(), v.getMinorVersion(), v.getIncrementalVersion(), v.getQualifier());
	}

}
