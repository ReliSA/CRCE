package cz.zcu.kiv.crce.metadata;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Represents a requirement to a capability with the same name.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Requirement extends AttributeProvider, DirectiveProvider {

    @Nonnull
    public String getNamespace();

    @Nonnull
    public List<Requirement> getChildren();
    
    @CheckForNull
    public Requirement getParent();
    
    @CheckForNull
    public Resource getResource();
}
