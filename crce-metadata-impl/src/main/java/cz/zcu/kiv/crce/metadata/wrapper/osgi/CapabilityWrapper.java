package cz.zcu.kiv.crce.metadata.wrapper.osgi;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class CapabilityWrapper implements org.osgi.service.obr.Capability {

    private final Capability capability;

    CapabilityWrapper(Capability capability) {
        this.capability = capability;
    }

    @Override
    public String getName() {
        return capability.getName();
    }

    @Override
    public Map getProperties() {
        Map<String, List<Object>> map = new HashMap<String, List<Object>>();
        for (Property prop : capability.getProperties()) {
            String key = prop.getName().toLowerCase();
            List<Object> values = map.get(key);
            if (values == null) {
                values = new ArrayList<Object>();
                map.put(key, values);
            }
            values.add(prop.getConvertedValue());

        }
        return map;
    }
}
