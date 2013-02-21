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
    public String getDirective(@Nonnull String name);

    @Nonnull
    public Map<String, String> getDirectives();

    public void setDirective(@Nonnull String name, @Nonnull String directive);
    
    public void unsetDirective(@Nonnull String name);
}
