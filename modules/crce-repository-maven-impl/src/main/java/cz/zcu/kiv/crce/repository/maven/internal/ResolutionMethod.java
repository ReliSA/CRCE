package cz.zcu.kiv.crce.repository.maven.internal;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public enum ResolutionMethod {
    POM,
    JAR;

    public static ResolutionMethod valueOfIgnoreCase(String value, ResolutionMethod defaultValue) {
        if (value != null) {
            try {
                return ResolutionMethod.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) { // NOPMD
                // do nothing
            }
        }
        return defaultValue;
    }
}
