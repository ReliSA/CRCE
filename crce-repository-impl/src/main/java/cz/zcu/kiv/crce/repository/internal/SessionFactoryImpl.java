package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionFactory;
import cz.zcu.kiv.crce.repository.Store;
import java.util.HashMap;
import java.util.Map;
import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyManager;
import org.osgi.service.log.LogService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class SessionFactoryImpl implements SessionFactory {

    private volatile DependencyManager m_dependencyManager; /* injected by dependency manager */
    
    private final Map<String, Session> m_sessions = new HashMap<String, Session>();

    private static class Session {
        private Component m_bufferComponent;
        
    }

    @Override
    public void createSession(String sessionId) {
        synchronized (m_sessions) {
            if (!m_sessions.containsKey(sessionId)) {
                Session sd = new Session();
                BufferImpl buffer = new BufferImpl(sessionId);
                
                sd.m_bufferComponent = m_dependencyManager.createComponent()
                        .setInterface(Buffer.class.getName(), buffer.getSessionProperties())
                        .setImplementation(buffer)
                        .add(m_dependencyManager.createServiceDependency().setService(PluginManager.class).setRequired(true))
                        .add(m_dependencyManager.createServiceDependency().setService(Store.class).setRequired(true))
                        .add(m_dependencyManager.createServiceDependency().setService(LogService.class).setRequired(false));
                
                m_dependencyManager.add(sd.m_bufferComponent);
                m_sessions.put(sessionId, sd);
            }
        }
    }

    @Override
    public void destroySession(String sessionId) {
        synchronized (m_sessions) {
            Session sd = m_sessions.remove(sessionId);
            if (sd != null) {
                m_dependencyManager.remove(sd.m_bufferComponent);
            }
        }
    }

}
