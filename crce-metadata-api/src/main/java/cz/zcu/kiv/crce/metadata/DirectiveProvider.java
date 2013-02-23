package cz.zcu.kiv.crce.metadata;

import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface DirectiveProvider {

    @CheckForNull
    String getDirective(@Nonnull String name);

    @Nonnull
    Map<String, String> getDirectives();

    boolean setDirective(@Nonnull String name, @Nonnull String directive);
    
    boolean unsetDirective(@Nonnull String name);
}
