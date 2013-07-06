package cz.zcu.kiv.crce.metadata.dao.internal.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface SequenceMapper {

    @Select("select nextval(#{sequence})")
    @Options(flushCache = true)
    long nextVal(String sequence);
}
