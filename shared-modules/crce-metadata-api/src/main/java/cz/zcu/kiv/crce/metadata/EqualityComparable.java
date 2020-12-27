package cz.zcu.kiv.crce.metadata;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * This interface allows comparison of implementing entities for their equality
 * with custom level of equality depth.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 * @param <T>
 * @see EqualityLevel Explanation of equality levels.
 */
public interface EqualityComparable<T> {

    /**
     * Indicates whether some other object is "equal to" this one
     * with custom level of comparison depth.
     * @param other
     * @param level
     * @return
     */
    boolean equalsTo(@CheckForNull T other, @Nonnull EqualityLevel level);
}
