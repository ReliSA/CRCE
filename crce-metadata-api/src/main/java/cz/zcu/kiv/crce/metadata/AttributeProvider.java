package cz.zcu.kiv.crce.metadata;

import java.util.List;
import java.util.Map;

/**
 * Common interface for subclasses that can provide Attributes.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface AttributeProvider {

    public <T> Attribute<T> getAttribute(DataType<T> t);

    public <T> T getAttributeValue(DataType<T> attribute);
    
    public String getAttributeStringValue(String name);

    public <T> void setAttribute(DataType<T> attribute, T value);
    
    public <T> void setAttribute(Attribute<T> attribute);

    public List<Attribute<?>> getAttributes();

    public Map<DataType<?>, Attribute<?>> getAttributesMap();
}
