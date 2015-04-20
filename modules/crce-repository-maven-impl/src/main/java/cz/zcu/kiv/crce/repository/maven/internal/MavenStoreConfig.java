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
	public static final String REMOTE_STORE_DEFAULT = "use.remote.maven.store.default";
	public static final String DEPENDENCY_HIERARCHY = "aether.find.dependency.hierarchy";
	public static final String RESOLVE_DEPENDENCIES = "aether.resolve.dependencies";
	public static final String LOCAL_STORE_NAME = "local.store.name";
	public static final String REMOTE_STORE_NAME = "remote.store.name";
	
		
	private static String localRepoURI = "mvn_store";
	private static String remoteRepoURI = "http://relisa-dev.kiv.zcu.cz:8081/nexus/content/groups/public";
	private static boolean remoteRepoDefault = false;
	private static boolean dependencyHierarchy = false;
	private static boolean resolveDependencies = false;	
	private static String storeName = "maven_store";

	
	public static void initConfig(Dictionary<String, ?> properties) {
		setLocalRepoURI((String) properties.get(LOCAL_MAVEN_STORE_URI));
		setRemoteRepoURI((String) properties.get(REMOTE_MAVEN_STORE_URI));
		
		
		try {
			setRemoteRepoDefault(toBoolean(properties.get(REMOTE_STORE_DEFAULT).toString()));
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


	public static boolean isResolveDependencies() {
		return resolveDependencies;
	}


	public static void setResolveDependencies(boolean resolveDependencies) {
		MavenStoreConfig.resolveDependencies = resolveDependencies;
	}


	public static String getStoreName() {
		return storeName;
	}


	public static void setStoreName(String storeName) {
		MavenStoreConfig.storeName = storeName;
	}	
	
}

