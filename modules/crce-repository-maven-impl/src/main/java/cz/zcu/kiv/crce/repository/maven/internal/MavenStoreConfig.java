package cz.zcu.kiv.crce.repository.maven.internal;

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
	public static final String USE_LOCAL_REPO = "use.local.maven.store";
	public static final String DEPENDENCY_HIERARCHY = "aether.find.dependency.hierarchy";
	public static final String RESOLVE_DEPENDENCIES = "aether.resolve.dependencies";
	
		
	private static String localRepoURI = "mvn_store";
	private static String remoteRepoURI = "http://relisa-dev.kiv.zcu.cz:8081/nexus/content/groups/public";
	private static boolean useLocalRepo = true;
	private static boolean dependencyHierarchy = false;
	private static boolean resolveDependencies = false;	

	
	public static void initConfig(Dictionary<String, ?> properties) {
		setLocalRepoURI((String) properties.get(LOCAL_MAVEN_STORE_URI));
		setRemoteRepoURI((String) properties.get(REMOTE_MAVEN_STORE_URI));
		
		
		try {
			setUseLocalRepo(toBoolean(properties.get(USE_LOCAL_REPO).toString()));
			setDependencyHierarchy(toBoolean(properties.get(DEPENDENCY_HIERARCHY).toString()));
			setResolveDependencies(toBoolean(properties.get(RESOLVE_DEPENDENCIES).toString()));
			
		} catch (ConfigurationException e) {
			logger.debug("Wrong configuration in config file ", e);
			e.printStackTrace();
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


	public static String getLocalRepoURI() {
		return localRepoURI;
	}


	public static void setLocalRepoURI(String localRepoURI) {
		MavenStoreConfig.localRepoURI = localRepoURI;
	}


	public static String getRemoteRepoURI() {
		return remoteRepoURI;
	}


	public static void setRemoteRepoURI(String remoteRepoURI) {
		MavenStoreConfig.remoteRepoURI = remoteRepoURI;
	}


	public static boolean isUseLocalRepo() {
		return useLocalRepo;
	}


	public static void setUseLocalRepo(boolean useLocalRepo) {
		MavenStoreConfig.useLocalRepo = useLocalRepo;
	}


	public static boolean isDependencyHierarchy() {
		return dependencyHierarchy;
	}


	public static void setDependencyHierarchy(boolean dependencyHierarchy) {
		MavenStoreConfig.dependencyHierarchy = dependencyHierarchy;
	}


	public static boolean isResolveDependencies() {
		return resolveDependencies;
	}


	public static void setResolveDependencies(boolean resolveDependencies) {
		MavenStoreConfig.resolveDependencies = resolveDependencies;
	}	
}

