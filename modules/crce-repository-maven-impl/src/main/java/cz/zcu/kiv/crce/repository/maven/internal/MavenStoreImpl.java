package cz.zcu.kiv.crce.repository.maven.internal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.concurrency.service.TaskRunnerService;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.MetadataDao;
import cz.zcu.kiv.crce.metadata.indexer.ResourceIndexerService;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.service.validation.MetadataValidator;
import cz.zcu.kiv.crce.metadata.service.validation.ResourceValidationResult;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.Executable;
import cz.zcu.kiv.crce.resolver.Operator;
import cz.zcu.kiv.crce.resolver.ResourceLoader;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class MavenStoreImpl implements Store {

    private static final Logger logger = LoggerFactory.getLogger(MavenStoreImpl.class);

	private volatile MetadataFactory metadataFactory;
	private volatile MetadataDao metadataDao;
	private volatile TaskRunnerService taskRunnerService;
    private volatile ResourceIndexerService resourceIndexerService;
    private volatile ResourceLoader resourceLoader;
    private volatile MetadataService metadataService;
    private volatile IdentityIndexer identityIndexer;
    private volatile MetadataValidator metadataValidator;

    private Repository repository;
    private final URI baseUri;

    MavenStoreImpl(URI baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public Resource put(Resource resource) throws IOException, RefusedArtifactException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean remove(Resource resource) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Resource> getResources() {
        try {
            return metadataDao.loadResources(repository);
        } catch (IOException e) {
            logger.error("Could not load resources of repository {}.", baseUri, e);
        }
        return Collections.emptyList();
    }

    @Override
    public synchronized List<Resource> getResources(Requirement requirement) {
        return getResources(Collections.singleton(requirement));
    }

    @Nonnull
    @Override
    public synchronized List<Resource> getResources(Set<Requirement> requirement) {
        return internalGetResources(requirement, Operator.AND);
    }

    @Nonnull
    @Override
    public synchronized List<Resource> getPossibleResources(Set<Requirement> requirement) {
        return internalGetResources(requirement, Operator.OR);
    }

    private List<Resource> internalGetResources(Set<Requirement> requirement, Operator op) {
        List<Resource> resources = Collections.emptyList();
        try {
            resources = resourceLoader.getResources(repository, requirement, op);
        } catch (IOException e) {
            logger.error("Could not load resources for requirement ({})", requirement.toString());
            logger.error(e.getMessage(), e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("getResources(requirement={}) returns {}", requirement.toString(), resources.size());
        }
        return resources;
    }

    @Override
    public void execute(List<Resource> resources, Executable executable, Properties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    synchronized void start() {
        try {
            repository = metadataDao.loadRepository(baseUri);
        } catch (IOException ex) {
            logger.error("Could not load repository for {}", baseUri, ex);
        }

        if (repository == null) { // TODO this is wrong when indexing fails
            repository = metadataFactory.createRepository(baseUri);
            try {
                metadataDao.saveRepository(repository);
            } catch (IOException ex) {
                logger.error("Could not save repository for {}", baseUri, ex);
            }
            index();
        }
    }

    void stop() {
        logger.info("Stopping DM component {}", this);
    }

    private void index() {
        taskRunnerService.scheduleTask(new LocalRepositoryIndexer(baseUri, new MetadataIndexerCallback() {

            @Override
            public void index(File file) {
                try {
                    if (resourceIndexerService != null && !metadataDao.existsResource(file.toURI())) {
                        Resource resource;
                        try {
                            resource = resourceIndexerService.indexResource(file);
                        } catch (IOException e) {
                            logger.error("Could not index file {}", file, e);
                            return;
                        }
                        metadataService.getIdentity(resource).setAttribute("repository-id", String.class, repository.getId());

                        identityIndexer.preIndex(file, file.getName(), resource);
                        identityIndexer.postIndex(file, resource);

                        ResourceValidationResult validationResult = metadataValidator.validate(resource);
                        if (!validationResult.isContextValid()) {
                            logger.error("Indexed Resource {} is not valid:\r\n{}", resource.getId(), validationResult);
                            return;
                        }
                        logger.info("Indexed resource {} is valid.", resource.getId());

                        try {
                            metadataDao.saveResource(resource);
                        } catch (IOException e) {
                            logger.error("Could not save indexed resource for file {}: {}", file, resource, e);
                        }
                    }
                } catch (IOException e) {
                    logger.error("Could not check that resource exists: {}", file, e);
                }
            }
        }));
    }

    @Override
    public String toString() {
        return "MavenStoreImpl{" + "baseUri=" + baseUri + '}';
    }
}
