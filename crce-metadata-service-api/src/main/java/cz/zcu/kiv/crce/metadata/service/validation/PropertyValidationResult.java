package cz.zcu.kiv.crce.metadata.service.validation;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cz.zcu.kiv.crce.metadata.Property;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface PropertyValidationResult extends ValidationResult {

    void setProperty(@Nonnull Property property);

    @Nullable
    Property getProperty();

    @Nonnull
    List<PropertyValidationResult> getChildResults();

    void addChildResult(@Nonnull PropertyValidationResult result);
}
