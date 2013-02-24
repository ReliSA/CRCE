package cz.zcu.kiv.crce.metadata;

import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a requirement to a capability with the same name.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Requirement extends AttributeProvider, DirectiveProvider {

    @Nonnull
    String getNamespace();

    @CheckForNull
    Resource getResource();
    
    @CheckForNull
    Requirement getParent();
    
    boolean setParent(@Nullable Requirement parent);

    boolean addNestedRequirement(@Nonnull Requirement requirement);
    
    boolean removeNestedRequirement(@Nonnull Requirement requirement);
    
    @Nonnull
    List<Requirement> getNestedRequirements();

    @CheckForNull
    <T> MatchingAttribute<T> getAttribute(@Nonnull AttributeType<T> t);
    
    <T> boolean setAttribute(@Nonnull AttributeType<T> attribute, @CheckForNull T value, @Nonnull Operator operator);
    
    @Nonnull
    @Override
    List<MatchingAttribute<?>> getAttributes();

    @Nonnull
    @Override
    Map<AttributeType<?>, MatchingAttribute<?>> getAttributesMap();

    Operator getAttributeOperator(@Nonnull AttributeType<?> attribute);
}
