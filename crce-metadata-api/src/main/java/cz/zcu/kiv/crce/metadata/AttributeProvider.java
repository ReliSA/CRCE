package cz.zcu.kiv.crce.metadata;

import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Common interface for subclasses that can provide Attributes.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface AttributeProvider {

    @CheckForNull
    public <T> Attribute<T> getAttribute(@Nonnull DataType<T> t);

    @CheckForNull
    public <T> T getAttributeValue(@Nonnull DataType<T> attribute);
    
    @CheckForNull
    public String getAttributeStringValue(@Nonnull String name);

    public <T> void setAttribute(@Nonnull DataType<T> attribute, @Nonnull T value);
    
    public <T> void setAttribute(@Nonnull Attribute<T> attribute);

    public <T> void unsetAttribute(@Nonnull Attribute<T> attribute);
    
    @Nonnull
    public List<Attribute<?>> getAttributes();

    @Nonnull
    public Map<DataType<?>, Attribute<?>> getAttributesMap();
}
