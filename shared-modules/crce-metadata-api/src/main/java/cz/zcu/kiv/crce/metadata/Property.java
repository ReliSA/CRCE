package cz.zcu.kiv.crce.metadata;


import javax.annotation.Nonnull;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Property extends AttributeProvider, EqualityComparable<Property>, Entity {

    @Nonnull
    String getId();

    @Nonnull
    String getNamespace();
}
