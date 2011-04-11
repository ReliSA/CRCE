package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class RepositoryImpl implements Repository, WritableRepository {

    private URI uri = null;
    private Map<String, Resource> resources = new HashMap<String, Resource>();
    private String name;
    private long lastModified;

    public RepositoryImpl(URI uri) {
        this.uri = uri;
    }

    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public synchronized Resource[] getResources() {
        return resources.values().toArray(new Resource[resources.size()]);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getLastModified() {
        return lastModified;
    }

    @Override
    public synchronized boolean contains(Resource resource) {
        return resources.containsKey(resource.getId());
    }

    @Override
    public synchronized boolean addResource(Resource resource) {
        if (resources.containsKey(resource.getId())) {
            return false;
        }
        resources.put(resource.getId(), resource);
        if (resource instanceof ResourceImpl) {
            ((ResourceImpl) resource).setRepository(this);
        }
        lastModified = System.nanoTime();
        return true;
    }

    @Override
    public synchronized Resource addResource(Resource resource, boolean force) {
        Resource out = resources.get(resource.getId());
        if (out == null || force) {
            resources.put(resource.getId(), resource);
            if (resource instanceof ResourceImpl) {
                ((ResourceImpl) resource).setRepository(this);
            }
            lastModified = System.nanoTime();
        }
        return out;
    }

    @Override
    public synchronized boolean removeResource(Resource resource) {
        boolean out = (resources.remove(resource.getId()) != null);
        if (resource instanceof ResourceImpl) {
            ((ResourceImpl) resource).setRepository(null);
        }
        lastModified = System.nanoTime();
        return out;
    }

    @Override
    public Resource[] getResources(String filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Resource[] getResources(Requirement[] requirements) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
