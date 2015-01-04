package cz.zcu.kiv.crce.metadata.service.validation;


import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.metadata.Property;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface PropertyValidationResult extends ValidationResult {

    void setProperty(Property property);

    @Nullable
    Property getProperty();
}
