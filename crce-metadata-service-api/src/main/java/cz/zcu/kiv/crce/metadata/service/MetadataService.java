package cz.zcu.kiv.crce.metadata.service;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface MetadataService {

    @Nonnull
    String getPresentationName(@Nonnull Resource resource);

    @Nonnull
    String getPresentationName(@Nonnull Capability capability);

    @Nonnull
    String getPresentationName(@Nonnull Requirement requirement);

    @Nonnull
    String getPresentationName(@Nonnull Property property);

    @Nonnull
    String getPresentationName(@Nonnull Attribute<?> attribute);
}
