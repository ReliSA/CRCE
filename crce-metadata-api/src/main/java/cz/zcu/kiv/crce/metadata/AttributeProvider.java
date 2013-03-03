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
    <T> Attribute<T> getAttribute(@Nonnull AttributeType<T> type);

    @CheckForNull
    <T> T getAttributeValue(@Nonnull AttributeType<T> type);
    
    @CheckForNull
    <T> String getAttributeStringValue(@Nonnull AttributeType<T> type);

    <T> boolean setAttribute(@Nonnull AttributeType<T> type, @Nonnull T value);
    
    <T> boolean setAttribute(@Nonnull Attribute<T> attribute);
    
    <T> boolean setAttribute(@Nonnull String name, @Nonnull Class<T> type, @Nonnull T value);

    <T> boolean unsetAttribute(@Nonnull Attribute<T> attribute);
    
    <T> boolean unsetAttribute(@Nonnull AttributeType<T> type);
    
    @Nonnull
    List<? extends Attribute<?>> getAttributes();

    @Nonnull
    Map<String, ? extends Attribute<?>> getAttributesMap();
}
