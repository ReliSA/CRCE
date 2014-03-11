package cz.zcu.kiv.crce.repository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Implementing class of this interface can instantiate and register Buffer.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface SessionRegister {

    String SERVICE_SESSION_ID = "service.sid";

    /**
     * Registers session data with Buffer for given ID.
     * @param sessionId
     */
    void registerSession(String sessionId);

    /**
     * Unregisters and erase session data with Buffer for given ID.
     * @param sessionId
     */
    void unregisterSession(String sessionId);

    /**
     * Registers session data with Buffer for given ID and returns it.
     * @param sessionId
     * @return
     */
    @Nonnull
    SessionData getSessionData(String sessionId);
}
