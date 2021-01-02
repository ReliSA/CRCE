package cz.zcu.kiv.crce.metadata;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Represents an Capability.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface Capability
        extends AttributeProvider, DirectiveProvider, PropertyProvider, RequirementProvider, EqualityComparable<Capability>, Entity {

    @Nonnull
    String getId();

    @Nonnull
    String getNamespace();

    boolean addChild(Capability capability);

    boolean removeChild(Capability capability);

    @Nonnull
    List<Capability> getChildren();
}
