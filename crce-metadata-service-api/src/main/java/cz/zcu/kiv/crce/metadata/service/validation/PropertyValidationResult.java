package cz.zcu.kiv.crce.metadata.service.validation;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cz.zcu.kiv.crce.metadata.EqualityComparable;
import cz.zcu.kiv.crce.metadata.Property;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 * @param <T>
 */
public interface PropertyValidationResult<T extends EqualityComparable<T>> extends ValidationResult {

    void setProperty(@Nonnull Property<T> property);

    @Nullable
    Property<T> getProperty();
}
