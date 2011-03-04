package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class RepositoryImpl implements Repository, WritableRepository {

    private URI uri = null;
    private Set<Resource> resources = new HashSet<Resource>();
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
    public Resource[] getResources() {
        return resources.toArray(new Resource[resources.size()]);
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
    public boolean Contains(Resource resource) {
        return resources.contains(resource);
    }

    @Override
    public boolean addResource(Resource resource) {
        if (resources.contains(resource)) {
            return false;
        }
        resources.add(resource);
        return true;
    }

    @Override
    public Resource addResource(Resource resource, boolean force) {
        Resource out = null;

        if (!addResource(resource)) {
            for (Resource i : resources) {
                if (i.equals(resource)) {
                    out = i;
                    resources.remove(i);
                    break;
                }
            }
            if (force) {
                resources.add(resource);
            }
        }
        return out;
    }
}
