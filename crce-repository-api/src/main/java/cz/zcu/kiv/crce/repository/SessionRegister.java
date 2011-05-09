package cz.zcu.kiv.crce.repository;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface SessionRegister {
    
    public static final String SERVICE_SESSION_ID = "service.sid";

    void registerSession(String sessionId);

    void unregisterSession(String sessionId);
    
    SessionData getSessionData(String sessionId);
}
