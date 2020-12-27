package cz.zcu.kiv.crce.metadata.dao.internal.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import cz.zcu.kiv.crce.metadata.dao.filter.ResourceFilter;
import cz.zcu.kiv.crce.metadata.dao.internal.db.DbResource;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface ResolvingMapper {

    @SelectProvider(type = SqlFilterProvider.class, method = "generateSQL")
    List<DbResource> getResources(@Param(SqlFilterProvider.PARAM_REPOSITORY_ID) long repositoryId, @Param(SqlFilterProvider.PARAM_FILTER) ResourceFilter filter);
}
