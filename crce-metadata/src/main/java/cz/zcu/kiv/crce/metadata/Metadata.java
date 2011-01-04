package cz.zcu.kiv.crce.metadata;

import java.net.URI;
import java.net.URL;
import java.util.Set;
import org.osgi.framework.Version;
import org.osgi.service.obr.Capability;
import org.osgi.service.obr.Requirement;
import org.osgi.service.obr.Resource;

/**
 *
 * @author kalwi
 */
public interface Metadata {
    
    public static final String METAFILE_EXTENSION = ".meta";

    public Resource getResource();
    
//    public Capability[] getCapabilities();
//
//    public Requirement[] getRequirements();


    /* REQUIREMENTS */
    
    public void addRequirement(Requirement requirement);

    /* CAPABILITIES */
    
    public void addCapability(Capability capability);

    public void setCapability(String capability, String property, String value);

    public void setCapability(String capability, String property, Version value);

    public void setCapability(String capability, String property, URI value);

    public void setCapability(String capability, String property, URL value);

    public void setCapability(String capability, String property, long value);

    public void setCapability(String capability, String property, double value);

    public void setCapability(String capability, String property, Set value);
    
    /* RESOURCE PROPERTIES */
    
    public void setSymbolicName(String name) throws ReadOnlyException;
    
    public void setVersion(String version) throws ReadOnlyException;
    
    /* OTHER */
    
    public void flush();
    
}
