package cz.zcu.kiv.crce.repository.maven.internal.aether;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.ConfigurationProperties;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.repository.maven.internal.MavenStoreConfiguration;
import cz.zcu.kiv.crce.repository.maven.internal.RepositoryWrapper;


/**
* Factory for creating repositories
* @author Miroslav Brožek
*/
public class RepositoryFactory {
    
    private static final Logger errorHandlerLogger = LoggerFactory.getLogger(DefaultServiceLocator.ErrorHandler.class);
    
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
                errorHandlerLogger.error("Service creation failed for type: " + type + ", impl: " + impl, exception);
            }
        });

        return locator.getService(RepositorySystem.class);
    }

    public static DefaultRepositorySystemSession newRepositorySystemSession(MavenStoreConfiguration configuration, RepositorySystem system) {
        LocalRepository localRepo = new LocalRepository(configuration.getLocalRepository().getURItoPath());        
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
        session.setConfigProperty(ConfigurationProperties.REQUEST_TIMEOUT, "2000");//2s
        session.setConfigProperty(ConfigurationProperties.CONNECT_TIMEOUT, "1000");//1000ms
        
        return session;
    }

    public static List<RemoteRepository> newRepositories(MavenStoreConfiguration configuration) {
        List<RemoteRepository> repositories = new ArrayList<>(2);
        
        //using remote repository? then search primary this one
        if (configuration.isRemoteRepoDefault()) {
            RepositoryWrapper rr = configuration.getRemoteRepository();
            repositories.add(new RemoteRepository.Builder(rr.getName(), "default", rr.getUri().toString()).build());            
        }
        
        // TODO configurable
        RemoteRepository central =  new RemoteRepository.Builder("central", "default", "http://central.maven.org/maven2/").build();
        
        repositories.add(central);            

        return repositories;
    }
    

    private RepositoryFactory() {
    }
}
