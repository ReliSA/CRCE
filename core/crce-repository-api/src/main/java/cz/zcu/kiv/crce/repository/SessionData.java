package cz.zcu.kiv.crce.repository;

import javax.annotation.Nonnull;

/**
 * This interface defines a session-dependent data for instances of Buffer.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface SessionData {

    @Nonnull
    Buffer getBuffer();
}
