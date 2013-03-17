package cz.zcu.kiv.crce.repository;

/**
 * Implementing class of this interface can instantiate and register Buffer.
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public interface SessionRegister {
    
    public static final String SERVICE_SESSION_ID = "service.sid";

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
    SessionData getSessionData(String sessionId);
}
