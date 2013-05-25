package cz.zcu.kiv.crce.metadata.service.validation;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface ResourceValidationResult extends ValidationResult {

    boolean isResourceValid();
}
