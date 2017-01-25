package cz.zcu.kiv.crce.metadata.osgi.util;

import javax.annotation.Nonnull;

import org.osgi.framework.InvalidSyntaxException;
import cz.zcu.kiv.crce.metadata.Requirement;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface FilterParser {

    @Nonnull
    Requirement parse(@Nonnull String filter, @Nonnull String namespace) throws InvalidSyntaxException;
}
