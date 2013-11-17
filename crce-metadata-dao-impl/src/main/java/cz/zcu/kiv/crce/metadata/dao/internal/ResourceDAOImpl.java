package cz.zcu.kiv.crce.metadata.dao.internal;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.Init;
import org.apache.felix.dm.annotation.api.LifecycleController;
import org.apache.felix.dm.annotation.api.ServiceDependency;
import org.apache.felix.dm.annotation.api.Start;
import org.apache.felix.dm.annotation.api.Stop;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbAttribute;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbCapability;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbDirective;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbRequirement;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbResource;
import cz.zcu.kiv.crce.metadata.dao.internal.mapper.SequenceMapper;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 * @author Jan Dyrczyk (dyrczyk@students.zcu.cz)
 * @author Pavel Cihlář
 */
@Component(provides={ResourceDAO.class})
public class ResourceDAOImpl implements ResourceDAO {

    private static final Logger logger = LoggerFactory.getLogger(ResourceDAOImpl.class);

    private static final String RESOURCE_MAPPER = "cz.zcu.kiv.crce.metadata.dao.internal.mapper.ResourceMapper.";

    @ServiceDependency private volatile ResourceFactory resourceFactory;
    @ServiceDependency private volatile MetadataService metadataService;
    @ServiceDependency private volatile SessionManager sessionManager;
    @ServiceDependency private volatile RepositoryDAOImpl repositoryDAOImpl;

    @LifecycleController
    Runnable lifeCycleController;

    @Init
    void init() {
        logger.info("Starting CRCE Metadata DAO.");

        try (SqlSession session = sessionManager.getSession()) {
            session.update("cz.zcu.kiv.crce.metadata.dao.internal.mapper.InitDbMapper.createTables");
            session.update("cz.zcu.kiv.crce.metadata.dao.internal.mapper.InitDbMapper.createSequences");
            session.commit();
            // starts the component only if initialization is successful
            lifeCycleController.run();
        } catch (Exception e) {
            logger.error("Could not initialize DB.", e);
        }
    }

    @Start
    void start() {
        logger.info("CRCE Metadata DAO started.");
    }

    @Stop
    synchronized void stop() {
        logger.info("CRCE Metadata DAO finished.");
    }

    @Override
    public synchronized Resource loadResource(URI uri) throws IOException { // TODO, only an URI as an argument is not nice
        logger.debug("loadResource(uri={})", uri);

        Resource result = null;

        try (SqlSession session = sessionManager.getSession()) {
            DbResource dbResource = session.selectOne(RESOURCE_MAPPER + "selectResourceByUri", uri.toString());

            if (dbResource != null) {
                result = loadResource(dbResource, session);

                Repository repository = repositoryDAOImpl.loadRepository(dbResource.getRepositoryId(), session);
                if (repository != null) {
                    result.setRepository(repository);
                } else {
                    logger.warn("Could not load repository for resource {}", result);
                }
            }
        }

        logger.debug("loadResource(uri) returns {}", result);
        return result;
    }

    private Resource loadResource(@Nonnull DbResource dbResource, @Nonnull SqlSession session) {
        Resource resource = resourceFactory.createResource(dbResource.getId());
//        metadataService.setUri(resource, dbResource.getUri());

        loadCapabilities(resource, dbResource.getResourceId(), session);
        loadRequirements(resource, dbResource.getResourceId(), session);

        try {
            assert dbResource.getUri() != null && new URI(dbResource.getUri()).equals(metadataService.getUri(resource));
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Illegal URI in resource table.", ex);
        }

        return resource;
    }

    void loadRequirements(Resource resource, long resourceId, SqlSession session) {
        List<DbRequirement> dbRequirements = session.selectList(RESOURCE_MAPPER + "selectRequirements", resourceId);

        Map<Long, Requirement> requirements = new HashMap<>(dbRequirements.size());
        Map<Long, Long> unprocessedRequirements = new HashMap<>(dbRequirements.size()); // K: requirement ID, V: requirement parent ID

        for (DbRequirement dbRequirement : dbRequirements) {
            Requirement requirement = resourceFactory.createRequirement(dbRequirement.getNamespace(), dbRequirement.getId());

            loadRequirementAttributes(requirement, dbRequirement.getRequirementId(), session);
            loadRequirementDirectives(requirement, dbRequirement.getRequirementId(), session);

            requirement.setResource(resource);

            requirements.put(dbRequirement.getRequirementId(), requirement);

            if (dbRequirement.getRequirementId() == dbRequirement.getParentRequirementId()) {
                assert dbRequirement.getLevel() == 0;

                resource.addRequirement(requirement);
            } else {
                Requirement parent = requirements.get(dbRequirement.getParentRequirementId());
                if (parent != null) {
                    parent.addChild(requirement);
                    requirement.setParent(parent);
                } else {
                    logger.warn("There is unprocessed requirement (missing parent) for resource {}, ID: {}, parent ID: {}.",
                            resourceId, dbRequirement.getRequirementId(), dbRequirement.getParentRequirementId());
                    unprocessedRequirements.put(dbRequirement.getRequirementId(), dbRequirement.getParentRequirementId());
                }
            }
        }

        assert unprocessedRequirements.isEmpty();

        while (!unprocessedRequirements.isEmpty()) {
            Iterator<Entry<Long, Long>> iterator = unprocessedRequirements.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<Long, Long> entry = iterator.next();
                Requirement parent = requirements.get(entry.getValue());
                if (parent != null) {
                    Requirement requirement = requirements.get(entry.getKey());
                    parent.addChild(requirement);
                    requirement.setParent(parent);
                    iterator.remove();
                }
            }
        }
    }

    void loadCapabilities(Resource resource, long resourceId, SqlSession session) {
        List<DbCapability> dbCapabilities = session.selectList(RESOURCE_MAPPER + "selectCapabilities", resourceId);

        Map<Long, Capability> capabilities = new HashMap<>(dbCapabilities.size());
        Map<Long, Long> unprocessedCapabilities = new HashMap<>(dbCapabilities.size()); // K: capability ID, V: capability parent ID

        for (DbCapability dbCapability : dbCapabilities) {
            Capability capability = resourceFactory.createCapability(dbCapability.getNamespace(), dbCapability.getId());

            loadCapabilityAttributes(capability, dbCapability.getCapabilityId(), session);
            loadCapabilityDirectives(capability, dbCapability.getCapabilityId(), session);

            capability.setResource(resource);
            resource.addCapability(capability);

            capabilities.put(dbCapability.getCapabilityId(), capability);

            if (dbCapability.getCapabilityId() == dbCapability.getParentCapabilityId()) {
                assert dbCapability.getLevel() == 0;

                resource.addRootCapability(capability);
            } else {
                Capability parent = capabilities.get(dbCapability.getParentCapabilityId());
                if (parent != null) {
                    parent.addChild(capability);
                    capability.setParent(parent);
                } else {
                    logger.warn("There is unprocessed capability (missing parent) for resource {}, ID: {}, parent ID: {}.",
                            resourceId, dbCapability.getCapabilityId(), dbCapability.getParentCapabilityId());
                    unprocessedCapabilities.put(dbCapability.getCapabilityId(), dbCapability.getParentCapabilityId());
                }
            }
        }

        assert unprocessedCapabilities.isEmpty();

        // Reconstruct capability tree structure - this would be optimized by MPTT or hierarchy leveling or:
        // http://www.codeproject.com/Articles/8355/Trees-in-SQL-databases
        while (!unprocessedCapabilities.isEmpty()) {
            Iterator<Entry<Long, Long>> iterator = unprocessedCapabilities.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<Long, Long> entry = iterator.next();
                Capability parent = capabilities.get(entry.getValue());
                if (parent != null) {
                    Capability capability = capabilities.get(entry.getKey());
                    parent.addChild(capability);
                    capability.setParent(parent);
                    iterator.remove();
                }
            }
        }
    }

    void loadCapabilityAttributes(Capability capability, long capabilityId, SqlSession session) {
        List<DbAttribute> dbAttributes = session.selectList(RESOURCE_MAPPER + "selectCapabilityAttributes", capabilityId);

        MetadataMapping.mapDbAttributes2Capability(dbAttributes, capability);
    }

    void loadCapabilityDirectives(Capability capability, long capabilityId, SqlSession session) {
        List<DbDirective> dbDirectives = session.selectList(RESOURCE_MAPPER + "selectCapabilityDirectives", capabilityId);

        MetadataMapping.mapDbDirectives2Capability(dbDirectives, capability);
    }

    void loadRequirementAttributes(Requirement requirement, long requirementId, SqlSession session) {
        List<DbAttribute> dbAttributes = session.selectList(RESOURCE_MAPPER + "selectRequirementAttributes", requirementId);

        MetadataMapping.mapDbAttributes2Requirement(dbAttributes, requirement);
    }

    void loadRequirementDirectives(Requirement requirement, long requirementId, SqlSession session) {
        List<DbDirective> dbDirectives = session.selectList(RESOURCE_MAPPER + "selectRequirementDirectives", requirementId);

        MetadataMapping.mapDbDirectives2Requirement(dbDirectives, requirement);
    }

    @Override
    public synchronized List<Resource> loadResources(Repository repository) throws IOException {
        logger.debug("loadResources(repository={})", repository);

        List<Resource> result = Collections.emptyList();
        try (SqlSession session = sessionManager.getSession()) {
            Long repositoryId = repositoryDAOImpl.getRepositoryId(repository, session);

            if (repositoryId != null) {
                List<DbResource> dbResources = session.selectList(RESOURCE_MAPPER + "selectResourcesByRepositoryId", repositoryId);
                if (dbResources != null && !dbResources.isEmpty()) {
                    result = new ArrayList<>(dbResources.size());
                    for (DbResource dbResource : dbResources) {
                        // TODO optimize - select in loop
                        Resource resource = loadResource(dbResource, session);
                        resource.setRepository(repository);
                        result.add(resource);
                    }
                }
            }
        }

        if (logger.isTraceEnabled()) {
            logger.trace("loadResources(repository={}) returns {}", repository, result);
        } else {
            logger.debug("loadResources(repository) returns {}", result.size());
        }

        return result;
    }

    @Override
    public synchronized void saveResource(Resource resource) throws IOException {
        logger.debug("saveResource(resource={})", resource);

        try (SqlSession session = sessionManager.getSession()) {
            SequenceMapper seqMapper = session.getMapper(SequenceMapper.class);

            DbResource dbResource = MetadataMapping.mapResource2DbResource(resource, metadataService);

            long resourceId = seqMapper.nextVal("resource_seq");
            dbResource.setResourceId(resourceId);

            Repository repository = resource.getRepository();
            if (repository == null) {
                throw new IllegalArgumentException("Repository is not set on resource: " + resource.getId());
            }
            Long repositoryId = repositoryDAOImpl.getRepositoryId(repository, session);
            if (repositoryId == null) {
                throw new IllegalStateException("Repository doesn't exist: " + repository.getURI());
            }
            dbResource.setRepositoryId(repositoryId);

            session.insert(RESOURCE_MAPPER + "insertResource", dbResource);

            // capabilities
            for (Capability capability : resource.getRootCapabilities()) {
                saveCapabilityRecursive(capability, resourceId, null, 0, session, seqMapper);
            }

            // requirements
            for (Requirement requirement : resource.getRequirements()) {
                saveRequirementRecursive(requirement, resourceId, null, 0, session, seqMapper);
            }

            session.commit();
        } catch (PersistenceException e) {
            throw new IOException("Could not save resource.", e);
        }

        logger.debug("saveResource(resource) returns", resource);
    }

    private void saveCapabilityRecursive(Capability capability, long resourceId, Long parentId, int level,
            SqlSession session, SequenceMapper seqMapper) {

        DbCapability dbCapability = MetadataMapping.mapCapability2DbCapability(capability, metadataService);
        dbCapability.setResourceId(resourceId);
        dbCapability.setLevel(level);

        long capabilityId = seqMapper.nextVal("capability_seq");
        dbCapability.setCapabilityId(capabilityId);
        dbCapability.setParentCapabilityId(parentId != null ? parentId : capabilityId);

        session.insert(RESOURCE_MAPPER + "insertCapability", dbCapability);

        List<DbAttribute> dbAttributes = MetadataMapping.mapAttributes2DbAttributes(capability.getAttributes(), capabilityId);
        if (!dbAttributes.isEmpty()) {
            session.insert(RESOURCE_MAPPER + "insertCapabilityAttributes", dbAttributes);
        }

        List<DbDirective> dbDirectives = MetadataMapping.mapDirectives2DbDirectives(capability.getDirectives(), capabilityId);
        if (!dbDirectives.isEmpty()) {
            session.insert(RESOURCE_MAPPER + "insertCapabilityDirectives", dbDirectives);
        }

        for (Capability child : capability.getChildren()) {
            saveCapabilityRecursive(child, resourceId, capabilityId, level + 1, session, seqMapper);
        }
    }

    private void saveRequirementRecursive(Requirement requirement, long resourceId, Long parentId, int level,
            SqlSession session, SequenceMapper seqMapper) {

        DbRequirement dbRequirement = MetadataMapping.mapRequirement2DbRequirement(requirement, metadataService);
        dbRequirement.setResourceId(resourceId);
        dbRequirement.setLevel(level);

        long requirementId = seqMapper.nextVal("requirement_seq");
        dbRequirement.setRequirementId(requirementId);
        dbRequirement.setParentRequirementId(parentId != null ? parentId : requirementId);

        session.insert(RESOURCE_MAPPER + "insertRequirement", dbRequirement);

        List<DbAttribute> dbAttributes = MetadataMapping.mapAttributes2DbAttributes(requirement.getAttributes(), requirementId, true);
        if (!dbAttributes.isEmpty()) {
            session.insert(RESOURCE_MAPPER + "insertRequirementAttributes", dbAttributes);
        }

        List<DbDirective> dbDirectives = MetadataMapping.mapDirectives2DbDirectives(requirement.getDirectives(), requirementId);
        if (!dbDirectives.isEmpty()) {
            session.insert(RESOURCE_MAPPER + "insertRequirementDirectives", dbDirectives);
        }

        for (Requirement child : requirement.getChildren()) {
            saveRequirementRecursive(child, resourceId, requirementId, level + 1, session, seqMapper);
        }
    }

    @Override
    public synchronized void deleteResource(URI uri) throws IOException {
        logger.debug("deleteResource(uri={})", uri);

        try (SqlSession session = sessionManager.getSession()) {
            DbResource dbResource = session.selectOne(RESOURCE_MAPPER + "selectResourceByUri", uri.toString());

            if (dbResource != null) {
                long resourceId = dbResource.getResourceId();
                session.delete(RESOURCE_MAPPER + "deleteResource", resourceId);
                session.commit();
            }
        }

        logger.debug("deleteResource(uri) returns");
    }

    @Override
    public synchronized boolean existsResource(URI uri) throws IOException {    // TODO just simple select, check if return null
        logger.debug("existsResource(uri={})", uri);

        boolean result = false;
        try (SqlSession session = sessionManager.getSession()) {
            if (session.selectOne(RESOURCE_MAPPER + "selectResourceByUri", uri.toString()) != null) {
                result = true;
            }
        }

        logger.debug("existsResource(uri) returns {}", result);

        return result;
    }

    @Override
    public boolean existsResource(URI uri, Repository repository) throws IOException {  // not needed
        logger.debug("existsResource(uri={}, repository={})", uri, repository);

        boolean result = false;
        try (SqlSession session = sessionManager.getSession()) {
            Long repositoryId = repositoryDAOImpl.getRepositoryId(repository, session);

            DbResource dbResource = session.selectOne(RESOURCE_MAPPER + "selectResourceByUri", uri.toString());
            if (dbResource != null && dbResource.getRepositoryId() == repositoryId) {
                result = true;
            }
        }

        logger.debug("existsResource(uri, repository) returns {}", result);

        return result;
    }
}
