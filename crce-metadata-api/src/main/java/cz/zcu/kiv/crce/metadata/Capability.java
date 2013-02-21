package cz.zcu.kiv.crce.metadata;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Represents an Capability.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Capability extends AttributeProvider, DirectiveProvider {

    @Nonnull
    public String getNamespace();

    @CheckForNull
    public Resource getResource();

    @CheckForNull
    public Capability getParent();

    public void addChild(@Nonnull Capability capability);
    
    public void removeChild(@Nonnull Capability capability);
    
    @Nonnull
    public List<Capability> getChildren();
}
