package cz.zcu.kiv.crce.repository.maven.internal;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public enum ResolutionDepth {

    NONE,
    DIRECT,
    TRANSITIVE;

    public static ResolutionDepth valueOfIgnoreCase(String value, ResolutionDepth defaultValue) {
        if (value != null) {
            try {
                return ResolutionDepth.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                // do nothing
            }
        }
        return defaultValue;
    }
}
