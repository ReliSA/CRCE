package cz.zcu.kiv.crce.webui.internal;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        String sid = se.getSession().getId();
        Activator.instance().getSessionFactory().createSession(sid);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        String sid = se.getSession().getId();
        Activator.instance().getSessionFactory().destroySession(sid);
    }

}
