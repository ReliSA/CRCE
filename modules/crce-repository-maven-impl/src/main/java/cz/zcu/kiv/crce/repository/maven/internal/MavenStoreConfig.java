package cz.zcu.kiv.crce.repository.maven.internal;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Config class holding parameters for CRCE maven store 
 * 
 * @author M.Brozek
 */
public class MavenStoreConfig {
	private static final Logger logger = LoggerFactory.getLogger(MavenStoreConfig.class);
	
	public static final String LOCAL_MAVEN_STORE_URI = "local.maven.store.uri";
	public static final String REMOTE_MAVEN_STORE_URI = "remote.maven.store.uri";
	public static final String REMOTE_STORE_DEFAULT = "use.remote.maven.store.default";
	public static final String DEPENDENCY_HIERARCHY = "aether.find.dependency.hierarchy";
	public static final String RESOLVE_ARTIFACTS = "aether.resolve.artifacts";
	public static final String LOCAL_STORE_NAME = "local.store.name";
	public static final String REMOTE_STORE_NAME = "remote.store.name";
	public static final String UPDATE_REPOSITORY = "update.repository";
	public static final String INDEXING_CONTEXT_URI ="indexing.context.uri";
	public static final String ARTIFACT_RESOLVE = "artifact.resolve";
	public static final String ARTIFACT_RESOLVE_PARAM = "artifact.resolve.param";
	public static final String AR_STRINGS = "gav:groupid:groupid-artifactid:groupid-artifactid-minversion";
	
		
	private static String localRepoPath = "mvn_store";
	private static String remoteRepoURI = "http://relisa-dev.kiv.zcu.cz:8081/nexus/content/groups/public";
	private static boolean remoteRepoDefault = false;
	private static boolean dependencyHierarchy = false;
	private static boolean resolveArtifacts = false;	
	private static String storeName = "maven_store";
	private static boolean updateRepository = false;
	private static String indexingContextPath = "mvn_store_index"; 
	private static ArtifactResolve artifactResolve = ArtifactResolve.NEWEST;
	private static String artifactResolveParam = "";

	
	public static void initConfig(Dictionary<String, ?> properties) {	
		
		try {
			String localRepo = properties.get(LOCAL_MAVEN_STORE_URI).toString();
			String remoteRepo = properties.get(REMOTE_MAVEN_STORE_URI).toString();
			String indexContext = properties.get(INDEXING_CONTEXT_URI).toString();
			
			setLocalRepoPath(getURItoFile(localRepo));
			setIndexingContextPath(getURItoFile(indexContext));
			setRemoteRepoURI(getRemoteRepoURI(remoteRepo));
			
			setRemoteRepoDefault(toBoolean(properties.get(REMOTE_STORE_DEFAULT).toString()));
			setDependencyHierarchy(toBoolean(properties.get(DEPENDENCY_HIERARCHY).toString()));
			setResolveArtifacts(toBoolean(properties.get(RESOLVE_ARTIFACTS).toString()));
			setUpdateRepository(toBoolean(properties.get(UPDATE_REPOSITORY).toString()));
			setArtifactResolve(ArtifactResolve.fromValue(properties.get(ARTIFACT_RESOLVE).toString()));
			
			if ( AR_STRINGS.contains ( artifactResolve.getValue().toLowerCase() ) ){
				setArtifactResolveParam(properties.get(ARTIFACT_RESOLVE_PARAM).toString());				
			}
			
			
		} catch (Exception e) {
			logger.error("Wrong configuration file ", e);
			e.printStackTrace();
		}
	}
	
	private static String getURItoFile (String localRepoURI) throws ConfigurationException{		
		URI uri = null;
		File file;

		try {
			uri = new URI(localRepoURI);
			if (uri.getScheme() == null) {
				file = new File(localRepoURI);
				uri = file.toURI();
			} else if ("file".equals(uri.getScheme())) {
				file = new File(uri);
				return file.getAbsolutePath();
				
			} else {
				throw new ConfigurationException(MavenStoreConfig.LOCAL_MAVEN_STORE_URI, "Wrong URI format: " + uri.getScheme());						
			}
			
		} catch (URISyntaxException ex) {
			// TODO verify this usecase and correctness
			file = new File(localRepoURI);
			uri = file.toURI();
		}
		
		return new File(uri).getAbsolutePath();
	}
	
	
	private static String getRemoteRepoURI(String remoteRepoURI) throws ConfigurationException{
		URI uri = null;
		
		try {
			uri = new URI(remoteRepoURI);
			if ("http".equals(uri.getScheme())) {
				return uri.toString();
			} else {
				throw new ConfigurationException(MavenStoreConfig.REMOTE_MAVEN_STORE_URI, "Wrong URI format: " + uri.getScheme());
			}

		} catch (URISyntaxException ex) {
			logger.error("Wrong URI syntax, check Configuration file: ", ex);
			return null;
		}
		
	}


	private static boolean toBoolean(String s) throws ConfigurationException {
		if (s.equalsIgnoreCase("true") || s.equals("1")) {
		    return true;	   
		}
		else if(s.equalsIgnoreCase("false") || s.equals("0")){
			return false;
		}
		else {
			throw new ConfigurationException("CONVERSION FAIL", "Not possible convert value: " + s + "to boolean value. "
					+ "Must be 'true' or '1' or 'false' or '0'");
		}
	}


	public static String getLocalRepoPath() {
		return localRepoPath;
	}

	public static void setLocalRepoPath(String localRepoPath) {
		MavenStoreConfig.localRepoPath = localRepoPath;
	}

	public static String getRemoteRepoURI() {
		return remoteRepoURI;
	}


	public static void setRemoteRepoURI(String remoteRepoURI) {
		MavenStoreConfig.remoteRepoURI = remoteRepoURI;
	}
		

	public static String getIndexingContextPath() {
		return indexingContextPath;
	}

	public static void setIndexingContextPath(String indexingContexPath) {
		MavenStoreConfig.indexingContextPath = indexingContexPath;
	}

	public static boolean isRemoteRepoDefault() {
		return remoteRepoDefault;
	}


	public static void setRemoteRepoDefault(boolean remoteRepoDefault) {
		MavenStoreConfig.remoteRepoDefault = remoteRepoDefault;
	}


	public static boolean isDependencyHierarchy() {
		return dependencyHierarchy;
	}


	public static void setDependencyHierarchy(boolean dependencyHierarchy) {
		MavenStoreConfig.dependencyHierarchy = dependencyHierarchy;
	}	


	public static boolean isResolveArtifacts() {
		return resolveArtifacts;
	}


	public static void setResolveArtifacts(boolean resolveArtifacts) {
		MavenStoreConfig.resolveArtifacts = resolveArtifacts;
	}


	public static String getStoreName() {
		return storeName;
	}


	public static void setStoreName(String storeName) {
		MavenStoreConfig.storeName = storeName;
	}


	public static boolean isUpdateRepository() {
		return updateRepository;
	}


	public static void setUpdateRepository(boolean updateRepository) {
		MavenStoreConfig.updateRepository = updateRepository;
	}


	public static ArtifactResolve getArtifactResolve() {
		return artifactResolve;
	}


	public static void setArtifactResolve(ArtifactResolve artifactResolve) {
		MavenStoreConfig.artifactResolve = artifactResolve;
	}


	public static String getArtifactResolveParam() {
		return artifactResolveParam;
	}


	public static void setArtifactResolveParam(String artifactResolveParam) {
		MavenStoreConfig.artifactResolveParam = artifactResolveParam;
	}
}

