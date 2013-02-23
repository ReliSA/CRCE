package cz.zcu.kiv.crce.metadata;

import java.util.List;

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

    boolean addChild(@Nonnull Requirement capability);
    
    boolean removeChild(@Nonnull Requirement capability);
    
    @Nonnull
    List<Requirement> getChildren();
}
