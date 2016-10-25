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
import cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db.DbRepository;
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

    // injected by DI
    @ServiceDependency
    private MetadataService metadataService;
    @ServiceDependency
    private MetadataFactory metadataFactory;


    public MetaDataDaoMongo() throws UnknownHostException, ClassNotFoundException {
        super(DbContext.getConnection());

        resources = getCollection("resources", DbResource.class, String.class);
        repositories = getCollection("repositories", DbRepository.class, String.class);
    }

    @Override
    public Resource loadResource(URI uri) throws IOException {
        DbResource res = resources.findOne(QueryBuilder.queryResourceByURI(uri.toString()));

        return ResourceMetadataMapper.map(res, metadataFactory, metadataService);
    }

    @Nonnull
    @Override
    public List<Resource> loadResourcesWithoutCategory(String category) throws IOException {
        DBCursor<DbResource> res = resources.find(QueryBuilder.queryByMissingCategory(category));
        return mapResult(res);
    }

    @Nonnull
    @Override
    public List<Resource> loadResources(Repository repository) throws IOException {
        DBCursor<DbResource> res = resources.find(QueryBuilder.queryResourceByRepositoryId(repository.getId()));

        return mapResult(res);
    }

    @Nonnull
    @Override
    public List<Resource> loadResources(Repository repository, ResourceFilter filter) throws IOException {
        DBQuery.Query q = DBQuery.and(QueryBuilder.queryResourceByRepositoryId(repository.getId()), QueryBuilder.processCapabilityFilters(filter));
        return mapResult(resources.find(q));
    }

    @Override
    public void saveResource(Resource resource) throws IOException {
        resources.save(ResourceMetadataMapper.map(resource, metadataService));
    }

    @Override
    public void deleteResource(URI uri) throws IOException {
        resources.remove(QueryBuilder.queryResourceByURI(uri.toString()));
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

    private List<Resource> mapResult(DBCursor<DbResource> res) {
        List<Resource> toRet = new LinkedList<>();
        while (res.hasNext()) {
            toRet.add(ResourceMetadataMapper.map(res.next(), metadataFactory, metadataService));
        }

        return toRet;
    }
}
