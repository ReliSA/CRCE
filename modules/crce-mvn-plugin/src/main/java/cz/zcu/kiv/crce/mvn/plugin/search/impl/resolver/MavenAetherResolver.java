package cz.zcu.kiv.crce.mvn.plugin.search.impl.resolver;

import cz.zcu.kiv.crce.mvn.plugin.search.FoundArtifact;
import cz.zcu.kiv.crce.mvn.plugin.search.MavenResolver;
import cz.zcu.kiv.crce.mvn.plugin.search.impl.aether.MavenAetherLocator;
import org.eclipse.aether.repository.RemoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * This implementation uses the Aether library to resolve artifacts from maven repositories.
 *
 * Created by Zdenek Vales on 9.4.2017.
 */
public class MavenAetherResolver implements MavenResolver {

    private static final Logger logger = LoggerFactory.getLogger(MavenAetherLocator.class);

    private static final String REPOSITORY_ID_DEF = "central";
    private static final String REPOSITORY_TYPE_DEF = "default";
    private static final String REPOSITORY_URL_DEF = "http://repo1.maven.org/maven2/";

    private static final String CONFIG_FILE_NAME = "/search/mavenAetherLocator.properties";
    public static final String REPOSITORY_ID_PROPERTY_NAME = "repository.id";
    public static final String REPOSITORY_TYPE_PROPERTY_NAME = "repository.type";
    public static final String REPOSITORY_URL_PROPERTY_NAME = "repository.url";

    private String repositoryId;
    private String repositoryType;
    private String repositoryUrl;

    private List<RemoteRepository> repositories;


    public MavenAetherResolver() {
        init();
    }

    /**
     * Creates a list of repositories containing central repository.
     * @return
     */
    private List<RemoteRepository> newRepositories()
    {
        return new ArrayList<RemoteRepository>( Arrays.asList( newCentralRepository() ) );
    }

    /**
     * Creates a new central repository object.
     * @return
     */
    private RemoteRepository newCentralRepository()
    {
        return new RemoteRepository.Builder(repositoryId, repositoryType, repositoryUrl).build();
    }

    /**
     * Initializes repository id, type and url from resource config file. If no file or values are found,
     * default ones are used.
     */
    private void init() {
        InputStream is = MavenAetherLocator.class.getResourceAsStream(CONFIG_FILE_NAME);
        Properties properties = new Properties();
        logger.info("Initializing "+MavenAetherLocator.class.getName()+".");

//        nexusIndexer = new DefaultNexusIndexer();
//        creators = new ArrayList<>();
//        creators.add(new MinimalArtifactInfoIndexCreator());

        if (is == null) {
            logger.debug("No properties file found.");
            // no file
            repositoryId = REPOSITORY_ID_DEF;
            repositoryType = REPOSITORY_TYPE_DEF;
            repositoryUrl = REPOSITORY_URL_DEF;
        } else {
            try {
                properties.load(is);
                repositoryId = properties.getProperty(REPOSITORY_ID_PROPERTY_NAME, REPOSITORY_ID_DEF);
                repositoryType = properties.getProperty(REPOSITORY_TYPE_PROPERTY_NAME, REPOSITORY_TYPE_DEF);
                repositoryUrl = properties.getProperty(REPOSITORY_URL_PROPERTY_NAME, REPOSITORY_URL_DEF);
            } catch (IOException e) {
                logger.error("Exception while configuring "+MavenAetherLocator.class.getName()+". "+e.getMessage());
                repositoryId = REPOSITORY_ID_DEF;
                repositoryType = REPOSITORY_TYPE_DEF;
                repositoryUrl = REPOSITORY_URL_DEF;
            }

            try {
                is.close();
            } catch (IOException e) {
                logger.error("Exception while closing the stream: "+e.getMessage());
            }
        }

        repositories = newRepositories();
    }

    @Override
    public FoundArtifact resolve(FoundArtifact artifact) {
        // todo: implementation + tests
        return null;
    }

    @Override
    public Collection<FoundArtifact> resolveArtifacts(Collection<FoundArtifact> artifacts) {
        // todo: implementation + tests
        return null;
    }
}
