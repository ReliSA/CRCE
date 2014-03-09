package cz.zcu.kiv.crce.metadata;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Common interface for subclasses that can provide Attributes.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface AttributeProvider extends Serializable {

    @CheckForNull
    <T> Attribute<T> getAttribute(@Nonnull AttributeType<T> type);

    @CheckForNull
    <T> T getAttributeValue(@Nonnull AttributeType<T> type);

    @CheckForNull
    <T> String getAttributeStringValue(@Nonnull AttributeType<T> type);

    <T> boolean setAttribute(@Nonnull AttributeType<T> type, @Nonnull T value);

    <T> boolean setAttribute(@Nonnull AttributeType<T> type, @Nonnull T value, @Nonnull Operator operator);

    <T> boolean setAttribute(@Nonnull String name, @Nonnull Class<T> type, @Nonnull T value);

    <T> boolean setAttribute(@Nonnull String name, @Nonnull Class<T> type, @Nonnull T value, @Nonnull Operator operator);

    <T> boolean setAttribute(@Nonnull Attribute<T> attribute);

    <T> boolean removeAttribute(@Nonnull Attribute<T> attribute);

    <T> boolean removeAttribute(@Nonnull AttributeType<T> type);

    <T> boolean removeAttribute(@Nonnull String name);

    @Nonnull
    List<Attribute<?>> getAttributes();

    @Nonnull
    Map<String, Attribute<?>> getAttributesMap();
}
