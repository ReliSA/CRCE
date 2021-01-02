package cz.zcu.kiv.crce.metadata;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Common interface for subclasses that can provide Attributes.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface AttributeProvider extends Serializable {

    @CheckForNull
    <T> Attribute<T> getAttribute(AttributeType<T> type);

    @CheckForNull
    <T> T getAttributeValue(AttributeType<T> type);

    @CheckForNull
    <T> String getAttributeStringValue(AttributeType<T> type);

    <T> boolean setAttribute(AttributeType<T> type, T value);

    <T> boolean setAttribute(AttributeType<T> type, T value, Operator operator);

    <T> boolean setAttribute(String name, Class<T> type, T value);

    <T> boolean setAttribute(String name, Class<T> type, T value, Operator operator);

    <T> boolean setAttribute(Attribute<T> attribute);

    <T> boolean removeAttribute(Attribute<T> attribute);

    <T> boolean removeAttribute(AttributeType<T> type);

    <T> boolean removeAttribute(String name);

    @Nonnull
    List<Attribute<?>> getAttributes();

    @Nonnull
    Map<String, Attribute<?>> getAttributesMap();
}
