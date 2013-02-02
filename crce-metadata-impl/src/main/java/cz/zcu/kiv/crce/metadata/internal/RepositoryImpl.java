package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.felix.utils.collections.MapToDictionary;
import org.apache.felix.utils.filter.FilterImpl;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

/**
 * Implementation of <code>Repository</code> interface.
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
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
        resource.setRepository(this);
        lastModified = System.nanoTime();
        return true;
    }

    @Override
    public synchronized Resource addResource(Resource resource, boolean force) {
        Resource out = resources.get(resource.getId());
        if (out == null || force) {
            resources.put(resource.getId(), resource);
            if (resource.getRepository() != this) {
                resource.setRepository(this);
            }
            lastModified = System.nanoTime();
        }
        return out;
    }

    @Override
    public synchronized boolean removeResource(Resource resource) {
        boolean out = (resources.remove(resource.getId()) != null);
        if (resource.getRepository() == this) {
            resource.setRepository(null);
        }
        lastModified = System.nanoTime();
        return out;
    }

    @Override
    public synchronized Resource[] getResources(String filter) throws InvalidSyntaxException {
        if (filter == null || filter.trim().equals("")) {
            return getResources();
        }
        MapToDictionary dict = new MapToDictionary(null);
        List<Resource> matches = new ArrayList<Resource>();
        Filter f = FilterImpl.newInstance(filter);
        for (Resource resource : resources.values()) {
            dict.setSourceMap(resource.getPropertiesMap());
            if (f == null || f.match(dict)) {
                matches.add(resource);
            }
        }
        return matches.toArray(new Resource[matches.size()]);
    }

    @Override
    public synchronized Resource[] getResources(Requirement[] requirements) {
        if (requirements == null || requirements.length == 0) {
            return getResources();
        }
        MapToDictionary dict = new MapToDictionary(null);
        List<Resource> matches = new ArrayList<Resource>();

        for (Resource resource : resources.values()) {
            dict.setSourceMap(resource.getPropertiesMap());
            boolean match = true;
            for (Requirement requirement : requirements) {
                boolean reqMatch = false;
                Capability[] caps = resource.getCapabilities();
                for (int capIdx = 0; (caps != null) && (capIdx < caps.length); capIdx++) {
                    if (requirement.isSatisfied(caps[capIdx])) {
                        reqMatch = true;
                        break;
                    }
                }
                match &= reqMatch;
                if (!match) {
                    break;
                }
            }
            if (match) {
                matches.add(resource);
            }
        }
        return matches.toArray(new Resource[matches.size()]);
    }

    @Override
    public String toString() {
        return "Repository on URI: " + String.valueOf(uri) + ", size: " + resources.size();
    }
}
