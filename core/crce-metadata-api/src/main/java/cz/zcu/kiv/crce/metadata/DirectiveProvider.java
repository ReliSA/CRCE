package cz.zcu.kiv.crce.metadata;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface DirectiveProvider extends Serializable {

    @CheckForNull
    String getDirective(String name);

    @Nonnull
    Map<String, String> getDirectives();

    boolean setDirective(String name, String directive);

    boolean unsetDirective(String name);
}
