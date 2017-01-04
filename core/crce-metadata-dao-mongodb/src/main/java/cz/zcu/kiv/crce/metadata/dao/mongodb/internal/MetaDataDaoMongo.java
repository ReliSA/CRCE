package cz.zcu.kiv.crce.metadata.dao.mongodb.internal;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;
import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.MetadataDao;
import cz.zcu.kiv.crce.metadata.dao.filter.ResourceFilter;
import cz.zcu.kiv.crce.metadata.dao.mongodb.BaseMongoDao;
import cz.zcu.kiv.crce.metadata.dao.mongodb.DbContext;
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db.DbCapability;
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db.DbProperty;
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db.DbRepository;
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db.DbRequirement;
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db.DbResource;
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.mapper.QueryBuilder;
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.mapper.ResourceMetadataMapper;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 * {@link MetadataDao} implementation for MongoDB
 *
 * Date: 22.9.16
 *
 * @author Jakub Danek
 */
@ParametersAreNonnullByDefault
@Component(provides={MetadataDao.class})
public class MetaDataDaoMongo extends BaseMongoDao implements MetadataDao {

    private JacksonDBCollection<DbResource, String> resources;
    private JacksonDBCollection<DbRepository, String> repositories;
    private JacksonDBCollection<DbCapability, String> capabilities;
    private JacksonDBCollection<DbRequirement, String> requirements;
    private JacksonDBCollection<DbProperty, String> properties;

    // injected by DI
    @ServiceDependency
    private MetadataService metadataService;
    @ServiceDependency
    private MetadataFactory metadataFactory;


    public MetaDataDaoMongo() throws UnknownHostException, ClassNotFoundException {
        super(DbContext.getConnection());

        resources = getCollection("resources", DbResource.class, String.class);
        repositories = getCollection("repositories", DbRepository.class, String.class);
        requirements = getCollection("requirements", DbRequirement.class, String.class);
        properties = getCollection("properties", DbProperty.class, String.class);
        capabilities = getCollection("capabilities", DbCapability.class, String.class);
    }

    @Override
    public Resource loadResource(URI uri) throws IOException {
        DbResource res = resources.findOne(QueryBuilder.queryResourceByURI(uri.toString()));

        return mapResult(res, true);
    }

    @Override
    public Resource loadResource(String uid, boolean withDetails) throws IOException {
        DbResource res = resources.findOne(QueryBuilder.queryByPK(uid));

        return mapResult(res, withDetails);
    }

    @Nonnull
    @Override
    public List<Resource> loadResourcesWithoutCategory(String category, boolean withDetails) throws IOException {
        DBCursor<DbResource> res = resources.find(QueryBuilder.queryByMissingCategory(category));

        return mapResult(res, withDetails);
    }

    @Nonnull
    @Override
    public List<Resource> loadResources(Repository repository, boolean withDetails) throws IOException {
        DBCursor<DbResource> res = resources.find(QueryBuilder.queryResourceByRepositoryId(repository.getId()));

        return mapResult(res, withDetails);
    }

    @Nonnull
    @Override
    public List<Resource> loadResources(Repository repository, boolean withDetails, ResourceFilter filter) throws IOException {
        List<String> ids = queryCapabilityFilter(filter);
        DBQuery.Query q = DBQuery.and(QueryBuilder.queryResourceByRepositoryId(repository.getId()), QueryBuilder.queryByPK(ids));
        return mapResult(resources.find(q), withDetails);
    }

    @Override
    public void saveResource(Resource resource) throws IOException {

        DbResource toSave = ResourceMetadataMapper.map(resource, metadataService);
        for (DbCapability dbCapability : toSave.getCapabilities()) {
            capabilities.save(dbCapability);
        }
        for (DbRequirement dbRequirement : toSave.getRequirements()) {
            requirements.save(dbRequirement);
        }
        for (DbProperty dbProperty : toSave.getProperties()) {
            properties.save(dbProperty);
        }

        resources.save(toSave);

    }

    @Override
    public void deleteResource(URI uri) throws IOException {
        DBQuery.Query uriQ = QueryBuilder.queryResourceByURI(uri.toString());
        DbResource res = resources.findOne();
        DBQuery.Query q = QueryBuilder.queryByResourceId(res.getId());
        capabilities.remove(q);
        requirements.remove(q);
        properties.remove(q);
        resources.remove(uriQ);
    }

    @Override
    public boolean existsResource(URI uri) throws IOException {
        return resources.getCount(QueryBuilder.queryResourceByURI(uri.toString())) > 0;
    }

    @Override
    public boolean existsResource(URI uri, Repository repository) throws IOException {
        DBQuery.Query q = DBQuery.and(QueryBuilder.queryResourceByRepositoryId(repository.getId()), QueryBuilder.queryResourceByURI(uri.toString()));

        return resources.getCount(q) > 0;
    }

    @Override
    public Repository loadRepository(URI uri) throws IOException {
        DbRepository repo = repositories.findOne(QueryBuilder.queryResourceByURI(uri.toString()));
        if(repo == null) {
            return null;
        }
        return ResourceMetadataMapper.map(repo, metadataFactory);
    }

    @Override
    public void deleteRepository(Repository repository) throws IOException {
        repositories.remove(QueryBuilder.queryByPK(repository.getId()));
    }

    @Override
    public void saveRepository(Repository repository) throws IOException {
        repositories.save(ResourceMetadataMapper.map(repository));
    }

    private List<String> queryCapabilityFilter(ResourceFilter filter) {
        return mapCapResourceId(capabilities.find(QueryBuilder.processCapabilityFilters(filter), QueryBuilder.RESOURCE_ID));
    }

    private List<String> mapCapResourceId(DBCursor<DbCapability> caps) {
        List<String> toRet = new LinkedList<>();

        while (caps.hasNext()) {
            toRet.add(caps.next().getResourceId());
        }

        return toRet;
    }

    private List<Resource> mapResult(DBCursor<DbResource> res, boolean withDetails) {
        List<Resource> toRet = new LinkedList<>();

        while (res.hasNext()) {
            toRet.add(mapResult(res.next(), withDetails));
        }

        return toRet;
    }

    private Resource mapResult(DbResource res, boolean withDetails) {
        Resource dest;

        if(res == null) {
            return null;
        }

        dest = ResourceMetadataMapper.map(res, metadataFactory, metadataService);

        if(withDetails) {
            ResourceMetadataMapper.mapCaps(dest, capabilities.find(QueryBuilder.queryByResourceId(res.getId())).toArray(), metadataFactory, metadataService);
            ResourceMetadataMapper.mapReqs(dest, requirements.find(QueryBuilder.queryByResourceId(res.getId())).toArray(), metadataFactory);
            ResourceMetadataMapper.mapProps(dest, properties.find(QueryBuilder.queryByResourceId(res.getId())).toArray(), metadataFactory);
        }
        return dest;
    }
}
