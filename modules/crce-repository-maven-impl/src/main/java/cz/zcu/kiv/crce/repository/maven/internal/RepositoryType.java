package cz.zcu.kiv.crce.repository.maven.internal;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public enum RepositoryType {

    LOCAL,
    REMOTE;

    public static RepositoryType valueOfIgnoreCase(String value, RepositoryType defaultValue) {
        if (value != null) {
            try {
                return RepositoryType.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                // do nothing
            }
        }
        return defaultValue;
    }
}
