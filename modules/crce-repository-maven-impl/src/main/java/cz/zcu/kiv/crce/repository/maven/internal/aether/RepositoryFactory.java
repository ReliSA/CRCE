package cz.zcu.kiv.crce.repository.maven.internal.aether;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

import cz.zcu.kiv.crce.repository.maven.internal.MavenStoreConfig;
import cz.zcu.kiv.crce.repository.maven.internal.RepositoryWrapper;


/**
* Factory for creating repositories
* @author Miroslav Brožek
*/
public class RepositoryFactory {
	private static ArrayList<RemoteRepository> repositories;

	public static RepositorySystem newRepositorySystem() {
		/*
		 * Aether's components implement org.eclipse.aether.spi.locator.Service
		 * to ease manual wiring and using the prepopulated
		 * DefaultServiceLocator, we only need to register the repository
		 * connector and transporter factories.
		 */
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
		locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
		locator.addService(TransporterFactory.class, FileTransporterFactory.class);
		locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
		
		

		locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
			@Override
			public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
				exception.printStackTrace();
			}
		});

		return locator.getService(RepositorySystem.class);
	}

	public static DefaultRepositorySystemSession newRepositorySystemSession(RepositorySystem system) {
		LocalRepository localRepo = new LocalRepository(MavenStoreConfig.getLocalRepository().getURItoPath());		
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
//		session.setConfigProperty(ConfigurationProperties.REQUEST_TIMEOUT, "2000");//2s
//		session.setConfigProperty(ConfigurationProperties.CONNECT_TIMEOUT, "1000");//1000ms
		
		return session;
	}

	public static List<RemoteRepository> newRepositories() {
		repositories = new ArrayList<RemoteRepository>();
		
		//using remote repository? then search primary this one
		if (MavenStoreConfig.isRemoteRepoDefault()) {
			RepositoryWrapper rr = MavenStoreConfig.getRemoteRepository();
			repositories.add(new RemoteRepository.Builder(rr.getName(), "default",rr.getUri().toString()).build());			
		}
		
		RemoteRepository central =  new RemoteRepository.Builder("central", "default", "http://central.maven.org/maven2/").build();	
		
		repositories.add(central);			

		return repositories;
	}
	

	public static ArrayList<RemoteRepository> getRepositories() {
		return repositories;
	}
	
}
