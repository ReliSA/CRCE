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

    String getPresentationName(@Nonnull Resource resource);

    String getPresentationName(@Nonnull Capability capability);

    String getPresentationName(@Nonnull Requirement requirement);

    String getPresentationName(@Nonnull Property property);

    String getPresentationName(@Nonnull Attribute<?> attribute);
}
