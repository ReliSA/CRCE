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
    <T> Attribute<T> getAttribute(@Nonnull DataType<T> t);

    @CheckForNull
    <T> T getAttributeValue(@Nonnull DataType<T> attribute);
    
    @CheckForNull
    String getAttributeStringValue(@Nonnull String name);

    <T> boolean setAttribute(@Nonnull DataType<T> attribute, @Nonnull T value);
    
    <T> boolean setAttribute(@Nonnull Attribute<T> attribute);

    <T> boolean unsetAttribute(@Nonnull Attribute<T> attribute);
    
    @Nonnull
    List<Attribute<?>> getAttributes();

    @Nonnull
    Map<DataType<?>, Attribute<?>> getAttributesMap();
}
