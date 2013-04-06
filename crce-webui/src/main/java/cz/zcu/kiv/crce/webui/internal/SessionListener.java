package cz.zcu.kiv.crce.webui.internal;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    	se.getSession();
        String sid = se.getSession().getId();
        Activator.instance().getSessionFactory().registerSession(sid);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        String sid = se.getSession().getId();
        Activator.instance().getSessionFactory().unregisterSession(sid);
    }

}
