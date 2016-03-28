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
import org.apache.felix.dm.annotation.api.ServiceDependency;
import org.apache.felix.dm.annotation.api.Start;
import org.apache.felix.dm.annotation.api.Stop;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.MetadataDao;
import cz.zcu.kiv.crce.metadata.dao.filter.ResourceFilter;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbAttribute;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbCapability;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbDirective;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbProperty;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbRepository;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbRequirement;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbResource;
import cz.zcu.kiv.crce.metadata.dao.internal.mapper.ResolvingMapper;
import cz.zcu.kiv.crce.metadata.dao.internal.mapper.SequenceMapper;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import javax.annotation.CheckForNull;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 * @author Jan Dyrczyk (dyrczyk@students.zcu.cz)
 * @author Pavel Cihlář
 */
@Component(provides={MetadataDao.class})
public class MetadataDaoImpl implements MetadataDao {

    private static final Logger logger = LoggerFactory.getLogger(MetadataDaoImpl.class);

    private static final String RESOURCE_MAPPER = "cz.zcu.kiv.crce.metadata.dao.internal.mapper.ResourceMapper.";
    private static final String REPOSITORY_MAPPER = "cz.zcu.kiv.crce.metadata.dao.internal.mapper.RepositoryMapper.";


    @ServiceDependency private volatile MetadataFactory metadataFactory;
    @ServiceDependency private volatile MetadataService metadataService;
    @ServiceDependency private volatile SessionManager sessionManager;

    @Start
    void start() {
        logger.info("CRCE Metadata DAO ResourceDAO started.");
    }

    @Stop
    synchronized void stop() {
        logger.info("CRCE Metadata DAO ResourceDAO finished.");
    }

    @Override
    public synchronized Resource loadResource(URI uri) throws IOException { // TODO, only an URI as an argument is not nice
        logger.debug("loadResource(uri={})", uri);

        Resource result = null;

        try (SqlSession session = sessionManager.getSession()) {
            DbResource dbResource = session.selectOne(RESOURCE_MAPPER + "selectResourceByUri", uri.toString());

            if (dbResource != null) {
                result = loadResource(dbResource, session);
            }
        } catch (PersistenceException e) {
            throw new IOException("Could not load resource.", e);
        }

        logger.debug("loadResource(uri) returns {}", result);
        return result;
    }

    private Resource loadResource(@Nonnull DbResource dbResource, @Nonnull SqlSession session) {
        Resource resource = metadataFactory.createResource(dbResource.getId());
        // loaded from DB as part of capabilities:
//        metadataService.setUri(resource, dbResource.getUri());
//        metadataService.getSingletonCapability(resource, metadataService.getIdentityNamespace())
//                .setAttribute("repository-id", String.class, dbResource.getRepositoryUuid());
        Map<Long, Requirement> requirements = loadRequirements(resource, dbResource.getResourceId(), session);
        loadCapabilities(resource, dbResource.getResourceId(), requirements, session);
        loadProperties(resource, dbResource.getResourceId(), session);

        try {
            assert dbResource.getUri() != null && new URI(dbResource.getUri()).equals(metadataService.getUri(resource));
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Illegal URI in resource table.", ex);
        }

        return resource;
    }


    Map<Long, Requirement> loadRequirements(Resource resource, long resourceId, SqlSession session) {
        List<DbRequirement> dbRequirements = session.selectList(RESOURCE_MAPPER + "selectRequirements", resourceId);

        Map<Long, Requirement> requirements = new HashMap<>(dbRequirements.size());
        Map<Long, Requirement> capabilityRequirements = new HashMap<>(dbRequirements.size());
        Map<Long, Long> unprocessedRequirements = new HashMap<>(dbRequirements.size()); // K: requirement ID, V: requirement parent ID

        for (DbRequirement dbRequirement : dbRequirements) {
            Requirement requirement = metadataFactory.createRequirement(dbRequirement.getNamespace(), dbRequirement.getId());

            loadRequirementAttributes(requirement, dbRequirement.getRequirementId(), session);
            loadRequirementDirectives(requirement, dbRequirement.getRequirementId(), session);

            requirements.put(dbRequirement.getRequirementId(), requirement);

            if (dbRequirement.getRequirementId() == dbRequirement.getParentRequirementId()) {
                assert dbRequirement.getLevel() == 0;

                if (dbRequirement.getCapabilityId() != null) {
                    capabilityRequirements.put(dbRequirement.getCapabilityId(), requirement);
                } else {
                    resource.addRequirement(requirement);
                }
            } else {
                Requirement parent = requirements.get(dbRequirement.getParentRequirementId());
                if (parent != null) {
                    parent.addChild(requirement);
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
                    iterator.remove();
                }
            }
        }

        return capabilityRequirements;
    }

    void loadCapabilities(Resource resource, long resourceId, Map<Long, Requirement> requirements, SqlSession session) {
        List<DbCapability> dbCapabilities = session.selectList(RESOURCE_MAPPER + "selectCapabilities", resourceId);

        Map<Long, Capability> capabilities = new HashMap<>(dbCapabilities.size());
        Map<Long, Long> unprocessedCapabilities = new HashMap<>(dbCapabilities.size()); // K: capability ID, V: capability parent ID

        for (DbCapability dbCapability : dbCapabilities) {
            Capability capability = metadataFactory.createCapability(dbCapability.getNamespace(), dbCapability.getId());

            loadCapabilityAttributes(capability, dbCapability.getCapabilityId(), session);
            loadCapabilityDirectives(capability, dbCapability.getCapabilityId(), session);
            loadProperties(capability, dbCapability.getCapabilityId(), session);

            Requirement requirement = requirements.get(dbCapability.getCapabilityId());
            if (requirement != null) {
                capability.addRequirement(requirement);
            }

            resource.addCapability(capability);

            capabilities.put(dbCapability.getCapabilityId(), capability);

            if (dbCapability.getCapabilityId() == dbCapability.getParentCapabilityId()) {
                assert dbCapability.getLevel() == 0;

                resource.addRootCapability(capability);
            } else {
                Capability parent = capabilities.get(dbCapability.getParentCapabilityId());
                if (parent != null) {
                    parent.addChild(capability);
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
                    iterator.remove();
                }
            }
        }
    }

    void loadProperties(Resource resource, long resourceId, SqlSession session) {
        List<DbProperty> dbProperties = session.selectList(RESOURCE_MAPPER + "selectResourceProperties", resourceId);

        for (DbProperty dbProperty : dbProperties) {
            Property property = metadataFactory.createProperty(dbProperty.getNamespace(), dbProperty.getId());

            loadResourcePropertyAttributes(property, dbProperty.getPropertyId(), session);

            resource.addProperty(property);
        }
    }

    void loadProperties(Capability capability, long resourceId, SqlSession session) {
        List<DbProperty> dbProperties = session.selectList(RESOURCE_MAPPER + "selectCapabilityProperties", resourceId);

        for (DbProperty dbProperty : dbProperties) {
            Property property = metadataFactory.createProperty(dbProperty.getNamespace(), dbProperty.getId());

            loadCapabilityPropertyAttributes(property, dbProperty.getPropertyId(), session);

            capability.addProperty(property);
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

    void loadResourcePropertyAttributes(Property property, long propertyId, SqlSession session) {
        List<DbAttribute> dbAttributes = session.selectList(RESOURCE_MAPPER + "selectResourcePropertyAttributes", propertyId);

        MetadataMapping.mapDbAttributes2Property(dbAttributes, property);
    }

    void loadCapabilityPropertyAttributes(Property property, long propertyId, SqlSession session) {
        List<DbAttribute> dbAttributes = session.selectList(RESOURCE_MAPPER + "selectCapabilityPropertyAttributes", propertyId);

        MetadataMapping.mapDbAttributes2Property(dbAttributes, property);
    }

    @Override
    public synchronized List<Resource> loadResources(Repository repository) throws IOException {
        logger.debug("loadResources(repository={})", repository);

        List<Resource> result = Collections.emptyList();
        try (SqlSession session = sessionManager.getSession()) {
            Long repositoryId = getRepositoryId(repository.getId(), session);

            if (repositoryId != null) {
                List<DbResource> dbResources = session.selectList(RESOURCE_MAPPER + "selectResourcesByRepositoryId", repositoryId);
                if (dbResources != null && !dbResources.isEmpty()) {
                    result = new ArrayList<>(dbResources.size());
                    for (DbResource dbResource : dbResources) {
                        // TODO optimize - select in loop
                        Resource resource = loadResource(dbResource, session);
                        metadataService.getIdentity(resource).setAttribute("repository-id", String.class, repository.getId());
                        result.add(resource);
                    }
                }
            }
        } catch (PersistenceException e) {
            throw new IOException("Could not load resources.", e);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("loadResources(repository={}) returns {}", repository, result);
        } else {
            logger.debug("loadResources(repository={) returns {}", repository, result.size());
        }

        return result;
    }

    @Override
    public List<Resource> loadResources(@Nonnull Repository repository, @Nonnull ResourceFilter filter) throws IOException {
        logger.debug("loadResources(repository={}, filter={})", repository,
                logger.isTraceEnabled() ? filter.toString() :  "(" + filter.getCapabilityFilters().size() + ")");

        List<Resource> result = Collections.emptyList();
        try (SqlSession session = sessionManager.getSession()) {
            Long repositoryId = getRepositoryId(repository.getId(), session);

            if (repositoryId != null) {
                List<DbResource> dbResources = session.getMapper(ResolvingMapper.class).getResources(repositoryId, filter);

                if (dbResources != null && !dbResources.isEmpty()) {
                    result = new ArrayList<>(dbResources.size());
                    for (DbResource dbResource : dbResources) {
                        // TODO optimize - select in loop
                        Resource resource = loadResource(dbResource, session);
                        metadataService.getIdentity(resource).setAttribute("repository-id", String.class, repository.getId());
                        result.add(resource);
                    }
                }
            }

        } catch (PersistenceException e) {
            throw new IOException("Could not load resources.", e);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("loadResources(repository={}, filter={}) returns {}",
                    repository, filter.toString() + "(" + filter.getCapabilityFilters().size() + ")", result);
        } else {
            logger.debug("loadResources(repository={}, filter={}) returns {}",
                    repository, "(" + filter.getCapabilityFilters().size() + ")", result.size());
        }

        return result;
    }

    @Override
    public synchronized void saveResource(Resource resource) throws IOException {
        logger.debug("saveResource(resource={})", resource);

        try (SqlSession session = sessionManager.getSession()) {
            /*
             * Workaround for updating of existing resources: Resource is deleted and then re-inserted
             * until some update solution will be implemented. This solution wastes internal unique IDs,
             * because a new ID is generated on each insertion.
             */
            if (existsResource(resource.getId())) {
                session.delete(RESOURCE_MAPPER + "deleteResourceById", resource.getId());
                logger.trace("Update workaround: existing resource {} was deleted before insert.", resource.getId());
            }

            SequenceMapper seqMapper = session.getMapper(SequenceMapper.class);

            DbResource dbResource = MetadataMapping.mapResource2DbResource(resource, metadataService);

            long resourceId = seqMapper.nextVal("resource_seq");
            dbResource.setResourceId(resourceId);

            String repositoryUuid = metadataService.getIdentity(resource)
                    .getAttributeValue(new SimpleAttributeType<>("repository-id", String.class));

            if (repositoryUuid == null) {
                throw new IllegalArgumentException("Repository is not set on resource: " + resource.getId());
            }

            Long repositoryId = getRepositoryId(repositoryUuid, session);
            if (repositoryId == null) {
                throw new IllegalArgumentException("Repository doesn't exist: " + repositoryUuid);
            }

            dbResource.setRepositoryId(repositoryId);

            session.insert(RESOURCE_MAPPER + "insertResource", dbResource);

            // capabilities
            for (Capability capability : resource.getRootCapabilities()) {
                saveCapabilityRecursive(capability, resourceId, null, 0, session, seqMapper);
            }

            // requirements
            for (Requirement requirement : resource.getRequirements()) {
                saveRequirementRecursive(requirement, resourceId, null, null, 0, session, seqMapper);
            }

            // properties
            for (Property property : resource.getProperties()) {
                saveResourceProperty(property, resourceId, session, seqMapper);
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

        for (Requirement requirement : capability.getRequirements()) {
            saveRequirementRecursive(requirement, resourceId, null, capabilityId, 0, session, seqMapper);
        }

        for (Property property : capability.getProperties()) {
            saveCapabilityProperty(property, capabilityId, session, seqMapper);
        }

        for (Capability child : capability.getChildren()) {
            saveCapabilityRecursive(child, resourceId, capabilityId, level + 1, session, seqMapper);
        }
    }

    private void saveRequirementRecursive(Requirement requirement, long resourceId, Long parentId, Long capabilityId, int level,
            SqlSession session, SequenceMapper seqMapper) {

        DbRequirement dbRequirement = MetadataMapping.mapRequirement2DbRequirement(requirement, metadataService);
        dbRequirement.setResourceId(resourceId);
        dbRequirement.setCapabilityId(capabilityId);
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
            saveRequirementRecursive(child, resourceId, requirementId, capabilityId, level + 1, session, seqMapper);
        }
    }

    private void saveResourceProperty(Property property, long resourceId, SqlSession session, SequenceMapper seqMapper) {
        long propertyId = seqMapper.nextVal("resource_property_seq");
        DbProperty dbProperty = MetadataMapping.mapProperty2DbProperty(property, propertyId, resourceId, metadataService);

        session.insert(RESOURCE_MAPPER + "insertResourceProperty", dbProperty);

        List<DbAttribute> dbAttributes = MetadataMapping.mapAttributes2DbAttributes(property.getAttributes(), propertyId);
        if (!dbAttributes.isEmpty()) {
            session.insert(RESOURCE_MAPPER + "insertResourcePropertyAttributes", dbAttributes);
        }
    }

    private void saveCapabilityProperty(Property property, long capabilityId, SqlSession session, SequenceMapper seqMapper) {
        long propertyId = seqMapper.nextVal("capability_property_seq");
        DbProperty dbProperty = MetadataMapping.mapProperty2DbProperty(property, propertyId, capabilityId, metadataService);

        session.insert(RESOURCE_MAPPER + "insertCapabilityProperty", dbProperty);

        List<DbAttribute> dbAttributes = MetadataMapping.mapAttributes2DbAttributes(property.getAttributes(), propertyId);
        if (!dbAttributes.isEmpty()) {
            session.insert(RESOURCE_MAPPER + "insertCapabilityPropertyAttributes", dbAttributes);
        }
    }

    @Override
    public synchronized void deleteResource(URI uri) throws IOException {
        logger.debug("deleteResource(uri={})", uri);

        try (SqlSession session = sessionManager.getSession()) {
            DbResource dbResource = session.selectOne(RESOURCE_MAPPER + "selectResourceByUri", uri.toString());

            if (dbResource != null) {
                long resourceId = dbResource.getResourceId();
                session.delete(RESOURCE_MAPPER + "deleteResourceByResourceId", resourceId);
                session.commit();
            }
        } catch (PersistenceException e) {
            throw new IOException("Could not delete resource.", e);
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
        } catch (PersistenceException e) {
            throw new IOException("Could not check presence of resource.", e);
        }

        logger.debug("existsResource(uri) returns {}", result);

        return result;
    }

    @Override
    public boolean existsResource(URI uri, Repository repository) throws IOException {  // not needed
        logger.debug("existsResource(uri={}, repository={})", uri, repository);

        boolean result = false;
        try (SqlSession session = sessionManager.getSession()) {
            Long repositoryId = getRepositoryId(repository.getId(), session);

            DbResource dbResource = session.selectOne(RESOURCE_MAPPER + "selectResourceByUri", uri.toString());
            if (dbResource != null && dbResource.getRepositoryId() == repositoryId) {
                result = true;
            }
        } catch (PersistenceException e) {
            throw new IOException("Could not check presence of resource.", e);
        }

        logger.debug("existsResource(uri, repository) returns {}", result);

        return result;
    }

    private boolean existsResource(@Nonnull String id) throws IOException {
        boolean result = false;

        try (SqlSession session = sessionManager.getSession()) {
            if (session.selectOne(RESOURCE_MAPPER + "selectResourceById", id) != null) {
                result = true;
            }
        } catch (PersistenceException e) {
            throw new IOException("Could not check presence of resource.", e);
        }

        return result;
    }


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
                repository = metadataFactory.createRepository(new URI(dbRepository.getUri()), dbRepository.getId());
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
            session.delete(REPOSITORY_MAPPER + "deleteRepository", repository.getUri().toString());
        }

        logger.debug("deleteRepository(repository={}) returns", repository);
    }

    @Override
    public void saveRepository(Repository repository) throws IOException {
        logger.debug("saveRepository(repository={})", repository);

        try (SqlSession session = sessionManager.getSession()) {
            if (loadRepository(repository.getUri()) == null) {
                SequenceMapper seqMapper = session.getMapper(SequenceMapper.class);

                DbRepository dbRepository = new DbRepository();

                long repositoryId = seqMapper.nextVal("resource_seq");

                dbRepository.setRepositoryId(repositoryId);
                dbRepository.setUri(repository.getUri().toString());
                dbRepository.setId(repository.getId());

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
    public Long getRepositoryId(@Nonnull String uuid, @Nonnull SqlSession session) {
        logger.debug("getRepositoryId(uuid={})", uuid);

        Long repositoryId = session.selectOne(REPOSITORY_MAPPER + "selectRepositoryId", uuid);

        logger.debug("getRepositoryId(uuid={}) returns {}", uuid, repositoryId);

        return repositoryId;
    }

    @CheckForNull
    public Repository loadRepository(long repositoryId, @Nonnull SqlSession session) throws IOException {
        logger.debug("getRepository(repositoryId={})", repositoryId);

        Repository repository = null;

        DbRepository dbRepository = session.selectOne(REPOSITORY_MAPPER + "selectRepositoryByRepositoryId", repositoryId);
        if (dbRepository != null) {
            try {
                repository = metadataFactory.createRepository(new URI(dbRepository.getUri()), dbRepository.getId());
            } catch (URISyntaxException e) {
                throw new IOException("Invalid URI syntax of repository with ID: " + repositoryId + ", URI: " + dbRepository.getUri(), e);
            }
        }

        logger.debug("getRepository(repositoryId={}) returns {}", repositoryId, repository);

        return repository;
    }
}
