package cz.zcu.kiv.crce.repository.maven.internal.aether;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;


/**
* Factory for creating repositories
* @author Miroslav Brožek
*/
public class RepositoryFactory {

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
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

		LocalRepository localRepo = new LocalRepository("mvn_store");
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

		// uncomment to generate dirty trees
		// session.setDependencyGraphTransformer( null );

		return session;
	}

	public static List<RemoteRepository> newRepositories(RepositorySystem system, RepositorySystemSession session) {
		ArrayList<RemoteRepository> repositories = new ArrayList<RemoteRepository>();
		RemoteRepository central =  new RemoteRepository.Builder("central", "default", "http://central.maven.org/maven2/").build();
		RemoteRepository relisa =  new RemoteRepository.Builder("relisa", "default", "http://relisa-dev.kiv.zcu.cz:8081/nexus/content/groups/public").build();
		RemoteRepository kalwi =  new RemoteRepository.Builder("kalwi", "default", "http://maven.kalwi.eu/repo/releases").build();
		
		repositories.add(central);
		repositories.add(relisa);
		repositories.add(kalwi);
		
		return repositories;
	}
	
}
