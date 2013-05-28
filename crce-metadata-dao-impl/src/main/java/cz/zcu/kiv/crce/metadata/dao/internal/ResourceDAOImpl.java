package cz.zcu.kiv.crce.metadata.dao.internal;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class ResourceDAOImpl implements ResourceDAO {

    private volatile MetadataService metadataService;

    Map<URI, Map<URI, Resource>> repositories = new HashMap<>();

    @Override
    public synchronized Resource loadResource(URI uri) throws IOException { // TODO, only an URI as an argument is not nice
        for (Map<URI, Resource> resources : repositories.values()) {
            if (resources != null) {
                Resource resource = resources.get(uri);
                if (resource != null) {
                    return resource;
                }
            }
        }
        return null;
    }

    @Override
    public synchronized List<Resource> loadResources(Repository repository) throws IOException {
        Map<URI, Resource> resources = repositories.get(repository.getURI());
        if (resources != null) {
            return Collections.unmodifiableList(new ArrayList<>(resources.values()));
        }
        return Collections.emptyList();
    }

    @Override
    public synchronized void saveResource(Resource resource) throws IOException {
        URI repositoryUri = null;
        Repository repository = resource.getRepository();
        if (repository != null) {
            repositoryUri = repository.getURI();
        }
        Map<URI, Resource> resources = repositories.get(repositoryUri);
        if (resources == null) {
            resources = new HashMap<>();
            repositories.put(repositoryUri, resources);
        }
        resources.put(metadataService.getUri(resource), resource);
    }

    @Override
    public synchronized void deleteResource(URI uri) throws IOException {
        for (Map<URI, Resource> resources : repositories.values()) {
            if (resources != null && resources.containsKey(uri)) {
                resources.remove(uri);
            }
        }
    }

    @Override
    public synchronized boolean existsResource(URI uri) throws IOException {
        for (Map<URI, Resource> resources : repositories.values()) {
            if (resources != null && resources.containsKey(uri)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean existsResource(URI uri, Repository repository) throws IOException {
        Map<URI, Resource> resources = repositories.get(repository.getURI());
        return resources.containsKey(uri);
    }

    @Override
    public String toString() {
        return "Persisted repositories and resources:\r\n" + repositories.toString();
    }
}
