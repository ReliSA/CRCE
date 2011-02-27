package cz.zcu.kiv.crce.repository;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface SessionFactory {
    
    public static final String SERVICE_SESSION_ID = "ssid";

    void createSession(String sessionId);

    void destroySession(String sessionId);
}
