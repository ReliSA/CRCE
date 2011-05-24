package cz.zcu.kiv.crce.metadata.wrapper.felix;

import cz.zcu.kiv.crce.metadata.Resource;
import java.util.Map;
import org.apache.felix.bundlerepository.Capability;
import org.apache.felix.bundlerepository.Repository;
import org.apache.felix.bundlerepository.Requirement;

import org.osgi.framework.Version;

/**
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class ResourceWrapper implements org.apache.felix.bundlerepository.Resource {

    final Resource resource;

    public ResourceWrapper(Resource resource) {
        this.resource = resource;
    }

    public Map getProperties() {
        return resource.getPropertiesMap();
    }

    public String getSymbolicName() {
        return resource.getSymbolicName();
    }

    public String getPresentationName() {
        return resource.getPresentationName();
    }

    public Version getVersion() {
        return resource.getVersion();
    }

    public String getId() {
        return resource.getId();
    }

    // TODO - k cemu to bylo?
//    public URL getURL() {
//        try {
//            return new URL(resource.getUri());
//        } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public Requirement[] getRequirements() {
        return Wrapper.wrap(resource.getRequirements());
    }

    public Capability[] getCapabilities() {
        return Wrapper.wrap(resource.getCapabilities());
    }

    public String[] getCategories() {
        return resource.getCategories();
    }

    public Repository getRepository() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getURI() {
        return resource.getUri().toString();
    }

    @Override
    public Long getSize() {
        return resource.getSize();
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}
