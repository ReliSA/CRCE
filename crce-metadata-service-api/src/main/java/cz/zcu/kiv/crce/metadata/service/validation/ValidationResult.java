package cz.zcu.kiv.crce.metadata.service.validation;

/**
 * Common validation result interface.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface ValidationResult {

    /**
     * Returns true if the whole validated content is valid.
     * @return
     */
    boolean isValid();
}
