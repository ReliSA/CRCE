package cz.zcu.kiv.crce.repository.maven.internal.metadata;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
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
import cz.zcu.kiv.crce.repository.maven.internal.MavenStoreConfig;
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

	public void createMavenArtifactMetadata(LocalMavenRepositoryIndexer caller, Artifact artifact, Resource resource) {

		metadataService.addCategory(resource, "maven");

		try{
			addArtifactCapability(artifact, resource);
			addArtifactRequirements(artifact, resource);

		} catch (Exception e) {
			logger.error("Not possible create maven artifact Capability or Requirements due error:", e);			
		}
	}	
	
	private void addArtifactCapability(Artifact a, Resource resource) {
		Capability cap = metadataFactory.createCapability(NAMESPACE__CRCE_MAVEN_ARTIFACT);
		cap.setAttribute(ATTRIBUTE__GROUP_ID, a.getGroupId());
		cap.setAttribute(ATTRIBUTE__ARTIFACT_ID, a.getArtifactId());
		Version v = new MavenArtifactVersion(a.getBaseVersion()).convertVersion();
		cap.setAttribute(ATTRIBUTE__VERSION, v);

		// empty string make no sense to store
		if (!(a.getClassifier().equals(""))) {
			cap.setAttribute(ATTRIBUTE__CLASSIFIER, a.getClassifier());
		}
		cap.setAttribute(ATTRIBUTE__EXTENSION, a.getExtension());
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
					
					metadataService.setPresentationName(resource, "POM - " + model.getName());
					Capability capOsgi = metadataFactory.createCapability(NAMESPACE__OSGI_BUNDLE);

					String symbName;
					Object symbolicNameProp = model.getProperties().get("bundle.symbolicName");
					if(symbolicNameProp!=null){
						String sm = symbolicNameProp.toString();
						int index = sm.indexOf(".");
						symbName = a.getGroupId() + sm.substring(index);						
					}
					else{
						symbName = a.getGroupId();
					}
					capOsgi.setAttribute(ATTRIBUTE__SYMBOLIC_NAME, symbName);
					capOsgi.setAttribute(ATTRIBUTE__VERSION, v);
					metadataService.addRootCapability(resource, capOsgi);

				} catch (IOException | XmlPullParserException e) {
					logger.error("{} POM file has corrupted XML structure, can't be read properly", a);
				}
			}

		}

		metadataService.addRootCapability(resource, cap);
	}
	
	private void addArtifactRequirements(Artifact artifact, Resource resource) {
		RepositorySystem system = RepositoryFactory.newRepositorySystem();
		DefaultRepositorySystemSession session = RepositoryFactory.newRepositorySystemSession( system );		
		session.setConfigProperty( ConflictResolver.CONFIG_PROP_VERBOSE, true );
        session.setConfigProperty( DependencyManagerUtils.CONFIG_PROP_VERBOSE, true );
        
        ArtifactDescriptorRequest descriptorRequest = new ArtifactDescriptorRequest();
        descriptorRequest.setArtifact( artifact );
        descriptorRequest.setRepositories( RepositoryFactory.newRepositories() );
        

        //Create Dependency Hierarchy
        if(MavenStoreConfig.isDependencyHierarchy()){
        	createDependencyHierarchy(artifact, resource, system, session, descriptorRequest);
        }
        
        //Create Only Direct Dependency
        else{
			createDirectDependency(resource, system, session, descriptorRequest);
		}
	}

	private void createDirectDependency(Resource resource, RepositorySystem system, DefaultRepositorySystemSession session,
			ArtifactDescriptorRequest descriptorRequest) {
		ArtifactDescriptorResult descriptorResult;
		
		
		try {
			descriptorResult = system.readArtifactDescriptor(session, descriptorRequest);		
			for (Dependency d : descriptorResult.getDependencies()) {
				Requirement requirement = metadataFactory.createRequirement(NAMESPACE__CRCE_MAVEN_ARTIFACT);
				createDependencyRequirement(d, requirement);
				resource.addRequirement(requirement);

				// resolve JAR dependency?
				if (MavenStoreConfig.isResolveArtifacts()) {
					resolveDependency(system, session, d);
				}
			}
			
		} catch (ArtifactDescriptorException e) {
			logger.error("Failed to read ArtifactDescriptor...", e);
			
		} catch (ArtifactResolutionException e) {
			logger.error("Couldn't resolve dependendency...",e);	
		}
	}


	private void resolveDependency(RepositorySystem system, DefaultRepositorySystemSession session, Dependency d)
			throws ArtifactResolutionException {
		Artifact artifactD = d.getArtifact();
		ArtifactRequest artifactRequest = new ArtifactRequest();
		artifactRequest.setArtifact(artifactD);
		artifactRequest.setRepositories(RepositoryFactory.newRepositories());

		ArtifactResult artifactResult = system.resolveArtifact(session, artifactRequest);
		artifactD = artifactResult.getArtifact();
		logger.info(artifactD + " resolved to  " + artifactD.getFile());
	}
	

	private void createDependencyHierarchy(Artifact artifact, Resource resource, RepositorySystem system,
			DefaultRepositorySystemSession session, ArtifactDescriptorRequest descriptorRequest) {
		ArtifactDescriptorResult descriptorResult;
		try {
			descriptorResult = system.readArtifactDescriptor(session, descriptorRequest);			
			CollectRequest collectRequest = new CollectRequest();
			collectRequest.setRootArtifact(descriptorResult.getArtifact());
			collectRequest.setDependencies(descriptorResult.getDependencies());
			collectRequest.setManagedDependencies(descriptorResult.getManagedDependencies());
			collectRequest.setRepositories(descriptorRequest.getRepositories());

			CollectResult collectResult = system.collectDependencies(session, collectRequest);
			createReqHierarchy(collectResult.getRoot().getChildren(), resource);

			// resolve JAR hieararchy dependencies?
			if (MavenStoreConfig.isResolveArtifacts()) {
				DependencyFilter depFilter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE);

				collectRequest = new CollectRequest();
				collectRequest.setRoot(new Dependency(artifact, JavaScopes.COMPILE));

				DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, depFilter);
				List<ArtifactResult> artifactResults = system.resolveDependencies(session, dependencyRequest).getArtifactResults();

				for (ArtifactResult artifactResult : artifactResults) {
					logger.debug(artifactResult.getArtifact() + " resolved to " + artifactResult.getArtifact().getFile());
				}
			}
			

		} catch (ArtifactDescriptorException e) {			
			logger.error("Failed to read ArtifactDescriptor...", e);
			
		} catch (DependencyResolutionException e) {
			logger.error("Couldn't resolve dependendencies...", e);
			
			
		} catch (DependencyCollectionException e) {
			logger.error("Couldn't collect dependendencies...", e);
		}
	}

	/**
	 * Adding Dependency info to Resource requirements
	 * Very very time consuming
	 * @param list of Node Dependencies
	 * @param resource is Artifact Node
	 */
	private void createReqHierarchy(List<DependencyNode> list, Resource resource) {
		for (DependencyNode dn : list) {
			Requirement requirement = metadataFactory.createRequirement(NAMESPACE__CRCE_MAVEN_ARTIFACT);
			createDependencyRequirement(dn.getDependency(), requirement);
			
			//any existing children dependcies?
			Iterator<DependencyNode> it = dn.getChildren().iterator(); 
	
			while(it.hasNext()){
				solveChildren(it.next(), requirement);
				
			}
			
			resource.addRequirement(requirement);
		}	
		
	}
	
	
	private void createDependencyRequirement(Dependency d, Requirement requirement) {
		Artifact a = d.getArtifact();
		requirement.addAttribute(ATTRIBUTE__GROUP_ID, a.getGroupId());
		requirement.addAttribute(ATTRIBUTE__ARTIFACT_ID, a.getArtifactId());	
		checkRangeVersion(requirement,new MavenArtifactVersion(a.getBaseVersion()));
					
		String scope = d.getScope();
		if(scope != null && !scope.isEmpty() && !scope.equals("compile")){
			requirement.setDirective(DEPENDENCY_SCOPE, scope);				
		}
		
		if( d.getOptional() ){
			requirement.setDirective(DEPENDENCY_OPTIONAL, d.getOptional().toString());
		}
	}

	/**
	 * Check if dependency Version is range of versions
	 * @param r is dependency
	 * @param v is version of dependency
	 */
	private void checkRangeVersion(Requirement r, MavenArtifactVersion v) {
		if(v.isRangeVersion()){
			if(!v.getvMin().equals("")){
				r.addAttribute(ATTRIBUTE__VERSION, new MavenArtifactVersion(v.getvMin()).convertVersion(), v.getvMinOperator());	
			}
			if(!v.getvMax().equals("")){			
				r.addAttribute(ATTRIBUTE__VERSION, new MavenArtifactVersion(v.getvMax()).convertVersion(), v.getvMaxOperator());	
			}
		}		
	}

	private void solveChildren(DependencyNode dn, Requirement requirement) {
		Requirement child = metadataFactory.createRequirement(NAMESPACE__CRCE_MAVEN_ARTIFACT);
		createDependencyRequirement(dn.getDependency(),child);
		
 		requirement.addChild(child);
		child.setParent(requirement);
		
		Iterator<DependencyNode> it = dn.getChildren().iterator(); 					
		while(it.hasNext()){
			solveChildren(it.next(), child);
		}	
	}
}
