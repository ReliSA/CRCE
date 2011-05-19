package cz.zcu.kiv.crce.metadata.wrapper.osgi;

import cz.zcu.kiv.crce.metadata.Resource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.osgi.service.obr.Capability;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.Requirement;

import org.osgi.framework.Version;

/**
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class ResourceWrapper implements org.osgi.service.obr.Resource {

    final Resource resource;

    public ResourceWrapper(Resource resource) {
        this.resource = resource;
    }

    @Override
    public Map getProperties() {
        return resource.getPropertiesMap();
    }

    @Override
    public String getSymbolicName() {
        return resource.getSymbolicName();
    }

    @Override
    public String getPresentationName() {
        return resource.getPresentationName();
    }

    @Override
    public Version getVersion() {
        return resource.getVersion();
    }

    @Override
    public String getId() {
        return resource.getId();
    }

    @Override
    public Requirement[] getRequirements() {
        return Wrapper.wrap(resource.getRequirements());
    }

    @Override
    public Capability[] getCapabilities() {
        return Wrapper.wrap(resource.getCapabilities());
    }

    @Override
    public String[] getCategories() {
        return resource.getCategories();
    }

    @Override
    public Repository getRepository() {
        throw new UnsupportedOperationException();
    }

    @Override
    public URL getURL() {
        try {
            return resource.getUri().toURL();
        } catch (MalformedURLException ex) {
            throw new UnsupportedOperationException("URI non-convertible to URL is not supported by this implementation", ex);
        }
    }
}
