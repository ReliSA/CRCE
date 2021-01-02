package cz.zcu.kiv.crce.metadata;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface PropertyProvider extends Entity, Serializable {

    @Nonnull
    List<Property> getProperties();

    @Nonnull
    List<Property> getProperties(String namespace);

    boolean hasProperty(Property property);

    void addProperty(Property property);

    void removeProperty(Property property);
}
