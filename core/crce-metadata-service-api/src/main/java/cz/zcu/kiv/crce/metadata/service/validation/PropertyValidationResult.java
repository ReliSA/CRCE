package cz.zcu.kiv.crce.metadata.service.validation;


import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.PropertyProvider;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 * @param <T>
 */
@ParametersAreNonnullByDefault
public interface PropertyValidationResult<T extends PropertyProvider<T>> extends ValidationResult {

    void setProperty(Property<T> property);

    @Nullable
    Property<T> getProperty();
}
