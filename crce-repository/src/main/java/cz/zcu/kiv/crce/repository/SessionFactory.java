package cz.zcu.kiv.crce.repository;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface SessionFactory {

    void createSession(String sessionId);

    void destroySession(String sessionId);
}
