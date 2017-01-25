package cz.zcu.kiv.crce.metadata.service.validation;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Common validation result interface.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface ValidationResult {

    String getEntityId();

    void setEntityId(String id);

    /**
     * Indicates whether the whole validated content is valid.<p>
     * Validated content can be structured in hierarchy tree. If any validated entity is not valid,
     * then the whole parent structure (which the entity is child of) is not valid.
     * @return true if the whole validated content is valid, false otherwise.
     */
    boolean isContextValid();

    /**
     * Sets validity of the validated content.
     * @param valid content validity.
     */
    void setContextValid(boolean valid);

    boolean isEntityValid();

    void setEntityValid(boolean valid);

    /**
     * Returns the list of reasons why the validated content is not valid.
     * @return
     */
    @Nonnull
    List<Reason> getReasons();

    void addReason(Reason reason);
}
