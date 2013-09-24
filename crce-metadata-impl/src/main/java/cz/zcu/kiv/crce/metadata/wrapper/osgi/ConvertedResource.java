package cz.zcu.kiv.crce.metadata.wrapper.osgi;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import cz.zcu.kiv.crce.metadata.internal.RequirementImpl;
import cz.zcu.kiv.crce.metadata.internal.ResourceImpl;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.osgi.framework.Version;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class ConvertedResource extends ResourceImpl {

    public ConvertedResource(org.osgi.service.obr.Resource resource) {
        for (org.osgi.service.obr.Capability fcap : resource.getCapabilities()) {
            Capability cap = new CapabilityImpl(fcap.getName());

            Map properties = fcap.getProperties();

            for (Iterator k = properties.keySet().iterator(); k.hasNext();) {
                String key = (String) k.next();
                List values = (List) properties.get(key);
                // TODO treat the case that values are not a list
                for (Iterator v = values.iterator(); v.hasNext();) {
                    Object value = v.next();
                    if (value != null) {
                        Property prop;
                        if (value instanceof Double || value instanceof Float) {
                            if (value != null) {
                                cap.setProperty(key, (Double) value);
                            }
                        } else if (value.getClass().isArray()) {
                            Set<String> set = new HashSet<String>();
                            for ( int i = 0; i < Array.getLength(value); i++) {
                                set.add(Array.get(value, i).toString());
                            }
                            cap.setProperty(key, set);
                        } else if (value instanceof String) {
                            cap.setProperty(key, (String) value);
                        } else if (value instanceof Version) {
                            cap.setProperty(key, (Version) value);
                        } else if (value instanceof URI) {
                            cap.setProperty(key, (URI) value);
                        } else if (value instanceof URL) {
                            cap.setProperty(key, (URL) value);
                        } else if (value instanceof Set) {
                            cap.setProperty(key, (Set) value);
                        } else if (value instanceof Integer || value instanceof Long) {
                            if (value != null) {
                                cap.setProperty(key, (Long) value);
                            }
                        } else {
                            cap.setProperty(key, value.toString());
                        }
                    }
                }
            }
            addCapability(cap);
        }
        for (org.osgi.service.obr.Requirement freq : resource.getRequirements()) {
            Requirement req = new RequirementImpl(freq.getName());

            req.setComment(freq.getComment());
            req.setFilter(freq.getFilter());
            req.setExtend(freq.isExtend());
            req.setMultiple(freq.isMultiple());
            req.setOptional(freq.isOptional());
            addRequirement(req);
        }
        for (String fcat : resource.getCategories()) {
            addCategory(fcat);
        }

        setSymbolicName(resource.getSymbolicName());
        setPresentationName(resource.getPresentationName());

//        resource.getProperties(); // TODO

//        setSize(resource.getSize() != null ? resource.getSize() : 0); // TODO set size
//        try {
//            setUri(new URI(resource.getURI())); // TODO set URI
//        } catch (Exception ex) {
////            System.out.println("Exception: " + ex.getLocalizedMessage() + ", uri: " + resource.getURI());
////            setUri(null); // TODO co s tim?
//        }

        setVersion(resource.getVersion());

        setId(resource.getId());

        setWritable(false);
    }
    
    
}
