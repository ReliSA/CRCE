package cz.zcu.kiv.crce.metadata.dao.internal.mapper;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbResource;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface ResolvingMapper {

    @SelectProvider(type = ResolvingSqlProvider.class, method = "getResourcesAnd")
    List<DbResource> getResourcesAnd(
            @Param("repositoryId") long repositoryId, @Param("namespace") @Nonnull String namespace, @Param("attributes") List<Attribute<?>> attributes);

    @SelectProvider(type = ResolvingSqlProvider.class, method = "getResourcesOr")
    List<DbResource> getResourcesOr(
            @Param("repositoryId") long repositoryId, @Param("namespace") @Nonnull String namespace, @Param("attributes") List<Attribute<?>> attributes);
}
