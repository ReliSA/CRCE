package cz.zcu.kiv.crce.metadata;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nonnull;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface RequirementProvider extends Entity, Serializable {

    @Nonnull
    List<Requirement> getRequirements();

    @Nonnull
    List<Requirement> getRequirements(String namespace);

    boolean hasRequirement(Requirement requirement);

    void addRequirement(Requirement requirement);

    void removeRequirement(Requirement requirement);
}
