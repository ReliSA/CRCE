package cz.zcu.kiv.crce.metadata.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.PropertyProvider;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class PropertyProviderImpl implements PropertyProvider {

    private static final long serialVersionUID = 1L;

    private final Map<String, List<Property>> allProperties = new HashMap<>();

    @Override
    public List<Property> getProperties() {
        List<Property> result = new ArrayList<>();
        for (List<Property> properties : allProperties.values()) {
            result.addAll(properties);
        }
        return result;
    }

    @Override
    public List<Property> getProperties(String namespace) {
        List<Property> result = allProperties.get(namespace);
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    @Override
    public boolean hasProperty(Property property) {
        List<Property> properties = allProperties.get(property.getNamespace());
        if (properties != null) {
            return properties.contains(property);
        }
        return false;
    }

    @Override
    public void addProperty(Property property) {
        List<Property> properties = allProperties.get(property.getNamespace());
        if (properties == null) {
            properties = new ArrayList<>();
            allProperties.put(property.getNamespace(), properties);
        }
        properties.add(property);
    }

    @Override
    public void removeProperty(Property property) {
        List<Property> properties = allProperties.get(property.getNamespace());
        if (properties != null) {
            properties.remove(property);
        }
    }

}
