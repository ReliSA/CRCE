package cz.zcu.kiv.crce.metadata.dao.internal;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.dao.RepositoryDAO;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbRepository;
import cz.zcu.kiv.crce.metadata.dao.internal.mapper.SequenceMapper;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@Component(provides={RepositoryDAO.class, RepositoryDAOImpl.class})
public class RepositoryDAOImpl implements RepositoryDAO {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryDAOImpl.class);

    private static final String REPOSITORY_MAPPER = "cz.zcu.kiv.crce.metadata.dao.internal.mapper.RepositoryMapper.";

    @ServiceDependency private volatile ResourceFactory resourceFactory;
    @ServiceDependency private volatile MetadataService metadataService; // NOPMD
    @ServiceDependency private volatile SessionManager sessionManager;

    @Override
    public Repository loadRepository(URI uri) throws IOException {
        logger.debug("loadRepository(uri={})", uri);

        Repository repository;
        try (SqlSession session = sessionManager.getSession()) {
            repository = loadRepository(uri, session);
        } catch (PersistenceException e) {
            throw new IOException(e);
        }

        logger.debug("loadRepository(uri={}) returns {}", uri, repository);

        return repository;
    }

    @CheckForNull
    private Repository loadRepository(@Nonnull URI uri, @Nonnull SqlSession session) {
        Repository repository = null;
        DbRepository dbRepository = session.selectOne(REPOSITORY_MAPPER + "selectRepositoryByUri", uri.toString());
        if (dbRepository != null) {
            try {
                repository = resourceFactory.createRepository(new URI(dbRepository.getUri()));
            } catch (URISyntaxException ex) {
                throw new IllegalArgumentException("Invalid URI: " + dbRepository.getUri(), ex);
            }
        }
        return repository;
    }

    @Override
    public void deleteRepository(Repository repository) throws IOException {
        logger.debug("deleteRepository(repository={})", repository);

        try (SqlSession session = sessionManager.getSession()) {
            session.delete(REPOSITORY_MAPPER + "deleteRepository", repository.getURI().toString());
        }

        logger.debug("deleteRepository(repository={}) returns", repository);
    }

    @Override
    public void saveRepository(Repository repository) throws IOException {
        logger.debug("saveRepository(repository={})", repository);

        try (SqlSession session = sessionManager.getSession()) {
            if (loadRepository(repository.getURI()) == null) {
                SequenceMapper seqMapper = session.getMapper(SequenceMapper.class);

                DbRepository dbRepository = new DbRepository();

                long repositoryId = seqMapper.nextVal("resource_seq");

                dbRepository.setRepositoryId(repositoryId);
                dbRepository.setUri(repository.getURI().toString());

                session.insert(REPOSITORY_MAPPER + "insertRepository", dbRepository);

                session.commit();
            } else {
                logger.info("Saved repository already exists, saving skipped (update not implemented).");
            }
        }

        logger.debug("saveRepository(repository={}) returns", repository);
    }

    /* Internal public methods */

    @CheckForNull
    public Long getRepositoryId(@Nonnull Repository repository, @Nonnull SqlSession session) {
        logger.debug("getRepositoryId(repository={})", repository);

        Long repositoryId = session.selectOne(REPOSITORY_MAPPER + "selectRepositoryId", repository.getURI().toString());

        logger.debug("getRepositoryId(repository={}) returns {}", repository, repositoryId);

        return repositoryId;
    }

    @CheckForNull
    public Repository loadRepository(long repositoryId, @Nonnull SqlSession session) {
        logger.debug("getRepository(repositoryId={})", repositoryId);

        Repository repository = session.selectOne(REPOSITORY_MAPPER + "selectRepositoryByRepositoryId", repositoryId);

        logger.debug("getRepository(repositoryId={}) returns {}", repositoryId, repository);

        return repository;
    }

}
